package org.example.parser;

import org.example.lexer.Lexer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class test {
    public static void main(String[] args) throws IOException {
      String code = Files.readString(Path.of("src/main/java/org/example/tests/condition.txt"));
      System.out.println("code = " + code);
      Lexer lexer = new Lexer(code);
      Parser parser = new Parser(lexer);
      var res = parser.parse();
      System.out.println(res);
    }
}
