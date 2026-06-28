package com.ptit.doancnpm.controller.admin;

import java.util.Locale;

final class AdminFilterSupport {

    private AdminFilterSupport() {
    }

    static boolean contains(String query, String... values) {
        String normalizedQuery = normalize(query);
        if (normalizedQuery.isEmpty()) {
            return true;
        }
        for (String value : values) {
            if (normalize(value).contains(normalizedQuery)) {
                return true;
            }
        }
        return false;
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
