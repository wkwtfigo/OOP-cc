public class UnterminatedCommentException extends LexerException {
    public UnterminatedCommentException(Location location) {
        super("Unterminated comment", location);
    }
}