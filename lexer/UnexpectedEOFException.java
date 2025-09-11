public class UnexpectedEOFException extends LexerException {
    public UnexpectedEOFException(Location location) {
        super("Unexpected end of file", location);
    }
}