package pt.tecnico.rec.exceptions;

/**
 * Exception for failed unbind from the ZooKeeper server.
 * 
 *
 */
public class ZooKeeperUnbindFailedException extends RecordException {

	/**
	 * Serial number for serialization.
	 */
	private static final long serialVersionUID = 7032943367642018797L;
	
	/**
	 * Constructs a ZooKeeperUnbindFailedException with a detailed message and cause.
	 * @param zooHost	the ZooKeeper host
	 * @param zooPort	the ZooKeeper port
	 * @param host		the server host
	 * @param port		the server port
	 * @param path		the path of the record server
	 */
	public ZooKeeperUnbindFailedException(String zooHost, String zooPort, String host, String port, String path) {
		super(Message.zooKeeperUnbindFailed(zooHost, zooPort, host, port, path));
	}
}
