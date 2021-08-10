package pt.tecnico.bicloin.hub;

import java.util.regex.Pattern;

/**
 * Contains utility classes.
 * 
 *
 */
public final class HubUtils {
	
    /**
	 * Pattern with expression to match strings as integers.
	 */
	private static final Pattern INTEGER_PATTERN = Pattern.compile("[-+]?[0-9]+");
	
	/**
	 * Pattern with expression to match strings as positive integers.
	 */
	private static final Pattern POSITIVE_INTEGER_PATTERN = Pattern.compile("\\+?[0-9]+");
	
	/**
	 * Pattern with expression to match strings as coordinates.
	 */
	private static final Pattern COORDINATE_PATTERN = Pattern.compile("[-+]?[0-9]+\\.?[0-9]*");
	
	/**
	 * Pattern with expression to match strings as phone numbers.
	 */
	private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("\\+[1-9][0-9]*");
	
	/**
	 * Pattern with expression to match strings as an alphanumeric string.
	 */
	private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("[a-zA-Z0-9]*");
	
	/**
	 * String added as a prefix to user keys. It must not be a prefix or equal to {@link #RECORD_KEY_STATION_PREFIX} to avoid collisions.
	 */
    private static final String RECORD_KEY_USER_PREFIX = "u";
	
	/**
	 * String added as a prefix to station keys. It must not be a prefix or equal to {@link #RECORD_KEY_USER_PREFIX} to avoid collisions.
	 */
    private static final String RECORD_KEY_STATION_PREFIX = "s";
	
	/**
	 * String inserted between the identifier and the variable name to form a key. It must contain at least one non-alphanumeric character to avoid collisions.
	 */
    private static final String RECORD_KEY_SEPARATOR = "_";
	
	/**
	 * Checks if a string can be parsed to an integer using {@link java.lang.Integer#parseInt(String)}.
	 * @param input	the string to be matched
	 * @return		true if the string can be parsed to an integer, otherwise false
	 */
	public static boolean isInteger(String input) {
		return matches(INTEGER_PATTERN, input);
	}
	
	/**
	 * Checks if a string can be parsed to a positive integer using {@link java.lang.Integer#parseInt(String)}.
	 * @param input	the string to be matched
	 * @return		true if the string can be parsed to a positive integer, otherwise false
	 */
	public static boolean isPositiveInteger(String input) {
		return matches(POSITIVE_INTEGER_PATTERN, input);
	}
	
	/**
	 * Checks if a string is a coordinate.
	 * @param input	the string to be matched
	 * @return		true if the string is a coordinate, otherwise false
	 */
	public static boolean isCoordinate(String input) {
		return matches(COORDINATE_PATTERN, input);
	}
	
	/**
	 * Checks if a string is a phone number.
	 * @param input	the string to be matched
	 * @return		true if the string is a phone number, otherwise false
	 */
	public static boolean isPhoneNumber(String input) {
		return matches(PHONE_NUMBER_PATTERN, input);
	}
	
	/**
	 * Checks if a string is only composed by alphanumeric characters.
	 * @param input	the string to be matched
	 * @return		true if the string is only composed by alphanumeric characters, otherwise false
	 */
	public static boolean isAlphanumeric(String input) {
		return matches(ALPHANUMERIC_PATTERN, input);
	}
	
	/**
	 * Attempts to match the given input against the pattern.
	 * @param pattern	pattern with the expression
	 * @param input		the character sequence to be matched
	 * @return			true if the input matches the expression of the pattern, otherwise false
	 */
	private static boolean matches(Pattern pattern, CharSequence input) {
		return pattern.matcher(input).matches();
	}
	
	/**
	 * Returns a key to map a variable of a user in the record server.
	 * @param userId		user identifier
	 * @param variableKey	variable key name
	 * @return				the key to map a variable of a user
	 */
	public static String getRecordKey(String userId, HubConstants.UserVariableKey variableKey) {
		return RECORD_KEY_USER_PREFIX + userId + RECORD_KEY_SEPARATOR + variableKey;
	}
	
	/**
	 * Returns a key to map a variable of a station in the record server.
	 * @param stationId		station identifier
	 * @param variableKey	variable key name
	 * @return				the key to map a variable of a station
	 */
	public static String getRecordKey(String stationId, HubConstants.StationVariableKey variableKey) {
		return RECORD_KEY_STATION_PREFIX + stationId + RECORD_KEY_SEPARATOR + variableKey;
	}
	
	/**
	 * Calculates the distance in meters between two coordinates given as decimal degrees using haversine formula.
	 * @param coordinates1	First coordinates
	 * @param coordinates2	second coordinates
	 * @return				distance between two coordinates
	 */
	public static double getDistance(Coordinates coordinates1, Coordinates coordinates2) {
		double latitude1 = Math.toRadians(coordinates1.getLatitude());
		double longitude1 = Math.toRadians(coordinates1.getLongitude());
		double latitude2 = Math.toRadians(coordinates2.getLatitude());
		double longitude2 = Math.toRadians(coordinates2.getLongitude());
		
		return 2 * HubConstants.EARTH_RADIUS
				* Math.asin(Math.sqrt(Math.pow(Math.sin(latitude2 - latitude1) / 2, 2)
				+ Math.cos(latitude1) * Math.cos(latitude2) * Math.pow(Math.sin(longitude2 - longitude1) / 2, 2)));
	}
}
