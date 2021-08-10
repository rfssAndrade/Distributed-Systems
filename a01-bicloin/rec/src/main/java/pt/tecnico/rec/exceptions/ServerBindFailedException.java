package pt.tecnico.rec.exceptions;

/**
 * Exception for failed bind of the server.
 * 
 *
 */
public class ServerBindFailedException extends RecordException {

	/**
	 * Serial number for serialization.
	 */
	private static final long serialVersionUID = 190940500575787648L;
	
	/**
	 * Constructs a ServerBindFailedException with a detailed message and cause.
	 * @param port	the server port
	 */
	public ServerBindFailedException(String port) {
		super(Message.serverBindFailed(port));
	}
}
