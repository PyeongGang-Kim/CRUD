package com.jsonparser.value;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JsonValue 타입 테스트")
class JsonValueTest {

    // ── JsonNull ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("JsonNull 싱글톤 동일성")
    void nullSingleton() {
        assertSame(JsonNull.INSTANCE, JsonNull.INSTANCE);
    }

    @Test
    @DisplayName("JsonNull 타입 검사")
    void nullTypeChecks() {
        JsonNull n = JsonNull.INSTANCE;
        assertTrue(n.isNull());
        assertFalse(n.isBoolean());
        assertFalse(n.isNumber());
        assertFalse(n.isString());
        assertFalse(n.isArray());
        assertFalse(n.isObject());
    }

    @Test
    @DisplayName("JsonNull asNull() 반환")
    void nullAsNull() {
        assertSame(JsonNull.INSTANCE, JsonNull.INSTANCE.asNull());
    }

    @Test
    @DisplayName("JsonNull equals")
    void nullEquals() {
        assertEquals(JsonNull.INSTANCE, JsonNull.INSTANCE);
    }

    @Test
    @DisplayName("JsonNull asBoolean() → UnsupportedOperationException")
    void nullAsBooleanThrows() {
        assertThrows(UnsupportedOperationException.class, () -> JsonNull.INSTANCE.asBoolean());
    }

    @Test
    @DisplayName("JsonNull asNumber() → UnsupportedOperationException")
    void nullAsNumberThrows() {
        assertThrows(UnsupportedOperationException.class, () -> JsonNull.INSTANCE.asNumber());
    }

    @Test
    @DisplayName("JsonNull toString")
    void nullToString() {
        assertEquals("null", JsonNull.INSTANCE.toString());
    }

    // ── JsonBoolean ───────────────────────────────────────────────────────

    @Test
    @DisplayName("JsonBoolean.of(true) 싱글톤 풀")
    void booleanTrueSingleton() {
        assertSame(JsonBoolean.TRUE, JsonBoolean.of(true));
    }

    @Test
    @DisplayName("JsonBoolean.of(false) 싱글톤 풀")
    void booleanFalseSingleton() {
        assertSame(JsonBoolean.FALSE, JsonBoolean.of(false));
    }

    @Test
    @DisplayName("JsonBoolean.TRUE getValue")
    void booleanTrueGetValue() {
        assertTrue(JsonBoolean.TRUE.getValue());
    }

    @Test
    @DisplayName("JsonBoolean.FALSE getValue")
    void booleanFalseGetValue() {
        assertFalse(JsonBoolean.FALSE.getValue());
    }

    @Test
    @DisplayName("JsonBoolean 타입 검사")
    void booleanTypeChecks() {
        assertTrue(JsonBoolean.TRUE.isBoolean());
        assertFalse(JsonBoolean.TRUE.isNull());
        assertFalse(JsonBoolean.TRUE.isNumber());
        assertFalse(JsonBoolean.TRUE.isString());
        assertFalse(JsonBoolean.TRUE.isArray());
        assertFalse(JsonBoolean.TRUE.isObject());
    }

    @Test
    @DisplayName("JsonBoolean asBoolean() 반환")
    void booleanAsBoolean() {
        assertSame(JsonBoolean.TRUE, JsonBoolean.TRUE.asBoolean());
    }

    @Test
    @DisplayName("JsonBoolean asNumber() → UnsupportedOperationException")
    void booleanAsNumberThrows() {
        assertThrows(UnsupportedOperationException.class, () -> JsonBoolean.TRUE.asNumber());
    }

    @Test
    @DisplayName("JsonBoolean equals")
    void booleanEquals() {
        assertEquals(JsonBoolean.TRUE,  JsonBoolean.of(true));
        assertEquals(JsonBoolean.FALSE, JsonBoolean.of(false));
        assertNotEquals(JsonBoolean.TRUE, JsonBoolean.FALSE);
    }

    @Test
    @DisplayName("JsonBoolean toString")
    void booleanToString() {
        assertEquals("true",  JsonBoolean.TRUE.toString());
        assertEquals("false", JsonBoolean.FALSE.toString());
    }

    // ── JsonNumber ────────────────────────────────────────────────────────

    @Test
    @DisplayName("JsonNumber long 생성자")
    void numberFromLong() {
        JsonNumber n = new JsonNumber(100L);
        assertEquals(100,  n.intValue());
        assertEquals(100L, n.longValue());
    }

    @Test
    @DisplayName("JsonNumber double 생성자")
    void numberFromDouble() {
        JsonNumber n = new JsonNumber(3.14);
        assertEquals(3.14, n.doubleValue(), 1e-10);
    }

    @Test
    @DisplayName("JsonNumber BigDecimal 생성자")
    void numberFromBigDecimal() {
        JsonNumber n = new JsonNumber(new BigDecimal("123.456"));
        assertEquals(new BigDecimal("123.456"), n.getValue());
    }

    @Test
    @DisplayName("JsonNumber floatValue()")
    void numberFloatValue() {
        JsonNumber n = new JsonNumber(1.5);
        assertEquals(1.5f, n.floatValue(), 1e-5f);
    }

    @Test
    @DisplayName("JsonNumber isIntegral: 정수 → true")
    void numberIsIntegralTrue() {
        assertTrue(new JsonNumber(42L).isIntegral());
    }

    @Test
    @DisplayName("JsonNumber isIntegral: 소수 → false")
    void numberIsIntegralFalse() {
        assertFalse(new JsonNumber(3.14).isIntegral());
    }

    @Test
    @DisplayName("JsonNumber isIntegral: 1.0 → true (소수점 제거)")
    void numberIsIntegralTrailingZero() {
        assertTrue(new JsonNumber(new BigDecimal("1.0")).isIntegral());
    }

    @Test
    @DisplayName("JsonNumber 타입 검사")
    void numberTypeChecks() {
        JsonNumber n = new JsonNumber(1L);
        assertTrue(n.isNumber());
        assertFalse(n.isNull());
        assertFalse(n.isBoolean());
        assertFalse(n.isString());
        assertFalse(n.isArray());
        assertFalse(n.isObject());
    }

    @Test
    @DisplayName("JsonNumber equals: 같은 값 다른 표현")
    void numberEqualsNormalized() {
        assertEquals(new JsonNumber(1L), new JsonNumber(new BigDecimal("1.0")));
    }

    @Test
    @DisplayName("JsonNumber asString() → UnsupportedOperationException")
    void numberAsStringThrows() {
        assertThrows(UnsupportedOperationException.class, () -> new JsonNumber(1L).asString());
    }

    @Test
    @DisplayName("JsonNumber toString: toPlainString 사용")
    void numberToString() {
        assertEquals("100", new JsonNumber(100L).toString());
    }

    // ── JsonString ────────────────────────────────────────────────────────

    @Test
    @DisplayName("JsonString getValue")
    void stringGetValue() {
        assertEquals("hello", new JsonString("hello").getValue());
    }

    @Test
    @DisplayName("JsonString 빈 문자열")
    void stringEmpty() {
        assertEquals("", new JsonString("").getValue());
    }

    @Test
    @DisplayName("JsonString null 생성 → NullPointerException")
    void stringNullThrows() {
        assertThrows(NullPointerException.class, () -> new JsonString(null));
    }

    @Test
    @DisplayName("JsonString 타입 검사")
    void stringTypeChecks() {
        JsonString s = new JsonString("x");
        assertTrue(s.isString());
        assertFalse(s.isNull());
        assertFalse(s.isBoolean());
        assertFalse(s.isNumber());
        assertFalse(s.isArray());
        assertFalse(s.isObject());
    }

    @Test
    @DisplayName("JsonString asNumber() → UnsupportedOperationException")
    void stringAsNumberThrows() {
        assertThrows(UnsupportedOperationException.class, () -> new JsonString("42").asNumber());
    }

    @Test
    @DisplayName("JsonString equals")
    void stringEquals() {
        assertEquals(new JsonString("abc"), new JsonString("abc"));
        assertNotEquals(new JsonString("abc"), new JsonString("ABC"));
    }

    // ── JsonArray ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("JsonArray 기본 생성: isEmpty, size")
    void arrayEmpty() {
        JsonArray a = new JsonArray();
        assertTrue(a.isEmpty());
        assertEquals(0, a.size());
    }

    @Test
    @DisplayName("JsonArray add/get")
    void arrayAddGet() {
        JsonArray a = new JsonArray();
        a.add(new JsonString("one"));
        a.add(new JsonString("two"));
        assertEquals(2, a.size());
        assertFalse(a.isEmpty());
        assertEquals("one", a.get(0).asString().getValue());
        assertEquals("two", a.get(1).asString().getValue());
    }

    @Test
    @DisplayName("JsonArray add null → NullPointerException")
    void arrayAddNull() {
        assertThrows(NullPointerException.class, () -> new JsonArray().add(null));
    }

    @Test
    @DisplayName("JsonArray values() 불변 뷰")
    void arrayValuesImmutable() {
        JsonArray a = new JsonArray();
        a.add(JsonNull.INSTANCE);
        assertThrows(UnsupportedOperationException.class,
                () -> a.values().add(JsonNull.INSTANCE));
    }

    @Test
    @DisplayName("JsonArray Iterable 순회")
    void arrayIterable() {
        JsonArray a = new JsonArray();
        a.add(new JsonNumber(1L));
        a.add(new JsonNumber(2L));
        int sum = 0;
        for (JsonValue v : a) sum += v.asNumber().intValue();
        assertEquals(3, sum);
    }

    @Test
    @DisplayName("JsonArray 타입 검사")
    void arrayTypeChecks() {
        JsonArray a = new JsonArray();
        assertTrue(a.isArray());
        assertFalse(a.isObject());
        assertFalse(a.isNull());
    }

    @Test
    @DisplayName("JsonArray equals")
    void arrayEquals() {
        JsonArray a1 = new JsonArray();
        a1.add(new JsonNumber(1L));
        JsonArray a2 = new JsonArray();
        a2.add(new JsonNumber(1L));
        assertEquals(a1, a2);
    }

    @Test
    @DisplayName("JsonArray asObject() → UnsupportedOperationException")
    void arrayAsObjectThrows() {
        assertThrows(UnsupportedOperationException.class, () -> new JsonArray().asObject());
    }

    // ── JsonObject ────────────────────────────────────────────────────────

    @Test
    @DisplayName("JsonObject put/get")
    void objectPutGet() {
        JsonObject obj = new JsonObject();
        obj.put("key", new JsonString("value"));
        assertEquals("value", obj.get("key").asString().getValue());
    }

    @Test
    @DisplayName("JsonObject 없는 키 get → null")
    void objectGetMissingKey() {
        assertNull(new JsonObject().get("missing"));
    }

    @Test
    @DisplayName("JsonObject has/remove")
    void objectHasRemove() {
        JsonObject obj = new JsonObject();
        obj.put("k", JsonNull.INSTANCE);
        assertTrue(obj.has("k"));
        obj.remove("k");
        assertFalse(obj.has("k"));
    }

    @Test
    @DisplayName("JsonObject getOptional: 존재하는 키 → present")
    void objectGetOptionalPresent() {
        JsonObject obj = new JsonObject();
        obj.put("x", JsonBoolean.TRUE);
        assertTrue(obj.getOptional("x").isPresent());
    }

    @Test
    @DisplayName("JsonObject getOptional: 없는 키 → empty")
    void objectGetOptionalAbsent() {
        assertTrue(new JsonObject().getOptional("missing").isEmpty());
    }

    @Test
    @DisplayName("JsonObject null 키 → NullPointerException")
    void objectNullKey() {
        assertThrows(NullPointerException.class,
                () -> new JsonObject().put(null, JsonNull.INSTANCE));
    }

    @Test
    @DisplayName("JsonObject null 값 → NullPointerException")
    void objectNullValue() {
        assertThrows(NullPointerException.class,
                () -> new JsonObject().put("k", null));
    }

    @Test
    @DisplayName("JsonObject isEmpty/size")
    void objectSizeEmpty() {
        JsonObject obj = new JsonObject();
        assertTrue(obj.isEmpty());
        assertEquals(0, obj.size());
        obj.put("a", JsonNull.INSTANCE);
        assertFalse(obj.isEmpty());
        assertEquals(1, obj.size());
    }

    @Test
    @DisplayName("JsonObject 키 덮어쓰기")
    void objectPutOverwrite() {
        JsonObject obj = new JsonObject();
        obj.put("k", new JsonString("old"));
        obj.put("k", new JsonString("new"));
        assertEquals(1, obj.size());
        assertEquals("new", obj.get("k").asString().getValue());
    }

    @Test
    @DisplayName("JsonObject keys() 불변 뷰")
    void objectKeysImmutable() {
        JsonObject obj = new JsonObject();
        obj.put("a", JsonNull.INSTANCE);
        assertThrows(UnsupportedOperationException.class,
                () -> obj.keys().add("b"));
    }

    @Test
    @DisplayName("JsonObject 타입 검사")
    void objectTypeChecks() {
        JsonObject obj = new JsonObject();
        assertTrue(obj.isObject());
        assertFalse(obj.isArray());
        assertFalse(obj.isNull());
    }

    @Test
    @DisplayName("JsonObject equals")
    void objectEquals() {
        JsonObject o1 = new JsonObject();
        o1.put("a", new JsonNumber(1L));
        JsonObject o2 = new JsonObject();
        o2.put("a", new JsonNumber(1L));
        assertEquals(o1, o2);
    }

    @Test
    @DisplayName("JsonObject asArray() → UnsupportedOperationException")
    void objectAsArrayThrows() {
        assertThrows(UnsupportedOperationException.class, () -> new JsonObject().asArray());
    }
}
