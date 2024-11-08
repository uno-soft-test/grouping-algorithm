package ru.unosoft.grouping.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * DTO для представления результатов обработки данных.
 * <p>
 * Содержит количество групп с более чем одним элементом и список отсортированных групп.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessResult {

    /**
     * Количество групп с более чем одним элементом.
     */
    long multiGroupCount;

    /**
     * Список отсортированных групп.
     * <p>
     * Каждая группа представлена как запись, содержащая идентификатор группы и список строк.
     */
    List<Map.Entry<Integer, List<String>>> sortedGroups;
}
