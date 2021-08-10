package pt.tecnico.bicloin.hub;

import java.util.regex.Pattern;

/**
 * Main class that runs the hub server.
 * 
 *
 */
public class HubMain {
	
	/**
	 * Main method that runs the records server.
	 * @param args	parameters: zooKeeperHost zooKeeperPort serverHost serverPort instanceNumber usersFile stationsFile ["initRec"]
	 */
	public static void main(String[] args) {
		System.out.println(HubMain.class.getSimpleName());
		
		System.out.println("Received " + args.length + " arguments");
		
		if(args.length != 7 && args.length != 8) {
			throw new IllegalArgumentException("Needs 7 or 8 arguments, but got " + args.length);
		}
		
		System.out.println("1. ZooKeeper host:\t" + args[0]);
		System.out.println("2. ZooKeeper port:\t" + args[1]);
		System.out.println("3. Server host:\t\t" + args[2]);
		System.out.println("4. Server port:\t\t" + args[3]);
		System.out.println("5. Instance number:\t" + args[4]);
		System.out.println("6. Users data file:\t" + args[5]);
		System.out.println("7. Stations data file:\t" + args[6]);
		if(args.length == 8)
			System.out.println("8. Option:\t\t" + args[7]);

		if(!Pattern.compile("[1-9]").matcher(args[4]).matches() || (args.length == 8 && !args[7].equals("initRec")))
			return;
		
		HubLauncher launcher;
		if (args.length == 7)
			launcher = new HubLauncher(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
		else
			launcher = new HubLauncher(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
		try {
			launcher.launch();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
