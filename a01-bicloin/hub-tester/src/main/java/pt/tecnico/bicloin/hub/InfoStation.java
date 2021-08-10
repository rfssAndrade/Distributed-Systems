package pt.tecnico.bicloin.hub;

public class InfoStation {
    private final String name;
    private final double latitude;
    private final double longitude;
    private final int docks;
    private final int prize;
    private final int availableBikes;
    private final int withdrawals;
    private final int returns;

    public InfoStation(String name, double latitude, double longitude, int docks, int prize, int availableBikes, int withdrawals, int returns) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.docks = docks;
        this.prize = prize;
        this.availableBikes = availableBikes;
        this.withdrawals = withdrawals;
        this.returns = returns;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getDocks() {
        return docks;
    }

    public int getPrize() {
        return prize;
    }

    public int getAvailableBikes() {
        return availableBikes;
    }

    public int getWithdrawals() {
        return withdrawals;
    }

    public int getReturns() {
        return returns;
    }
}
