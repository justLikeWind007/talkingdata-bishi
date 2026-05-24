package com.talkingdata.studentcourse.web.service;

import com.talkingdata.studentcourse.common.entity.EnrollRecord;
import com.talkingdata.studentcourse.common.util.EnrollmentProcessor;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class EnrollmentService {

    private static final Logger log = LoggerFactory.getLogger(EnrollmentService.class);
    private static final String DEFAULT_COURSE_TYPE = "选修课";
    private static final Pattern RECORD_SPLIT_PATTERN = Pattern.compile("[\\r\\n;；]+");
    private static final String SAMPLE_CSV = String.join("\n",
            "S000001,C000001,Java程序设计,专业课",
            "S000002,C000003,计算机网络,公共课",
            "S000003,C000008,摄影基础,选修课",
            "S000001,C000001,Java程序设计（重修）,专业课"
    );

    private final AtomicReference<List<EnrollRecord>> enrollmentsRef = new AtomicReference<>(List.of());
    private final LongAdder importCount = new LongAdder();
    private final LongAdder searchCount = new LongAdder();
    private final LongAdder importedRecordCount = new LongAdder();
    private final LongAdder matchedRecordCount = new LongAdder();
    private final AtomicLong lastImportDurationMs = new AtomicLong(0);
    private final AtomicLong lastSearchDurationMs = new AtomicLong(0);
    private final AtomicReference<String> lastImportAt = new AtomicReference<>("N/A");
    private final AtomicReference<String> lastSearchAt = new AtomicReference<>("N/A");
    private final AtomicReference<String> lastSearchKeyword = new AtomicReference<>("");

    @PostConstruct
    public void initSampleEnrollments() {
        List<EnrollRecord> sampleRecords = EnrollmentProcessor.deduplicateAndSort(parseCSV(SAMPLE_CSV));
        enrollmentsRef.set(Collections.unmodifiableList(sampleRecords));
    }

    public ImportResult importCSVData(String csvData) {
        long startTime = System.currentTimeMillis();

        ParseResult parseResult = parseCSVWithSummary(csvData);
        List<EnrollRecord> records = parseResult.records();
        int validRecordCount = parseResult.validRecordCount();
        int invalidRecordCount = parseResult.invalidRecordCount();

        long sortStartTime = System.currentTimeMillis();
        List<EnrollRecord> processed = EnrollmentProcessor.deduplicateAndSort(records);
        long sortEndTime = System.currentTimeMillis();

        enrollmentsRef.set(Collections.unmodifiableList(new ArrayList<>(processed)));

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        long sortTime = sortEndTime - sortStartTime;
        int duplicatesRemoved = validRecordCount - processed.size();

        log.info("=== CSV导入性能统计 ===");
        log.info("输入总行数: {}", parseResult.totalLineCount());
        log.info("有效记录数: {}", validRecordCount);
        log.info("无效记录数: {}", invalidRecordCount);
        log.info("去重后记录数: {}", processed.size());
        log.info("去重去除记录数: {}", duplicatesRemoved);
        log.info("解析耗时: {} ms", totalTime - sortTime);
        log.info("去重排序耗时: {} ms", sortTime);
        log.info("总耗时: {} ms", totalTime);

        importCount.increment();
        importedRecordCount.add(processed.size());
        lastImportDurationMs.set(totalTime);
        lastImportAt.set(new Date().toString());

        return new ImportResult(
                processed,
                parseResult.totalLineCount(),
                validRecordCount,
                invalidRecordCount,
                duplicatesRemoved
        );
    }

    public List<EnrollRecord> processCSVData(String csvData) {
        return importCSVData(csvData).records();
    }

    public List<EnrollRecord> search(String keyword) {
        long startTime = System.currentTimeMillis();
        List<EnrollRecord> snapshot = enrollmentsRef.get();

        List<EnrollRecord> result;
        if (keyword == null || keyword.trim().isEmpty()) {
            result = snapshot;
        } else {
            String k = keyword.trim().toLowerCase(Locale.ROOT);
            result = snapshot.stream()
                    .filter(r -> safeLower(r.getStudentId()).contains(k)
                            || safeLower(r.getCourseId()).contains(k)
                            || safeLower(r.getCourseName()).contains(k)
                            || safeLower(r.getCourseType()).contains(k))
                    .collect(Collectors.toList());
        }

        long endTime = System.currentTimeMillis();
        log.info("=== 检索性能统计 ===");
        log.info("检索关键词: {}", keyword);
        log.info("总记录数: {}", snapshot.size());
        log.info("匹配记录数: {}", result.size());
        log.info("检索耗时: {} ms", endTime - startTime);

        searchCount.increment();
        matchedRecordCount.add(result.size());
        lastSearchDurationMs.set(endTime - startTime);
        lastSearchAt.set(new Date().toString());
        lastSearchKeyword.set(keyword == null ? "" : keyword);

        return result;
    }

    private List<EnrollRecord> parseCSV(String csvData) {
        return parseCSVWithSummary(csvData).records();
    }

    private ParseResult parseCSVWithSummary(String csvData) {
        List<EnrollRecord> records = new ArrayList<>();
        if (csvData == null || csvData.isBlank()) {
            return new ParseResult(records, 0, 0, 0);
        }
        String[] lines = RECORD_SPLIT_PATTERN.split(csvData);
        int totalLineCount = 0;
        int invalidRecordCount = 0;
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            totalLineCount++;
            String[] parts = line.split(",");
            if (parts.length >= 3) {
                String studentId = parts[0].trim();
                String courseId = parts[1].trim();
                String courseName = parts[2].trim();
                if (studentId.isBlank() || courseId.isBlank() || courseName.isBlank()) {
                    invalidRecordCount++;
                    continue;
                }
                String rawCourseType = parts.length > 3 ? parts[3].trim() : "";
                String courseType = resolveCourseType(rawCourseType, courseName);
                records.add(new EnrollRecord(studentId, courseId, courseName, courseType));
            } else {
                invalidRecordCount++;
            }
        }
        return new ParseResult(records, totalLineCount, records.size(), invalidRecordCount);
    }

    public Map<String, List<EnrollRecord>> getClassifiedEnrollments() {
        return EnrollmentProcessor.classifyByCourseType(enrollmentsRef.get());
    }

    public Map<String, List<EnrollRecord>> classify(List<EnrollRecord> records) {
        return EnrollmentProcessor.classifyByCourseType(records);
    }

    public List<EnrollRecord> getAllEnrollments() {
        return enrollmentsRef.get();
    }

    public RuntimeStats getRuntimeStats() {
        return new RuntimeStats(
                enrollmentsRef.get().size(),
                importCount.sum(),
                searchCount.sum(),
                importedRecordCount.sum(),
                matchedRecordCount.sum(),
                lastImportDurationMs.get(),
                lastSearchDurationMs.get(),
                lastImportAt.get(),
                lastSearchAt.get(),
                lastSearchKeyword.get()
        );
    }

    private String resolveCourseType(String rawCourseType, String courseName) {
        if (rawCourseType != null && !rawCourseType.isBlank()) {
            return normalizeCourseType(rawCourseType.trim());
        }
        return detectCourseTypeByName(courseName);
    }

    private String normalizeCourseType(String courseType) {
        if (courseType == null || courseType.isBlank()) {
            return DEFAULT_COURSE_TYPE;
        }
        return switch (courseType) {
            case "公共课", "专业课", "选修课" -> courseType;
            case "公共" -> "公共课";
            case "专业" -> "专业课";
            default -> DEFAULT_COURSE_TYPE;
        };
    }

    private String detectCourseTypeByName(String courseName) {
        String name = safeLower(courseName);

        if (containsAny(name, "高等数学", "大学英语", "线性代数", "概率论", "大学物理", "思想政治", "马克思", "毛概")) {
            return "公共课";
        }
        if (containsAny(name, "程序", "数据", "网络", "算法", "数据库", "软件", "操作系统", "人工智能", "编译", "体系结构")) {
            return "专业课";
        }
        if (containsAny(name, "摄影", "音乐", "美术", "书法", "舞蹈", "电影", "心理", "创业")) {
            return "选修课";
        }
        return DEFAULT_COURSE_TYPE;
    }

    private boolean containsAny(String source, String... tokens) {
        for (String token : tokens) {
            if (source.contains(token.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    private String safeLower(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }

    public record ImportResult(
            List<EnrollRecord> records,
            int totalLineCount,
            int validRecordCount,
            int invalidRecordCount,
            int duplicatesRemoved
    ) {
    }

    private record ParseResult(
            List<EnrollRecord> records,
            int totalLineCount,
            int validRecordCount,
            int invalidRecordCount
    ) {
    }

    public record RuntimeStats(
            int currentRecordCount,
            long importCount,
            long searchCount,
            long totalImportedRecords,
            long totalMatchedRecords,
            long lastImportDurationMs,
            long lastSearchDurationMs,
            String lastImportAt,
            String lastSearchAt,
            String lastSearchKeyword
    ) {
    }
}
