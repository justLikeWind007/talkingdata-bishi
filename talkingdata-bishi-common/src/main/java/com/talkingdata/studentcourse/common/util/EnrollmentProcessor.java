package com.talkingdata.studentcourse.common.util;

import com.talkingdata.studentcourse.common.entity.EnrollRecord;

import java.util.*;
import java.util.stream.Collectors;

public class EnrollmentProcessor {
    private static final List<String> COURSE_TYPE_ORDER = List.of("公共课", "专业课", "选修课");

    private EnrollmentProcessor() {
    }

    public static List<EnrollRecord> deduplicate(List<EnrollRecord> records) {
        if (records == null || records.isEmpty()) {
            return new ArrayList<>();
        }
        Set<String> seen = new HashSet<>();
        List<EnrollRecord> result = new ArrayList<>();
        for (EnrollRecord record : records) {
            if (record == null || record.getStudentId() == null || record.getCourseId() == null) {
                continue;
            }
            String key = record.getStudentId() + "|" + record.getCourseId();
            if (seen.add(key)) {
                result.add(record);
            }
        }
        return result;
    }

    public static List<EnrollRecord> sort(List<EnrollRecord> records) {
        if (records == null || records.isEmpty()) {
            return new ArrayList<>();
        }
        return records.stream()
                .sorted(Comparator.comparing(EnrollRecord::getStudentId)
                        .thenComparing(EnrollRecord::getCourseId))
                .collect(Collectors.toList());
    }

    public static List<EnrollRecord> deduplicateAndSort(List<EnrollRecord> records) {
        return sort(deduplicate(records));
    }

    public static Map<String, List<EnrollRecord>> classifyByCourseType(List<EnrollRecord> records) {
        if (records == null || records.isEmpty()) {
            return new LinkedHashMap<>();
        }
        Map<String, List<EnrollRecord>> grouped = records.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getCourseType() != null ? r.getCourseType() : "选修课",
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
        Map<String, List<EnrollRecord>> ordered = new LinkedHashMap<>();
        for (String type : COURSE_TYPE_ORDER) {
            if (grouped.containsKey(type)) {
                ordered.put(type, grouped.get(type));
            }
        }
        for (Map.Entry<String, List<EnrollRecord>> entry : grouped.entrySet()) {
            if (!ordered.containsKey(entry.getKey())) {
                ordered.put(entry.getKey(), entry.getValue());
            }
        }
        return ordered;
    }

    public static List<EnrollRecord> processAndPrint(List<EnrollRecord> records) {
        List<EnrollRecord> result = deduplicateAndSort(records);
        System.out.println("=== 选课记录处理结果 ===");
        for (EnrollRecord record : result) {
            System.out.println(record);
        }
        return result;
    }
}
