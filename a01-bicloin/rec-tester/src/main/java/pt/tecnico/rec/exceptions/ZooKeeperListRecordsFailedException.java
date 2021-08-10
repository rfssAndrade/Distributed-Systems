package pt.tecnico.rec.exceptions;

/**
 * Exception for failed list of records on the ZooKeeper server.
 * 
 *
 */
public class ZooKeeperListRecordsFailedException extends RecordApiException {

	/**
	 * Serial number for serialization.
	 */
	private static final long serialVersionUID = -5742469271778124415L;

	/**
	 * Constructs a ZooKeeperLookupFailedException with a detailed message and cause.
	 * @param zooHost	the ZooKeeper host
	 * @param zooPort	the ZooKeeper port
	 * @param path		the path of the record server
	 */
	public ZooKeeperListRecordsFailedException(String zooHost, String zooPort, String path) {
		super(Message.zooKeeperListRecordsFailed(zooHost, zooPort, path));
	}
}
