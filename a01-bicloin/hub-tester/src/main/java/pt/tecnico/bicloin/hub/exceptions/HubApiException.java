package pt.tecnico.bicloin.hub.exceptions;

/**
 * HubApiException is the superclass of all exceptions from the HubApi.
 * 
 * @see {@link pt.tecnico.bicloin.hub.HubApi}
 *
 */
public abstract class HubApiException extends Exception {

	/**
	 * Serial number for serialization.
	 */
	private static final long serialVersionUID = 3309259323248169189L;

	/**
	 * Constructs a HubApiException with a detailed message.
	 * @param message	the message
	 */
	public HubApiException(String message) {
		super(message);
	}
}
