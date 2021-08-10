package pt.tecnico.rec;

import pt.tecnico.rec.exceptions.UnavailableServerInstanceException;
import pt.tecnico.rec.exceptions.ZooKeeperListRecordsFailedException;

public class RecordTester {
	
	private final static String zooKeeperHost = "localhost";
	private final static String zooKeeperPort = "2181";
	
	public static void main(String[] args) throws ZooKeeperListRecordsFailedException, UnavailableServerInstanceException {
		// receive and print arguments
		System.out.println("Received " + args.length + "arguments");
		for (int i = 0; i < args.length; i++) {
			System.out.println("arg[" + i + "] = " + args[i]);
		}
	}
}
