# talkingdata-bishi

高校选课管理系统笔试实现。

## 1. 第一性原理：怎样提高通过率

面试官最关心三件事：

1. 能不能快速跑起来（低摩擦）  
2. 需求是否完整覆盖（高匹配）  
3. 你是否具备工程意识（可维护、可观测、可说明）

本仓库的改造目标就是围绕这三点。

## 2. 为什么保留多模块

本题天然是三个子题，保留多模块更利于“题目-实现”一一对应：

- `talkingdata-bishi-common`：题目一（实体、去重/排序工具、基础测试）
- `talkingdata-bishi-sql`：题目二（SQL 查询与建表样例）
- `talkingdata-bishi-web`：题目三（Spring Boot 3.x、CSV 导入、分类、检索、页面）

如果做成单模块，短期看目录更少；但会弱化“按题组织”的表达力。  
对笔试场景，**清晰映射题号** 比“极致扁平”更重要。

## 3. 指定 Maven 与一键验证

指定 Maven 路径：

`C:\Users\23655\Desktop\aiEngineer\mvn\apache-maven-3.9.12\bin\mvn.cmd`

### 3.1 快速验证（推荐）

```powershell
.\scripts\verify.ps1
```

### 3.2 手动执行

```powershell
& "C:\Users\23655\Desktop\aiEngineer\mvn\apache-maven-3.9.12\bin\mvn.cmd" test
& "C:\Users\23655\Desktop\aiEngineer\mvn\apache-maven-3.9.12\bin\mvn.cmd" -pl talkingdata-bishi-web -am package
java -jar .\talkingdata-bishi-web\target\talkingdata-bishi-web-1.0.0.jar
```

页面地址：`http://localhost:8080/enrollment`

## 4. 可观测性（面试加分点）

### 4.1 业务可观测

- 导入时记录：输入总行、有效行、无效行、去重后行数、耗时
- 检索时记录：关键词、匹配条数、耗时
- 运行态统计端点：`GET /enrollment/ops/stats`

### 4.2 系统可观测（Actuator）

- `GET /actuator/health`
- `GET /actuator/info`
- `GET /actuator/metrics`

## 5. 你在二次开发中做了什么

1. 修复页面模板表达式问题，避免分类标题渲染异常。  
2. 重构 Service：新增导入统计结果对象，Controller 仅负责装配（更贴近分层规范）。  
3. 分类展示逻辑修正为“跟随当前展示结果”，避免检索后分类与列表不一致。  
4. 增加运行态观测能力（业务统计 + Actuator）。  
5. 增加脚本化验证入口（`scripts/verify.ps1`、`scripts/run-web.ps1`）。  
6. 清理仓库工程噪音（`.gitignore`，忽略 `target` 和 IDE 文件）。

## 6. 交付文档

- 详细提交稿：`SUBMISSION.md`
- 精简交付说明：`DELIVERABLE.md`
- 原题目：`task.md`
