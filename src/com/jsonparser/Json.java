package com.jsonparser;

import com.jsonparser.parser.JsonParser;
import com.jsonparser.value.*;
import com.jsonparser.writer.JsonWriter;
import com.jsonparser.writer.WriteOptions;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * JSON 파서 라이브러리의 진입점.
 *
 * <pre>{@code
 * // 파싱
 * JsonValue v = Json.parse("{\"name\":\"Alice\",\"age\":30}");
 * String name = v.asObject().get("name").asString().getValue(); // "Alice"
 *
 * // 직렬화
 * String compact  = Json.stringify(v);
 * String pretty   = Json.prettify(v);
 *
 * // 파일 저장 / 로드
 * Json.saveToFile(v, Path.of("out.json"));
 * JsonValue loaded = Json.parseFile(Path.of("out.json"));
 * }</pre>
 */
public final class Json {

    private Json() {}

    // -------------------------------------------------------------------------
    // 파싱
    // -------------------------------------------------------------------------

    /** JSON 문자열을 파싱한다. */
    public static JsonValue parse(String json) {
        return new JsonParser().parse(json);
    }

    /** 파일에서 JSON을 읽어 파싱한다 (UTF-8). */
    public static JsonValue parseFile(Path path) throws IOException {
        return parse(Files.readString(path, StandardCharsets.UTF_8));
    }

    /** 파일에서 JSON을 읽어 파싱한다 (UTF-8). */
    public static JsonValue parseFile(File file) throws IOException {
        return parseFile(file.toPath());
    }

    // -------------------------------------------------------------------------
    // 직렬화
    // -------------------------------------------------------------------------

    /** JsonValue를 압축된 JSON 문자열로 직렬화한다. */
    public static String stringify(JsonValue value) {
        return new JsonWriter(WriteOptions.COMPACT).write(value);
    }

    /** JsonValue를 지정된 옵션으로 JSON 문자열로 직렬화한다. */
    public static String stringify(JsonValue value, WriteOptions options) {
        return new JsonWriter(options).write(value);
    }

    /** JsonValue를 들여쓰기가 적용된 JSON 문자열로 직렬화한다. */
    public static String prettify(JsonValue value) {
        return new JsonWriter(WriteOptions.PRETTY).write(value);
    }

    // -------------------------------------------------------------------------
    // 파일 저장
    // -------------------------------------------------------------------------

    /** JsonValue를 들여쓰기 형식으로 파일에 저장한다 (UTF-8). */
    public static void saveToFile(JsonValue value, Path path) {
        new JsonWriter(WriteOptions.PRETTY).writeToFile(value, path);
    }

    /** JsonValue를 지정된 옵션으로 파일에 저장한다 (UTF-8). */
    public static void saveToFile(JsonValue value, Path path, WriteOptions options) {
        new JsonWriter(options).writeToFile(value, path);
    }

    /** JsonValue를 들여쓰기 형식으로 파일에 저장한다 (UTF-8). */
    public static void saveToFile(JsonValue value, File file) {
        new JsonWriter(WriteOptions.PRETTY).writeToFile(value, file);
    }

    /** JsonValue를 OutputStream에 쓴다 (UTF-8). */
    public static void writeToStream(JsonValue value, OutputStream out) {
        new JsonWriter(WriteOptions.COMPACT).writeToStream(value, out);
    }

    // -------------------------------------------------------------------------
    // 값 생성 팩토리 (빌더 없이 JsonValue 트리 구성 시 편의 메서드)
    // -------------------------------------------------------------------------

    public static JsonNull   nullValue()            { return JsonNull.INSTANCE; }
    public static JsonBoolean of(boolean value)     { return JsonBoolean.of(value); }
    public static JsonNumber  of(long value)        { return new JsonNumber(value); }
    public static JsonNumber  of(double value)      { return new JsonNumber(value); }
    public static JsonString  of(String value)      { return new JsonString(value); }
    public static JsonArray   array()               { return new JsonArray(); }
    public static JsonObject  object()              { return new JsonObject(); }
}
