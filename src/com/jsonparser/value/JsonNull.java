package com.jsonparser.value;

public final class JsonNull implements JsonValue {

    public static final JsonNull INSTANCE = new JsonNull();

    private JsonNull() {}

    @Override public boolean isNull()    { return true; }
    @Override public boolean isBoolean() { return false; }
    @Override public boolean isNumber()  { return false; }
    @Override public boolean isString()  { return false; }
    @Override public boolean isArray()   { return false; }
    @Override public boolean isObject()  { return false; }

    @Override public JsonNull asNull() { return this; }

    @Override public String toString() { return "null"; }

    @Override public boolean equals(Object obj) { return obj instanceof JsonNull; }
    @Override public int hashCode() { return 0; }
}
