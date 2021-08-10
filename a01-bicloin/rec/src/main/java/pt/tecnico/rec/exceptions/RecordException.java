package pt.tecnico.rec.exceptions;

/**
 * RecordException is the superclass of all exceptions from the Record Server.
 * 
 *
 */
public abstract class RecordException extends Exception {

	/**
	 * Serial number for serialization.
	 */
	private static final long serialVersionUID = 4525190347495783102L;

	/**
	 * Constructs a RecordException with the specified detail message.
	 * @param message	the message
	 */
	public RecordException(String message) {
		super(message);
	}
}
