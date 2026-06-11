package com.jsonparser.value;

public final class JsonBoolean implements JsonValue {

    public static final JsonBoolean TRUE  = new JsonBoolean(true);
    public static final JsonBoolean FALSE = new JsonBoolean(false);

    private final boolean value;

    private JsonBoolean(boolean value) { this.value = value; }

    public static JsonBoolean of(boolean value) {
        return value ? TRUE : FALSE;
    }

    public boolean getValue() { return value; }

    @Override public boolean isNull()    { return false; }
    @Override public boolean isBoolean() { return true; }
    @Override public boolean isNumber()  { return false; }
    @Override public boolean isString()  { return false; }
    @Override public boolean isArray()   { return false; }
    @Override public boolean isObject()  { return false; }

    @Override public JsonBoolean asBoolean() { return this; }

    @Override public String toString() { return value ? "true" : "false"; }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof JsonBoolean other && value == other.value;
    }

    @Override public int hashCode() { return Boolean.hashCode(value); }
}
