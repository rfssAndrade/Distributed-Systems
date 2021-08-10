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

public class BalanceIT {

    private static final String zooKeeperHost = "localhost";
    private static final String zooKeeperPort = "2181";
    private static HubApi hubApi;
    private final static String userId = "alice";
    private final static String phoneNumber = "+35191102030";

    @BeforeAll
    public static void oneTimeSetUp() throws UnavailableServerInstanceException, ZooKeeperListRecordsFailedException {
        hubApi = new HubApi(zooKeeperHost, zooKeeperPort);
    }

    @AfterAll
    public static void oneTimeTearDown() {
        hubApi.close();
    }

    @Test
    public void balanceOKTest() throws ServerStatusException {
        int balance = 10;
        assertEquals(0, hubApi.balance(userId));
        hubApi.topUp("alice", balance, phoneNumber);
        assertEquals(balance, hubApi.balance(userId));
    }

    @Test
    public void nonExistentUserIdTest() {
        String userId = "dani";
        assertEquals(NOT_FOUND.getCode(), assertThrows(ServerStatusException.class, () -> hubApi.balance(userId)).getStatus().getCode());
    }

    @Test
    public void emptyUserIdTest() {
        String userId = "";
        assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(ServerStatusException.class, () -> hubApi.balance(userId)).getStatus().getCode());
    }
}
