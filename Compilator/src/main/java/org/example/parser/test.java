package org.example.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.example.lexer.Lexer;

public class test {
    public static void main(String[] args) throws IOException {
      String code = Files.readString(Path.of("Compilator/src/main/java/org/example/tests/condition.txt"));
      System.out.println("code = " + code);
      Lexer lexer = new Lexer(code);
      Parser parser = new Parser(lexer);
      var res = parser.parse();
      System.out.println(res);
    }
}
