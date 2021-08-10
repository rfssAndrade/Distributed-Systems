package pt.tecnico.bicloin.hub;

public class Station {
    private final String name;
    private final String id;
    private final Coordinates coordinates;
    private final int numberDocks;
    private final int prize;

    public Station(String name, String id, String coordinateLatitude, String coordinateLongitude, String numberDocks, String prize) {
        this.name = name;
        this.id = id;
        this.coordinates = new Coordinates(coordinateLatitude + "," + coordinateLongitude);
        this.numberDocks = Integer.parseInt(numberDocks);
        this.prize = Integer.parseInt(prize);
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public int getNumberDocks() {
        return numberDocks;
    }

    public int getPrize() { return prize; }
}
