package pt.tecnico.bicloin.hub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import pt.tecnico.rec.RecordApi;
import pt.tecnico.rec.exceptions.ServerStatusException;
import pt.tecnico.rec.exceptions.UnavailableServerInstanceException;
import pt.tecnico.rec.exceptions.ZooKeeperListRecordsFailedException;

/**
 * Manages the all data in the hub.
 */

public class HubManager {
	private final RecordApi recordApi;
	private final Map<String, User> users = new HashMap<>();
	private final Map<String, Station> stations = new HashMap<>();

	public HubManager(String zooHost, String zooPort, String path) throws UnavailableServerInstanceException, ZooKeeperListRecordsFailedException {
		this.recordApi = new RecordApi(zooHost, zooPort, path);
	}

	public void setUserIdHasBicycle(String userId, boolean userIdHasBicycle) throws ServerStatusException {
		recordApi.write(HubUtils.getRecordKey(userId, HubConstants.UserVariableKey.USER_HAS_BICYCLE), String.valueOf(userIdHasBicycle));
	}

	public boolean hasUserId(String userId) {
		return users.containsKey(userId);
	}

	public boolean hasStationId(String stationId) {
		return stations.containsKey(stationId);
	}

	public int getUserIdBalance(String userId) throws ServerStatusException {
		return checkInt(recordApi.read(HubUtils.getRecordKey(userId, HubConstants.UserVariableKey.USER_BALANCE)));
	}

	public void setUserIdBalance(String userId, int balanceBIC) throws ServerStatusException {
		recordApi.write(HubUtils.getRecordKey(userId, HubConstants.UserVariableKey.USER_BALANCE), String.valueOf(balanceBIC));
	}

	public int getStationNumberBicycles(String stationId) throws ServerStatusException {
		return checkInt(recordApi.read(HubUtils.getRecordKey(stationId, HubConstants.StationVariableKey.STATION_NUMBER_BICYCLES)));
	}

	public void setStationIdNumberBikes(String stationId, int numberBikes) throws ServerStatusException {
		recordApi.write(HubUtils.getRecordKey(stationId, HubConstants.StationVariableKey.STATION_NUMBER_BICYCLES), String.valueOf(numberBikes));
	}

	public String getPhoneNumberOfUserId(String userId) {
		return users.get(userId).getPhoneNumber();
	}

	public boolean userIdHasBike(String userId) throws ServerStatusException {
		return checkBoolean(recordApi.read(HubUtils.getRecordKey(userId, HubConstants.UserVariableKey.USER_HAS_BICYCLE)));
	}

	public int getStationIdNumberWithdrawals(String stationId) throws ServerStatusException {
		return checkInt(recordApi.read(HubUtils.getRecordKey(stationId, HubConstants.StationVariableKey.STATION_NUMBER_WITHDRAWALS)));
	}

	public void setStationIdNumberWithdrawals(String stationId, int numberWithdrawals) throws ServerStatusException {
		recordApi.write(HubUtils.getRecordKey(stationId, HubConstants.StationVariableKey.STATION_NUMBER_WITHDRAWALS), String.valueOf(numberWithdrawals));
	}

	public int getStationIdNumberReturns(String stationId) throws ServerStatusException {
		return checkInt(recordApi.read(HubUtils.getRecordKey(stationId, HubConstants.StationVariableKey.STATION_NUMBER_RETURNS)));
	}

	public void setStationIdNumberReturns(String stationId, int numberReturns) throws ServerStatusException {
		recordApi.write(HubUtils.getRecordKey(stationId, HubConstants.StationVariableKey.STATION_NUMBER_RETURNS), String.valueOf(numberReturns));

	}

	public void addUser(String[] userData, boolean initRec) throws ServerStatusException {
		User user = new User(userData[0], userData[1], userData[2]);
		users.put(userData[0], user);
		if (initRec) {
			setUserIdBalance(userData[0], 0);
			setUserIdHasBicycle(userData[0], false);
		}
	}

	public void addStation(String[] stationData, boolean initRec) throws ServerStatusException {
		Station station = new Station(stationData[0], stationData[1], stationData[2], stationData[3], stationData[4], stationData[6]);
		stations.put(stationData[1], station);
		if (initRec) {
			setStationIdNumberBikes(stationData[1], Integer.parseInt(stationData[5]));
			setStationIdNumberWithdrawals(stationData[1], 0);
			setStationIdNumberReturns(stationData[1], 0);
		}
	}

	public Station getStation(String stationId) {
		return stations.get(stationId);
	}

	public int topUp(String userId, int amountBIC) throws ServerStatusException {
		addUserIdBalance(userId, amountBIC);
		return getUserIdBalance(userId);
	}

	public int addUserIdBalance(String userId, int amountBIC) throws ServerStatusException {
		int balance = getUserIdBalance(userId) + amountBIC;
		setUserIdBalance(userId, balance);
		return balance;
	}

	public int removeUserIdBalance(String userId, int amountBIC) throws ServerStatusException {
		int balance = getUserIdBalance(userId) - amountBIC;
		setUserIdBalance(userId, balance);
		return balance;
	}

	public void withdrawStationIdBike(String stationId) throws ServerStatusException {
		setStationIdNumberBikes(stationId, getStationNumberBicycles(stationId) - 1);
		setStationIdNumberWithdrawals(stationId, getStationIdNumberWithdrawals(stationId) + 1);
	}

	public void returnStationIdBike(String stationId) throws ServerStatusException {
		setStationIdNumberBikes(stationId, getStationNumberBicycles(stationId) + 1);
		setStationIdNumberReturns(stationId, getStationIdNumberReturns(stationId) + 1);
	}

	public String bikeUp(String userId, double latitude, double longitude, String stationId) throws ServerStatusException {
		Coordinates coordinates = new Coordinates(latitude, longitude);
		if (userIdHasBike(userId)) {
			return HubResponseError.userHasABicycle();
		}

		if (HubUtils.getDistance(coordinates, stations.get(stationId).getCoordinates()) > 200) {
			return HubResponseError.stationTooFar();
		}

		if (getUserIdBalance(userId) < HubConstants.BIKE_COST) {
			return HubResponseError.lowBalance();
		}

		if (getStationNumberBicycles(stationId) == 0) {
			return HubResponseError.stationHasNoBikes();
		}

		removeUserIdBalance(userId, HubConstants.BIKE_COST);
		setUserIdHasBicycle(userId, true);
		withdrawStationIdBike(stationId);

		return "OK";
	}

	public String bikeDown(String userId, double latitude, double longitude, String stationId) throws ServerStatusException {
		Coordinates coordinates = new Coordinates(latitude, longitude);
		if (!userIdHasBike(userId)) {
			return HubResponseError.userHasNoBike();
		}

		if (HubUtils.getDistance(coordinates, stations.get(stationId).getCoordinates()) > 200) {
			return HubResponseError.stationTooFar();
		}

		if (getStationNumberBicycles(stationId) == stations.get(stationId).getNumberDocks()) {
			return HubResponseError.stationHasNoAvailableDock();
		}

		addUserIdBalance(userId, stations.get(stationId).getPrize());
		setUserIdHasBicycle(userId, false);
		returnStationIdBike(stationId);

		return "OK";
	}

	public ArrayList<String> locateStation(double latitude, double longitude, int k) {
		TreeMap<Double, String> distances = new TreeMap<>();
		ArrayList<String> stationIds = new ArrayList<>();
		Coordinates coordinates = new Coordinates(latitude, longitude);

		for (Map.Entry<String, Station> entry : stations.entrySet()) {
			String stationId = entry.getKey();
			Station station = entry.getValue();
			distances.put(HubUtils.getDistance(coordinates, station.getCoordinates()), stationId);
		}

		int nStations = Math.min(k, distances.size());

		for (int i = 0; i < nStations; i++)
			stationIds.add(distances.pollFirstEntry().getValue());

		return stationIds;
	}

	public int checkInt(String value) {
		if(value.isBlank())
			return 0;
		else
			return Integer.parseInt(value);
	}

	public boolean checkBoolean(String value) {
		if(value.isBlank())
			return false;
		else
			return Boolean.parseBoolean(value);
	}
}
