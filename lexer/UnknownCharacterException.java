public class UnknownCharacterException extends LexerException {
    public UnknownCharacterException(char ch, Location location) {
        super("Unknown character '" + ch + "'", location);
    }
}