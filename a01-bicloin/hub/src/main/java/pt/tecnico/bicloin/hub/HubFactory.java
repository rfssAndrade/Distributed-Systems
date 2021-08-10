package pt.tecnico.bicloin.hub;

import pt.tecnico.bicloin.hub.exceptions.InvalidDataException;
import pt.tecnico.bicloin.hub.exceptions.NonUniqueIdsException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class HubFactory {
	
	private final String usersFile;
	private final String stationsFile;
	private final List<String[]> usersData = new ArrayList<String[]>();
	private final List<String[]> stationsData = new ArrayList<String[]>();
	
	public HubFactory(String usersFile, String stationsFile) {
		this.usersFile = usersFile;
		this.stationsFile = stationsFile;
	}
	
	public void compile() throws NonUniqueIdsException, IOException, InvalidDataException {
		parseUsersData(usersFile);
		parseStationsData(stationsFile);
		if (!areIdsUnique()) {
			throw new NonUniqueIdsException();
		}
	}
	
	public List<String[]> getUsersData() {
		return usersData;
	}
	
	public List<String[]> getStationsData() {
		return stationsData;
	}
	
	private void parseUsersData(String usersFile) throws IOException, InvalidDataException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(usersFile));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.isBlank())
					continue;
				String[] fields = line.split(",");
				registerUserFromFields(fields);
			}
		} catch (IOException e) {
			throw new IOException("Failed to retrive user data from csv file.");
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new IOException("Failed to close csv user data file.");
				}
			}
		}
	}
	
	private void registerUserFromFields(String[] fields) throws InvalidDataException {
		if (fields.length != 3) {
			throw new InvalidDataException("missing user info");
		}
		
		// ID
		if (fields[0].length() < 3 || fields[0].length() > 10 || !HubUtils.isAlphanumeric(fields[0])) {
			throw new InvalidDataException("user id does not meet the requirements");
		}

		// name
		if (fields[1].length() > 30) {
			throw new InvalidDataException("user name does not meet the requirements");
		}

		// phone number
		if (!HubUtils.isPhoneNumber(fields[2])) {
			throw new InvalidDataException("user phone number does not meed the requirements");
		}

		usersData.add(fields.clone());
	}
	
	private void parseStationsData(String stationsFile) throws IOException, InvalidDataException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(stationsFile));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.isBlank())
					continue;
				String[] fields = line.split(",");
				registerStationFromFields(fields);
			}
		} catch (IOException e) {
			throw new IOException("Failed to retrive station data from csv file.");
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new IOException("Failed to close csv station data file.");
				}
			}
		}
	}
	
	private void registerStationFromFields(String[] fields) throws InvalidDataException {
		if (fields.length != 7) {
			throw new InvalidDataException("missing station info");
		}

		// name
		// nothing to check
		
		// ID
		if (fields[1].length() != 4 || !HubUtils.isAlphanumeric(fields[1])) {
			throw new InvalidDataException("station id does not meet the requirements");
		}

		// latitude
		if (!HubUtils.isCoordinate(fields[2])) {
			throw new InvalidDataException("station coordinates does not meet the requirements");
		}

		double latitude = Double.parseDouble(fields[2]);
		if (latitude < -90 || latitude > 90) {
			throw new InvalidDataException("station coordinates does not meet the requirements");
	}
		
		// longitude
		if (!HubUtils.isCoordinate(fields[3])) {
			throw new InvalidDataException("station coordinates does not meet the requirements");
		}
		double longitude = Double.parseDouble(fields[3]);
		if (longitude < -180 || longitude > 180) {
			throw new InvalidDataException("station coordinates does not meet the requirements");
		}

		// number of docks
		if (!HubUtils.isInteger(fields[4])) {
			throw new InvalidDataException("station number of docks does not meet the requirements");
		}
		int numberDocks = Integer.parseInt(fields[4]);
		if (numberDocks < 0) {
			throw new InvalidDataException("station number of docks does not meet the requirements");
		}
		// number of bicycles
		if (!HubUtils.isInteger(fields[5])) {
			throw new InvalidDataException("station number of bicycles does not meet the requirements");
		}
		int numberBicycles = Integer.parseInt(fields[5]);
		if (numberBicycles < 0 || numberBicycles > numberDocks) {
			throw new InvalidDataException("station number of bicycles does not meet the requirements");
		}

		// prize
		if (!HubUtils.isInteger(fields[6])) {
			throw new InvalidDataException("station prize does not meet the requirements");
		}
		int prize = Integer.parseInt(fields[6]);
		if (prize <= 0) {
			throw new InvalidDataException("station prize does not meet the requirements");
		}

		stationsData.add(fields.clone());
	}
	
	private boolean areIdsUnique() {
		HashSet<String> set = new HashSet<String>();
		for(String[] user : usersData) {
			if(set.contains(user[0]))
				return false;
			else
				set.add(user[0]);
		}
		
		set.clear();
		for(String[] station : stationsData) {
			if(set.contains(station[1]))
				return false;
			else
				set.add(station[1]);
		}
		
		return true;
	}
}
