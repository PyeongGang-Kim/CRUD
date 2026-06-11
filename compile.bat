@echo off
echo [컴파일 중...]
if not exist out mkdir out

javac -encoding UTF-8 -d out -sourcepath src ^
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

if %errorlevel% == 0 (
    echo [컴파일 완료]
) else (
    echo [컴파일 실패]
    exit /b 1
)
