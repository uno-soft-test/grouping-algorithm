package ru.unosoft.grouping.runner;

import ru.unosoft.grouping.configuration.AppConfiguration;
import ru.unosoft.grouping.dto.ProcessResult;
import ru.unosoft.grouping.service.ProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Класс запуска приложения, реализующий интерфейс CommandLineRunner.
 * <p>
 * Выполняет обработку CSV данных при запуске приложения, измеряет время выполнения и используемую память,
 * а затем выводит результаты.
 */
@Component
@RequiredArgsConstructor
public class ApplicationRunner implements CommandLineRunner {

    private final ProcessingService processingService;

    private final AppConfiguration appConfiguration;

    /**
     * Метод, выполняемый при запуске приложения.
     * <p>
     * Вызывает метод обработки данных, вычисляет время выполнения и используемую память, а затем выводит результаты.
     *
     * @param args Аргументы командной строки.
     * @throws IOException Если произошла ошибка при обработке CSV данных.
     */
    @Override
    public void run(String... args) throws IOException {
        String inputFile = determineInputFile(args);
        long startTime = System.currentTimeMillis();
        ProcessResult result = processingService.processCsvData(inputFile);
        double durationSeconds = processingService.calculateDurationInSeconds(startTime, System.currentTimeMillis());
        double usedMemoryGB = processingService.calculateUsedMemoryInGB();
        processingService.printResults(result.getMultiGroupCount(), durationSeconds, usedMemoryGB);
    }

    /**
     * Определяет путь к входному файлу.
     * <p>
     * Если путь указан в аргументах командной строки, используется он, иначе используется путь из конфигурации.
     *
     * @param args Аргументы командной строки.
     * @return Путь к входному файлу.
     */
    private String determineInputFile(String... args) {
        if (args.length > 0) {
            System.out.println("Используется входной файл из аргументов командной строки: " + args[0]);
            return args[0];
        } else {
            System.out.println("Используется входной файл из конфигурации: " + appConfiguration.getInputFile());
            return appConfiguration.getInputFile();
        }
    }
}
