package pt.tecnico.bicloin.hub;

public class SysStatus {
    private final String path;
    private final boolean isUp;

    public SysStatus(String path, boolean isUp) {
        this.path = path;
        this.isUp = isUp;
    }

    public String getPath() {
        return path;
    }

    public boolean isUp() {
        return isUp;
    }
}
