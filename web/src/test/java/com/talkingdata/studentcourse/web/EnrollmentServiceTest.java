package com.talkingdata.studentcourse.web;

import com.talkingdata.studentcourse.common.entity.EnrollRecord;
import com.talkingdata.studentcourse.web.service.EnrollmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EnrollmentServiceTest {

    @Autowired
    private EnrollmentService enrollmentService;

    @BeforeEach
    void setUp() {
        // 每个测试前重置为当前测试数据，避免相互影响
        enrollmentService.processCSVData("");
    }

    // ==================== CSV导入测试 ====================

    @Test
    void testProcessCSVData_BasicImport() {
        String csvData = "S000001,C000001,Java程序设计,专业课\n" +
                "S000002,C000003,计算机网络,公共课\n" +
                "S000001,C000002,数据结构,专业课";

        List<EnrollRecord> result = enrollmentService.processCSVData(csvData);

        assertEquals(3, result.size());
        assertEquals("S000001", result.get(0).getStudentId());
        assertEquals("C000001", result.get(0).getCourseId());
    }

    @Test
    void testProcessCSVData_Deduplication() {
        String csvData = "S000001,C000001,Java程序设计,专业课\n" +
                "S000001,C000001,Java程序设计（重修）,专业课\n" +
                "S000002,C000001,计算机网络,公共课";

        List<EnrollRecord> result = enrollmentService.processCSVData(csvData);

        assertEquals(2, result.size());
    }

    @Test
    void testProcessCSVData_Sorting() {
        String csvData = "S000003,C000001,高等数学,公共课\n" +
                "S000001,C000001,大学英语,公共课\n" +
                "S000002,C000001,大学物理,公共课";

        List<EnrollRecord> result = enrollmentService.processCSVData(csvData);

        assertEquals("S000001", result.get(0).getStudentId());
        assertEquals("S000002", result.get(1).getStudentId());
        assertEquals("S000003", result.get(2).getStudentId());
    }

    @Test
    void testProcessCSVData_DefaultCourseType() {
        String csvData = "S000001,C000001,Java程序设计";

        List<EnrollRecord> result = enrollmentService.processCSVData(csvData);

        assertEquals("专业课", result.get(0).getCourseType());
    }

    @Test
    void testProcessCSVData_AutoDetectPublicCourseType() {
        String csvData = "S000001,C000002,高等数学";

        List<EnrollRecord> result = enrollmentService.processCSVData(csvData);

        assertEquals("公共课", result.get(0).getCourseType());
    }

    // ==================== 检索功能测试 ====================

    @Test
    void testSearch_ByStudentId() {
        String csvData = "S000001,C000001,Java程序设计,专业课\n" +
                "S000002,C000002,数据结构,专业课\n" +
                "S000001,C000003,计算机网络,公共课";
        enrollmentService.processCSVData(csvData);

        List<EnrollRecord> result = enrollmentService.search("S000001");

        assertEquals(2, result.size());
    }

    @Test
    void testSearch_ByCourseId() {
        String csvData = "S000001,C000001,Java程序设计,专业课\n" +
                "S000002,C000002,数据结构,专业课\n" +
                "S000001,C000003,计算机网络,公共课";
        enrollmentService.processCSVData(csvData);

        List<EnrollRecord> result = enrollmentService.search("C000001");

        assertEquals(1, result.size());
        assertEquals("Java程序设计", result.get(0).getCourseName());
    }

    @Test
    void testSearch_ByCourseName() {
        String csvData = "S000001,C000001,Java程序设计,专业课\n" +
                "S000002,C000002,数据结构,专业课\n" +
                "S000001,C000003,计算机网络,公共课";
        enrollmentService.processCSVData(csvData);

        List<EnrollRecord> result = enrollmentService.search("Java");

        assertEquals(1, result.size());
    }

    @Test
    void testSearch_ByCourseType() {
        String csvData = "S000001,C000001,Java程序设计,专业课\n" +
                "S000002,C000002,高等数学,公共课\n" +
                "S000001,C000003,摄影技术,选修课";
        enrollmentService.processCSVData(csvData);

        List<EnrollRecord> result = enrollmentService.search("专业课");

        assertEquals(1, result.size());
        assertEquals("Java程序设计", result.get(0).getCourseName());
    }

    @Test
    void testSearch_NoMatch() {
        String csvData = "S000001,C000001,Java程序设计,专业课";
        enrollmentService.processCSVData(csvData);

        List<EnrollRecord> result = enrollmentService.search("不存在的关键词");

        assertTrue(result.isEmpty());
    }

    @Test
    void testSearch_EmptyKeyword() {
        String csvData = "S000001,C000001,Java程序设计,专业课\n" +
                "S000002,C000002,数据结构,专业课";
        enrollmentService.processCSVData(csvData);

        List<EnrollRecord> result = enrollmentService.search("");

        assertEquals(2, result.size());
    }

    // ==================== 分类功能测试 ====================

    @Test
    void testGetClassifiedEnrollments() {
        String csvData = "S000001,C000001,Java程序设计,专业课\n" +
                "S000002,C000002,高等数学,公共课\n" +
                "S000001,C000003,摄影技术,选修课\n" +
                "S000003,C000004,数据结构,专业课";
        enrollmentService.processCSVData(csvData);

        Map<String, List<EnrollRecord>> classified = enrollmentService.getClassifiedEnrollments();

        assertEquals(3, classified.size());
        assertEquals(2, classified.get("专业课").size());
        assertEquals(1, classified.get("公共课").size());
        assertEquals(1, classified.get("选修课").size());
    }

    // ==================== 性能测试 ====================

    @Test
    void testPerformance_BatchImport500Records() {
        StringBuilder csvData = new StringBuilder();
        for (int i = 1; i <= 500; i++) {
            String studentId = String.format("S%06d", i);
            String courseId = String.format("C%06d", (i % 100) + 1);
            csvData.append(studentId).append(",").append(courseId)
                    .append(",课程").append(i).append(",专业课\n");
        }

        long startTime = System.currentTimeMillis();
        List<EnrollRecord> result = enrollmentService.processCSVData(csvData.toString());
        long endTime = System.currentTimeMillis();

        assertTrue(endTime - startTime < 1000, "导入500条记录应在1秒内完成");
        assertFalse(result.isEmpty());
    }

    @Test
    void testPerformance_Search1000Records() {
        StringBuilder csvData = new StringBuilder();
        for (int i = 1; i <= 1000; i++) {
            String studentId = String.format("S%06d", i);
            String courseId = String.format("C%06d", (i % 200) + 1);
            csvData.append(studentId).append(",").append(courseId)
                    .append(",课程").append(i).append(",专业课\n");
        }
        enrollmentService.processCSVData(csvData.toString());

        long startTime = System.currentTimeMillis();
        List<EnrollRecord> result = enrollmentService.search("S000001");
        long endTime = System.currentTimeMillis();

        assertTrue(endTime - startTime < 1000, "检索1000条记录应在1秒内完成");
        assertFalse(result.isEmpty());
    }
}
