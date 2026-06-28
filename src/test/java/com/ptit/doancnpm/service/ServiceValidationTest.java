package com.ptit.doancnpm.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ServiceValidationTest {

    @Test
    void rejectsEmptyAccountUsernameBeforeAccessingDatabase() {
        assertThrows(IllegalArgumentException.class,
                () -> new AccountManagementService().createAccount("", "123456", null, null, null, null));
    }

    @Test
    void rejectsInvalidSubjectCredits() {
        assertThrows(IllegalArgumentException.class,
                () -> new SubjectService().createSubject("TEST", "Test", "0", null, null));
    }

    @Test
    void rejectsInvalidSemesterDateRange() {
        assertThrows(IllegalArgumentException.class,
                () -> new SemesterService().createSemester(
                        "TEST", "Test", "2026-2027",
                        LocalDate.of(2027, 1, 1), LocalDate.of(2026, 1, 1), null));
    }

    @Test
    void rejectsInvalidCourseSectionCapacity() {
        assertThrows(IllegalArgumentException.class,
                () -> new CourseSectionService().createCourseSection(
                        "TEST", "Test", null, null, null, "0", null, null));
    }

    @Test
    void rejectsStudentClassActionWithoutCourseSection() {
        assertThrows(IllegalArgumentException.class,
                () -> new StudentClassService().addStudentsToCourseSection(null, List.of(), null));
    }

    @Test
    void rejectsEmptyTopicAssignment() {
        assertThrows(IllegalArgumentException.class,
                () -> new AssignTopicService().assignTopics(1, List.of(), null));
    }

    @Test
    void rejectsInvalidLecturerAssignmentIdentifiers() {
        assertThrows(IllegalArgumentException.class,
                () -> new RegistrationResultService().assignStudentManually(0, 0, 0, null));
    }

    @Test
    void rejectsInvalidStudentRegistrationIdentifiers() {
        assertThrows(IllegalArgumentException.class,
                () -> new TopicRegistrationService().register(0, 0));
    }

    @Test
    void rejectsChangingToCurrentTopic() {
        assertThrows(IllegalArgumentException.class,
                () -> new TopicRegistrationService().changeTopic(1, 1, 2, 2));
    }

    @Test
    void rejectsPasswordChangeWithoutSessionUser() {
        assertThrows(IllegalStateException.class,
                () -> new ChangePasswordService().changePassword(null, "old", "123456", "123456"));
    }
}
