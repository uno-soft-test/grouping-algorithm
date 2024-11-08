import org.junit.jupiter.api.Test;
import ru.unosoft.grouping.service.CsvReaderService;
import ru.unosoft.grouping.service.GroupingService;
import ru.unosoft.grouping.util.LineUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GroupingServiceTest {

    private final LineUtil lineUtil = new LineUtil();

    private final CsvReaderService csvReaderService = new CsvReaderService(lineUtil);

    private final GroupingService groupingService = new GroupingService(lineUtil);

    @Test
    public void testGroupingWithTxtFile_Case1() throws IOException {
        List<String[]> lines = csvReaderService.readCsv("src/test/resources/input/case1.txt");
        Map<Integer, List<String>> groups = groupingService.groupLines(lines);
        List<String> expectedOutput = readAllLines("src/test/resources/expected-output/case1.txt");
        assertGroupedOutput(groups, expectedOutput);
    }

    @Test
    public void testGroupingWithGzFile_Case1() throws IOException {
        List<String[]> lines = csvReaderService.readCsv("src/test/resources/input/case1.txt.gz");
        Map<Integer, List<String>> groups = groupingService.groupLines(lines);
        List<String> expectedOutput = readAllLines("src/test/resources/expected-output/case1.txt");
        assertGroupedOutput(groups, expectedOutput);
    }

    @Test
    public void testGroupingWithTxtFile_Case2() throws IOException {
        List<String[]> lines = csvReaderService.readCsv("src/test/resources/input/case2.txt");
        Map<Integer, List<String>> groups = groupingService.groupLines(lines);
        List<String> expectedOutput = readAllLines("src/test/resources/expected-output/case2.txt");
        assertGroupedOutput(groups, expectedOutput);
    }

    @Test
    public void testGroupingWithGzFile_Case2() throws IOException {
        List<String[]> lines = csvReaderService.readCsv("src/test/resources/input/case2.txt.gz");
        Map<Integer, List<String>> groups = groupingService.groupLines(lines);
        List<String> expectedOutput = readAllLines("src/test/resources/expected-output/case2.txt");
        assertGroupedOutput(groups, expectedOutput);
    }

    private void assertGroupedOutput(Map<Integer, List<String>> groups, List<String> expectedOutput) {
        List<String> actualOutput = groups.values().stream()
                .map(group -> String.join("\n", group))
                .collect(Collectors.joining("\n\n"))
                .lines()
                .toList();
        assertEquals(expectedOutput, actualOutput);
    }

    private List<String> readAllLines(String filePath) throws IOException {
        return Files.readAllLines(Path.of(filePath));
    }
}
