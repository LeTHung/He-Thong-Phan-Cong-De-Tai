package com.ptit.doancnpm.model.dto;

public class AdminDashboardSummary {
    private final int totalAccounts;
    private final int activeAccounts;
    private final int totalStudents;
    private final int activeStudents;
    private final int totalLecturers;
    private final int activeLecturers;
    private final int totalCourseSections;
    private final int openCourseSections;

    public AdminDashboardSummary(
            int totalAccounts,
            int activeAccounts,
            int totalStudents,
            int activeStudents,
            int totalLecturers,
            int activeLecturers,
            int totalCourseSections,
            int openCourseSections) {
        this.totalAccounts = totalAccounts;
        this.activeAccounts = activeAccounts;
        this.totalStudents = totalStudents;
        this.activeStudents = activeStudents;
        this.totalLecturers = totalLecturers;
        this.activeLecturers = activeLecturers;
        this.totalCourseSections = totalCourseSections;
        this.openCourseSections = openCourseSections;
    }

    public int getTotalAccounts() {
        return totalAccounts;
    }

    public int getActiveAccounts() {
        return activeAccounts;
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public int getActiveStudents() {
        return activeStudents;
    }

    public int getTotalLecturers() {
        return totalLecturers;
    }

    public int getActiveLecturers() {
        return activeLecturers;
    }

    public int getTotalCourseSections() {
        return totalCourseSections;
    }

    public int getOpenCourseSections() {
        return openCourseSections;
    }
}
