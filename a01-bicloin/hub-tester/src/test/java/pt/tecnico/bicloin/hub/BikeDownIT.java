package pt.tecnico.bicloin.hub;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pt.tecnico.bicloin.hub.exceptions.ServerStatusException;
import pt.tecnico.bicloin.hub.exceptions.UnavailableServerInstanceException;
import pt.tecnico.bicloin.hub.exceptions.ZooKeeperListRecordsFailedException;

import static io.grpc.Status.*;
import static io.grpc.Status.FAILED_PRECONDITION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BikeDownIT {

    private static final String zooKeeperHost = "localhost";
    private static final String zooKeeperPort = "2181";
    private static HubApi hubApi;
    private static final String userId = "carlos";
    private static final double latitude = 38.7372;
    private static final double longitude = -9.3023;
    private static final String stationId = "istt";

    @BeforeAll
    public static void oneTimeSetUp() throws UnavailableServerInstanceException, ZooKeeperListRecordsFailedException, ServerStatusException {
        hubApi = new HubApi(zooKeeperHost, zooKeeperPort);
        hubApi.topUp(userId, 100, "+34203040");
        hubApi.topUp("eva", 100, "+155509080706");
    }

    @AfterAll
    public static void oneTimeTearDown() {
        hubApi.close();
    }

    @Test
    public void bikeDownOKTest() throws ServerStatusException {
        hubApi.bikeUp(userId, latitude, longitude, stationId);

        InfoStation infoStation = hubApi.infoStation(stationId);
        assertEquals(11, infoStation.getAvailableBikes());
        assertEquals(1, infoStation.getWithdrawals());
        assertEquals(0, infoStation.getReturns());
        assertEquals(100 - 10, hubApi.balance(userId));

        hubApi.bikeDown(userId, latitude, longitude, stationId);

        infoStation = hubApi.infoStation(stationId);
        assertEquals(12, infoStation.getAvailableBikes());
        assertEquals(1, infoStation.getWithdrawals());
        assertEquals(1, infoStation.getReturns());
        assertEquals(100 - 10, hubApi.balance(userId) - infoStation.getPrize());
    }

    @Test
    public void returnNoBikeTest() {
        assertEquals(FAILED_PRECONDITION.getCode(), assertThrows(ServerStatusException.class, () -> hubApi.bikeUp("eva", latitude, longitude, stationId)).getStatus().getCode());
    }

    @Test
    public void tooFarFromStationTest() throws ServerStatusException {
        hubApi.topUp("Alguem", 50, "+234123");
        hubApi.bikeUp("Alguem", 41.1599, -8.6298, "urbe");
        assertEquals(FAILED_PRECONDITION.getCode(), assertThrows(ServerStatusException.class, () -> hubApi.bikeDown("Alguem", 0, 0, "urbe")).getStatus().getCode());
    }

    @Test
    public void noDocksAtStationTest() {
        assertEquals(FAILED_PRECONDITION.getCode(), assertThrows(ServerStatusException.class, () -> hubApi.bikeUp("eva", 38.7376, -9.1545, "gulb")).getStatus().getCode());
    }

    @Test
    public void nonExistentUserIdTest() {
        assertEquals(NOT_FOUND.getCode(), assertThrows(ServerStatusException.class, () -> hubApi.bikeDown("Dani", latitude, longitude, stationId)).getStatus().getCode());
    }

    @Test
    public void emptyUserIdTest() {
        assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(ServerStatusException.class, () -> hubApi.bikeDown("", latitude, longitude, stationId)).getStatus().getCode());
    }

    @Test
    public void invalidLatitudeTest() {
        assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(ServerStatusException.class, () -> hubApi.bikeDown(userId, -100, longitude, stationId)).getStatus().getCode());
    }

    @Test
    public void invalidLongitudeTest() {
        assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(ServerStatusException.class, () -> hubApi.bikeDown(userId, latitude, -200, stationId)).getStatus().getCode());
    }

    @Test
    public void nonExistentStationIdTest() {
        assertEquals(NOT_FOUND.getCode(), assertThrows(ServerStatusException.class, () -> hubApi.bikeDown(userId, latitude, longitude, "Vila")).getStatus().getCode());
    }

    @Test
    public void emptyStationIdTest() {
        assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(ServerStatusException.class, () -> hubApi.bikeDown(userId, latitude, longitude, "")).getStatus().getCode());
    }
}
