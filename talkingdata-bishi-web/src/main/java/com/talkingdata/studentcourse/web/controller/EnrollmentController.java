package com.talkingdata.studentcourse.web.controller;

import com.talkingdata.studentcourse.common.entity.EnrollRecord;
import com.talkingdata.studentcourse.web.service.EnrollmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/enrollment")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @GetMapping
    public String index(Model model) {
        List<EnrollRecord> enrollments = enrollmentService.getAllEnrollments();
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("classified", enrollmentService.classify(enrollments));
        return "enrollment";
    }

    @PostMapping("/import")
    public String importCSV(@RequestParam("csvData") String csvData, Model model) {
        EnrollmentService.ImportResult importResult = enrollmentService.importCSVData(csvData);
        List<EnrollRecord> processed = importResult.records();
        model.addAttribute("enrollments", processed);
        model.addAttribute("classified", enrollmentService.classify(processed));
        model.addAttribute("message", String.format(
                "导入完成：输入 %d 行，有效 %d 行，无效 %d 行，去重后 %d 行（去重移除 %d 行）",
                importResult.totalLineCount(),
                importResult.validRecordCount(),
                importResult.invalidRecordCount(),
                processed.size(),
                importResult.duplicatesRemoved()
        ));
        return "enrollment";
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        List<EnrollRecord> results = enrollmentService.search(keyword);
        if (results.isEmpty()) {
            model.addAttribute("message", "无匹配选课记录");
        }
        model.addAttribute("enrollments", results);
        model.addAttribute("classified", enrollmentService.classify(results));
        model.addAttribute("keyword", keyword);
        return "enrollment";
    }

    @GetMapping("/ops/stats")
    @ResponseBody
    public EnrollmentService.RuntimeStats stats() {
        return enrollmentService.getRuntimeStats();
    }
}
