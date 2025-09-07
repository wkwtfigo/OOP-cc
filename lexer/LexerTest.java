import java.io.IOException;
import java.nio.file.*;

public class LexerTest {
    public static void main(String[] args) throws IOException {
        Path testsDir = Path.of("tests");
        if (!Files.exists(testsDir) || !Files.isDirectory(testsDir)) {
            System.out.println("Tests folder not found!");
            return;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(testsDir, "*.txt")) {
            for (Path file : stream) {
                System.out.println("=== " + file.getFileName() + " ===");
                String code = Files.readString(file);

                Lexer lexer = new Lexer(code);
                Token token;
                do {
                    token = lexer.nextToken();
                    System.out.println(token);
                } while (token.getType() != TokenType.EOF);

                System.out.println();
            }
        }
    }
}
