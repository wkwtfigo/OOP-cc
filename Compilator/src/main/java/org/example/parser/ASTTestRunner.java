package org.example.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.example.lexer.Lexer;

/**
 * Утилита для тестирования парсера и вывода AST
 */
public class ASTTestRunner {
    
    public static void testFile(String filePath) {
        try {
            // Читаем код из файла
            String code = Files.readString(Path.of(filePath));
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
            
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Использование: java ASTTestRunner <путь_к_файлу>");
            System.out.println("Примеры:");
            System.out.println("  java ASTTestRunner src/main/java/org/example/tests/fibonacci.txt");
            System.out.println("  java ASTTestRunner src/main/java/org/example/tests/class_test");
            return;
        }
        
        testFile(args[0]);
    }
}
