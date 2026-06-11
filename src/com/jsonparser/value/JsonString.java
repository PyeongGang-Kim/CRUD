package com.jsonparser.value;

import java.util.Objects;

public final class JsonString implements JsonValue {

    private final String value;

    public JsonString(String value) {
        this.value = Objects.requireNonNull(value, "value must not be null");
    }

    public String getValue() { return value; }

    @Override public boolean isNull()    { return false; }
    @Override public boolean isBoolean() { return false; }
    @Override public boolean isNumber()  { return false; }
    @Override public boolean isString()  { return true; }
    @Override public boolean isArray()   { return false; }
    @Override public boolean isObject()  { return false; }

    @Override public JsonString asString() { return this; }

    @Override public String toString() { return "\"" + value + "\""; }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof JsonString other && value.equals(other.value);
    }

    @Override public int hashCode() { return value.hashCode(); }
}
