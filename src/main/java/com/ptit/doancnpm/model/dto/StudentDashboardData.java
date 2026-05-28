package com.ptit.doancnpm.model.dto;

import java.util.List;

public record StudentDashboardData(
        StudentInfo studentInfo,
        List<StudentTopicSummary> topics) {
}
