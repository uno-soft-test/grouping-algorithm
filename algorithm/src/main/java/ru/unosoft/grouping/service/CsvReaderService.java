package ru.unosoft.grouping.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import ru.unosoft.grouping.util.LineUtil;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

/**
 * Сервис для чтения и парсинга CSV или текстовых файлов из ресурсов.
 * <p>
 * Поддерживает чтение файлов в форматах `.txt` и `.txt.gz`. Обеспечивает уникальность записей.
 */
@Service
@RequiredArgsConstructor
public class CsvReaderService {

    private static final int INITIAL_CAPACITY = 1_000_000;

    private static final String GZ_FILE_FORMAT = ".gz";

    private final LineUtil lineUtil;

    /**
     * Читает и парсит файл из ресурсов, поддерживает форматы .txt и .txt.gz.
     *
     * @param filePath Путь к входному файлу.
     * @return Список массивов строк, каждая из которых представляет отдельную строку файла.
     * @throws IOException Если произошла ошибка при чтении файла.
     */
    public List<String[]> readCsv(String filePath) throws IOException {
        Set<String> uniqueLines = new HashSet<>(INITIAL_CAPACITY);
        try (CSVParser csvParser = createCsvParser(filePath)) {
            return csvParser.stream()
                    .map(this::parseRecord)
                    .filter(this::isValidLine)
                    .filter(lines -> addIfUnique(lines, uniqueLines))
                    .collect(Collectors.toList());
        }
    }

    /**
     * Создаёт CSVParser для заданного файла.
     * <p>
     * Если файл имеет расширение `.txt.gz`, создается GZIPInputStream для чтения сжатого содержимого,
     * иначе создаётся обычный InputStream.
     *
     * @param filePath Путь к файлу.
     * @return Настроенный CSVParser.
     * @throws IOException Если произошла ошибка при создании CSVParser или при чтении файла.
     */
    private CSVParser createCsvParser(String filePath) throws IOException {
        InputStream inputStream = getInputStream(filePath);
        InputStream fileStream = new BufferedInputStream(inputStream);
        if (isGzFile(filePath)) fileStream = new GZIPInputStream(fileStream);
        Reader reader = new InputStreamReader(fileStream, StandardCharsets.UTF_8);
        return createCsvParser(reader);
    }

    /**
     * Проверяет, является ли файл сжатым.
     *
     * @param filePath Путь к файлу.
     * @return true, если файл сжат; иначе false.
     */
    private boolean isGzFile(String filePath) {
        return filePath.endsWith(GZ_FILE_FORMAT);
    }

    /**
     * Создаёт CSVParser с заданными настройками.
     *
     * @param reader Reader для CSVParser.
     * @return Настроенный CSVParser.
     * @throws IOException Если произошла ошибка при создании CSVParser.
     */
    private CSVParser createCsvParser(Reader reader) throws IOException {
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setDelimiter(';')
                .setIgnoreEmptyLines(true)
                .setTrim(true)
                .build();
        return new CSVParser(reader, csvFormat);
    }

    /**
     * Получает InputStream для заданного пути к файлу.
     * <p>
     * Если файл существует в файловой системе, используется Files.newInputStream.
     * Иначе, если файл находится в classpath, используется getResourceAsStream.
     *
     * @param filePath Путь к файлу.
     * @return InputStream для файла.
     * @throws FileNotFoundException Если файл не найден ни в файловой системе, ни в classpath.
     */
    private InputStream getInputStream(String filePath) throws FileNotFoundException {
        Path path = Paths.get(filePath);
        return Optional.of(path)
                .filter(Files::exists)
                .flatMap(p -> {
                    try {
                        return Optional.of(Files.newInputStream(p));
                    } catch (IOException e) {
                        return Optional.empty();
                    }
                })
                .or(() -> Optional.ofNullable(getClass().getClassLoader().getResourceAsStream(filePath)))
                .orElseThrow(() -> new FileNotFoundException("Файл " + filePath + " не найден."));
    }

    /**
     * Преобразует CSVRecord в массив строк.
     *
     * @param record CSVRecord для преобразования.
     * @return Массив строк, представляющий запись.
     */
    private String[] parseRecord(CSVRecord record) {
        return record.stream()
                .map(String::trim)
                .toArray(String[]::new);
    }


    /**
     * Проверяет валидность строки.
     * Строка считается валидной, если все колонки с кавычками начинаются и заканчиваются кавычками.
     *
     * @param line Строка в записи.
     * @return true, если запись валидна; false в противном случае.
     */
    private boolean isValidLine(String[] line) {
        return Arrays.stream(line)
                .allMatch(column -> !column.contains("\"") || (column.startsWith("\"") && column.endsWith("\"")));
    }

    /**
     * Проверяет уникальность строки и добавляет её в множество уникальных строк.
     *
     * @param line        Строка в записи.
     * @param uniqueLines Множество для отслеживания уникальных строк.
     * @return true, если запись уникальна и была добавлена; false в противном случае.
     */
    private boolean addIfUnique(String[] line, Set<String> uniqueLines) {
        String originalLine = lineUtil.buildLine(line);
        return uniqueLines.add(originalLine);
    }
}