param(
    [string]$MavenPath = "C:\Users\23655\Desktop\aiEngineer\mvn\apache-maven-3.9.12\bin\mvn.cmd"
)

$ErrorActionPreference = "Stop"

if (-not (Test-Path $MavenPath)) {
    throw "指定的 Maven 不存在: $MavenPath"
}

Write-Host "==> 使用 Maven: $MavenPath"
Write-Host "==> 打包 Web 模块（含依赖模块）"
& $MavenPath -q -pl talkingdata-bishi-web -am package

$jarPath = "talkingdata-bishi-web\target\talkingdata-bishi-web-1.0.0.jar"
if (-not (Test-Path $jarPath)) {
    throw "未找到可执行 JAR: $jarPath"
}

Write-Host "==> 启动应用: $jarPath"
java -jar $jarPath
