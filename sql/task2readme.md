# 题目二：SQL 编程题 - 需求文档与处理逻辑

## 需求说明

基于选课管理系统的选课记录表（enrollments）和课程表（courses），编写 2 道 SQL 统计查询。

---

## 数据库表结构

### enrollments 表（选课记录表）

| 字段名 | 数据类型 | 说明 |
|--------|----------|------|
| student_id | VARCHAR(20) | 学生ID（联合主键部分） |
| course_id | VARCHAR(20) | 课程ID（联合主键部分） |
| enroll_time | DATETIME | 选课时间 |

**主键**：PRIMARY KEY (student_id, course_id)

### courses 表（课程表）

| 字段名 | 数据类型 | 说明 |
|--------|----------|------|
| course_id | VARCHAR(20) | 课程ID（主键） |
| course_name | VARCHAR(50) | 课程名称 |
| course_type | VARCHAR(20) | 课程类型（公共课/专业课） |
| capacity | INT | 课程容量 |

---

## 题目1：统计每门课程的选课人数

### 需求
统计每门课程的选课人数，返回课程ID、课程名称、选课人数（别名：enroll_count），结果按选课人数降序排序。

### 处理逻辑
```
1. 从 courses 表作为主表（LEFT JOIN 保证所有课程都展示）
2. 与 enrollments 表通过 course_id 关联
3. 使用 COUNT() 统计每个课程的学生数量
4. 按 enroll_count 降序排列
```

### SQL
```sql
SELECT
    c.course_id AS course_id,
    c.course_name AS course_name,
    COUNT(e.student_id) AS enroll_count
FROM courses c
LEFT JOIN enrollments e ON c.course_id = e.course_id
GROUP BY c.course_id, c.course_name
ORDER BY enroll_count DESC;
```

---

## 题目2：统计选课人数超过50人的专业课

### 需求
统计选课人数超过50人的专业课，返回课程ID、课程名称、选课人数，结果按选课人数升序排序。

### 处理逻辑
```
1. 从 courses 表筛选 course_type = '专业课'
2. 与 enrollments 表通过 course_id 关联（INNER JOIN 只取有选课的课程）
3. 使用 COUNT() 统计每个课程的学生数量
4. 使用 HAVING 筛选人数 > 50
5. 按 enroll_count 升序排列
```

### SQL
```sql
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
```

---

## 关键知识点

| 知识点 | 说明 |
|--------|------|
| LEFT JOIN vs INNER JOIN | 题目1需展示所有课程用LEFT JOIN；题目2只关心有选课的课程用INNER JOIN |
| GROUP BY | 聚合统计必须使用GROUP BY |
| HAVING vs WHERE | WHERE筛选分组前的数据，HAVING筛选分组后的数据 |
| COUNT(col) vs COUNT(*) | COUNT(col)忽略NULL值，COUNT(*)计算所有行 |
| ORDER BY ASC/DESC | ASC升序，DESC降序 |

---

## 测试数据说明

测试数据需覆盖以下场景：
- 选课人数为0的课程（验证LEFT JOIN）
- 选课人数小于50的专业课（验证HAVING筛选）
- 选课人数大于50的专业课（验证题目2能筛选出来）
- 不同课程类型（公共课、专业课、选修课）
- 同一学生选修多门课程
- 同一课程被多个学生选修
