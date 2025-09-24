import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class ParserTest {
    public static void main(String[] args) throws IOException {
        Path testsDir = Path.of("tests"); // папка с тестовыми файлами
        if (!Files.exists(testsDir) || !Files.isDirectory(testsDir)) {
            System.out.println("Tests folder not found!");
            return;
        }

        // Пройтись по всем файлам с расширением .oop
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(testsDir, "*.oop")) {
            for (Path file : stream) {
                System.out.println("=== " + file.getFileName() + " ===");
                String code = Files.readString(file);

                Lexer lexer = new Lexer(code);
                Parser parser = new Parser(lexer);

                try {
                    Object result = parser.parse(); // запуск парсера
                    System.out.println("Parsing finished successfully.");
                } catch (Exception e) {
                    System.err.println("Parsing failed: " + e.getMessage());
                    e.printStackTrace();
                }

                System.out.println();
            }
        }
    }
}
