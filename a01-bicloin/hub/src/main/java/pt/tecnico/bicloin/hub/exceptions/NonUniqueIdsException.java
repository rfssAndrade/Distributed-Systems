package pt.tecnico.bicloin.hub.exceptions;

/**
 * Exception for non unique ids on the csv files.
 */
public class NonUniqueIdsException extends HubException {

    /**
     * Serial number for serialization.
     */
    private static final long serialVersionUID = 190940500575787648L;

    /**
     * Constructs a NonUniqueIdsException with a detailed message and cause.
     */
    public NonUniqueIdsException() {
        super(Message.nonUniqueIds());
    }
}
