package pt.tecnico.bicloin.hub;

/**
 * Contains constants.
 * 
 *
 */
public final class HubConstants {
	
	/**
	 * Server path in the ZooKeeper server.
	 */
	public static final String SERVER_PATH = "/grpc/bicloin/hub";

	/**
	 * Server path of the record server in the ZooKeeper server.
	 */
	public static final String RECORD_SERVER_PATH = "/grpc/bicloin/rec";
	
	/**
	 * Key name of the user variables.
	 * 
	 *
	 */
	public enum UserVariableKey {
		/**
		 * Key name of the variable that stores the balance of a user.
		 */
		USER_BALANCE,

		/**
		 * Key name of the variable that tells if the user has a bicycle.
		 */
		USER_HAS_BICYCLE
	}
	
	/**
	 * Key name of the station variables.
	 * 
	 *
	 */
	public enum StationVariableKey {
		/**
	     * Key name of the variable that stores the number of bicycles in a station.
	     */
		STATION_NUMBER_BICYCLES,
		
		/**
	     * Key name of the variable that stores the number of withdrawals done in a station.
	     */
		STATION_NUMBER_WITHDRAWALS,
		
		/**
	     * Key name of the variable that stores the number of returns done in a station.
	     */
		STATION_NUMBER_RETURNS
	}
    
    /**
     * Mean radius of the Earth in meters.
     */
    public static final int EARTH_RADIUS = 6371000;

    public static final int BIKE_COST = 10;
}
