package pt.tecnico.rec;

import java.util.*;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import pt.tecnico.rec.exceptions.ServerStatusException;
import pt.tecnico.rec.exceptions.UnavailableServerInstanceException;
import pt.tecnico.rec.exceptions.ZooKeeperListRecordsFailedException;
import pt.tecnico.rec.grpc.Rec;
import pt.tecnico.rec.grpc.RecordServiceGrpc;
import pt.tecnico.rec.grpc.RecordServiceGrpc.RecordServiceBlockingStub;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import pt.ulisboa.tecnico.sdis.zk.ZKRecord;

/**
 * API to connect with the records server.
 * 
 *
 */
public class RecordApi implements AutoCloseable {
	
	/**
	 * ZooKeeper host.
	 */
	private final ZKNaming zkNaming;
	
	/**
	 * Path of the record server.
	 */
	private final String path = "/grpc/bicloin/rec";

	private final int numberInstances;

	private final String clientId;

	private final Map<String, StubManager> recordInstances = new HashMap<>();

	private final Set<String> failedInstances = new HashSet<>();
	
	/**
	 * Initializes a connection with the server.
	 * @param zooHost	ZooKeeper host
	 * @param zooPort	ZooKeeper port
	 * @throws ZooKeeperListRecordsFailedException 
	 * @throws UnavailableServerInstanceException 
	 */
	public RecordApi(String zooHost, String zooPort, String clientId) throws ZooKeeperListRecordsFailedException {
		this.clientId = clientId;
		zkNaming = new ZKNaming(zooHost, zooPort);

		ArrayList<ZKRecord> records;
		try {
			records = new ArrayList<>(zkNaming.listRecords(path));
		} catch (ZKNamingException e) {
			throw new ZooKeeperListRecordsFailedException(zooHost, zooPort, path);
		}
		numberInstances = records.size();
		for (ZKRecord record : records) {
			System.out.println("> DEBUG: starting replica " + path);
			String path = record.getPath();
			recordInstances.put(path, new StubManager(path, record.getURI()));
		}
	}
	
	/**
	 * Sends a ping request to the server.
	 * @param input	string to be returned
	 * @return		message with input string
	 * @throws ServerStatusException
	 */
	public String ping(String input, String path) throws ServerStatusException {
		ZKRecord zkRecord;
		try {
			zkRecord = zkNaming.lookup(path);
		} catch (ZKNamingException e) {
			throw new ServerStatusException(Status.INTERNAL.asRuntimeException());
		}
		String target = zkRecord.getURI();

		ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
		RecordServiceBlockingStub stub = RecordServiceGrpc.newBlockingStub(channel);

		Rec.PingRequest.newBuilder().setInput(input).build();

		String output;
		try {
			output = stub.withDeadlineAfter(2000, TimeUnit.MILLISECONDS).ping(Rec.PingRequest.newBuilder().setInput(input).build()).getOutput();
		} catch (StatusRuntimeException e) {
			throw new ServerStatusException(e);
		}

		channel.shutdownNow();
		return output;
	}
	
	/**
	 * Sends a read request to the server to obtain a value.
	 * @param key	the key whose associated value is to be returned 
	 * @return		the value to which the specified key is mapped
	 * @throws ServerStatusException
	 */
	public String read(String key) throws ServerStatusException {
		System.out.println("> DEBUG: received read request");
		if (key == null || key.isBlank())
			throw new ServerStatusException(Status.INVALID_ARGUMENT.asRuntimeException());

		DataCollector data = getData(key);

		return data.getValue();
	}

	/**
	 * Sends a write request to the server to store a value.
	 * @param key	key with which the specified value is to be associated
	 * @param value	value to be associated with the specified key
	 * @throws ServerStatusException
	 */
	public void write(String key, String value) throws ServerStatusException {
		System.out.println("> DEBUG: received write request");
		if (key == null || key.isBlank() || value == null)
			throw new ServerStatusException(Status.INVALID_ARGUMENT.asRuntimeException());

		DataCollector data = getData(key);

		updateInstances();

		WriteCollector collector = new WriteCollector(numberInstances, failedInstances);

		synchronized (collector) {
			for (StubManager manager : recordInstances.values()) {
				if (manager.isActive()) {
					System.out.println("> DEBUG: sending write request to replica " + manager.getPath());
					manager.write(collector, key, value, data.getSequence() + 1, clientId);
				}
				else {
					System.out.println("> DEBUG: not sending write request to replica " + manager.getPath() + " because it is down");
					collector.onErrorUpdate(manager.getPath());
				}
			}

			try {
				collector.wait(0);
				System.out.println("> DEBUG: write request completed");
				if (collector.wasSuccessful() == false)
					throw new ServerStatusException(Status.INTERNAL.asRuntimeException());
			} catch (InterruptedException e) {
				throw new ServerStatusException(Status.INTERNAL.asRuntimeException());
			}
		}
	}

	private DataCollector getData(String key) throws ServerStatusException {
		ReadCollector collector = new ReadCollector(numberInstances, failedInstances);

		updateInstances();

		synchronized (collector) {
			for (StubManager manager : recordInstances.values()) {
				if (manager.isActive()) {
					System.out.println("> DEBUG: sending read request to replica " + manager.getPath());
					manager.read(collector, key);
				}
				else {
					System.out.println("> DEBUG: not sending read request to replica " + manager.getPath() + " because it is down");
					collector.onErrorUpdate(manager.getPath());
				}
			}

			try {
				collector.wait(0);
				System.out.println("> DEBUG: read request completed");
				if (collector.wasSuccessful() == false)
					throw new ServerStatusException(Status.INTERNAL.asRuntimeException());
				return new DataCollector(collector);
			} catch (InterruptedException e) {
				throw new ServerStatusException(Status.INTERNAL.asRuntimeException());
			}
		}
	}

	private void updateInstances() throws ServerStatusException {
		synchronized (failedInstances) {
			if (failedInstances.isEmpty())
				return;

			Collection<ZKRecord> records;
			try {
				records = zkNaming.listRecords("/grpc/bicloin/rec");
			} catch (ZKNamingException e) {
				throw new ServerStatusException(Status.INTERNAL.asRuntimeException());
			}

			for (ZKRecord record : records) {
				String path = record.getPath();
				if (failedInstances.contains(path)) {
					System.out.println("> DEBUG: trying to recover replica " + path);
					recordInstances.get(path).close();
					System.out.println("> DEBUG: recovered replica " + path + " with success");
					recordInstances.put(path, new StubManager(path, record.getURI()));
					failedInstances.remove(path);
				}
			}
		}
	}

	@Override
	public void close() {
		for (StubManager manager : recordInstances.values()) {
			manager.close();
		}
	}
}
