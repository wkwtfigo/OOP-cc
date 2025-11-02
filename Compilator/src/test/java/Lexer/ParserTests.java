package Lexer;


import org.example.lexer.Lexer;
import org.example.parser.Parser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
public class ParserTests {
  public static Stream<Path> testFiles  () throws IOException {
    Path dir = Path.of("src/main/java/org/example/tests/positive_tests");
    return Files.walk(dir).filter(Files::isRegularFile);
  }

  @ParameterizedTest
  @MethodSource("testFiles")
  public void test(Path p) throws IOException {
    try {

      String code = Files.readString(p);
      Lexer lexer = new Lexer(code);
      Parser parser = new Parser(lexer);
      boolean parseResult = parser.parse();
      Assertions.assertTrue(parseResult, "checked file: "+ p.getFileName());
    }catch (Exception e) {
      Assertions.fail((e.getMessage() + " file: "  + p.getFileName()));
    }
  }
}
