package com.talkingdata.studentcourse.common;

import com.talkingdata.studentcourse.common.entity.EnrollRecord;
import com.talkingdata.studentcourse.common.util.EnrollmentProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EnrollmentProcessorTestRunner {
    private static int passed = 0;
    private static int failed = 0;

    private static List<EnrollRecord> toList(String... records) {
        List<EnrollRecord> list = new ArrayList<>();
        for (String r : records) {
            String[] parts = r.split(",");
            list.add(new EnrollRecord(parts[0], parts[1], parts[2]));
        }
        return list;
    }

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   题目一：选课基础处理工具 - 测试运行");
        System.out.println("========================================\n");

        testDeduplicate_BasicDuplicate();
        testDeduplicate_SameStudentDifferentCourses();
        testSort_ByStudentIdAscending();
        testSort_SameStudentIdThenByCourseIdAscending();
        testDeduplicateAndSort_EmptyList();
        testDeduplicateAndSort_NullList();
        testDeduplicateAndSort_AllDuplicates();
        testDeduplicateAndSort_Combined();
        testToString_Format();

        System.out.println("\n========================================");
        System.out.println("   测试结果: " + passed + " 通过, " + failed + " 失败");
        System.out.println("========================================");
    }

    private static void testDeduplicate_BasicDuplicate() {
        System.out.println("【用例1】基本去重");
        List<EnrollRecord> input = toList(
                "S000001,C000001,Java程序设计",
                "S000001,C000001,Java程序设计（重修）",
                "S000002,C000001,计算机网络"
        );
        List<EnrollRecord> result = EnrollmentProcessor.deduplicate(input);
        printResult(result, 2);
        assertEquals(2, result.size(), "记录数应为2");
        System.out.println();
    }

    private static void testDeduplicate_SameStudentDifferentCourses() {
        System.out.println("【用例2】同学生不同课程");
        List<EnrollRecord> input = toList(
                "S000001,C000001,Java程序设计",
                "S000001,C000002,计算机网络",
                "S000001,C000003,数据结构"
        );
        List<EnrollRecord> result = EnrollmentProcessor.deduplicate(input);
        printResult(result, 3);
        assertEquals(3, result.size(), "记录数应为3");
        System.out.println();
    }

    private static void testSort_ByStudentIdAscending() {
        System.out.println("【用例3】排序验证 - 按学生ID升序");
        List<EnrollRecord> input = toList(
                "S000003,C000001,高等数学",
                "S000001,C000001,大学英语",
                "S000002,C000001,大学物理"
        );
        List<EnrollRecord> result = EnrollmentProcessor.sort(input);
        printResult(result);
        assertEquals("S000001", result.get(0).getStudentId(), "第1条学生ID");
        assertEquals("S000002", result.get(1).getStudentId(), "第2条学生ID");
        assertEquals("S000003", result.get(2).getStudentId(), "第3条学生ID");
        System.out.println("  ✓ 学生ID升序排列正确\n");
    }

    private static void testSort_SameStudentIdThenByCourseIdAscending() {
        System.out.println("【用例4】studentId相同，按courseId升序");
        List<EnrollRecord> input = toList(
                "S000001,C000003,数据结构",
                "S000001,C000001,Java程序设计",
                "S000001,C000002,计算机网络"
        );
        List<EnrollRecord> result = EnrollmentProcessor.sort(input);
        printResult(result);
        assertEquals("C000001", result.get(0).getCourseId(), "第1条课程ID");
        assertEquals("C000002", result.get(1).getCourseId(), "第2条课程ID");
        assertEquals("C000003", result.get(2).getCourseId(), "第3条课程ID");
        System.out.println("  ✓ 课程ID升序排列正确\n");
    }

    private static void testDeduplicateAndSort_EmptyList() {
        System.out.println("【用例5】空列表处理");
        List<EnrollRecord> result = EnrollmentProcessor.deduplicateAndSort(Collections.emptyList());
        assertEquals(0, result.size(), "空列表应返回空结果");
        System.out.println("  ✓ 空列表返回空结果\n");
    }

    private static void testDeduplicateAndSort_NullList() {
        System.out.println("【用例6】null列表处理");
        List<EnrollRecord> result = EnrollmentProcessor.deduplicateAndSort(null);
        assertEquals(0, result.size(), "null应返回空结果");
        System.out.println("  ✓ null返回空结果\n");
    }

    private static void testDeduplicateAndSort_AllDuplicates() {
        System.out.println("【用例7】全部重复");
        List<EnrollRecord> input = toList(
                "S000001,C000001,Java程序设计",
                "S000001,C000001,Java程序设计",
                "S000001,C000001,Java程序设计"
        );
        List<EnrollRecord> result = EnrollmentProcessor.deduplicateAndSort(input);
        printResult(result, 1);
        assertEquals(1, result.size(), "应只保留1条");
        System.out.println();
    }

    private static void testDeduplicateAndSort_Combined() {
        System.out.println("【用例8】综合测试：去重+排序");
        List<EnrollRecord> input = toList(
                "S000002,C000001,大学物理",
                "S000001,C000001,大学英语",
                "S000001,C000001,大学英语（重修）",
                "S000003,C000001,高等数学",
                "S000002,C000001,大学物理"
        );
        List<EnrollRecord> result = EnrollmentProcessor.deduplicateAndSort(input);
        printResult(result, 3);
        assertEquals(3, result.size(), "去重后应有3条");
        assertEquals("S000001", result.get(0).getStudentId(), "第1条学生ID");
        assertEquals("S000002", result.get(1).getStudentId(), "第2条学生ID");
        assertEquals("S000003", result.get(2).getStudentId(), "第3条学生ID");
        System.out.println("  ✓ 去重正确，排序正确\n");
    }

    private static void testToString_Format() {
        System.out.println("【用例9】输出格式验证");
        EnrollRecord record = new EnrollRecord("S000001", "C000001", "Java程序设计");
        String expected = "学生ID：S000001，课程ID：C000001，课程名称：Java程序设计";
        assertEquals(expected, record.toString(), "toString格式");
        System.out.println("  输出: " + record);
        System.out.println("  ✓ 格式正确\n");
    }

    private static void printResult(List<EnrollRecord> result) {
        System.out.println("  --- 输出结果 ---");
        for (EnrollRecord r : result) {
            System.out.println("  " + r);
        }
    }

    private static void printResult(List<EnrollRecord> result, int expectedSize) {
        System.out.println("  --- 输出结果 ---");
        for (EnrollRecord r : result) {
            System.out.println("  " + r);
        }
        System.out.println("  共 " + result.size() + " 条记录 (预期 " + expectedSize + " 条)");
    }

    private static void assertEquals(Object expected, Object actual, String msg) {
        if (expected == null ? actual == null : expected.equals(actual)) {
            System.out.println("  ✓ " + msg + " - 通过");
            passed++;
        } else {
            System.out.println("  ✗ " + msg + " - 失败: 预期 " + expected + ", 实际 " + actual);
            failed++;
        }
    }
}