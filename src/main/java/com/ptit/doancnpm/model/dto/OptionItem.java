package com.ptit.doancnpm.model.dto;

public class OptionItem {
    private final int id;
    private final String code;
    private final String name;

    public OptionItem(int id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        if (code == null || code.isBlank()) {
            return name;
        }
        return code + " - " + name;
    }
}
