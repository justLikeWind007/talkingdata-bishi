package com.talkingdata.studentcourse.web;

import com.talkingdata.studentcourse.web.service.EnrollmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EnrollmentServiceUnitTest {

    @Autowired
    private EnrollmentService enrollmentService;

    @Test
    void testCSVParse_Basic() {
        String csv = "S000001,C000001,Java程序设计,专业课";
        enrollmentService.processCSVData(csv);
        assertEquals(1, enrollmentService.getAllEnrollments().size());
    }

    @Test
    void testCSVParse_MultipleLines() {
        String csv = "S000001,C000001,Java程序设计,专业课\n" +
                     "S000002,C000002,数据结构,公共课\n" +
                     "S000003,C000003,计算机网络,选修课";
        enrollmentService.processCSVData(csv);
        assertEquals(3, enrollmentService.getAllEnrollments().size());
    }

    @Test
    void testCSVParse_EmptyLines() {
        String csv = "S000001,C000001,Java程序设计,专业课\n\n\nS000002,C000002,数据结构,公共课";
        enrollmentService.processCSVData(csv);
        assertEquals(2, enrollmentService.getAllEnrollments().size());
    }

    @Test
    void testCSVParse_TrimWhitespace() {
        String csv = "  S000001  ,  C000001  ,  Java程序设计  ,  专业课  ";
        enrollmentService.processCSVData(csv);
        assertEquals("S000001", enrollmentService.getAllEnrollments().get(0).getStudentId());
    }

    @Test
    void testDeduplication_StudentIdAndCourseIdBothSame() {
        String csv = "S000001,C000001,Java程序设计,专业课\n" +
                     "S000001,C000001,Java程序设计（重修）,专业课";
        enrollmentService.processCSVData(csv);
        assertEquals(1, enrollmentService.getAllEnrollments().size());
    }

    @Test
    void testDeduplication_DifferentStudentSameCourse() {
        String csv = "S000001,C000001,Java程序设计,专业课\n" +
                     "S000002,C000001,计算机网络,公共课";
        enrollmentService.processCSVData(csv);
        assertEquals(2, enrollmentService.getAllEnrollments().size());
    }

    @Test
    void testDeduplication_DifferentCourseSameStudent() {
        String csv = "S000001,C000001,Java程序设计,专业课\n" +
                     "S000001,C000002,数据结构,公共课";
        enrollmentService.processCSVData(csv);
        assertEquals(2, enrollmentService.getAllEnrollments().size());
    }

    @Test
    void testSorting_ByStudentIdAscending() {
        String csv = "S000003,C000001,课程1,公共课\n" +
                     "S000001,C000001,课程2,公共课\n" +
                     "S000002,C000001,课程3,公共课";
        enrollmentService.processCSVData(csv);
        assertEquals("S000001", enrollmentService.getAllEnrollments().get(0).getStudentId());
        assertEquals("S000002", enrollmentService.getAllEnrollments().get(1).getStudentId());
        assertEquals("S000003", enrollmentService.getAllEnrollments().get(2).getStudentId());
    }

    @Test
    void testSorting_ByCourseIdWhenStudentIdSame() {
        String csv = "S000001,C000003,课程1,公共课\n" +
                     "S000001,C000001,课程2,公共课\n" +
                     "S000001,C000002,课程3,公共课";
        enrollmentService.processCSVData(csv);
        assertEquals("C000001", enrollmentService.getAllEnrollments().get(0).getCourseId());
        assertEquals("C000002", enrollmentService.getAllEnrollments().get(1).getCourseId());
        assertEquals("C000003", enrollmentService.getAllEnrollments().get(2).getCourseId());
    }

    @Test
    void testSearch_CaseInsensitive() {
        String csv = "S000001,C000001,JAVA程序设计,专业课";
        enrollmentService.processCSVData(csv);
        assertEquals(1, enrollmentService.search("java").size());
        assertEquals(1, enrollmentService.search("JAVA").size());
        assertEquals(1, enrollmentService.search("Java").size());
    }

    @Test
    void testSearch_PartialMatch() {
        String csv = "S000001,C000001,Java程序设计,专业课\n" +
                     "S000002,C000002,JavaWeb开发,专业课";
        enrollmentService.processCSVData(csv);
        assertEquals(2, enrollmentService.search("Java").size());
    }

    @Test
    void testSearch_FuzzyMatch() {
        String csv = "S000001,C000001,Java程序设计,专业课\n" +
                     "S000002,C000002,数据结构,专业课";
        enrollmentService.processCSVData(csv);
        assertEquals(1, enrollmentService.search("S000001").size());
        assertEquals(1, enrollmentService.search("C000002").size());
    }

    @Test
    void testSearch_NullKeyword() {
        String csv = "S000001,C000001,Java程序设计,专业课";
        enrollmentService.processCSVData(csv);
        assertEquals(1, enrollmentService.search(null).size());
    }

    @Test
    void testClassification_ThreeTypes() {
        String csv = "S000001,C000001,课程1,公共课\n" +
                     "S000002,C000002,课程2,专业课\n" +
                     "S000003,C000003,课程3,选修课";
        enrollmentService.processCSVData(csv);
        assertEquals(3, enrollmentService.getClassifiedEnrollments().size());
    }

    @Test
    void testClassification_AutoDetectedType() {
        String csv = "S000001,C000001,Java程序设计";
        enrollmentService.processCSVData(csv);
        assertEquals("专业课", enrollmentService.getClassifiedEnrollments().get("专业课").get(0).getCourseType());
    }

    @Test
    void testImportSummary_WithInvalidRows() {
        String csv = "S000001,C000001,Java程序设计,专业课\n" +
                "invalid_line\n" +
                ",C000003,课程名缺失学生\n" +
                "S000002,C000002,数据结构,专业课";
        EnrollmentService.ImportResult result = enrollmentService.importCSVData(csv);

        assertEquals(4, result.totalLineCount());
        assertEquals(2, result.validRecordCount());
        assertEquals(2, result.invalidRecordCount());
        assertEquals(2, result.records().size());
    }

    @Test
    void testRuntimeStats_ShouldBeUpdatedAfterImportAndSearch() {
        enrollmentService.importCSVData("S000001,C000001,Java程序设计,专业课");
        enrollmentService.search("S000001");

        EnrollmentService.RuntimeStats stats = enrollmentService.getRuntimeStats();
        assertTrue(stats.importCount() >= 1);
        assertTrue(stats.searchCount() >= 1);
        assertTrue(stats.currentRecordCount() >= 1);
        assertEquals("S000001", stats.lastSearchKeyword());
    }
}
