package com.jsonparser.value;

public sealed interface JsonValue permits JsonNull, JsonBoolean, JsonNumber, JsonString, JsonArray, JsonObject {

    boolean isNull();
    boolean isBoolean();
    boolean isNumber();
    boolean isString();
    boolean isArray();
    boolean isObject();

    default JsonNull asNull() {
        throw new UnsupportedOperationException("Not a null value: " + getClass().getSimpleName());
    }

    default JsonBoolean asBoolean() {
        throw new UnsupportedOperationException("Not a boolean: " + getClass().getSimpleName());
    }

    default JsonNumber asNumber() {
        throw new UnsupportedOperationException("Not a number: " + getClass().getSimpleName());
    }

    default JsonString asString() {
        throw new UnsupportedOperationException("Not a string: " + getClass().getSimpleName());
    }

    default JsonArray asArray() {
        throw new UnsupportedOperationException("Not an array: " + getClass().getSimpleName());
    }

    default JsonObject asObject() {
        throw new UnsupportedOperationException("Not an object: " + getClass().getSimpleName());
    }
}
