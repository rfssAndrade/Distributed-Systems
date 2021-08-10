package pt.tecnico.rec.exceptions;

/**
 * Exception for failed bind to the ZooKeeper server.
 * 
 *
 */
public class ZooKeeperBindFailedException extends RecordException {

	/**
	 * Serial number for serialization.
	 */
	private static final long serialVersionUID = 4650922573954930561L;
	
	/**
	 * Constructs a ZooKeeperBindFailedException with a detailed message and cause.
	 * @param zooHost	the ZooKeeper host
	 * @param zooPort	the ZooKeeper port
	 * @param host		the server host
	 * @param port		the server port
	 * @param path		the path of the record server
	 */
	public ZooKeeperBindFailedException(String zooHost, String zooPort, String host, String port, String path) {
		super(Message.zooKeeperBindFailed(zooHost, zooPort, host, port, path));
	}

}
