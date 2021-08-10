package pt.tecnico.bicloin.app;

/**
 * Coordinates in decimal degrees.
 * 
 *
 */
public class Coordinates {

	private double latitude;
	private double longitude;
	
	/**
	 * Constructor with a String.
	 * @param coords	Coordinates stored in a String with format "%f,%f"
	 */
	public Coordinates(String coords) {
		setCoords(coords);
	}
	
	/**
	 * Constructor with a double array.
	 * @param coords	array with latitude and longitude in decimal degrees
	 */
	public Coordinates(double[] coords) {
		setCoords(coords);
	}
	
	/**
	 * Constructor with latitude and longitude.
	 * @param latitude	latitude value in decimal degrees
	 * @param longitude	longitude value in decimal degrees
	 */
	public Coordinates(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * Returns the latitude.
	 * @return	latitude in decimal degrees
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * Sets the latitude.
	 * @param latitude	latitude to set in decimal degrees
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * Returns the longitude.
	 * @return	longitude in decimal degrees
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * Sets the longitude.
	 * @param longitude	longitude to set in decimal degrees
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	/**
	 * Sets the latitude and longitude.
	 * @param coords	array with latitude and longitude in decimal degrees
	 */
	public void setCoords(double[] coords) {
		latitude = coords[0];
		longitude = coords[1];
	}
	
	/**
	 * Gets an array with latitude and longitude.
	 * @return	array with latitude and longitude in decimal degrees.
	 */
	public double[] getCoords() {
		return new double[] {latitude, longitude};
	}
	
	/**
	 * Sets the latitude and longitude.
	 * @param coords	coordinates stored in a String with format "%f,%f"
	 */
	public void setCoords(String coords) {
		String[] splitCoords = coords.split(",");
		this.latitude = Double.parseDouble(splitCoords[0]);
		this.longitude = Double.parseDouble(splitCoords[1]);
	}

	@Override
	public String toString() {
		return latitude + "," + longitude;
	}
}
