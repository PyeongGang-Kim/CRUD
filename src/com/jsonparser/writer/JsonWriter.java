package com.jsonparser.writer;

import com.jsonparser.exception.JsonWriteException;
import com.jsonparser.value.*;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonWriter {

    private final WriteOptions options;

    public JsonWriter() {
        this.options = WriteOptions.COMPACT;
    }

    public JsonWriter(WriteOptions options) {
        this.options = options;
    }

    // -------------------------------------------------------------------------
    // 공개 API
    // -------------------------------------------------------------------------

    /** JsonValue를 JSON 문자열로 직렬화한다. */
    public String write(JsonValue value) {
        StringBuilder sb = new StringBuilder();
        writeValue(value, sb, 0);
        return sb.toString();
    }

    /** Path로 지정된 파일에 JSON을 저장한다 (UTF-8). */
    public void writeToFile(JsonValue value, Path path) {
        try {
            Files.writeString(path, write(value), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new JsonWriteException("Failed to write JSON to file: " + path, e);
        }
    }

    /** File 객체로 지정된 경로에 JSON을 저장한다 (UTF-8). */
    public void writeToFile(JsonValue value, File file) {
        writeToFile(value, file.toPath());
    }

    /** OutputStream에 JSON을 쓴다 (UTF-8). */
    public void writeToStream(JsonValue value, OutputStream out) {
        try {
            out.write(write(value).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new JsonWriteException("Failed to write JSON to stream", e);
        }
    }

    // -------------------------------------------------------------------------
    // 내부 직렬화 메서드
    // -------------------------------------------------------------------------

    private void writeValue(JsonValue value, StringBuilder sb, int depth) {
        if (value instanceof JsonNull) {
            sb.append("null");
        } else if (value instanceof JsonBoolean b) {
            sb.append(b.getValue() ? "true" : "false");
        } else if (value instanceof JsonNumber n) {
            sb.append(n.getValue().toPlainString());
        } else if (value instanceof JsonString s) {
            writeEscapedString(s.getValue(), sb);
        } else if (value instanceof JsonArray a) {
            writeArray(a, sb, depth);
        } else if (value instanceof JsonObject o) {
            writeObject(o, sb, depth);
        } else {
            throw new JsonWriteException("Unknown JsonValue type: " + value.getClass());
        }
    }

    private void writeEscapedString(String value, StringBuilder sb) {
        sb.append('"');
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '"'  -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default   -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        sb.append('"');
    }

    private void writeArray(JsonArray array, StringBuilder sb, int depth) {
        if (array.isEmpty()) {
            sb.append("[]");
            return;
        }

        sb.append('[');
        boolean first = true;
        for (JsonValue item : array) {
            if (!first) sb.append(',');
            if (options.isPrettyPrint()) {
                sb.append('\n');
                indent(sb, depth + 1);
            }
            writeValue(item, sb, depth + 1);
            first = false;
        }

        if (options.isPrettyPrint()) {
            sb.append('\n');
            indent(sb, depth);
        }
        sb.append(']');
    }

    private void writeObject(JsonObject object, StringBuilder sb, int depth) {
        if (object.isEmpty()) {
            sb.append("{}");
            return;
        }

        sb.append('{');
        boolean first = true;
        for (var entry : object.entries()) {
            if (!first) sb.append(',');
            if (options.isPrettyPrint()) {
                sb.append('\n');
                indent(sb, depth + 1);
            }
            writeEscapedString(entry.getKey(), sb);
            sb.append(':');
            if (options.isPrettyPrint()) sb.append(' ');
            writeValue(entry.getValue(), sb, depth + 1);
            first = false;
        }

        if (options.isPrettyPrint()) {
            sb.append('\n');
            indent(sb, depth);
        }
        sb.append('}');
    }

    private void indent(StringBuilder sb, int depth) {
        sb.append(options.getIndent().repeat(depth));
    }
}
