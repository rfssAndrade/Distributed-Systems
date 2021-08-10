package pt.tecnico.bicloin.app;

import java.util.regex.Pattern;

/**
 * Contains utility classes.
 * 
 *
 */
public final class AppUtils {

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
	 * Returns the URL of a place in Google Maps, specified by its coordinates.
	 * @param coordinates	coordinates of the place
	 * @return				URL of the place in Google Maps
	 */
	public static String getGoogleMapsPlaceURL(Coordinates coordinates) {
		return AppConstants.GOOGLE_MAPS_PLACE_URL + coordinates;
	}
	
	/**
	 * Converts balance in euros to bicloins.
	 * @param euros	balance in euros
	 * @return		balance in bicloins
	 */
	public static int eurosToBicloins(int euros) {
		return euros * 10;
	}

	public static boolean isUser(String input) {
		return input.length() <= 30;
	}

	public static boolean isUsername (String input) {
		return isAlphanumeric(input) && input.length() <= 10 && input.length() >= 3;
	}

	public static boolean isStation(String input) {
		return isAlphanumeric(input) && input.length() == 4;
	}

	public static boolean isCharge(String input) {
		if (isInteger(input)) {
			int charge = Integer.parseInt(input);
			return charge >= 1 && charge <= 20;
		}
		return false;
	}

	public static boolean isLatitude(String input) {
		if (isCoordinate(input)) {
			double latitude = Double.parseDouble(input);
			return latitude >= -90 && latitude <= 90;
		}
		return false;
	}

	public static boolean isLongitude(String input) {
		if (isCoordinate(input)) {
			double longitude = Double.parseDouble(input);
			return longitude >= -180 && longitude <= 180;
		}
		return false;
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

		return 2 * AppConstants.EARTH_RADIUS
				* Math.asin(Math.sqrt(Math.pow(Math.sin(latitude2 - latitude1) / 2, 2)
				+ Math.cos(latitude1) * Math.cos(latitude2) * Math.pow(Math.sin(longitude2 - longitude1) / 2, 2)));
	}
}
