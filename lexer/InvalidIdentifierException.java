public class InvalidIdentifierException extends LexerException {
    public InvalidIdentifierException(String message, Location location) {
        super(message, location);
    }
}
