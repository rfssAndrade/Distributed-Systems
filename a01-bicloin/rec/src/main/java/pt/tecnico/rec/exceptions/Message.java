package pt.tecnico.rec.exceptions;

/**
 * Messages for all exceptions that inherit {@link RecordException}.
 * 
 *
 */
final class Message {
	
	/**
	 * Returns a string with a message for exception {@link ServerAlreadyStartedException}.
	 * @param port	the server port
	 * @return		the string with the message for exception {@link ServerAlreadyStartedException}
	 */
	static final String serverAlreadyStarted(String port) {
		return "Server is already running on port " + port;
	}
	
	/**
	 * Returns a string with a message for exception {@link ServerBindFailedException}.
	 * @param port	the server port
	 * @return		the string with the message for exception {@link ServerBindFailedException}
	 */
	static final String serverBindFailed(String port) {
		return "Could not bind server on port " + port;
	}
	
	/**
	 * Returns a string with a message for exception {@link ServerThreadInterruptedException}.
	 * @param port	the server port
	 * @return		the string with the message for exception {@link ServerThreadInterruptedException}
	 */
	static final String serverThreadInterrupted(String port) {
		return "Thread of server in port " + port + " was abruptly interrupted";
	}
	
	/**
	 * Returns a string with a message for exception {@link ZooKeeperBindFailedException}.
	 * @param zooHost	the ZooKeeper host
	 * @param zooPort	the ZooKeeper port
	 * @param host		the server host
	 * @param port		the server port
	 * @param path		the server path
	 * @return			the string with the message for exception {@link ZooKeeperBindFailedException}
	 */
	static final String zooKeeperBindFailed(String zooHost, String zooPort, String host, String port, String path) {
		return "Failed to bind the server with socket address " + host + ":" + port + " and path " + path + " to the ZooKeeper server " + zooHost + ":" + zooPort;
	}
	
	/**
	 * Returns a string with a message for exception {@link ZooKeeperUnbindFailedException}.
	 * @param zooHost	the ZooKeeper host
	 * @param zooPort	the ZooKeeper port
	 * @param host		the server host
	 * @param port		the server port
	 * @param path		the server path
	 * @return			the string with the message for exception {@link ZooKeeperUnbindFailedException}
	 */
	static final String zooKeeperUnbindFailed(String zooHost, String zooPort, String host, String port, String path) {
		return "Failed to unbind the server with socket address " + host + ":" + port + " and path " + path + " to the ZooKeeper server " + zooHost + ":" + zooPort;
	}
}
