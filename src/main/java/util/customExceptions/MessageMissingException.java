package util.customExceptions;

public class MessageMissingException extends RuntimeException {
    public MessageMissingException(final String message) {
        super(message);
    }
}
