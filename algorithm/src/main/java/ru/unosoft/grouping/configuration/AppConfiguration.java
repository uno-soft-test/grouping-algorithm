package ru.unosoft.grouping.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурационный класс для управления параметрами приложения.
 * <p>
 * Считывает значения из файла конфигурации (application.properties или аналогичного) и предоставляет доступ к ним.
 */
@Configuration
@Getter
public class AppConfiguration {

    /**
     * Путь к входному CSV файлу.
     */
    @Value("${input.file}")
    private String inputFile;

    /**
     * Путь к выходному файлу для записи результатов.
     */
    @Value("${output.file}")
    private String outputFile;
}
