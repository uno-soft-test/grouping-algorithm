package ru.unosoft.grouping.service;

import ru.unosoft.grouping.configuration.AppConfiguration;
import ru.unosoft.grouping.dto.ProcessResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Сервис для обработки CSV данных, группировки и записи результатов.
 * <p>
 * Включает этапы чтения, группировки, записи результатов, а также вычисления времени выполнения и использования памяти.
 */
@Service
@RequiredArgsConstructor
public class ProcessingService {

    private static final double MILLISECONDS_IN_SECOND = 1000.0;

    private static final double BYTES_IN_GIGABYTE = 1024.0 * 1024.0 * 1024.0;

    private final CsvReaderService csvReaderService;

    private final GroupingService groupingService;

    private final OutputWriterService outputWriterService;

    /**
     * Обрабатывает данные из CSV файла, группирует строки и записывает результаты в файл.
     *
     * @param inputFile Путь к входному CSV файлу. Если null, используется значение из конфигурации.
     * @return Объект ProcessResult, содержащий количество групп с более чем одним элементом и отсортированные группы.
     * @throws IOException Если произошла ошибка при чтении или записи данных.
     */
    public ProcessResult processCsvData(String inputFile) throws IOException {
        List<String[]> parsedLines = csvReaderService.readCsv(inputFile);
        Map<Integer, List<String>> groups = groupingService.groupLines(parsedLines);
        long multiGroupCount = groupingService.countMultiGroups(groups);
        List<Map.Entry<Integer, List<String>>> sortedGroups = groupingService.sortGroups(groups);
        outputWriterService.writeOutput(sortedGroups, multiGroupCount);
        return new ProcessResult(multiGroupCount, sortedGroups);
    }

    /**
     * Вычисляет продолжительность выполнения программы в секундах.
     *
     * @param startTime Время начала в миллисекундах.
     * @param endTime   Время окончания в миллисекундах.
     * @return Продолжительность в секундах.
     */
    public double calculateDurationInSeconds(long startTime, long endTime) {
        return (endTime - startTime) / MILLISECONDS_IN_SECOND;
    }

    /**
     * Вычисляет объем используемой памяти в гигабайтах.
     * <p>
     * Принудительно вызывает сборщик мусора перед вычислением памяти.
     *
     * @return Использованная память в гигабайтах.
     */
    public double calculateUsedMemoryInGB() {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        return (runtime.totalMemory() - runtime.freeMemory()) / BYTES_IN_GIGABYTE;
    }

    /**
     * Выводит результаты обработки данных в консоль.
     *
     * @param multiGroupCount Количество групп с более чем одним элементом.
     * @param durationSeconds Время выполнения программы в секундах.
     * @param usedMemoryGB    Использованная память в гигабайтах.
     */
    public void printResults(long multiGroupCount, double durationSeconds, double usedMemoryGB) {
        System.out.printf("Количество групп с более чем одним элементом: %d%n", multiGroupCount);
        System.out.printf("Время выполнения программы: %.3f секунд%n", durationSeconds);
        System.out.printf("Использованная память: %.3f ГБ%n", usedMemoryGB);
    }
}
