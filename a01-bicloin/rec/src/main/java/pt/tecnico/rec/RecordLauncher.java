package pt.tecnico.rec;

import java.io.IOException;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import pt.tecnico.rec.exceptions.ServerAlreadyStartedException;
import pt.tecnico.rec.exceptions.ServerBindFailedException;
import pt.tecnico.rec.exceptions.ServerThreadInterruptedException;
import pt.tecnico.rec.exceptions.ZooKeeperBindFailedException;
import pt.tecnico.rec.exceptions.ZooKeeperUnbindFailedException;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

/**
 * Launches the server.
 * 
 *
 */
public class RecordLauncher {
	
	/**
	 * ZooKeeper host
	 */
	private final String zooHost;
	
	/**
	 * ZooKeeper port
	 */
	private final String zooPort;
	
	/**
	 * host to listen on
	 */
	private final String host;
	
	/**
	 * port to listen on
	 */
	private final String port;
	
	/**
	 * path to save in the ZooKeeper
	 */
	private final String path;

	/**
	 * To unbind the server from ZooKeeper in case of a user interrupt, such as typing ^C.
	 */
	private Thread shutdownThread;
	
	/**
	 * Constructor.
	 * @param zooHost	ZooKeeper host
	 * @param zooPort	ZooKeeper port
	 * @param host		host to listen on
	 * @param port		port to listen on
	 * @param instance	server instance
	 */
	public RecordLauncher(String zooHost, String zooPort, String host, String port, String instance) {
		this.zooHost = zooHost;
		this.zooPort = zooPort;
		this.host = host;
		this.port = port;
		this.path = RecordConstants.SERVER_PATH + "/" + instance;
	}
	
	/**
	 * launches the server.
	 * @throws ZooKeeperBindFailedException
	 * @throws ZooKeeperUnbindFailedException
	 * @throws ServerBindFailedException
	 * @throws ServerAlreadyStartedException
	 * @throws ServerThreadInterruptedException
	 */
	public void launch() throws ZooKeeperBindFailedException, ZooKeeperUnbindFailedException, ServerBindFailedException, ServerAlreadyStartedException, ServerThreadInterruptedException {
		registerZooKeeper();
	}

	/**
	 * Registers the server in the ZooKeeper and runs it.
	 * @throws ZooKeeperBindFailedException 
	 * @throws ZooKeeperUnbindFailedException 
	 * @throws ServerThreadInterruptedException 
	 * @throws ServerAlreadyStartedException 
	 * @throws ServerBindFailedException 
	 */
	private void registerZooKeeper() throws ZooKeeperBindFailedException, ZooKeeperUnbindFailedException, ServerBindFailedException, ServerAlreadyStartedException, ServerThreadInterruptedException {
		ZKNaming zkNaming = null;
		try {
			zkNaming = new ZKNaming(zooHost, zooPort);
			
			try {
				zkNaming.rebind(path, host, port);
			} catch (ZKNamingException e) {
				throw new ZooKeeperBindFailedException(zooHost, zooPort, host, port, path);
			}
			
			System.out.println("Server binded to ZooKeeper");
			
			Runtime.getRuntime().addShutdownHook(getShutdownThread(zkNaming));
			
			runServer();
		} finally {
			if (zkNaming != null) {
				try {
					zkNaming.unbind(path, host, port);
				} catch (ZKNamingException e) {
					throw new ZooKeeperUnbindFailedException(zooHost, zooPort, host, port, path);
				}
				
				System.out.println("Server unbinded from ZooKeeper");
				
				Runtime.getRuntime().removeShutdownHook(shutdownThread);
			}
		}
	}
	
	/**
	 * Runs the server.
	 * @throws ServerBindFailedException
	 * @throws ServerAlreadyStartedException 
	 * @throws ServerThreadInterruptedException 
	 */
	private void runServer() throws ServerBindFailedException, ServerAlreadyStartedException, ServerThreadInterruptedException {
		final BindableService impl = new RecordServerImpl();
		
		Server server = ServerBuilder.forPort(Integer.parseInt(port)).addService(impl).build();

		try {
			server.start();
		} catch(IllegalStateException e) {
			throw new ServerAlreadyStartedException(port);
		} catch (IOException e) {
			throw new ServerBindFailedException(port);
		}

		System.out.println("Server started");

		try {
			server.awaitTermination();
		} catch (InterruptedException e) {
			throw new ServerThreadInterruptedException(port);
		}
	}
	
	/**
	 * Creates a thread to unbind the server from ZooKeeper in case of a user interrupt.
	 * @param zkNaming	Manager Api of the ZooKeeper server
	 * @return			the thread
	 */
	private Thread getShutdownThread(ZKNaming zkNaming) {
		return shutdownThread = new Thread(() -> {
			if (zkNaming != null) {
				try {
					zkNaming.unbind(path, host, port);
					System.out.println("Shutdown Thread: unbinded server from ZooKeeper");
				} catch (ZKNamingException e) {
					System.err.println(new ZooKeeperUnbindFailedException(zooHost, zooPort, host, port, path).getMessage());
				}
			}
		});
	}
}
