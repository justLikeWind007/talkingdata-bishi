# 高校选课管理系统交付说明（精简）

详细提交稿见 [SUBMISSION.md](/C:/Users/23655/Desktop/talkingdata-bishi/SUBMISSION.md)。

## 项目结构

- `talkingdata-bishi-common`：题目一（实体、去重排序工具、测试）
- `talkingdata-bishi-sql`：题目二（schema 与查询 SQL）
- `talkingdata-bishi-web`：题目三（Spring Boot 3.x 分层、CSV 导入、检索、分类、页面展示）

## 运行命令（按指定 Maven）

```powershell
& "C:\Users\23655\Desktop\aiEngineer\mvn\apache-maven-3.9.12\bin\mvn.cmd" test
& "C:\Users\23655\Desktop\aiEngineer\mvn\apache-maven-3.9.12\bin\mvn.cmd" -pl talkingdata-bishi-web -am package
java -jar .\talkingdata-bishi-web\target\talkingdata-bishi-web-1.0.0.jar
```

页面访问：`http://localhost:8080/enrollment`
