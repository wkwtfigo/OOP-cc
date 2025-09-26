package org.example.lexer;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LexerTest {
    public static void main(String[] args) throws IOException {
        Path testsDir = Path.of("tests");
        if (!Files.exists(testsDir) || !Files.isDirectory(testsDir)) {
            System.out.println("Tests folder not found!");
            return;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(testsDir, "loop.txt")) {
            for (Path file : stream) {
                System.out.println("=== " + file.getFileName() + " ===");
                String code = Files.readString(file);

                Lexer lexer = new Lexer(code);
                Token token;

                int currentLine = 1;
                int firstColumn = 1;
                List<String> lineTokens = new ArrayList<>();

                do {
                    token = lexer.nextToken();

                    if (token.getLine() != currentLine) {
                        if (!lineTokens.isEmpty()) {
                            String indent = " ".repeat(firstColumn - 1);
                            System.out.println(indent + String.join(" ", lineTokens));
                            lineTokens.clear();
                        }
                        currentLine = token.getLine();
                        firstColumn = token.getColumn();
                    }

                    if (lineTokens.isEmpty()) {
                        firstColumn = token.getColumn();
                    }

                    lineTokens.add(token.getType().toString());

                } while (token.getType() != TokenType.TOK_EOF);

                if (!lineTokens.isEmpty()) {
                    System.out.println(String.join(" ", lineTokens));
                }

                System.out.println();
            }
        }
    }
}
