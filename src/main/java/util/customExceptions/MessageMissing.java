package util.customExceptions;

public class MessageMissing extends RuntimeException {
    public MessageMissing(String message) {
        super(message);
    }
}
