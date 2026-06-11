# JUnit 5 독립 실행형 JAR 다운로드 스크립트
$version = "1.11.4"
$jar     = "junit-platform-console-standalone-$version.jar"
$url     = "https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/$version/$jar"
$dest    = Join-Path $PSScriptRoot "lib\$jar"

if (Test-Path $dest) {
    Write-Host "[OK] 이미 존재합니다: $dest" -ForegroundColor Green
    exit 0
}

New-Item -ItemType Directory -Force -Path (Join-Path $PSScriptRoot "lib") | Out-Null
Write-Host "다운로드 중: $url"
Invoke-WebRequest -Uri $url -OutFile $dest -UseBasicParsing
Write-Host "[완료] $dest" -ForegroundColor Green
