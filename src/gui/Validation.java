package gui;

import model.*;
import backend.*;

public class Validation {
    public static boolean isEmpty(String input) {
        return input == null || input.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email != null && email.matches(emailRegex);
    }

    public static boolean isValidInstructorId(String id) {    
        return id.matches("^\\d+$");
    }

    public static boolean isValidStudentId(String id) {

        return id.matches("^\\d+$");
    }

    public static boolean isValidName(String name) {
        return name.matches("^[a-zA-Z ]+$");
    }

    public static boolean isValidCourseId(String id, CourseManager cmanager) {
        if (isEmpty(id)) return false;
        return cmanager.getCourseById(id) == null;
    }

    public static boolean isValidCourseTitle(String title) {
        return !isEmpty(title);
    }

    public static boolean isValidLessonId(String id,CourseManager cmanager, String courseId) {
        if (isEmpty(id)) return false;
        Course course = cmanager.getCourseById(courseId);
        if (course == null) return false;

        for (model.Lesson lesson : course.getLessons()) {
            if (lesson.getLessonId().equals(id)) {
                return false; }
        }
        return true;
    }
    
}
