package pt.tecnico.bicloin.hub;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pt.tecnico.bicloin.hub.exceptions.ServerStatusException;
import pt.tecnico.bicloin.hub.exceptions.UnavailableServerInstanceException;
import pt.tecnico.bicloin.hub.exceptions.ZooKeeperListRecordsFailedException;

import java.util.ArrayList;

import static io.grpc.Status.INVALID_ARGUMENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LocateStationIT {

    private static final String zooKeeperHost = "localhost";
    private static final String zooKeeperPort = "2181";
    private static HubApi hubApi;

    private static double latitude = 38.7633;
    private static double longitude = -9.0950;
    private static int numberStations = 5;

    @BeforeAll
    public static void oneTimeSetUp() throws UnavailableServerInstanceException, ZooKeeperListRecordsFailedException {
        hubApi = new HubApi(zooKeeperHost, zooKeeperPort);
    }

    @AfterAll
    public static void oneTimeTearDown() {
        hubApi.close();
    }

    @Test
    public void locateStationOKTest() throws ServerStatusException {
        ArrayList<String> stations = hubApi.locateStation(latitude, longitude, numberStations);
        assertEquals(5, stations.size());
        assertEquals("ocea", stations.get(0));
        assertEquals("ista", stations.get(1));
        assertEquals("gulb", stations.get(2));
        assertEquals("cate", stations.get(3));
        assertEquals("prcm", stations.get(4));
    }

    @Test
    public void invalidLatitudeTest() {
        assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(ServerStatusException.class, () -> hubApi.locateStation(100, longitude, numberStations)).getStatus().getCode());
    }

    @Test
    public void invalidLongitudeTest() {
        assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(ServerStatusException.class, () -> hubApi.locateStation(latitude, 200, numberStations)).getStatus().getCode());
    }

    @Test
    public void invalidNumberStationsTest() {
        assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(ServerStatusException.class, () -> hubApi.locateStation(latitude, longitude, -2)).getStatus().getCode());
    }
}
