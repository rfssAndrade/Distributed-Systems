package pt.tecnico.bicloin.app;

public class AppMain {
	
	public static void main(String[] args) {
		System.out.println(AppMain.class.getSimpleName());
		
		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		// check arguments
		if (args.length < 6) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s host port%n", AppMain.class.getName());
			return;
		}
		final String zooKeeperHost = args[0];
		final String zooKeeperport = args[1];

		if (!AppUtils.isUsername(args[2]) || !AppUtils.isPhoneNumber(args[3]) || !AppUtils.isLatitude(args[4]) || !AppUtils.isLongitude(args[5])) {
			System.out.println("ERRO argumentos invalidos");
			System.exit(0);
		}

		final String username = args[2];
		final String phoneno = args[3];
		final Coordinates coordinates = new Coordinates(Double.parseDouble(args[4]), Double.parseDouble(args[5]));

		try {
			final App app = new App(username, phoneno, coordinates, zooKeeperHost, zooKeeperport);
			app.run();
		} catch (Exception e) {
			System.out.println("ERRO " + e.getMessage());
		}
	}
}
