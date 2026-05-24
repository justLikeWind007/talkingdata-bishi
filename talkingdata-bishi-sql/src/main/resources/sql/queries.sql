-- ============================================
-- 题目二：SQL 编程题 - 查询语句
-- ============================================

-- ============================================
-- 题目1：统计每门课程的选课人数
--
-- 需求：返回课程ID、课程名称、选课人数（别名：enroll_count）
--      结果按选课人数降序排序
--
-- 逻辑说明：
--   - 使用 LEFT JOIN 确保所有课程都能展示（包括没人选的课）
--   - COUNT(e.student_id) 统计每个课程的学生数量（忽略NULL）
--   - GROUP BY 按课程分组
--   - ORDER BY enroll_count DESC 按选课人数降序
-- ============================================

SELECT
    c.course_id AS course_id,
    c.course_name AS course_name,
    COUNT(e.student_id) AS enroll_count
FROM courses c
LEFT JOIN enrollments e ON c.course_id = e.course_id
GROUP BY c.course_id, c.course_name
ORDER BY enroll_count DESC;

-- ============================================
-- 题目2：统计选课人数超过50人的专业课
--
-- 需求：返回课程ID、课程名称、选课人数
--      结果按选课人数升序排序
--
-- 逻辑说明：
--   - 使用 INNER JOIN 只保留有选课记录的课程
--   - WHERE c.course_type = '专业课' 筛选专业课
--   - HAVING COUNT(e.student_id) > 50 筛选选课人数超过50的
--   - ORDER BY enroll_count ASC 按选课人数升序
-- ============================================

SELECT
    c.course_id AS course_id,
    c.course_name AS course_name,
    COUNT(e.student_id) AS enroll_count
FROM courses c
INNER JOIN enrollments e ON c.course_id = e.course_id
WHERE c.course_type = '专业课'
GROUP BY c.course_id, c.course_name
HAVING COUNT(e.student_id) > 50
ORDER BY enroll_count ASC;
