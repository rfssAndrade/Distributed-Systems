package pt.tecnico.bicloin.hub;

public final class HubResponseError {

	/**
	 * Formats the response error message when the record server service is unavailable.
	 * @return	the response error message
	 */
	public static String unavailableService() {
		return "The service is currently unavailable";
	}

	/**
	 * Formats the response error message when an exception was thrown by the record server.
	 * @return	the response error message
	 */
	public static String internalError() {
		return "Some error occurred in one of the servers";
	}


	/**
	 * Formats the response error message to an empty ping.
	 * @return the response error message
	 */
	public static String emptyPing() {
		return "Input cannot be empty";
	}

	/**
	 * Formats the response error message to an invalid userId.
	 * @return	the response error message
	 */
	public static String invalidUserId() {
		return "User is invalid";
	}

	/**
	 * Formats the response error message to a not found userId.
	 * @return	the response error message
	 */
	public static String userIdNotFound() {
		return "User was not found";
	}

	/**
	 * Formats the response error message to an invalid phoneNumber .
	 * @return	the response error message
	 */
	public static String invalidPhoneNumber() {
		return "Phone number is invalid";
	}

	/**
	 * Formats the response error message to an invalid phoneNumber of userId.
	 * @return	the response error message
	 */
	public static String invalidPhoneNumberOfUserId() {
		return "Phone number is not of given user id";
	}

	/**
	 * Formats the response error message to a non positive integer top_up amount.
	 * @return	the response error message
	 */
	public static String invalidTopUpAmount() {
		return "Amount is invalid";
	}

	/**
	 * Formats the response error message to invalid coordinates.
	 * @return	the response error message
	 */
	public static String invalidCoordinates() {
		return "Coordinates are invalid";
	}

	/**
	 * Formats the response error message to an invalid stationId.
	 * @return	the response error message
	 */
	public static String invalidStationId() {
		return "Station is invalid";
	}

	/**
	 * Formats the response error message to a not found stationId.
	 * @return	the response error message
	 */
	public static String stationIdNotFound() {
		return "Station was not found";
	}

	/**
	 * Formats the response error message to a userId requesting another bicycle.
	 * @return	the response error message
	 */
	public static String userHasABicycle() {
		return "User can only have one bicycle at a time";
	}

	/**
	 * Formats the response error message to a userId is too far from the stationId.
	 * @return	the response error message
	 */
	public static String stationTooFar() {
		return "User is more than 200 meters away from the station";
	}

	/**
	 * Formats the response error message to a userId requesting a bicycle without enough balance.
	 * @return	the response error message
	 */
	public static String lowBalance() {
		return "User has not enough balance";
	}

	/**
	 * Formats the response error message to a userId requesting a bicycle in a stationId with no bicycles.
	 * @return	the response error message
	 */
	public static String stationHasNoBikes() {
		return "Station has no available bicycles";
	}

	/**
	 * Formats the response error message to a userId returning a bicycle without having one.
	 * @return	the response error message
	 */
	public static String userHasNoBike() {
		return "User has no bicycle";
	}

	/**
	 * Formats the response error message to a userId returning a bicycle to a station with no available dock.
	 * @return	the response error message
	 */
	public static String stationHasNoAvailableDock() {
		return "Station has no available dock";
	}

	public static String invalidNumber() {
		return "Invalid number of stations";
	}
}