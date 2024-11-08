package ru.unosoft.grouping.util;

import org.springframework.stereotype.Component;

/**
 * Утилитарный класс для работы со строками.
 */
@Component
public class LineUtil {

    /**
     * Строит строку из колонок, разделённых точкой с запятой.
     *
     * @param line Строка в записи.
     * @return Объединённая строка.
     */
    public String buildLine(String[] line) {
        return String.join(";", line);
    }
}
