package com.talkingdata.studentcourse.web.controller;

import com.talkingdata.studentcourse.common.entity.EnrollRecord;
import com.talkingdata.studentcourse.web.service.EnrollmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/enrollment")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("enrollments", enrollmentService.getAllEnrollments());
        model.addAttribute("classified", enrollmentService.getClassifiedEnrollments());
        return "enrollment";
    }

    @PostMapping("/import")
    public String importCSV(@RequestParam("csvData") String csvData, Model model) {
        List<EnrollRecord> processed = enrollmentService.processCSVData(csvData);
        model.addAttribute("enrollments", processed);
        model.addAttribute("classified", enrollmentService.getClassifiedEnrollments());
        model.addAttribute("message", "导入成功，共 " + processed.size() + " 条记录");
        return "enrollment";
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        List<EnrollRecord> results = enrollmentService.search(keyword);
        if (results.isEmpty()) {
            model.addAttribute("message", "无匹配选课记录");
        }
        model.addAttribute("enrollments", results);
        model.addAttribute("classified", enrollmentService.getClassifiedEnrollments());
        model.addAttribute("keyword", keyword);
        return "enrollment";
    }
}
