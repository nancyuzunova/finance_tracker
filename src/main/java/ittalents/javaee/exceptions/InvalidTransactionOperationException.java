package ittalents.javaee.exceptions;

public class InvalidTransactionOperationException extends RuntimeException {

    public InvalidTransactionOperationException(String message) {
        super(message);
    }
}
