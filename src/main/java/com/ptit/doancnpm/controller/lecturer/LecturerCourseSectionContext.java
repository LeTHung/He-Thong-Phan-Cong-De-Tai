package com.ptit.doancnpm.controller.lecturer;

/** Lưu lớp được chọn khi chuyển giữa các màn giảng viên. */
public final class LecturerCourseSectionContext {
    private static Integer selectedCourseSectionId;

    private LecturerCourseSectionContext() {
    }

    public static Integer getSelectedCourseSectionId() {
        return selectedCourseSectionId;
    }

    public static void setSelectedCourseSectionId(Integer courseSectionId) {
        selectedCourseSectionId = courseSectionId;
    }
}
