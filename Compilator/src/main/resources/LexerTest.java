package Lexer;

import org.example.lexer.Lexer;
import org.example.parser.Parser;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class LexerTest {

  static Stream<Path> testFiles() throws IOException {
    Path dir = Path.of("src/main/java/org/example/tests");
    return Files.walk(dir)
            .filter(Files::isRegularFile);
  }

  @ParameterizedTest
  @MethodSource("testFiles")
  public void testAllTestFiles(Path path) throws IOException {
  String code = Files.readString(path);
    Lexer lexer = new Lexer(code);
    Parser parser = new Parser(lexer);
    boolean parseResult = parser.parse();
  }
}
