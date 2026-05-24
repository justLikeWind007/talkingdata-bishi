package com.talkingdata.studentcourse.common;

import com.talkingdata.studentcourse.common.entity.EnrollRecord;
import com.talkingdata.studentcourse.common.util.EnrollmentProcessor;

import java.util.Arrays;
import java.util.List;

public class EnrollmentProcessorDemo {
    public static void main(String[] args) {
        System.out.println("========== 用例1：基本去重 ==========");
        List<EnrollRecord> input1 = Arrays.asList(
                new EnrollRecord("S000001", "C000001", "Java程序设计"),
                new EnrollRecord("S000001", "C000001", "Java程序设计（重修）"),
                new EnrollRecord("S000002", "C000001", "计算机网络")
        );
        EnrollmentProcessor.processAndPrint(input1);

        System.out.println("\n========== 用例2：同学生不同课程 ==========");
        List<EnrollRecord> input2 = Arrays.asList(
                new EnrollRecord("S000001", "C000001", "Java程序设计"),
                new EnrollRecord("S000001", "C000002", "计算机网络"),
                new EnrollRecord("S000001", "C000003", "数据结构")
        );
        EnrollmentProcessor.processAndPrint(input2);

        System.out.println("\n========== 用例3：排序验证 ==========");
        List<EnrollRecord> input3 = Arrays.asList(
                new EnrollRecord("S000003", "C000001", "高等数学"),
                new EnrollRecord("S000001", "C000001", "大学英语"),
                new EnrollRecord("S000002", "C000001", "大学物理")
        );
        EnrollmentProcessor.processAndPrint(input3);

        System.out.println("\n========== 用例4：studentId相同，courseId不同 ==========");
        List<EnrollRecord> input4 = Arrays.asList(
                new EnrollRecord("S000001", "C000003", "数据结构"),
                new EnrollRecord("S000001", "C000001", "Java程序设计"),
                new EnrollRecord("S000001", "C000002", "计算机网络")
        );
        EnrollmentProcessor.processAndPrint(input4);
    }
}