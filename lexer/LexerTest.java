import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class LexerTest {
    public static void main(String[] args) throws IOException {
        Path testsDir = Path.of("tests");
        if (!Files.exists(testsDir) || !Files.isDirectory(testsDir)) {
            System.out.println("Tests folder not found!");
            return;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(testsDir, "invalid_name.txt")) {
            for (Path file : stream) {
                System.out.println("=== " + file.getFileName() + " ===");
                String code = Files.readString(file);

                Lexer lexer = new Lexer(code);
                Token token;

                int currentLine = 1;
                List<String> lineTokens = new ArrayList<>();

                do {
                    token = lexer.nextToken();

                    if (token.getLine() != currentLine) {
                        if (!lineTokens.isEmpty()) {
                            System.out.println(String.join(" ", lineTokens));
                            lineTokens.clear();
                        }
                        currentLine = token.getLine();
                    }

                    lineTokens.add(token.getType().toString());

                } while (token.getType() != TokenType.EOF);

                if (!lineTokens.isEmpty()) {
                    System.out.println(String.join(" ", lineTokens));
                }

                System.out.println();
            }
        }
    }
}
