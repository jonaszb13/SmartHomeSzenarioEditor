package util.customexceptions;

public class MessageMissingException extends RuntimeException {
    public MessageMissingException(final String message) {
        super(message);
    }
}
