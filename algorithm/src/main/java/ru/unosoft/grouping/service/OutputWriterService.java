package ru.unosoft.grouping.service;

import ru.unosoft.grouping.configuration.AppConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * Сервис для записи результатов группировки в выходной файл.
 * <p>
 * Сохраняет количество групп с более чем одним элементом и детали каждой группы в формате,
 * удобном для последующего анализа.
 */
@Service
@RequiredArgsConstructor
public class OutputWriterService {

    private final AppConfiguration appConfiguration;

    /**
     * Записывает результаты группировки в выходной файл.
     *
     * @param sortedGroups    Отсортированный список групп, где каждая группа представлена
     *                        как пара из идентификатора группы и списка строк.
     * @param multiGroupCount Количество групп с более чем одним элементом.
     * @throws IOException Если произошла ошибка при записи в файл.
     */
    public void writeOutput(
            List<Map.Entry<Integer, List<String>>> sortedGroups, long multiGroupCount
    ) throws IOException {
        try (BufferedWriter writer = createBufferedWriter(appConfiguration.getOutputFile())) {
            writeHeader(writer, multiGroupCount);
            writeGroups(writer, sortedGroups);
        }
    }

    /**
     * Создаёт BufferedWriter для записи в выходной файл.
     *
     * @param outputFile Путь к выходному файлу.
     * @return BufferedWriter для записи данных в файл.
     * @throws IOException Если произошла ошибка при создании BufferedWriter.
     */
    private BufferedWriter createBufferedWriter(String outputFile) throws IOException {
        return Files.newBufferedWriter(Paths.get(outputFile), StandardCharsets.UTF_8);
    }

    /**
     * Записывает заголовок в файл, включая общее количество групп с более чем одним элементом.
     *
     * @param writer          BufferedWriter для записи данных.
     * @param multiGroupCount Количество групп с более чем одним элементом.
     * @throws IOException Если произошла ошибка при записи в файл.
     */
    private void writeHeader(BufferedWriter writer, long multiGroupCount) throws IOException {
        writer.write("Количество групп с более чем одним элементом: " + multiGroupCount);
        writer.newLine();
        writer.newLine();
    }

    /**
     * Записывает все группы в файл.
     * <p>
     * Группы записываются в порядке, начиная с группы 1, и каждая группа отделяется пустой строкой.
     *
     * @param writer       BufferedWriter для записи данных.
     * @param sortedGroups Отсортированный список групп для записи.
     * @throws IOException Если произошла ошибка при записи в файл.
     */
    private void writeGroups(
            BufferedWriter writer, List<Map.Entry<Integer, List<String>>> sortedGroups
    ) throws IOException {
        int groupNumber = 1;
        for (Map.Entry<Integer, List<String>> entry : sortedGroups) {
            writeGroup(writer, entry, groupNumber++);
        }
    }

    /**
     * Записывает одну группу в файл.
     * <p>
     * Формат записи: "Группа {номер}", затем список строк группы, каждая строка с новой строки.
     *
     * @param writer      BufferedWriter для записи данных.
     * @param entry       Запись группы, содержащая идентификатор и список строк.
     * @param groupNumber Номер группы для отображения в файле.
     * @throws IOException Если произошла ошибка при записи в файл.
     */
    private void writeGroup(
            BufferedWriter writer, Map.Entry<Integer, List<String>> entry, int groupNumber
    ) throws IOException {
        writer.write("Группа " + groupNumber);
        writer.newLine();
        for (String line : entry.getValue()) {
            writer.write(line);
            writer.newLine();
        }
        writer.newLine();
    }
}
