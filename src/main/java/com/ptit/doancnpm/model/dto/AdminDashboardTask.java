package com.ptit.doancnpm.model.dto;

public class AdminDashboardTask {
    private final String module;
    private final String owner;
    private final String status;
    private final String note;

    public AdminDashboardTask(String module, String owner, String status, String note) {
        this.module = module;
        this.owner = owner;
        this.status = status;
        this.note = note;
    }

    public String getModule() {
        return module;
    }

    public String getOwner() {
        return owner;
    }

    public String getStatus() {
        return status;
    }

    public String getNote() {
        return note;
    }
}
