package pt.tecnico.bicloin.hub.exceptions;

/**
 * Exception for invalid user data on the csv file.
 */
public class InvalidDataException extends HubException {

    /**
     * Serial number for serialization.
     */
    private static final long serialVersionUID = 190940500575787648L;

    /**
     * Constructs a InvalidUserDataException with a detailed message and cause.
     */
    public InvalidDataException(String cause) {
        super(Message.invalidData(cause));
    }
}
