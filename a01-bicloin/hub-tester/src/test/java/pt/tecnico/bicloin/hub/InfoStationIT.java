package pt.tecnico.bicloin.hub;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pt.tecnico.bicloin.hub.exceptions.ServerStatusException;
import pt.tecnico.bicloin.hub.exceptions.UnavailableServerInstanceException;
import pt.tecnico.bicloin.hub.exceptions.ZooKeeperListRecordsFailedException;

import static io.grpc.Status.NOT_FOUND;
import static io.grpc.Status.INVALID_ARGUMENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InfoStationIT {

    private static final String zooKeeperHost = "localhost";
    private static final String zooKeeperPort = "2181";
    private static HubApi hubApi;
    private static String stationID = "ocea";

    @BeforeAll
    public static void oneTimeSetUp() throws UnavailableServerInstanceException, ZooKeeperListRecordsFailedException {
        hubApi = new HubApi(zooKeeperHost, zooKeeperPort);
    }

    @AfterAll
    public static void oneTimeTearDown() {
        hubApi.close();
    }

    @Test
    public void infoStationOKTest() throws ServerStatusException {
        InfoStation infoStation = hubApi.infoStation(stationID);
        assertEquals("Oceanário", infoStation.getName());
        assertEquals(38.7633, infoStation.getLatitude());
        assertEquals(-9.0950, infoStation.getLongitude());
        assertEquals(20, infoStation.getDocks());
        assertEquals(2, infoStation.getPrize());
        assertEquals(15, infoStation.getAvailableBikes());
        assertEquals(0, infoStation.getWithdrawals());
        assertEquals(0, infoStation.getReturns());

        hubApi.topUp("eva", 100, "+155509080706");
        hubApi.bikeUp("eva", 38.7633, -9.0950, stationID);
        hubApi.bikeDown("eva", 38.7633, -9.0950, stationID);
        hubApi.bikeUp("eva", 38.7633, -9.0950, stationID);

        infoStation = hubApi.infoStation(stationID);
        assertEquals("Oceanário", infoStation.getName());
        assertEquals(38.7633, infoStation.getLatitude());
        assertEquals(-9.0950, infoStation.getLongitude());
        assertEquals(20, infoStation.getDocks());
        assertEquals(2, infoStation.getPrize());
        assertEquals(14, infoStation.getAvailableBikes());
        assertEquals(2, infoStation.getWithdrawals());
        assertEquals(1, infoStation.getReturns());
    }

    @Test
    public void nonExistentStationIdTest() {
        assertEquals(NOT_FOUND.getCode(), assertThrows(ServerStatusException.class, () -> hubApi.infoStation("Alve")).getStatus().getCode());
    }

    @Test
    public void emptyStationIdTest() {
        assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(ServerStatusException.class, () -> hubApi.infoStation("")).getStatus().getCode());
    }
}
