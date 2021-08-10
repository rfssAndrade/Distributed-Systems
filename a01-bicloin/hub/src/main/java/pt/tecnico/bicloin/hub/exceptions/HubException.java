package pt.tecnico.bicloin.hub.exceptions;

/**
 * HubException is the superclass of all exceptions from the Hub Server.
 */
public abstract class HubException extends Exception {

	/**
	 * Serial number for serialization.
	 */
	private static final long serialVersionUID = 4525190347495783102L;

	/**
	 * Constructs a RecordException with the specified detail message.
	 * @param message	the message
	 */
	public HubException(String message) {
		super(message);
	}
}
