package pt.tecnico.rec.exceptions;

/**
 * Exception for no available instance of Record Server.
 * 
 *
 */
public class UnavailableServerInstanceException extends RecordApiException {

	/**
	 * Serial number for serialization.
	 */
	private static final long serialVersionUID = -5855160688598957459L;

	/**
	 * Constructs a UnavailableServerInstanceException with the specified detail message.
	 * @param zooHost	the ZooKeeper host
	 * @param zooPort	the ZooKeeper port
	 * @param path		the path of the record server
	 */
	public UnavailableServerInstanceException(String zooHost, String zooPort, String path) {
		super(Message.unavailableServerInstance(zooHost, zooPort, path));
	}
}
