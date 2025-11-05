package org.example.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.example.lexer.Lexer;

public class test {
    public static void main(String[] args) throws IOException {
        // Читаем код из файла
        String code = Files
                .readString(Path.of("Compilator/src/main/java/org/example/tests/negative_tests/unreachable_after_return.txt"));
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

        // Если парсинг успешен, выводим AST и выполняем семантическую проверку
        if (parseResult) {
            System.out.println("=== AST ===");
            ProgramNode rootNode = parser.getRootNode();
            if (rootNode != null) {
                ASTPrinter printer = new ASTPrinter();
                rootNode.accept(printer);
                System.out.println(printer.getOutput());

                // Выполняем семантическую проверку
                System.out.println("=== Семантическая проверка ===");
                SemanticChecker checker = new SemanticChecker();
                rootNode.accept(checker);

                if (checker.getErrorCount() > 0) {
                    System.out.println("Найдено ошибок: " + checker.getErrorCount());
                    checker.printErrors();
                } else {
                    System.out.println("Семантических ошибок не найдено.");

                    System.out.println("=== Оптимизация ===");
                    checker.optimize(rootNode);

                    System.out.println("\n=== AST после оптимизаций ===");
                    ASTPrinter optimizedPrinter = new ASTPrinter();
                    rootNode.accept(optimizedPrinter);
                    System.out.println(optimizedPrinter.getOutput());
                }
            } else {
                System.out.println("Корневой узел AST не найден");
            }
        }
    }
}
