param(
    [string]$MavenPath = "C:\Users\23655\Desktop\aiEngineer\mvn\apache-maven-3.9.12\bin\mvn.cmd"
)

$ErrorActionPreference = "Stop"

if (-not (Test-Path $MavenPath)) {
    throw "指定的 Maven 不存在: $MavenPath"
}

Write-Host "==> 使用 Maven: $MavenPath"
Write-Host "==> 执行测试"
& $MavenPath -q test

Write-Host "==> 执行打包"
& $MavenPath -q -pl talkingdata-bishi-web -am package

Write-Host "==> 验证完成"
