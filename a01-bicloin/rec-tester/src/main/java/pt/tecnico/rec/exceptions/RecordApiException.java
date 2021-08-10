package pt.tecnico.rec.exceptions;

/**
 * RecordApiException is the superclass of all exceptions from the RecordApi.
 * 
 * @see {@link pt.tecnico.rec.RecordApi}
 *
 */
public abstract class RecordApiException extends Exception {

	/**
	 * Serial number for serialization.
	 */
	private static final long serialVersionUID = 3309259323248169189L;

	/**
	 * Constructs a RecordApiException with a detailed message.
	 * @param message	the message
	 */
	public RecordApiException(String message) {
		super(message);
	}
}
