package ru.unosoft.grouping.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import ru.unosoft.grouping.util.LineUtil;
import ru.unosoft.grouping.util.UnionFind;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Сервис для группировки строк по совпадению значений в одних и тех же колонках.
 * <p>
 * Строки объединяются в группы, если хотя бы одно непустое значение в одной и той же колонке совпадает.
 * Группы представлены в виде мапы, где ключ — идентификатор группы, а значение — список строк в группе.
 */
@Service
@RequiredArgsConstructor
public class GroupingService {

    private static final int INITIAL_CAPACITY = 1_000_000;

    private final LineUtil lineUtil;

    /**
     * Группирует строки на основе совпадений непустых значений в одной и той же колонке.
     *
     * @param lines Лист строк, каждая строка представлена массивом значений.
     * @return Мапа групп, где ключ — идентификатор группы, а значение — список строк в группе.
     */
    public Map<Integer, List<String>> groupLines(List<String[]> lines) {
        UnionFind uf = createUnionFind(lines);
        return createGroups(lines, uf);
    }

    /**
     * Создаёт структуру UnionFind и ассоциирует значения колонок с индексами строк.
     * Объединяет строки, если их значения совпадают в одной и той же колонке.
     *
     * @param lines Лист строк.
     * @return Структура UnionFind с объединёнными строками.
     */
    private UnionFind createUnionFind(List<String[]> lines) {
        UnionFind uf = new UnionFind(lines.size());
        Map<Pair<Integer, String>, Integer> columnsToLine = new HashMap<>(INITIAL_CAPACITY);
        IntStream.range(0, lines.size())
                .forEach(lineIdx ->
                        associateColumnsWithLine(columnsToLine, lines.get(lineIdx), lineIdx, uf));
        return uf;
    }

    /**
     * Ассоциирует значения колонок с индексами строк и объединяет строки, если значения совпадают.
     *
     * @param columnsToLine Мапа, сопоставляющая значения колонок с индексами первых строк.
     * @param line          Текущая строка, представленная массивом значений.
     * @param lineIdx       Индекс текущей строки в списке.
     * @param uf            Структура UnionFind для объединения строк.
     */
    private void associateColumnsWithLine(
            Map<Pair<Integer, String>, Integer> columnsToLine, String[] line, int lineIdx, UnionFind uf
    ) {
        IntStream.range(0, line.length)
                .mapToObj(colIdx -> Pair.of(colIdx, line[colIdx]))
                .filter(pair -> isNonEmpty(pair.getSecond()))
                .forEach(pair -> {
                    Pair<Integer, String> idxColPair = Pair.of(pair.getFirst(), pair.getSecond());
                    Optional.ofNullable(columnsToLine.get(idxColPair))
                            .ifPresentOrElse(
                                    firstIdx -> uf.union(firstIdx, lineIdx),
                                    () -> columnsToLine.put(idxColPair, lineIdx)
                            );
                });
    }

    /**
     * Проверяет, что строка не является null и не пустой.
     *
     * @param value Строка для проверки.
     * @return true, если строка не null и не пустая, иначе false.
     */
    private boolean isNonEmpty(String value) {
        return value != null && !value.isEmpty();
    }

    /**
     * Создаёт группы строк на основе структуры UnionFind.
     *
     * @param lines Лист строк.
     * @param uf    Структура UnionFind с объединёнными строками.
     * @return Мапа групп, где ключ — идентификатор группы, а значение — список строк в группе.
     */
    private Map<Integer, List<String>> createGroups(List<String[]> lines, UnionFind uf) {
        return IntStream.range(0, lines.size())
                .mapToObj(idx -> Map.entry(uf.find(idx), lineUtil.buildLine(lines.get(idx))))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));
    }

    /**
     * Подсчитывает количество групп с более чем одним элементом.
     *
     * @param groups Мапа групп.
     * @return Количество групп с более чем одним элементом.
     */
    public long countMultiGroups(Map<Integer, List<String>> groups) {
        return groups.values().stream()
                .filter(group -> group.size() > 1)
                .count();
    }

    /**
     * Сортирует группы по убыванию количества элементов в группе.
     *
     * @param groups Мапа групп.
     * @return Список групп, отсортированных по убыванию количества элементов.
     */
    public List<Map.Entry<Integer, List<String>>> sortGroups(Map<Integer, List<String>> groups) {
        List<Map.Entry<Integer, List<String>>> sortedGroups = new ArrayList<>(groups.entrySet());
        sortedGroups.sort((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()));
        return sortedGroups;
    }
}
