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

public class TopUpIT {

    private static final String zooKeeperHost = "localhost";
    private static final String zooKeeperPort = "2181";
    private static HubApi hubApi;
    private static String userId = "bruno";
    private static String phoneNumber = "+35193334444";

    @BeforeAll
    public static void oneTimeSetUp() throws UnavailableServerInstanceException, ZooKeeperListRecordsFailedException {
        hubApi = new HubApi(zooKeeperHost, zooKeeperPort);
    }

    @AfterAll
    public static void oneTimeTearDown() {
        hubApi.close();
    }

    @Test
    public void topUpOKTest() throws ServerStatusException {
        assertEquals(10, hubApi.topUp(userId, 10, phoneNumber));
        assertEquals(15, hubApi.topUp(userId, 5, phoneNumber));
    }

    @Test
    public void nonExistentUserIdTest() {
        assertEquals(NOT_FOUND.getCode(), assertThrows(ServerStatusException.class, () -> hubApi.topUp("dani", 10, phoneNumber)).getStatus().getCode());
    }

    @Test
    public void emptyUserIdTest() {
        assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(ServerStatusException.class, () -> hubApi.topUp("", 10, phoneNumber)).getStatus().getCode());
    }

    @Test
    public void wrongPhoneNumber() {
        assertEquals(NOT_FOUND.getCode(), assertThrows(ServerStatusException.class, () -> hubApi.topUp(userId, 10, "+123456789")).getStatus().getCode());
    }

    @Test
    public void emptyPhoneNumber() {
        assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(ServerStatusException.class, () -> hubApi.topUp(userId, 10, "")).getStatus().getCode());
    }

    @Test
    public void negativeAmount() {
        assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(ServerStatusException.class, () -> hubApi.topUp(userId, -10, phoneNumber)).getStatus().getCode());
    }
}
