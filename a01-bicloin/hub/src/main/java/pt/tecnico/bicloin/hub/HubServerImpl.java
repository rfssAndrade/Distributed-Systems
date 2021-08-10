package pt.tecnico.bicloin.hub;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import pt.tecnico.rec.exceptions.ServerStatusException;
import pt.tecnico.rec.grpc.Rec;
import pt.tecnico.bicloin.hub.grpc.Hub;
import pt.tecnico.bicloin.hub.grpc.HubServiceGrpc;
import pt.tecnico.bicloin.hub.grpc.HubServiceGrpc.HubServiceImplBase;
import pt.tecnico.rec.exceptions.UnavailableServerInstanceException;
import pt.tecnico.rec.exceptions.ZooKeeperListRecordsFailedException;
import pt.tecnico.rec.grpc.RecordServiceGrpc;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import pt.ulisboa.tecnico.sdis.zk.ZKRecord;

import java.rmi.ServerRuntimeException;
import java.util.ArrayList;

import static io.grpc.Status.*;

public class HubServerImpl extends HubServiceImplBase {

	private HubManager hubManager;

	private final String zooHost;

	private final String zooPort;

	public HubServerImpl(HubFactory factory, boolean initRec, String zooHost, String zooPort, String path) throws UnavailableServerInstanceException, ZooKeeperListRecordsFailedException, ServerStatusException {
		this.zooHost = zooHost;
		this.zooPort = zooPort;

		hubManager = new HubManager(zooHost, zooPort, path);

		for (String[] userData : factory.getUsersData()) {
			hubManager.addUser(userData, initRec);
		}

		for (String[] stationData : factory.getStationsData()) {
			hubManager.addStation(stationData, initRec);
		}

	}

	public void ping(Hub.PingRequest request, StreamObserver<Hub.PingResponse> responseObserver) {
		String input = request.getInput();

		System.out.println("Received ping request: message=\"" + input + "\"");

		if (!isValidString(input)) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(HubResponseError.emptyPing()).asRuntimeException());
			return;
		}

		Hub.PingResponse response = Hub.PingResponse.newBuilder().setOutput(input).build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	public void sysStatus(Hub.SysStatusRequest request, StreamObserver<Hub.SysStatusResponse> responseObserver) {
		System.out.println("Received sys_status request");

		ArrayList<String> path = new ArrayList<String>();
		ArrayList<Boolean> isUp = new ArrayList<Boolean>();

		ZKNaming zkNaming = new ZKNaming(zooHost, zooPort);

		try {
			process(zkNaming, HubConstants.SERVER_PATH, path, isUp, true);
			process(zkNaming, HubConstants.RECORD_SERVER_PATH, path, isUp, false);
		} catch (pt.tecnico.bicloin.hub.exceptions.ZooKeeperListRecordsFailedException e) {
			responseObserver.onError(INTERNAL.withDescription(HubResponseError.emptyPing()).asRuntimeException());
			return;
		}

		Hub.SysStatusResponse.Builder responseBuilder = Hub.SysStatusResponse.newBuilder();
		for (int i = 0; i < path.size(); i++)
			responseBuilder.addStatus(Hub.SysStatusResponse.Status.newBuilder().setPath(path.get(i)).setIsUp(isUp.get(i)).build());

		Hub.SysStatusResponse response = responseBuilder.build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	private void process(ZKNaming zkNaming, String serverPath, ArrayList<String> path, ArrayList<Boolean> isUp, boolean isHub) throws pt.tecnico.bicloin.hub.exceptions.ZooKeeperListRecordsFailedException {
		ManagedChannel channel;
		HubServiceGrpc.HubServiceBlockingStub hubStub;
		RecordServiceGrpc.RecordServiceBlockingStub recordStub;

		ArrayList<ZKRecord> records = null;
		try {
			records = new ArrayList<ZKRecord>(zkNaming.listRecords(serverPath));
		} catch (ZKNamingException e) {
			throw new pt.tecnico.bicloin.hub.exceptions.ZooKeeperListRecordsFailedException(zooHost, zooPort, serverPath);
		}

		String pingMessage = "hello";
		for (int i = 0; i < records.size(); i++) {
			String target = records.get(i).getURI();

			channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();

			String pingResponse;
			try {
				if (isHub) {
					hubStub = HubServiceGrpc.newBlockingStub(channel);
					pingResponse = hubStub.ping(Hub.PingRequest.newBuilder().setInput(pingMessage).build()).getOutput();
				} else {
					recordStub = RecordServiceGrpc.newBlockingStub(channel);
					pingResponse = recordStub.ping(Rec.PingRequest.newBuilder().setInput(pingMessage).build()).getOutput();
				}
			} catch (StatusRuntimeException e) {
				pingResponse = pingMessage + "ER";
			}

			path.add(records.get(i).getPath());
			try {
				isUp.add(pingResponse.equals(pingMessage));
			} catch (StatusRuntimeException e) {
				isUp.add(false);
			}
		}
	}

	public void balance(Hub.BalanceRequest request, StreamObserver<Hub.BalanceResponse> responseObserver) {
		String userId = request.getUserId();

		System.out.println("Received balance request: userId=\"" + userId + "\"");

		if (!isValidUserId(userId)) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(HubResponseError.invalidUserId()).asRuntimeException());
			return;
		}

		if (!isAnExistentUserId(userId)) {
			responseObserver.onError(NOT_FOUND.withDescription(HubResponseError.userIdNotFound()).asRuntimeException());
			return;
		}

		int userBalance;
		try {
			userBalance = hubManager.getUserIdBalance(userId);
		} catch (ServerStatusException e) {
			if (e.getStatus().getCode() == UNAVAILABLE.getCode()) {
				responseObserver.onError(UNAVAILABLE.withDescription(HubResponseError.unavailableService()).asRuntimeException());
			} else {
				responseObserver.onError(INTERNAL.withDescription(HubResponseError.internalError()).asRuntimeException());
			}
			return;
		}

		Hub.BalanceResponse response = Hub.BalanceResponse.newBuilder().setBalance(userBalance).build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	public void topUp(Hub.TopUpRequest request, StreamObserver<Hub.TopUpResponse> responseObserver) {
		String userId = request.getUserId();
		int amount = request.getAmount();
		String phoneNumber = request.getPhoneNumber();

		System.out.println("Received top_up request: userId=\"" + userId + "\", amount=\"" + amount + "\", phoneNumber=\"" + phoneNumber + "\"");

		if (!isValidUserId(userId)) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(HubResponseError.invalidUserId()).asRuntimeException());
			return;
		}

		if (!isAnExistentUserId(userId)) {
			responseObserver.onError(NOT_FOUND.withDescription(HubResponseError.userIdNotFound()).asRuntimeException());
			return;
		}

		if (!isValidPhoneNumber(phoneNumber, userId)) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(HubResponseError.invalidPhoneNumber()).asRuntimeException());
			return;
		}

		if (!isPhoneNumberOfUserId(phoneNumber, userId)) {
			responseObserver.onError(NOT_FOUND.withDescription(HubResponseError.invalidPhoneNumberOfUserId()).asRuntimeException());
			return;
		}

		if (amount < 0) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(HubResponseError.invalidTopUpAmount()).asRuntimeException());
			return;
		}

		int userBalance = 0;
		try {
			userBalance = hubManager.topUp(userId, amount);
		} catch (ServerStatusException e) {
			if (e.getStatus().getCode() == UNAVAILABLE.getCode()) {
				responseObserver.onError(UNAVAILABLE.withDescription(HubResponseError.unavailableService()).asRuntimeException());
			} else {
				responseObserver.onError(INTERNAL.withDescription(HubResponseError.internalError()).asRuntimeException());
			}
			return;
		}

		Hub.TopUpResponse response = Hub.TopUpResponse.newBuilder().setBalance(userBalance).build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	public void bikeUp(Hub.BikeUpRequest request, StreamObserver<Hub.BikeUpResponse> responseObserver) {
		String userId = request.getUserId();
		double latitude = request.getCoordinates().getLatitude();
		double longitude = request.getCoordinates().getLongitude();
		String stationId = request.getStationId();

		System.out.println("Received bike_up request: userId=\"" + userId + "\", coordinates=\"" + latitude + ", " + longitude + "\", stationId=\"" + stationId + "\"");

		if (!isValidUserId(userId)) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(HubResponseError.invalidUserId()).asRuntimeException());
			return;
		}

		if (!isAnExistentUserId(userId)) {
			responseObserver.onError(NOT_FOUND.withDescription(HubResponseError.userIdNotFound()).asRuntimeException());
			return;
		}

		if (!areValidCoordinates(latitude, longitude)) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(HubResponseError.invalidCoordinates()).asRuntimeException());
			return;
		}

		if (!isValidStationId(stationId)) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(HubResponseError.invalidStationId()).asRuntimeException());
			return;
		}

		if (!isAnExistentStationId(stationId)) {
			responseObserver.onError(NOT_FOUND.withDescription(HubResponseError.stationIdNotFound()).asRuntimeException());
			return;
		}

		String bikeUpReturn = null;
		try {
			synchronized (stationId.intern()) {
				bikeUpReturn = hubManager.bikeUp(userId, latitude, longitude, stationId);
			}
		} catch (ServerStatusException e) {
			if (e.getStatus().getCode() == UNAVAILABLE.getCode()) {
				responseObserver.onError(UNAVAILABLE.withDescription(HubResponseError.unavailableService()).asRuntimeException());
			} else {
				responseObserver.onError(INTERNAL.withDescription(HubResponseError.internalError()).asRuntimeException());
			}
			return;
		}
		if (!bikeUpReturn.equals("OK")) {
			responseObserver.onError(FAILED_PRECONDITION.withDescription(bikeUpReturn).asRuntimeException());
			return;
		}

		Hub.BikeUpResponse response = Hub.BikeUpResponse.getDefaultInstance();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	public void bikeDown(Hub.BikeDownRequest request, StreamObserver<Hub.BikeDownResponse> responseObserver) {
		String userId = request.getUserId();
		double latitude = request.getCoordinates().getLatitude();
		double longitude = request.getCoordinates().getLongitude();
		String stationId = request.getStationId();

		System.out.println("Received bike_down request: userId=\"" + userId + "\", coordinates=\"" + latitude + ", " + longitude + "\", stationId=\"" + stationId + "\"");

		if (!isValidUserId(userId)) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(HubResponseError.invalidUserId()).asRuntimeException());
			return;
		}

		if (!isAnExistentUserId(userId)) {
			responseObserver.onError(NOT_FOUND.withDescription(HubResponseError.userIdNotFound()).asRuntimeException());
			return;
		}

		if (!areValidCoordinates(latitude, longitude)) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(HubResponseError.invalidCoordinates()).asRuntimeException());
			return;
		}

		if (!isValidStationId(stationId)) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(HubResponseError.invalidStationId()).asRuntimeException());
			return;
		}

		if (!isAnExistentStationId(stationId)) {
			responseObserver.onError(NOT_FOUND.withDescription(HubResponseError.stationIdNotFound()).asRuntimeException());
			return;
		}

		String bikeDownReturn = null;
		try {
			synchronized (stationId.intern()) {
				bikeDownReturn = hubManager.bikeDown(userId, latitude, longitude, stationId);
			}
		} catch (ServerStatusException e) {
			if (e.getStatus().getCode() == UNAVAILABLE.getCode()) {
				responseObserver.onError(UNAVAILABLE.withDescription(HubResponseError.unavailableService()).asRuntimeException());
			} else {
				responseObserver.onError(INTERNAL.withDescription(HubResponseError.internalError()).asRuntimeException());
			}
			return;
		}

		if (!bikeDownReturn.equals("OK")) {
			responseObserver.onError(FAILED_PRECONDITION.withDescription(bikeDownReturn).asRuntimeException());
			return;
		}

		Hub.BikeDownResponse response = Hub.BikeDownResponse.getDefaultInstance();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	public void infoStation(Hub.InfoStationRequest request, StreamObserver<Hub.InfoStationResponse> responseObserver) {
		String stationId = request.getStationId();

		System.out.println("Received info_station request: stationId=\"" + stationId + "\"");

		if (!isValidStationId(stationId)) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(HubResponseError.invalidStationId()).asRuntimeException());
			return;
		}

		if (!isAnExistentStationId(stationId)) {
			responseObserver.onError(NOT_FOUND.withDescription(HubResponseError.stationIdNotFound()).asRuntimeException());
			return;
		}

		Hub.InfoStationResponse response = null;
		try {
			synchronized (stationId.intern()) {
				Station station = hubManager.getStation(stationId);

				response = Hub.InfoStationResponse.newBuilder().setName(station.getName())
						.setCoordinates(
								Hub.Coordinates.newBuilder().setLatitude(station.getCoordinates().getLatitude())
										.setLongitude(station.getCoordinates().getLongitude()).build()
						)
						.setPrize(station.getPrize())
						.setNumberDocks(station.getNumberDocks()).setPrize(station.getPrize())
						.setAvailableBikes(hubManager.getStationNumberBicycles(stationId))
						.setNumberWithdrawals(hubManager.getStationIdNumberWithdrawals(stationId))
						.setNumberReturns(hubManager.getStationIdNumberReturns(stationId)).build();
			}
		} catch (ServerStatusException e) {
			if (e.getStatus().getCode() == UNAVAILABLE.getCode()) {
				responseObserver.onError(UNAVAILABLE.withDescription(HubResponseError.unavailableService()).asRuntimeException());
			} else {
				responseObserver.onError(INTERNAL.withDescription(HubResponseError.internalError()).asRuntimeException());
			}
			return;
		}
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	public void locateStation(Hub.LocateStationRequest request, StreamObserver<Hub.LocateStationResponse> responseObserver) {
		double latitude = request.getCoordinates().getLatitude();
		double longitude = request.getCoordinates().getLongitude();
		int k = request.getNumberStations();

		System.out.println("Received locate_station request: coordinates=\"" + latitude + ", " + longitude + "\", numberStations=\"" + k + "\"");

		if (!areValidCoordinates(latitude, longitude)) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(HubResponseError.invalidCoordinates()).asRuntimeException());
			return;
		}

		if (k <= 0) {
			responseObserver.onError(INVALID_ARGUMENT.withDescription(HubResponseError.invalidNumber()).asRuntimeException());
			return;
		}

		ArrayList<String> stationIds = hubManager.locateStation(latitude, longitude, k);

		Hub.LocateStationResponse.Builder responseBuilder = Hub.LocateStationResponse.newBuilder();

		for (String stationId : stationIds)
			responseBuilder.addStationIds(stationId);

		responseObserver.onNext(responseBuilder.build());
		responseObserver.onCompleted();
	}

	/**
	 * Checks if string is not null, empty or contains only white spaces codepoints.
	 * @param string	string to check
	 * @return			false if string is null, empty or contains only white spaces codepoints, otherwise true
	 */
	private boolean isValidString(String string) {
		return string != null && !string.isBlank();
	}

	/**
	 * Checks if string is a valid userId.
	 * @param userId	string to check
	 * @return			true if string is a valid userId, otherwise false
	 */
	private boolean isValidUserId(String userId) {
		return isValidString(userId) && HubUtils.isAlphanumeric(userId)
				&& 3 <= userId.length() && userId.length() <= 10;
	}

	/**
	 * Checks if string is an existent userId.
	 * @param userId	string to check
	 * @return			true if string is an existent userId, otherwise false
	 */
	private boolean isAnExistentUserId(String userId) {
		return hubManager.hasUserId(userId);
	}

	/**
	 * Checks if string is a valid phoneNumber.
	 * @param userId	string to check
	 * @return			true if string is a valid phoneNumber, otherwise false
	 */
	private boolean isValidPhoneNumber(String phoneNumber, String userId) {
		return isValidString(phoneNumber) && isValidUserId(userId)
				&& HubUtils.isPhoneNumber(phoneNumber);
	}

	/**
	 * Checks if string is a phoneNumber of userId.
	 * @param userId	string to check
	 * @return			true if string is a phoneNumber of userId, otherwise false
	 */
	private boolean isPhoneNumberOfUserId(String phoneNumber, String userId) {
		return hubManager.getPhoneNumberOfUserId(userId).equals(phoneNumber);
	}

	/**
	 * Checks if string is a valid coordinate.
	 * @param latitude	latitude
	 * @param longitude	longitude
	 * @return			true if string a valid coordinate, otherwise false
	 */
	private boolean areValidCoordinates(Double latitude, Double longitude) {
		return (latitude >= -90 && latitude <= 90) && (longitude >= -180 && longitude <= 180);
	}

	/**
	 * Checks if string is a valid stationId.
	 * @param stationId	string to check
	 * @return			true if string a valid stationId, otherwise false
	 */
	private boolean isValidStationId(String stationId) {
		return isValidString(stationId) && HubUtils.isAlphanumeric(stationId)
				&& stationId.length() == 4;
	}

	/**
	 * Checks if string is a valid stationId.
	 * @param stationId	string to check
	 * @return			true if string an existent stationId, otherwise false
	 */
	private boolean isAnExistentStationId(String stationId) {
		return hubManager.hasStationId(stationId);
	}
}
