@echo off
setlocal

set VERSION=1.11.4
set JUNIT_JAR=lib\junit-platform-console-standalone-%VERSION%.jar

if not exist "%JUNIT_JAR%" (
    echo [오류] JUnit JAR 없음: %JUNIT_JAR%
    echo 먼저 실행하세요: powershell -File download-test-deps.ps1
    exit /b 1
)

if not exist out-test mkdir out-test

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

echo [컴파일] 테스트...
javac -encoding UTF-8 -cp "%JUNIT_JAR%;out" -d out-test ^
  test\com\jsonparser\lexer\LexerTest.java ^
  test\com\jsonparser\parser\JsonParserTest.java ^
  test\com\jsonparser\value\JsonValueTest.java ^
  test\com\jsonparser\writer\JsonWriterTest.java ^
  test\com\crud\model\ProductTest.java ^
  test\com\crud\repository\ProductRepositoryTest.java
if errorlevel 1 ( echo [실패] 테스트 컴파일 오류 & exit /b 1 )

echo [완료] 컴파일 성공
