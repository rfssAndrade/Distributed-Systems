package pt.tecnico.rec.exceptions;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;

/**
 * Exception for any exception coming from the server
 *
 *
 */
public class ServerStatusException extends RecordApiException {

    /**
     * Serial number for serialization.
     */
    private static final long serialVersionUID = -3456789876545678987L;

    private final Status status;

    /**
     * Constructs a ServerStatusException with the specified detail message.
     * @param cause the cause
     */
    public ServerStatusException(StatusRuntimeException cause) {
        super(cause.getMessage());
        this.status = cause.getStatus();
    }

    /**
     * Returns the status code as a {@link Status} object.
     * @return  the status
     */
    public Status getStatus() {
        return status;
    }
}
