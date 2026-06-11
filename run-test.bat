@echo off
setlocal

set VERSION=1.11.4
set JUNIT_JAR=lib\junit-platform-console-standalone-%VERSION%.jar

if not exist "%JUNIT_JAR%" (
    echo [오류] JUnit JAR 없음: %JUNIT_JAR%
    echo 먼저 실행하세요: powershell -File download-test-deps.ps1
    exit /b 1
)

if not exist out-test (
    echo [오류] out-test 디렉토리 없음. compile-test.bat 를 먼저 실행하세요.
    exit /b 1
)

echo [실행] JUnit 테스트...
java -jar "%JUNIT_JAR%" execute --class-path "out-test;out" --scan-class-path
