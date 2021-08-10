package pt.tecnico.bicloin.app.exceptions;

public class BicloinException extends Exception {
    private final ErrorMessage errorMessage;

    public BicloinException(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage.message;
    }
}
