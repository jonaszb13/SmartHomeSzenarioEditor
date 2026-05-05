package util.customExceptions;

public class MessageMissing extends RuntimeException {
    public MessageMissing(final String message) {
        super(message);
    }
}
