package com.jsonparser.value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public final class JsonArray implements JsonValue, Iterable<JsonValue> {

    private final List<JsonValue> items;

    public JsonArray() {
        this.items = new ArrayList<>();
    }

    public JsonArray(List<JsonValue> items) {
        this.items = new ArrayList<>(items);
    }

    public void add(JsonValue value) {
        items.add(Objects.requireNonNull(value, "value must not be null"));
    }

    public JsonValue get(int index) {
        return items.get(index);
    }

    public int size() { return items.size(); }

    public boolean isEmpty() { return items.isEmpty(); }

    public List<JsonValue> values() {
        return Collections.unmodifiableList(items);
    }

    @Override public boolean isNull()    { return false; }
    @Override public boolean isBoolean() { return false; }
    @Override public boolean isNumber()  { return false; }
    @Override public boolean isString()  { return false; }
    @Override public boolean isArray()   { return true; }
    @Override public boolean isObject()  { return false; }

    @Override public JsonArray asArray() { return this; }

    @Override
    public Iterator<JsonValue> iterator() {
        return items.iterator();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof JsonArray other && items.equals(other.items);
    }

    @Override public int hashCode() { return items.hashCode(); }

    @Override public String toString() { return items.toString(); }
}
