package pt.tecnico.rec;

import java.util.regex.Pattern;

/**
 * Main class that runs the records server.
 * 
 *
 */
public class RecordMain {
	
	/**
	 * Main method that runs the records server.
	 * @param args	parameters: zooKeeperHost zooKeeperPort serverHost serverPort instanceNumber
	 */
	public static void main(String[] args) {
		System.out.println(RecordMain.class.getSimpleName());
		
		System.out.println("Received " + args.length + " arguments");
		
		if(args.length != 5) {
			throw new IllegalArgumentException("Needs 5 arguments, but got " + args.length);
		}
		
		System.out.println("1. ZooKeeper host:\t" + args[0]);
		System.out.println("2. ZooKeeper port:\t" + args[1]);
		System.out.println("3. Server host:\t\t" + args[2]);
		System.out.println("4. Server port:\t\t" + args[3]);
		System.out.println("5. Instance number:\t" + args[4]);

		if(!Pattern.compile("[1-9]").matcher(args[4]).matches())
			return;
		
		RecordLauncher launcher = new RecordLauncher(args[0], args[1], args[2], args[3], args[4]);
		try {
			launcher.launch();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
