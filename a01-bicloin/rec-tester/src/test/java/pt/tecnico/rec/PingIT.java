package pt.tecnico.rec;

import static io.grpc.Status.INVALID_ARGUMENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.grpc.StatusRuntimeException;
import pt.tecnico.rec.exceptions.ServerStatusException;
import pt.tecnico.rec.exceptions.UnavailableServerInstanceException;
import pt.tecnico.rec.exceptions.ZooKeeperListRecordsFailedException;

public class PingIT {

	private static final String zooKeeperHost = "localhost";
	private static final String zooKeeperPort = "2181";
	private static RecordApi recordApi;

	private String path = "/grpc/bicloin/rec/1";

	@BeforeAll
	public static void oneTimeSetUp() throws UnavailableServerInstanceException, ZooKeeperListRecordsFailedException {
		recordApi = new RecordApi(zooKeeperHost, zooKeeperPort, "DanielLopes");
	}

	@AfterAll
	public static void oneTimeTearDown() throws Exception {
		recordApi.close();
	}

	@Test
	public void pingOKTest() throws ServerStatusException {
		String pingMessage = "friend";
		assertEquals(pingMessage, recordApi.ping(pingMessage, path));
	}
	
	@Test
	public void emptyPingTest() {
		String pingMessage = "";
		assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(ServerStatusException.class, () -> recordApi.ping(pingMessage, path)).getStatus().getCode());
	}
}
