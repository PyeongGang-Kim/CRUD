# 상품 관리 CRUD 콘솔 애플리케이션

직접 구현한 JSON 파서 라이브러리를 사용하여 데이터를 JSON 파일로 관리하는 콘솔 기반 CRUD 애플리케이션입니다.

---

## 프로젝트 구조

```
CRUD/
├── src/
│   └── com/
│       ├── jsonparser/                  # JSON 파서 라이브러리
│       │   ├── Json.java                # 라이브러리 진입점 (파싱/직렬화/파일 I/O)
│       │   ├── exception/
│       │   │   ├── JsonParseException.java
│       │   │   └── JsonWriteException.java
│       │   ├── lexer/
│       │   │   ├── Lexer.java           # 토크나이저
│       │   │   ├── Token.java
│       │   │   └── TokenType.java
│       │   ├── parser/
│       │   │   └── JsonParser.java      # 재귀 하강 파서
│       │   ├── value/
│       │   │   ├── JsonValue.java       # sealed interface
│       │   │   ├── JsonObject.java
│       │   │   ├── JsonArray.java
│       │   │   ├── JsonString.java
│       │   │   ├── JsonNumber.java
│       │   │   ├── JsonBoolean.java
│       │   │   └── JsonNull.java
│       │   └── writer/
│       │       ├── JsonWriter.java      # JSON 직렬화기
│       │       └── WriteOptions.java    # 출력 옵션 (COMPACT / PRETTY)
│       └── crud/                        # CRUD 애플리케이션
│           ├── Main.java                # 진입점
│           ├── model/
│           │   └── Product.java         # 상품 모델 (id, name, price, quantity)
│           ├── repository/
│           │   └── ProductRepository.java  # JSON 파일 I/O + CRUD 로직
│           └── app/
│               └── ConsoleApp.java      # 콘솔 UI
├── data/
│   └── products.json                    # 데이터 저장 파일 (자동 생성)
├── compile.bat                          # 컴파일 스크립트
└── run.bat                              # 실행 스크립트
```

---

## 요구 환경

| 항목 | 버전 |
|------|------|
| Java | 17 이상 |
| OS   | Windows (compile.bat / run.bat 기준) |

---

## 빌드 및 실행

```bat
# 1. 컴파일
compile.bat

# 2. 실행
run.bat
```

또는 직접 명령어로 실행:

```bat
javac -encoding UTF-8 -d out src\com\jsonparser\**\*.java src\com\jsonparser\*.java src\com\crud\**\*.java src\com\crud\*.java
java -cp out com.crud.Main
```

---

## CRUD 기능

### 1. Create — 상품 추가
상품명, 가격, 수량을 입력하면 자동으로 ID가 부여되어 `data/products.json`에 저장됩니다.

```
  상품명 > 노트북
  가격(원) > 1200000
  수량 > 5
  ──────────────────────────────────────────────────
  상품이 추가되었습니다.
  ID       : 1
  상품명   : 노트북
  가격     : 1,200,000 원
  수량     : 5 개
```

### 2. Read — 상품 조회

**전체 목록 조회**
```
  ID     상품명                     가격(원)     수량
  ────────────────────────────────────────────────
  1      노트북                    1,200,000        5
  2      마우스                       35,000       10
  총 2개
```

**ID로 검색**
```
  조회할 상품 ID > 1
  ID       : 1
  상품명   : 노트북
  가격     : 1,200,000 원
  수량     : 5 개
```

**상품명 키워드로 검색** (부분 일치, 대소문자 무시)
```
  검색할 상품명 (부분 일치) > 노트
  [ 상품명 검색 결과: "노트" ]
  ID     상품명                     가격(원)     수량
  ────────────────────────────────────────────────
  1      노트북                    1,200,000        5
  1건 검색됨
```

### 3. Update — 상품 수정
수정할 ID를 선택하고 필드별로 새 값을 입력합니다. **Enter 입력 시 기존 값이 유지**됩니다.

```
  수정할 상품 ID > 1
  현재 정보:
  ID       : 1
  상품명   : 노트북
  가격     : 1,200,000 원
  수량     : 5 개
  (변경하지 않으려면 Enter 를 누르세요)

  새 상품명 [노트북] >           ← Enter → 기존값 유지
  새 가격(원) [1200000] > 990000
  새 수량 [5] > 3
```

### 4. Delete — 상품 삭제
삭제 전 상세 정보를 확인하고 `y` 입력 시에만 삭제됩니다.

```
  삭제할 상품 ID > 1
  ID       : 1
  상품명   : 노트북
  가격     : 990,000 원
  수량     : 3 개
  정말 삭제하시겠습니까? (y/N) > y
  상품이 삭제되었습니다.
```

---

## 데이터 파일 형식

`data/products.json`에 Pretty Print 형식으로 저장됩니다.

```json
[
  {
    "id": 1,
    "name": "노트북",
    "price": 990000.0,
    "quantity": 3
  },
  {
    "id": 2,
    "name": "마우스",
    "price": 35000.0,
    "quantity": 10
  }
]
```

---

## JSON 파서 라이브러리 구조

본 프로젝트의 JSON 파서는 외부 라이브러리 없이 직접 구현되었습니다.

```
문자열 입력
    │
    ▼
 Lexer          문자열 → Token 목록으로 분리
    │
    ▼
 JsonParser     Token 목록 → JsonValue 트리로 변환 (재귀 하강 파싱)
    │
    ▼
 JsonValue      JsonObject / JsonArray / JsonString / JsonNumber / JsonBoolean / JsonNull
    │
    ▼
 JsonWriter     JsonValue 트리 → JSON 문자열 직렬화 (COMPACT / PRETTY)
```

**라이브러리 사용 예시:**
```java
// 파싱
JsonValue v = Json.parse("{\"name\":\"Alice\",\"age\":30}");
String name = v.asObject().get("name").asString().getValue(); // "Alice"

// 생성
JsonObject obj = Json.object();
obj.put("name", Json.of("Bob"));
obj.put("age",  Json.of(25L));

// 직렬화
String compact = Json.stringify(obj);   // {"name":"Bob","age":25}
String pretty  = Json.prettify(obj);    // 들여쓰기 포함

// 파일 저장 / 로드
Json.saveToFile(obj, Path.of("out.json"));
JsonValue loaded = Json.parseFile(Path.of("out.json"));
```

---

## 참조

- JSON 파서 라이브러리 원본 저장소: [JsonParser](https://github.com/PyeongGang-Kim/JsonParser)
