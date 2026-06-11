package com.jsonparser.writer;

import com.jsonparser.value.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JsonWriter 직렬화 테스트")
class JsonWriterTest {

    private final JsonWriter compact = new JsonWriter(WriteOptions.COMPACT);
    private final JsonWriter pretty  = new JsonWriter(WriteOptions.PRETTY);

    // ── 기본값 직렬화 (COMPACT) ────────────────────────────────────────────

    @Test
    @DisplayName("null 직렬화")
    void writeNull() {
        assertEquals("null", compact.write(JsonNull.INSTANCE));
    }

    @Test
    @DisplayName("true 직렬화")
    void writeTrue() {
        assertEquals("true", compact.write(JsonBoolean.TRUE));
    }

    @Test
    @DisplayName("false 직렬화")
    void writeFalse() {
        assertEquals("false", compact.write(JsonBoolean.FALSE));
    }

    @Test
    @DisplayName("정수 직렬화")
    void writeInteger() {
        assertEquals("42", compact.write(new JsonNumber(42L)));
    }

    @Test
    @DisplayName("소수 직렬화")
    void writeDecimal() {
        assertEquals("3.14", compact.write(new JsonNumber(3.14)));
    }

    @Test
    @DisplayName("문자열 직렬화")
    void writeString() {
        assertEquals("\"hello\"", compact.write(new JsonString("hello")));
    }

    // ── 문자열 escape ──────────────────────────────────────────────────────

    @Test
    @DisplayName("escape: 큰따옴표")
    void writeEscapeQuote() {
        assertEquals("\"say \\\"hi\\\"\"", compact.write(new JsonString("say \"hi\"")));
    }

    @Test
    @DisplayName("escape: 백슬래시")
    void writeEscapeBackslash() {
        assertEquals("\"a\\\\b\"", compact.write(new JsonString("a\\b")));
    }

    @Test
    @DisplayName("escape: 개행 \\n")
    void writeEscapeNewline() {
        assertEquals("\"line1\\nline2\"", compact.write(new JsonString("line1\nline2")));
    }

    @Test
    @DisplayName("escape: 탭 \\t")
    void writeEscapeTab() {
        assertEquals("\"a\\tb\"", compact.write(new JsonString("a\tb")));
    }

    @Test
    @DisplayName("escape: 캐리지 리턴 \\r")
    void writeEscapeCarriageReturn() {
        assertEquals("\"a\\rb\"", compact.write(new JsonString("a\rb")));
    }

    @Test
    @DisplayName("escape: 백스페이스 \\b")
    void writeEscapeBackspace() {
        assertEquals("\"a\\bb\"", compact.write(new JsonString("a\bb")));
    }

    @Test
    @DisplayName("escape: 폼 피드 \\f")
    void writeEscapeFormFeed() {
        assertEquals("\"a\\fb\"", compact.write(new JsonString("a\fb")));
    }

    @Test
    @DisplayName("제어 문자 \\u00XX 형식")
    void writeControlChar() {
        String result = compact.write(new JsonString(""));
        assertEquals("\"\\u0001\"", result);
    }

    // ── 배열 직렬화 ───────────────────────────────────────────────────────

    @Test
    @DisplayName("빈 배열 직렬화")
    void writeEmptyArray() {
        assertEquals("[]", compact.write(new JsonArray()));
    }

    @Test
    @DisplayName("단일 요소 배열 (compact)")
    void writeSingleElementArray() {
        JsonArray a = new JsonArray();
        a.add(new JsonNumber(1L));
        assertEquals("[1]", compact.write(a));
    }

    @Test
    @DisplayName("복수 요소 배열 (compact)")
    void writeMultiElementArray() {
        JsonArray a = new JsonArray();
        a.add(new JsonNumber(1L));
        a.add(new JsonString("two"));
        a.add(JsonBoolean.TRUE);
        assertEquals("[1,\"two\",true]", compact.write(a));
    }

    @Test
    @DisplayName("배열 (pretty): 개행과 들여쓰기 포함")
    void writeArrayPretty() {
        JsonArray a = new JsonArray();
        a.add(new JsonNumber(1L));
        a.add(new JsonNumber(2L));
        String result = pretty.write(a);
        assertTrue(result.contains("\n"));
        assertTrue(result.contains("  1"));
        assertTrue(result.contains("  2"));
        assertTrue(result.startsWith("["));
        assertTrue(result.endsWith("]"));
    }

    // ── 객체 직렬화 ───────────────────────────────────────────────────────

    @Test
    @DisplayName("빈 객체 직렬화")
    void writeEmptyObject() {
        assertEquals("{}", compact.write(new JsonObject()));
    }

    @Test
    @DisplayName("단일 필드 객체 (compact)")
    void writeSingleFieldObject() {
        JsonObject obj = new JsonObject();
        obj.put("k", new JsonString("v"));
        assertEquals("{\"k\":\"v\"}", compact.write(obj));
    }

    @Test
    @DisplayName("복수 필드 객체 (compact)")
    void writeMultiFieldObject() {
        JsonObject obj = new JsonObject();
        obj.put("a", new JsonNumber(1L));
        obj.put("b", JsonBoolean.FALSE);
        assertEquals("{\"a\":1,\"b\":false}", compact.write(obj));
    }

    @Test
    @DisplayName("객체 (pretty): 개행과 들여쓰기 포함")
    void writeObjectPretty() {
        JsonObject obj = new JsonObject();
        obj.put("name", new JsonString("Alice"));
        String result = pretty.write(obj);
        assertTrue(result.contains("\n"));
        assertTrue(result.contains("\"name\": \"Alice\""));
    }

    // ── 중첩 구조 직렬화 ──────────────────────────────────────────────────

    @Test
    @DisplayName("중첩 객체 직렬화 (compact)")
    void writeNestedObject() {
        JsonObject inner = new JsonObject();
        inner.put("x", new JsonNumber(1L));
        JsonObject outer = new JsonObject();
        outer.put("inner", inner);
        assertEquals("{\"inner\":{\"x\":1}}", compact.write(outer));
    }

    @Test
    @DisplayName("배열 내 객체 직렬화 (compact)")
    void writeArrayOfObjects() {
        JsonArray arr = new JsonArray();
        JsonObject o1 = new JsonObject();
        o1.put("id", new JsonNumber(1L));
        JsonObject o2 = new JsonObject();
        o2.put("id", new JsonNumber(2L));
        arr.add(o1);
        arr.add(o2);
        assertEquals("[{\"id\":1},{\"id\":2}]", compact.write(arr));
    }

    // ── WriteOptions ──────────────────────────────────────────────────────

    @Test
    @DisplayName("기본 생성자: COMPACT 모드")
    void defaultConstructorIsCompact() {
        JsonWriter defaultWriter = new JsonWriter();
        JsonObject obj = new JsonObject();
        obj.put("a", JsonBoolean.TRUE);
        assertFalse(defaultWriter.write(obj).contains("\n"));
    }

    @Test
    @DisplayName("WriteOptions.Builder: 커스텀 탭 들여쓰기")
    void customTabIndent() {
        WriteOptions opts = WriteOptions.builder().prettyPrint(true).indent("\t").build();
        JsonObject obj = new JsonObject();
        obj.put("a", JsonBoolean.TRUE);
        String result = new JsonWriter(opts).write(obj);
        assertTrue(result.contains("\t\"a\""));
    }

    @Test
    @DisplayName("WriteOptions COMPACT: isPrettyPrint → false")
    void compactIsPrettyFalse() {
        assertFalse(WriteOptions.COMPACT.isPrettyPrint());
    }

    @Test
    @DisplayName("WriteOptions PRETTY: isPrettyPrint → true")
    void prettyIsPrettyTrue() {
        assertTrue(WriteOptions.PRETTY.isPrettyPrint());
    }

    // ── 파일/스트림 출력 ───────────────────────────────────────────────────

    @Test
    @DisplayName("writeToStream: OutputStream 에 UTF-8 출력")
    void writeToStream() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compact.writeToStream(new JsonString("test"), baos);
        assertEquals("\"test\"", baos.toString(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("writeToFile(Path): 파일에 저장 후 내용 확인")
    void writeToFilePath() throws Exception {
        Path tmp = Files.createTempFile("json-writer-test", ".json");
        try {
            JsonObject obj = new JsonObject();
            obj.put("ok", JsonBoolean.TRUE);
            compact.writeToFile(obj, tmp);
            String content = Files.readString(tmp, StandardCharsets.UTF_8);
            assertEquals("{\"ok\":true}", content);
        } finally {
            Files.deleteIfExists(tmp);
        }
    }
}
