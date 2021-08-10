package pt.tecnico.bicloin.hub.exceptions;

/**
 * Exception for an abruptly interrupted thread of the server.
 */
public class ServerThreadInterruptedException extends HubException {

	/**
	 * Serial number for serialization.
	 */
	private static final long serialVersionUID = 397270754071011255L;
	
	/**
	 * Constructs a ServerThreadInterruptedException with a detailed message and cause.
	 * @param port	the server port
	 */
	public ServerThreadInterruptedException(String port) {
		super(Message.serverThreadInterrupted(port));
	}
}
