package com.ptit.doancnpm.controller.student;

/**
 * Giữ tạm mã đề tài lớp được chọn để truyền giữa màn hình danh sách
 * và màn hình chi tiết (do điều hướng bằng setRoot không truyền tham số).
 */
public final class StudentTopicContext {

    private static Integer selectedMaDeTaiLop;

    private StudentTopicContext() {
    }

    public static void setSelectedTopic(Integer maDeTaiLop) {
        selectedMaDeTaiLop = maDeTaiLop;
    }

    public static Integer getSelectedTopic() {
        return selectedMaDeTaiLop;
    }

    public static void clear() {
        selectedMaDeTaiLop = null;
    }
}
