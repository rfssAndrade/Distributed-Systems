package pt.tecnico.bicloin.hub.exceptions;

/**
 * Messages for all exceptions that inherit {@link HubApiException}.
 * 
 *
 */
final class Message {
	
	/**
	 * Returns a string with a message for exception {@link UnavailableServerInstanceException}.
	 * @param zooHost	the ZooKeeper host
	 * @param zooPort	the ZooKeeper port
	 * @param path		the path of the record server
	 * @return			the string with the message for exception {@link UnavailableServerInstanceException}
	 */
	static final String unavailableServerInstance(String zooHost, String zooPort, String path) {
		return "There are no available instances of servers for path " + path + " in the ZooKeeper server " + zooHost + ":" + zooPort;
	}
	
	/**
	 * Returns a string with a message for exception {@link ZooKeeperListRecordsFailedException}.
	 * @param zooHost	the ZooKeeper host
	 * @param zooPort	the ZooKeeper port
	 * @param path		the path of the record server
	 * @return			the string with the message for exception {@link ZooKeeperListRecordsFailedException}
	 */
	static final String zooKeeperListRecordsFailed(String zooHost, String zooPort, String path) {
		return "Failed to list records for path " + path + " in the ZooKeeper server " + zooHost + ":" + zooPort;
	}
}
