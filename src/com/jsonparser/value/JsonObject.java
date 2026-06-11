package com.jsonparser.value;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class JsonObject implements JsonValue {

    private final Map<String, JsonValue> fields;

    public JsonObject() {
        this.fields = new LinkedHashMap<>();
    }

    public JsonObject(Map<String, JsonValue> fields) {
        this.fields = new LinkedHashMap<>(fields);
    }

    public void put(String key, JsonValue value) {
        fields.put(
            Objects.requireNonNull(key,   "key must not be null"),
            Objects.requireNonNull(value, "value must not be null")
        );
    }

    public JsonValue get(String key) {
        return fields.get(key);
    }

    public Optional<JsonValue> getOptional(String key) {
        return Optional.ofNullable(fields.get(key));
    }

    public boolean has(String key) {
        return fields.containsKey(key);
    }

    public void remove(String key) {
        fields.remove(key);
    }

    public Set<String> keys() {
        return Collections.unmodifiableSet(fields.keySet());
    }

    public Set<Map.Entry<String, JsonValue>> entries() {
        return Collections.unmodifiableSet(fields.entrySet());
    }

    public int size() { return fields.size(); }

    public boolean isEmpty() { return fields.isEmpty(); }

    @Override public boolean isNull()    { return false; }
    @Override public boolean isBoolean() { return false; }
    @Override public boolean isNumber()  { return false; }
    @Override public boolean isString()  { return false; }
    @Override public boolean isArray()   { return false; }
    @Override public boolean isObject()  { return true; }

    @Override public JsonObject asObject() { return this; }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof JsonObject other && fields.equals(other.fields);
    }

    @Override public int hashCode() { return fields.hashCode(); }

    @Override public String toString() { return fields.toString(); }
}
