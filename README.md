研发工程师Vibe Coding

## 高校选课管理系统 - 学生选课基础处理工具
### 功能需求
1. 接收学生选课信息列表，实现去重、排序、输出三大核心功能：
2. 去重规则：学生 ID + 课程 ID 完全一致视为重复记录，直接移除（与课程名称无关）；
3. 排序规则：先按学生 ID 升序，学生 ID 相同时按课程 ID 升序；
4. 输出要求：返回处理后的列表，同时逐行打印格式化信息：学生ID：XXX，课程ID：XXX，课程名称：XXX。
#### 选课记录实体类（Java）

```java
/**
 * 选课记录实体类
 */
class EnrollRecord {
    /**
     * 学生ID，格式：S+6位数字
     */
    private String studentId;

    /**
     * 课程ID，格式：C+6位数字
     */
    private String courseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 全参构造器
     */
    public EnrollRecord(String studentId, String courseId, String courseName) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.courseName = courseName;
    }

    // getter 和 setter 方法
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    /**
     * 重写toString方法，满足输出格式要求
     */
    @Override
    public String toString() {
        return String.format("学生ID：%s，课程ID：%s，课程名称：%s", studentId, courseId, courseName);
    }
}
```

## SQL 编程题目
基于选课管理系统的选课记录表（enrollments） 和课程表（courses），表结构如下：
表名 字段名 数据类型 说明

enrollments 表 字段如下

student_id VARCHAR(20) 学生ID（主键部分）
course_id VARCHAR(20) 课程ID（主键部分）
enroll_time DATETIME 选课时间

courses 表 字段如下
course_id VARCHAR(20) 课程ID（主键）
course_name VARCHAR(50) 课程名称
course_type VARCHAR(20) 课程类型（公共课/专业课）
capacity INT 课程容量

题目要求
编写2道SQL语句，基于上述表结构实现统计需求。
1. 题目1 ：统计每门课程的选课人数，返回课程ID、课程名称、选课人数（别名：enroll_count），结果按选课人数降序排序。
2. 题目2：统计选课人数超过50人的专业课，返回课程ID、课程名称、选课人数，结果按选课人数升序排序。

## 编程实战
基于第一题的“学生选课基础处理工具”，使用AI编程工具（如ChatGPT、Claude、Copilot），采用SpringBoot 3.x框架完成功能升级，同时设计简单页面用于CSV数据批量导入及数据展示，适配选课管理系统的实际使用需求，具体要求如下：
1. 后端功能升级：在第一题去重、排序的基础上，增加“选课分类”功能（按课程类型：公共课、专业课、选修课分类存储，支持手动标注或自动识别）；增加“选课检索”功能（支持按学生ID、课程ID、课程名称、课程类型四种关键词检索，检索不到提示“无匹配选课记录”）；优化性能（1000条以上记录检索/排序响应≤1秒，支持单次≥500条批量导入）。
2. 页面设计要求：设计1个简单的前端页面，包含两个核心功能——① 数据批量导入：提供文本框，支持用户输入多条CSV格式的选课数据（CSV格式示例：S000001,C000001,Java程序设计,专业课；S000002,C000003,计算机网络,公共课，每条数据一行），点击导入按钮提交数据；② 数据展示：展示导入的选课数据（经后端处理后）或后台写死的样例选课数据（无需复杂样式，清晰展示即可，可结合分类功能按课程类型展示）。
3. 前后端衔接：页面上传的数据需提交至SpringBoot后端，经去重、排序、分类处理后，再回显至页面展示；后台写死的样例数据，可直接在页面加载时展示。
4. 分层设计：严格遵循Controller → Service → 实体层架构，禁止业务逻辑写在Controller中；前端页面可使用HTML原生开发或结合Thymeleaf（SpringBoot常用模板引擎），无需引入复杂前端框架（贴合3年以下经验、应届生/实习生水平）。

要求
• 明确写出所用AI编程工具名称，以及给AI的完整提示词（需体现SpringBoot 3.x版本、分层要求、后端功能需求、页面设计需求——仅文本框输入CSV批量导入、前后端衔接要求、性能要求）；
• 复制AI生成的完整代码：包含SpringBoot后端（Controller、Service、实体类等，遵循分层设计）、前端页面代码（HTML+CSS+JavaScript，无需复杂样式，简洁可用），标注哪些部分是AI生成、哪些部分是自己修改优化的（需说明修改原因，如适配选课场景、优化页面交互、完善前后端衔接等）；

## 分析及设计

基于前两题的功能，简化设计高校选课管理系统的核心数据架构，无需完整系统设计，仅聚焦以下3点：
1. 核心数据模型：补充教师表（teachers），完善学生表、课程表、选课记录表的字段，说明表间关联关系，不局限于以上实体；能给出ER图。
2. 并发风险：分析选课高峰期的核心并发问题，给出1个简单可行的解决方案；
3. 索引设计：针对选课记录表、课程表，设计合理的数据库索引，说明索引类型及设计理由。