package pt.tecnico.bicloin.hub;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pt.tecnico.bicloin.hub.exceptions.ServerStatusException;
import pt.tecnico.bicloin.hub.exceptions.UnavailableServerInstanceException;
import pt.tecnico.bicloin.hub.exceptions.ZooKeeperListRecordsFailedException;

import static io.grpc.Status.INVALID_ARGUMENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PingIT {
	
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
	public void pingOKTest() throws ServerStatusException {
		String pingMessage = "friend";
		assertEquals(pingMessage, hubApi.ping(pingMessage));
	}
	
	@Test
	public void emptyPingTest() {
		String pingMessage = "";
		assertEquals(INVALID_ARGUMENT.getCode(), assertThrows(ServerStatusException.class, () -> hubApi.ping(pingMessage)).getStatus().getCode());
	}
}
