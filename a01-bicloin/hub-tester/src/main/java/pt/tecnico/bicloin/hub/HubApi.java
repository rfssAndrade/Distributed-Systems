package pt.tecnico.bicloin.hub;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Function;

import com.google.protobuf.Message;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import pt.tecnico.bicloin.hub.exceptions.ServerStatusException;
import pt.tecnico.bicloin.hub.grpc.Hub.*;
import pt.tecnico.bicloin.hub.grpc.HubServiceGrpc;
import pt.tecnico.bicloin.hub.grpc.HubServiceGrpc.HubServiceBlockingStub;
import pt.tecnico.bicloin.hub.exceptions.UnavailableServerInstanceException;
import pt.tecnico.bicloin.hub.exceptions.ZooKeeperListRecordsFailedException;
import pt.tecnico.bicloin.hub.grpc.Hub.PingRequest;
import pt.tecnico.bicloin.hub.grpc.Hub.PingResponse;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import pt.ulisboa.tecnico.sdis.zk.ZKRecord;

/**
 * API to connect with the hub server.
 * 
 *
 */
public class HubApi implements AutoCloseable {

	/**
	 * ZooKeeper host.
	 */
	private final String zooHost;
	
	/**
	 * ZooKeeper port.
	 */
	private final String zooPort;
	
	/**
	 * Channel of the connection with the server.
	 */
	private ManagedChannel channel;
	
	/**
	 * Stub of the connection with the server.
	 */
	private HubServiceBlockingStub stub;
	
	/**
	 * Path of the hub server.
	 */
	private final String path = "/grpc/bicloin/hub";
	
	/**
	 * Initializes a connection with the server.
	 * @param zooHost	ZooKeeper host
	 * @param zooPort	ZooKeeper port
	 * @throws ZooKeeperListRecordsFailedException 
	 * @throws UnavailableServerInstanceException 
	 */
	public HubApi(String zooHost, String zooPort) throws ZooKeeperListRecordsFailedException, UnavailableServerInstanceException {
		this.zooHost = zooHost;
		this.zooPort = zooPort;

		channel = null;
		stub = null;
	}
	
	/**
	 * Connects to one instance of the server.
	 * @throws ZooKeeperListRecordsFailedException
	 * @throws UnavailableServerInstanceException
	 */
	private void connect() throws ZooKeeperListRecordsFailedException, UnavailableServerInstanceException {
		ZKNaming zkNaming = new ZKNaming(zooHost, zooPort);
		
		ArrayList<ZKRecord> records = null;
		try {
			records = new ArrayList<ZKRecord>(zkNaming.listRecords(path));
		} catch (ZKNamingException e) {
			throw new ZooKeeperListRecordsFailedException(zooHost, zooPort, path);
		}
		
		Collections.shuffle(records);

		String pingMessage = "hello";
		for (int i = 0; i < records.size(); i++) {
			String target = records.get(i).getURI();
			
			channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
			stub = HubServiceGrpc.newBlockingStub(channel);

			try {
				if (ping(pingMessage).equals(pingMessage)) {
					System.out.println("Connected to hub instance " + (i + 1) + "/" + records.size());
					return;
				} else {
					System.out.print("Failed to connect to hub instance " + (i + 1) + "/" + records.size());
				}
			} catch (ServerStatusException e) {
				System.out.print("Failed to connect to hub instance " + (i + 1) + "/" + records.size());
			}
		}

		channel = null;
		stub = null;

		throw new UnavailableServerInstanceException(zooHost, zooPort, path);
	}
	
	public String ping(String input) throws ServerStatusException {
		return ((PingResponse) action(request -> stub.ping((PingRequest) request),
				PingRequest.newBuilder().setInput(input).build())).getOutput();
	}

	public int balance(String userId) throws ServerStatusException {
		return ((BalanceResponse) action(request -> stub.balance((BalanceRequest) request),
				BalanceRequest.newBuilder().setUserId(userId).build())).getBalance();
	}

	public int topUp(String userId, int amount, String phoneNumber) throws ServerStatusException {
		return ((TopUpResponse) action(request -> stub.topUp((TopUpRequest) request),
				TopUpRequest.newBuilder().setUserId(userId).setAmount(amount).setPhoneNumber(phoneNumber).build())).getBalance();
	}

	public void bikeUp(String userId, double latitude, double longitude, String stationId) throws ServerStatusException {
		action(request -> stub.bikeUp((BikeUpRequest) request),
				BikeUpRequest.newBuilder().setUserId(userId).setCoordinates(Coordinates.newBuilder().setLatitude(latitude).setLongitude(longitude).build()).setStationId(stationId).build());
	}

	public void bikeDown(String userId, double latitude, double longitude, String stationId) throws ServerStatusException {
		action(request -> stub.bikeDown((BikeDownRequest) request),
				BikeDownRequest.newBuilder().setUserId(userId).setCoordinates(Coordinates.newBuilder().setLatitude(latitude).setLongitude(longitude).build()).setStationId(stationId).build());
	}

	public InfoStation infoStation(String stationId) throws ServerStatusException {
		InfoStationResponse response = ((InfoStationResponse) action(request -> stub.infoStation((InfoStationRequest) request),
				InfoStationRequest.newBuilder().setStationId(stationId).build()));
		return new InfoStation(response.getName(), response.getCoordinates().getLatitude(), response.getCoordinates().getLongitude(),
				response.getNumberDocks(), response.getPrize(), response.getAvailableBikes(), response.getNumberWithdrawals(),
				response.getNumberReturns());
	}

	public ArrayList<String> locateStation(double latitude, double longitude, int k) throws ServerStatusException {
		LocateStationResponse response = ((LocateStationResponse) action(request -> stub.locateStation((LocateStationRequest) request),
				LocateStationRequest.newBuilder().setCoordinates(Coordinates.newBuilder().setLatitude(latitude).setLongitude(longitude).build()).setNumberStations(k).build()));

		ArrayList<String> stations = new ArrayList<>();
		for (int i = 0; i < response.getStationIdsCount(); i++)
			stations.add(response.getStationIds(i));

		return stations;
	}

	public ArrayList<SysStatus> sysStatus() throws ServerStatusException {
		SysStatusResponse response = (SysStatusResponse) action(request -> stub.sysStatus((SysStatusRequest) request),
				SysStatusRequest.getDefaultInstance());

		ArrayList<SysStatus> systems = new ArrayList<>();
		for (int i = 0; i < response.getStatusCount(); i++)
			systems.add(new SysStatus(response.getStatus(i).getPath(), response.getStatus(i).getIsUp()));

		return systems;
	}

	private Message action(Function<Message, Message> service, Message request) throws ServerStatusException {
		if (channel == null || stub == null) {
			try {
				connect();
			} catch (ZooKeeperListRecordsFailedException | UnavailableServerInstanceException e) {
				throw new ServerStatusException(Status.UNAVAILABLE.asRuntimeException());
			}
		}

		for (int i = 0; i < 2; i++) {
			try {
				return service.apply(request);
			} catch (StatusRuntimeException e) {
				if (i == 0 && e.getStatus().getCode() == Status.Code.UNAVAILABLE) {
					try {
						connect();
					} catch (ZooKeeperListRecordsFailedException | UnavailableServerInstanceException e2) {
						throw new ServerStatusException(e);
					}
				} else {
					throw new ServerStatusException(e);
				}
			}
		}
		return null;
	}

	/**
	 * Closes the connection with the server. No need to use if this instance was created in a try-with-resources.
	 */
	@Override
	public void close() {
		channel.shutdownNow();
	}
}
