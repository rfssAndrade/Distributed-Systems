package pt.tecnico.bicloin.hub;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pt.tecnico.bicloin.hub.exceptions.ServerStatusException;
import pt.tecnico.bicloin.hub.exceptions.UnavailableServerInstanceException;
import pt.tecnico.bicloin.hub.exceptions.ZooKeeperListRecordsFailedException;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SysStatusIT {

    private static final String zooKeeperHost = "localhost";
    private static final String zooKeeperPort = "2181";
    private static HubApi hubApi;

    @BeforeAll
    public static void oneTimeSetUp() throws UnavailableServerInstanceException, ZooKeeperListRecordsFailedException {
        hubApi = new HubApi(zooKeeperHost, zooKeeperPort);
    }

    @AfterAll
    public static void oneTimeTearDown() {
        hubApi.close();
    }

    @Test
    public void sysStatusOKTest() throws ServerStatusException {
        ArrayList<SysStatus> servers = hubApi.sysStatus();
        assertEquals(6, servers.size());
        assertEquals("/grpc/bicloin/hub/1", servers.get(0).getPath());
        assertEquals(true, servers.get(0).isUp());
        for(int i = 1; i < 6; i++) {
            assertEquals(true, servers.get(i).isUp());
        }
    }
}
