package pt.tecnico.bicloin.hub;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pt.tecnico.bicloin.hub.exceptions.ServerStatusException;
import pt.tecnico.bicloin.hub.exceptions.UnavailableServerInstanceException;
import pt.tecnico.bicloin.hub.exceptions.ZooKeeperListRecordsFailedException;

import static io.grpc.Status.INVALID_ARGUMENT;
import static io.grpc.Status.FAILED_PRECONDITION;
import static io.grpc.Status.NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BikeUpIT {

    private static final String zooKeeperHost = "localhost";
    private static final String zooKeeperPort = "2181";
    private static HubApi hubApi;
    private static final String userId = "diana";
    private static final double latitude = 38.6972;
    private static final double longitude = -9.2064;
    private static final String stationId = "jero";

    @BeforeAll
    public static void oneTimeSetUp() throws UnavailableServerInstanceException, ZooKeeperListRecordsFailedException, ServerStatusException {
        hubApi = new HubApi(zooKeeperHost, zooKeeperPort);
        hubApi.topUp(userId, 100, "+34010203");
        hubApi.topUp("eva", 100, "+155509080706");
    }

    @AfterAll
    public static void oneTimeTearDown() {
        hubApi.close();
    }

    @Test
    public void bikeUpOKTest() throws ServerStatusException {
        InfoStation infoStation = hubApi.infoStation(stationId);
        assertEquals(20, infoStation.getAvailableBikes());
        assertEquals(0, infoStation.getWithdrawals());
        assertEquals(0, infoStation.getReturns());
        assertEquals(100, hubApi.balance(userId));

        hubApi.bikeUp(userId, latitude, longitude, stationId);

        infoStation = hubApi.infoStation(stationId);
        assertEquals(19, infoStation.getAvailableBikes());
        assertEquals(1, infoStation.getWithdrawals());
        assertEquals(0, infoStation.getReturns());
        assertEquals(100 - 10, hubApi.balance(userId));

        hubApi.bikeDown(userId, latitude, longitude, stationId);
    }

    @Test
    public void withdrawalTwoBikesTest() throws ServerStatusException {
        hubApi.topUp("Dede", 50, "+9145345678");
        hubApi.bikeUp("Dede", 38.7075, -9.1364, "prcm");
        assertEquals(FAILED_PRECONDITION.getCode(), assertThrows(ServerStatusException.class, () -> hubApi.bikeUp("Dede", 38.7075, -9.1364, "prcm")).getStatus().getCode());
    }

    @Test
    public void tooFarFromStationTest() throws ServerStatusException {
        hubApi.topUp("Lopes", 50, "+54325");
        assertEquals(FAILED_PRECONDITION.getCode(), assertThrows(ServerStatusException.class, () -> hubApi.bikeUp("Lopes", 0, 0, "urbe")).getStatus().getCode());
    }

    @Test
    public void noBikesAtStationTest() {
        assertEquals(FAILED_PRECONDITION.getCode(), assertThrows(ServerStatusException.class, () -> hubApi.bikeUp("eva", 38.7097, -9.1336, "cate")).getStatus().getCode());
    }

    @Test
    public void noMoneyWithdrawal() {
        assertEquals(FAILED_PRECONDITION.getCode(), assertThrows(ServerStatusException.class, () -> hubApi.bikeUp("andrade", 38.6867, -9.3124, "stao")).getStatus().getCode());
    }

    @Test
    public void nonExistentUserIdTest() {
        assertEquals(NOT_FOUND.getCode(), assertThrows(ServerStatusException.class, () -> hubApi.bikeUp("Dani", latitude, longitude, stationId)).getStatus().getCode());
    }

    @Test
    public void emptyUserIdTest() {
        assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(ServerStatusException.class, () -> hubApi.bikeUp("", latitude, longitude, stationId)).getStatus().getCode());
    }

    @Test
    public void invalidLatitudeTest() {
        assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(ServerStatusException.class, () -> hubApi.bikeUp(userId, -100, longitude, stationId)).getStatus().getCode());
    }

    @Test
    public void invalidLongitudeTest() {
        assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(ServerStatusException.class, () -> hubApi.bikeUp(userId, latitude, -200, stationId)).getStatus().getCode());
    }

    @Test
    public void nonExistentStationIdTest() {
        assertEquals(NOT_FOUND.getCode(), assertThrows(ServerStatusException.class, () -> hubApi.bikeUp(userId, latitude, longitude, "Vila")).getStatus().getCode());
    }

    @Test
    public void emptyStationIdTest() {
        assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(ServerStatusException.class, () -> hubApi.bikeUp(userId, latitude, longitude, "")).getStatus().getCode());
    }
}
