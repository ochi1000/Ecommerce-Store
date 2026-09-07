package com.mrdiy.common;

import java.util.Locale;

public final class Slugger {
    private Slugger() {
    }

    public static String slug(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }
}
