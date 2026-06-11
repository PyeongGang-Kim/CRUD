@echo off
setlocal

set VERSION=1.11.4
set JUNIT_JAR=lib\junit-platform-console-standalone-%VERSION%.jar

if not exist "%JUNIT_JAR%" (
    echo [오류] JUnit JAR 없음: %JUNIT_JAR%
    echo 먼저 실행하세요: powershell -File download-test-deps.ps1
    exit /b 1
)

if not exist out      mkdir out
if not exist tmp\out  mkdir tmp\out

echo [컴파일] 소스...
javac -encoding UTF-8 -d out ^
  src\com\jsonparser\exception\*.java ^
  src\com\jsonparser\lexer\*.java ^
  src\com\jsonparser\parser\*.java ^
  src\com\jsonparser\value\*.java ^
  src\com\jsonparser\writer\*.java ^
  src\com\jsonparser\Json.java ^
  src\com\crud\model\*.java ^
  src\com\crud\repository\*.java ^
  src\com\crud\app\*.java ^
  src\com\crud\Main.java
if errorlevel 1 ( echo [실패] 소스 컴파일 오류 & exit /b 1 )

echo [컴파일] Safety 테스트...
javac -encoding UTF-8 -cp "%JUNIT_JAR%;out" -d tmp\out ^
  tmp\test\com\jsonparser\lexer\LexerSafetyTest.java ^
  tmp\test\com\jsonparser\parser\JsonParserSafetyTest.java ^
  tmp\test\com\jsonparser\writer\JsonWriterSafetyTest.java ^
  tmp\test\com\crud\repository\ProductRepositorySafetyTest.java
if errorlevel 1 ( echo [실패] Safety 테스트 컴파일 오류 & exit /b 1 )

echo [완료] Safety 테스트 컴파일 성공
