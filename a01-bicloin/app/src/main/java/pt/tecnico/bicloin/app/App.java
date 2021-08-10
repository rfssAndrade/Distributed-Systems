package pt.tecnico.bicloin.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import pt.tecnico.bicloin.app.exceptions.BicloinException;
import pt.tecnico.bicloin.app.exceptions.ErrorMessage;
import pt.tecnico.bicloin.hub.HubApi;
import pt.tecnico.bicloin.hub.InfoStation;
import pt.tecnico.bicloin.hub.SysStatus;
import pt.tecnico.bicloin.hub.exceptions.ServerStatusException;
import pt.tecnico.bicloin.hub.exceptions.UnavailableServerInstanceException;
import pt.tecnico.bicloin.hub.exceptions.ZooKeeperListRecordsFailedException;

import static io.grpc.Status.UNAVAILABLE;

public class App {
    private final String username;
    private final String phoneno;
    private Coordinates coordinates;
    private HashMap<String, Coordinates> tags = new HashMap<>();
    private HubApi hubApi;

    public App(String username, String phoneno, Coordinates coordinates, String zooKeeperHost, String zooKeeperPort) throws ZooKeeperListRecordsFailedException, UnavailableServerInstanceException {
        this.username = username;
        this.phoneno = phoneno;
        this.coordinates = coordinates;
        this.hubApi = new HubApi(zooKeeperHost, zooKeeperPort);
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String[] tokens = scanner.nextLine().split(" ");

            try {
                switch (tokens[0]) {
                    case "balance":
                        doBalance(tokens);
                        break;
                    case "top-up":
                        doTopUp(tokens);
                        break;
                    case "tag":
                        doTag(tokens);
                        break;
                    case "at":
                        doAt(tokens);
                        break;
                    case "scan":
                        doScan(tokens);
                        break;
                    case "info":
                        doInfo(tokens);
                        break;
                    case "bike-up":
                        doBikeUp(tokens);
                        break;
                    case "bike-down":
                        doBikeDown(tokens);
                        break;
                    case "move":
                        doMove(tokens);
                        break;
                    case "zzz":
                        doZZZ(tokens);
                        break;
                    case "ping":
                        doPing(tokens);
                        break;
                    case "sys-status":
                        doSysStatus(tokens);
                        break;
                    case "exit":
                        doExit(tokens);
                        break;
                    case "help":
                        doHelp(tokens);
                        break;
                    default:
                        if (tokens[0].charAt(0) != '#')
                            System.out.println("ERRO comando nao reconhecido");
                        break;
                }
            } catch (BicloinException e) {
                System.out.println(e.getErrorMessage());
            } catch (ServerStatusException e) {
                System.out.println("ERRO " + e.getStatus().getDescription());
            } catch (InterruptedException e) {
                System.out.println("ERRO Something went wrong while sleeping...shutting down now :(");
                System.exit(0);
            }
        }
    }

    private void doBalance(String[] tokens) throws BicloinException, ServerStatusException {
        if (tokens.length != 1) throw new BicloinException(ErrorMessage.INVALID_INPUT);

        int balance = hubApi.balance(username);

        System.out.println(username + " " + balance + " BIC");
    }

    private void doTopUp(String[] tokens) throws BicloinException, ServerStatusException {
        if (tokens.length != 2) throw new BicloinException(ErrorMessage.INVALID_INPUT);
        if (!AppUtils.isCharge(tokens[1])) throw new BicloinException(ErrorMessage.INVALID_VALUE);

        int charge =  AppUtils.eurosToBicloins(Integer.parseInt(tokens[1]));
        hubApi.topUp(username, charge, phoneno);

        System.out.println(username + " " + charge + " BIC");
    }

    private void doTag(String[] tokens) throws BicloinException {
        if (tokens.length != 4) throw new BicloinException(ErrorMessage.INVALID_INPUT);
        if (!AppUtils.isLatitude(tokens[1]) || !AppUtils.isLongitude(tokens[2])) throw new BicloinException(ErrorMessage.INVALID_COORDINATES);

        Coordinates coordinates = new Coordinates(Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2]));
        tags.put(tokens[3], coordinates);

        System.out.println("OK");
    }

    private void doAt(String[] tokens) throws BicloinException {
        if (tokens.length != 1) throw new BicloinException(ErrorMessage.INVALID_INPUT);

        System.out.println(username + " em " + AppConstants.GOOGLE_MAPS_PLACE_URL + coordinates.getLatitude()
                + "," + coordinates.getLongitude());
    }

    private void doScan(String[] tokens) throws BicloinException, ServerStatusException {
        if (tokens.length != 2) throw new BicloinException(ErrorMessage.INVALID_INPUT);
        if (!AppUtils.isPositiveInteger(tokens[1])) throw new BicloinException(ErrorMessage.INVALID_VALUE);

        int nScan = Integer.parseInt(tokens[1]);
        ArrayList<String> stations = hubApi.locateStation(coordinates.getLatitude(), coordinates.getLongitude(), nScan);
        for (String station: stations) {
            InfoStation infoStation = hubApi.infoStation(station);
            System.out.println(station + ", lat " + infoStation.getLatitude() + ", " + infoStation.getLongitude()
                    + " long, " + infoStation.getDocks() + " docas, " + infoStation.getPrize() + " BIC prémio, "
                    + infoStation.getAvailableBikes() + " bicicletas, a "
                    + Math.round(AppUtils.getDistance(new Coordinates(infoStation.getLatitude(), infoStation.getLongitude()), coordinates))
                    + " metros");
        }
    }

    private void doInfo(String[] tokens) throws BicloinException, ServerStatusException {
        if (tokens.length != 2) throw new BicloinException(ErrorMessage.INVALID_INPUT);
        if (!AppUtils.isStation(tokens[1])) throw new BicloinException(ErrorMessage.INVALID_STATION);

        String station = tokens[1];

        InfoStation infoStation = hubApi.infoStation(station);

        System.out.println(infoStation.getName() + ", " + "lat " + infoStation.getLatitude() + ", "
                + infoStation.getLongitude() + " long, " + infoStation.getPrize() + " BIC prémio, " +
                infoStation.getAvailableBikes() + " bicicletas, " + infoStation.getWithdrawals() + " levantamentos, "
                + infoStation.getReturns() + " devolvuções, " + AppConstants.GOOGLE_MAPS_PLACE_URL
                + infoStation.getLatitude() + "," + infoStation.getLongitude());
    }

    private void doBikeUp(String[] tokens) throws BicloinException, ServerStatusException {
        if (tokens.length != 2) throw new BicloinException(ErrorMessage.INVALID_INPUT);
        if (!AppUtils.isStation(tokens[1])) throw new BicloinException(ErrorMessage.INVALID_STATION);

        String station = tokens[1];

        hubApi.bikeUp(username, coordinates.getLatitude(), coordinates.getLongitude(), station);

        System.out.println("OK");
    }

    private void doBikeDown(String[] tokens) throws BicloinException, ServerStatusException {
        if (tokens.length != 2) throw new BicloinException(ErrorMessage.INVALID_INPUT);
        if (!AppUtils.isStation(tokens[1])) throw new BicloinException(ErrorMessage.INVALID_STATION);

        String station = tokens[1];

        hubApi.bikeDown(username, coordinates.getLatitude(), coordinates.getLongitude(), station);

        System.out.println("OK");
    }

    private void doMove(String[] tokens) throws BicloinException {
        if (tokens.length == 2) doMoveTag(tokens[1]);
        else if (tokens.length == 3) doMoveCoordinates(tokens);
        else throw new BicloinException(ErrorMessage.INVALID_INPUT);
    }

    private void doMoveTag(String token) throws BicloinException {
        if (!tags.containsKey(token)) throw new BicloinException(ErrorMessage.INVALID_TAG);

        Coordinates tag = tags.get(token);
        coordinates.setLatitude(tag.getLatitude());
        coordinates.setLongitude(tag.getLongitude());

        System.out.println(username + " em " + AppConstants.GOOGLE_MAPS_PLACE_URL + coordinates.getLatitude()
                + "," + coordinates.getLongitude());
    }

    private void doMoveCoordinates(String[] tokens) throws BicloinException {
        if (!AppUtils.isLatitude(tokens[1]) || !AppUtils.isLongitude(tokens[2])) throw new BicloinException(ErrorMessage.INVALID_COORDINATES);

        coordinates.setLatitude(Double.parseDouble(tokens[1]));
        coordinates.setLongitude(Double.parseDouble(tokens[2]));

        System.out.println(username + " em " + AppConstants.GOOGLE_MAPS_PLACE_URL + coordinates.getLatitude()
                + "," + coordinates.getLongitude());
    }

    private void doZZZ(String[] tokens) throws BicloinException, InterruptedException {
        if (tokens.length != 2) throw new BicloinException(ErrorMessage.INVALID_INPUT);
        if (!AppUtils.isPositiveInteger(tokens[1])) throw new BicloinException(ErrorMessage.INVALID_INPUT);

        TimeUnit.MILLISECONDS.sleep(Integer.parseInt(tokens[1]));
    }

    private void doPing(String[] tokens) throws BicloinException, ServerStatusException {
        if (tokens.length != 2 || tokens[1].isBlank()) throw new BicloinException(ErrorMessage.INVALID_INPUT);

        String response = hubApi.ping(tokens[1]);

        System.out.println("Hub answered: " + response);
    }

    private void doSysStatus(String[] tokens) throws BicloinException, ServerStatusException {
        if (tokens.length != 1) throw new BicloinException(ErrorMessage.INVALID_INPUT);

        ArrayList<SysStatus> systems = hubApi.sysStatus();

        for (SysStatus system: systems) System.out.println("Path: " + system.getPath() + " Is on: " + system.isUp());
    }

    private void doExit(String[] tokens) throws BicloinException {
        if (tokens.length != 1) throw new BicloinException(ErrorMessage.INVALID_INPUT);

        System.out.println("Bye " + username);
        System.exit(0);
    }

    private void doHelp(String[] tokens) throws BicloinException {
        if (tokens.length != 1) throw new BicloinException(ErrorMessage.INVALID_INPUT);

        System.out.println("#######################################################################");
        System.out.println("# Comandos disponiveis:                                               #");
        System.out.println("# balance ---------------------------- check user balance             #");
        System.out.println("# top-up <number> -------------------- charge user account            #");
        System.out.println("# bike-up <station> ------------------ pick-up bike                   #");
        System.out.println("# bike-down <station> ---------------- return bike                    #");
        System.out.println("# info <station> --------------------- get all info from station      #");
        System.out.println("# scan <number> ---------------------- locate n closest stations      #");
        System.out.println("# tag <latitude> <longitude> <name> -- save local as tag              #");
        System.out.println("# move <tag> ------------------------- move user to tag location      #");
        System.out.println("# move <latitude> <longitude> -------- move user to coordinates given #");
        System.out.println("# at --------------------------------- show current location          #");
        System.out.println("# ping <word> ------------------------ check if hub is responding     #");
        System.out.println("# sys-status ------------------------- check status of all systems    #");
        System.out.println("# exit ------------------------------- exit the application           #");
        System.out.println("#######################################################################");

    }
}