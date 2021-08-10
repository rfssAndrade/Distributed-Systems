package pt.tecnico.rec.exceptions;

/**
 * Exception for when the server tries to run after already started.
 * 
 *
 */
public class ServerAlreadyStartedException extends RecordException {
	
	/**
	 * Serial number for serialization.
	 */
	private static final long serialVersionUID = -2697118585768083488L;
	
	/**
	 * Constructs a ServerAlreadyStartedException with a detailed message and cause.
	 * @param port	the server port
	 */
	public ServerAlreadyStartedException(String port) {
		super(Message.serverAlreadyStarted(port));
	}
}
