package pt.tecnico.rec;

/**
 * Contains methods to format the server response error messages.
 * 
 *
 */
public final class RecordResponseError {
	
	/**
	 * Formats the response error message to an empty key.
	 * @return	the response error message
	 */
	public static String emptyKey() {
		return "Key cannot be empty";
	}
	
	/**
	 * Formats the response error message to an empty ping.
	 * @return	the response error message
	 */
	public static String emptyPing() {
		return "Input cannot be empty";
	}
	
	/**
	 * Formats the response error message to a null value.
	 * @return	the response error message
	 */
	public static String nullValue() {
		return "Value cannot be null";
	}
}
