package org.example.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.example.lexer.Lexer;

public class test {
    public static void main(String[] args) throws IOException {
        // Читаем код из файла
        String code = Files.readString(Path.of("Compilator/src/main/java/org/example/tests/change_final_field.txt"));
        System.out.println("=== Исходный код ===");
        System.out.println(code);
        System.out.println();
        
        // Создаем лексер и парсер
        Lexer lexer = new Lexer(code);
        Parser parser = new Parser(lexer);
        
        // Парсим код
        boolean parseResult = parser.parse();
        System.out.println("=== Результат парсинга ===");
        System.out.println("Успешно: " + parseResult);
        System.out.println("Ошибок: " + parser.getNumberOfErrors());
        System.out.println();
        
        // Если парсинг успешен, выводим AST
        if (parseResult) {
            System.out.println("=== AST ===");
            ProgramNode rootNode = parser.getRootNode();
            if (rootNode != null) {
                ASTPrinter printer = new ASTPrinter();
                rootNode.accept(printer);
                System.out.println(printer.getOutput());
            } else {
                System.out.println("Корневой узел AST не найден");
            }
        }
    }
}
