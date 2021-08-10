package pt.tecnico.rec;

import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pt.tecnico.rec.exceptions.ServerStatusException;
import pt.tecnico.rec.exceptions.UnavailableServerInstanceException;
import pt.tecnico.rec.exceptions.ZooKeeperListRecordsFailedException;

import static io.grpc.Status.INVALID_ARGUMENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReadIT {

    private static final String zooKeeperHost = "localhost";
    private static final String zooKeeperPort = "2181";
    private static RecordApi recordApi;

    @BeforeAll
    public static void oneTimeSetUp() throws UnavailableServerInstanceException, ZooKeeperListRecordsFailedException {
        recordApi = new RecordApi(zooKeeperHost, zooKeeperPort, "AndradeChefeDeProjeto");
    }

    @AfterAll
    public static void oneTimeTearDown() throws Exception {
        recordApi.close();
    }

    @Test
    public void readOKTest() throws ServerStatusException {
        String key = "readKey";
        String value = "readValue";
        assertEquals("", recordApi.read(key));

        recordApi.write(key, value);
        assertEquals(value, recordApi.read(key));
    }

    @Test
    public void readNOKTest() {
        String key = "";
        assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(ServerStatusException.class, () -> recordApi.read(key)).getStatus().getCode());
    }
}
