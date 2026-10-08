package org.takoyaki.reportmaker.util.json;

import java.math.BigDecimal;

public record JsonNumber(BigDecimal value) implements JsonValue {
    public JsonNumber(int value) {
        this(BigDecimal.valueOf(value));
    }
}
