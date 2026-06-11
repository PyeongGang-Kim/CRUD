package com.jsonparser.value;

import java.math.BigDecimal;

public final class JsonNumber implements JsonValue {

    private final BigDecimal value;

    public JsonNumber(BigDecimal value) { this.value = value; }
    public JsonNumber(long value)       { this.value = BigDecimal.valueOf(value); }
    public JsonNumber(double value)     { this.value = BigDecimal.valueOf(value); }

    public BigDecimal getValue()  { return value; }
    public int        intValue()  { return value.intValue(); }
    public long       longValue() { return value.longValue(); }
    public double     doubleValue() { return value.doubleValue(); }
    public float      floatValue()  { return value.floatValue(); }

    public boolean isIntegral() {
        return value.scale() <= 0 || value.stripTrailingZeros().scale() <= 0;
    }

    @Override public boolean isNull()    { return false; }
    @Override public boolean isBoolean() { return false; }
    @Override public boolean isNumber()  { return true; }
    @Override public boolean isString()  { return false; }
    @Override public boolean isArray()   { return false; }
    @Override public boolean isObject()  { return false; }

    @Override public JsonNumber asNumber() { return this; }

    @Override public String toString() { return value.toPlainString(); }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof JsonNumber other && value.compareTo(other.value) == 0;
    }

    @Override public int hashCode() { return value.stripTrailingZeros().hashCode(); }
}
