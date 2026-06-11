package com.jsonparser.parser;

import com.jsonparser.exception.JsonParseException;
import com.jsonparser.value.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JsonParser 파싱 테스트")
class JsonParserTest {

    private JsonValue parse(String json) {
        return new JsonParser().parse(json);
    }

    // ── 기본 값 타입 ──────────────────────────────────────────────────────

    @Test
    @DisplayName("null 파싱")
    void parseNull() {
        JsonValue v = parse("null");
        assertTrue(v.isNull());
        assertSame(JsonNull.INSTANCE, v);
    }

    @Test
    @DisplayName("true 파싱")
    void parseTrue() {
        JsonValue v = parse("true");
        assertTrue(v.isBoolean());
        assertTrue(v.asBoolean().getValue());
    }

    @Test
    @DisplayName("false 파싱")
    void parseFalse() {
        JsonValue v = parse("false");
        assertTrue(v.isBoolean());
        assertFalse(v.asBoolean().getValue());
    }

    @Test
    @DisplayName("정수 파싱")
    void parseInteger() {
        assertEquals(42, parse("42").asNumber().intValue());
    }

    @Test
    @DisplayName("음수 파싱")
    void parseNegative() {
        assertEquals(-7, parse("-7").asNumber().intValue());
    }

    @Test
    @DisplayName("소수 파싱")
    void parseDecimal() {
        assertEquals(3.14, parse("3.14").asNumber().doubleValue(), 1e-10);
    }

    @Test
    @DisplayName("지수 표기 숫자 파싱")
    void parseExponent() {
        assertEquals(1000.0, parse("1e3").asNumber().doubleValue(), 1e-10);
    }

    @Test
    @DisplayName("문자열 파싱")
    void parseString() {
        assertEquals("hello", parse("\"hello\"").asString().getValue());
    }

    @Test
    @DisplayName("빈 문자열 파싱")
    void parseEmptyString() {
        assertEquals("", parse("\"\"").asString().getValue());
    }

    @Test
    @DisplayName("escape 포함 문자열 파싱")
    void parseEscapedString() {
        assertEquals("line1\nline2", parse("\"line1\\nline2\"").asString().getValue());
    }

    // ── 배열 ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("빈 배열 파싱")
    void parseEmptyArray() {
        JsonArray arr = parse("[]").asArray();
        assertTrue(arr.isEmpty());
        assertEquals(0, arr.size());
    }

    @Test
    @DisplayName("단일 요소 배열 파싱")
    void parseSingleElementArray() {
        JsonArray arr = parse("[1]").asArray();
        assertEquals(1, arr.size());
        assertEquals(1, arr.get(0).asNumber().intValue());
    }

    @Test
    @DisplayName("혼합 타입 배열 파싱")
    void parseMixedArray() {
        JsonArray arr = parse("[1, \"two\", true, null, false]").asArray();
        assertEquals(5, arr.size());
        assertEquals(1,     arr.get(0).asNumber().intValue());
        assertEquals("two", arr.get(1).asString().getValue());
        assertTrue(arr.get(2).asBoolean().getValue());
        assertTrue(arr.get(3).isNull());
        assertFalse(arr.get(4).asBoolean().getValue());
    }

    @Test
    @DisplayName("배열 트레일링 쉼표 → JsonParseException")
    void arrayTrailingComma() {
        assertThrows(JsonParseException.class, () -> parse("[1,]"));
    }

    @Test
    @DisplayName("배열 쉼표 누락 → JsonParseException")
    void arrayMissingComma() {
        assertThrows(JsonParseException.class, () -> parse("[1 2]"));
    }

    // ── 객체 ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("빈 객체 파싱")
    void parseEmptyObject() {
        JsonObject obj = parse("{}").asObject();
        assertTrue(obj.isEmpty());
    }

    @Test
    @DisplayName("단일 필드 객체 파싱")
    void parseSingleFieldObject() {
        JsonObject obj = parse("{\"key\":\"value\"}").asObject();
        assertTrue(obj.has("key"));
        assertEquals("value", obj.get("key").asString().getValue());
    }

    @Test
    @DisplayName("복수 필드 객체 파싱")
    void parseMultiFieldObject() {
        JsonObject obj = parse("{\"a\":1,\"b\":true,\"c\":null}").asObject();
        assertEquals(3, obj.size());
        assertEquals(1, obj.get("a").asNumber().intValue());
        assertTrue(obj.get("b").asBoolean().getValue());
        assertTrue(obj.get("c").isNull());
    }

    @Test
    @DisplayName("객체 트레일링 쉼표 → JsonParseException")
    void objectTrailingComma() {
        assertThrows(JsonParseException.class, () -> parse("{\"a\":1,}"));
    }

    @Test
    @DisplayName("객체 콜론 누락 → JsonParseException")
    void objectMissingColon() {
        assertThrows(JsonParseException.class, () -> parse("{\"a\" 1}"));
    }

    @Test
    @DisplayName("객체 키가 문자열이 아닌 경우 → JsonParseException")
    void objectNonStringKey() {
        assertThrows(JsonParseException.class, () -> parse("{1:\"v\"}"));
    }

    // ── 중첩 구조 ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("중첩 객체 파싱")
    void parseNestedObject() {
        int x = parse("{\"inner\":{\"x\":99}}")
                .asObject().get("inner").asObject().get("x").asNumber().intValue();
        assertEquals(99, x);
    }

    @Test
    @DisplayName("객체를 포함한 배열 파싱")
    void parseArrayOfObjects() {
        JsonArray arr = parse("[{\"id\":1},{\"id\":2}]").asArray();
        assertEquals(1, arr.get(0).asObject().get("id").asNumber().intValue());
        assertEquals(2, arr.get(1).asObject().get("id").asNumber().intValue());
    }

    @Test
    @DisplayName("배열을 포함한 객체 파싱")
    void parseObjectWithArray() {
        JsonArray tags = parse("{\"tags\":[\"a\",\"b\",\"c\"]}")
                .asObject().get("tags").asArray();
        assertEquals(3, tags.size());
        assertEquals("b", tags.get(1).asString().getValue());
    }

    @Test
    @DisplayName("깊이 중첩 배열 파싱")
    void parseDeepNestedArray() {
        int val = parse("[[1]]").asArray().get(0).asArray().get(0).asNumber().intValue();
        assertEquals(1, val);
    }

    // ── 오류 처리 ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("null 입력 → JsonParseException")
    void parseNullInput() {
        assertThrows(JsonParseException.class, () -> parse(null));
    }

    @Test
    @DisplayName("공백만 있는 입력 → JsonParseException")
    void parseBlankInput() {
        assertThrows(JsonParseException.class, () -> parse("   "));
    }

    @Test
    @DisplayName("루트 값 뒤 추가 토큰 → JsonParseException")
    void parseExtraToken() {
        assertThrows(JsonParseException.class, () -> parse("1 2"));
    }

    @Test
    @DisplayName("미완성 객체 → JsonParseException")
    void parseIncompleteObject() {
        assertThrows(JsonParseException.class, () -> parse("{\"a\":"));
    }

    @Test
    @DisplayName("미완성 배열 → JsonParseException")
    void parseIncompleteArray() {
        assertThrows(JsonParseException.class, () -> parse("[1,"));
    }

    @Test
    @DisplayName("타입 불일치 asObject 호출 → UnsupportedOperationException")
    void wrongTypeCast() {
        assertThrows(UnsupportedOperationException.class, () -> parse("42").asObject());
    }
}
