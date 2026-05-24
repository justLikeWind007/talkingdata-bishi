package com.talkingdata.studentcourse.common;

import com.talkingdata.studentcourse.common.entity.EnrollRecord;
import com.talkingdata.studentcourse.common.util.EnrollmentProcessor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EnrollmentProcessorTest {

    private List<EnrollRecord> toList(String... records) {
        List<EnrollRecord> list = new ArrayList<>();
        for (String r : records) {
            String[] parts = r.split(",");
            list.add(new EnrollRecord(parts[0], parts[1], parts[2]));
        }
        return list;
    }

    // ==================== 用例1：基本去重 ====================
    @Test
    void testDeduplicate_BasicDuplicate() {
        List<EnrollRecord> input = toList(
                "S000001,C000001,Java程序设计",
                "S000001,C000001,Java程序设计（重修）",
                "S000002,C000001,计算机网络"
        );

        List<EnrollRecord> result = EnrollmentProcessor.deduplicate(input);

        assertEquals(2, result.size());
        assertEquals("S000001", result.get(0).getStudentId());
        assertEquals("C000001", result.get(0).getCourseId());
        assertEquals("Java程序设计", result.get(0).getCourseName());
        assertEquals("S000002", result.get(1).getStudentId());
    }

    // ==================== 用例2：同学生不同课程 ====================
    @Test
    void testDeduplicate_SameStudentDifferentCourses() {
        List<EnrollRecord> input = toList(
                "S000001,C000001,Java程序设计",
                "S000001,C000002,计算机网络",
                "S000001,C000003,数据结构"
        );

        List<EnrollRecord> result = EnrollmentProcessor.deduplicate(input);

        assertEquals(3, result.size());
    }

    // ==================== 用例3：排序验证 ====================
    @Test
    void testSort_ByStudentIdAscending() {
        List<EnrollRecord> input = toList(
                "S000003,C000001,高等数学",
                "S000001,C000001,大学英语",
                "S000002,C000001,大学物理"
        );

        List<EnrollRecord> result = EnrollmentProcessor.sort(input);

        assertEquals("S000001", result.get(0).getStudentId());
        assertEquals("S000002", result.get(1).getStudentId());
        assertEquals("S000003", result.get(2).getStudentId());
    }

    // ==================== 用例4：studentId相同，courseId不同 ====================
    @Test
    void testSort_SameStudentIdThenByCourseIdAscending() {
        List<EnrollRecord> input = toList(
                "S000001,C000003,数据结构",
                "S000001,C000001,Java程序设计",
                "S000001,C000002,计算机网络"
        );

        List<EnrollRecord> result = EnrollmentProcessor.sort(input);

        assertEquals("C000001", result.get(0).getCourseId());
        assertEquals("C000002", result.get(1).getCourseId());
        assertEquals("C000003", result.get(2).getCourseId());
    }

    // ==================== 用例5：空列表 ====================
    @Test
    void testDeduplicateAndSort_EmptyList() {
        List<EnrollRecord> result = EnrollmentProcessor.deduplicateAndSort(Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    void testDeduplicateAndSort_NullList() {
        List<EnrollRecord> result = EnrollmentProcessor.deduplicateAndSort(null);
        assertTrue(result.isEmpty());
    }

    // ==================== 用例6：全部重复 ====================
    @Test
    void testDeduplicateAndSort_AllDuplicates() {
        List<EnrollRecord> input = toList(
                "S000001,C000001,Java程序设计",
                "S000001,C000001,Java程序设计",
                "S000001,C000001,Java程序设计"
        );

        List<EnrollRecord> result = EnrollmentProcessor.deduplicateAndSort(input);

        assertEquals(1, result.size());
        assertEquals("S000001", result.get(0).getStudentId());
        assertEquals("C000001", result.get(0).getCourseId());
    }

    // ==================== 综合测试：去重+排序 ====================
    @Test
    void testDeduplicateAndSort_Combined() {
        List<EnrollRecord> input = toList(
                "S000002,C000001,大学物理",
                "S000001,C000001,大学英语",
                "S000001,C000001,大学英语（重修）",
                "S000003,C000001,高等数学",
                "S000002,C000001,大学物理"
        );

        List<EnrollRecord> result = EnrollmentProcessor.deduplicateAndSort(input);

        assertEquals(3, result.size());
        // 按studentId升序
        assertEquals("S000001", result.get(0).getStudentId());
        assertEquals("S000002", result.get(1).getStudentId());
        assertEquals("S000003", result.get(2).getStudentId());
        // 相同studentId时按courseId升序
        assertEquals("C000001", result.get(0).getCourseId());
    }

    // ==================== 输出格式测试 ====================
    @Test
    void testToString_Format() {
        EnrollRecord record = new EnrollRecord("S000001", "C000001", "Java程序设计");
        assertEquals("学生ID：S000001，课程ID：C000001，课程名称：Java程序设计", record.toString());
    }
}