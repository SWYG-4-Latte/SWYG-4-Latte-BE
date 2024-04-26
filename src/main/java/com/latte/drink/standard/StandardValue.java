package com.latte.drink.standard;

import lombok.AllArgsConstructor;
import lombok.Data;

import static com.latte.drink.standard.StandardValueConstants.*;

@Data
@AllArgsConstructor
public class StandardValue {
    private int minNormal;
    private int maxNormal;
    private int maxCaffeine;

    public static StandardValue createStandardValue(String type) {
        return switch(type) {
            case "MN" -> new StandardValue(MN_MIN_NORMAL, MN_MAX_NORMAL, MN_MAX_CAFFEINE);
            case "MY" -> new StandardValue(MY_MIN_NORMAL, MY_MAX_NORMAL, MY_MAX_CAFFEINE);
            case "FNN" -> new StandardValue(FNN_MIN_NORMAL, FNN_MAX_NORMAL, FNN_MAX_CAFFEINE);
            case "FNY" -> new StandardValue(FNY_MIN_NORMAL, FNY_MAX_NORMAL, FNY_MAX_CAFFEINE);
            case "FYE" -> new StandardValue(FYE_MIN_NORMAL, FYE_MAX_NORMAL, FYE_MAX_CAFFEINE);
            case "FYM" -> new StandardValue(FYM_MIN_NORMAL, FYM_MAX_NORMAL, FYM_MAX_CAFFEINE);
            case "FYL" -> new StandardValue(FYL_MIN_NORMAL, FYL_MAX_NORMAL, FYL_MAX_CAFFEINE);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }
}
