package backend;

import model.*;
import java.util.List;

public class CompletionManager {

    private JsonDatabaseManager jsonDB;

    public CompletionManager(JsonDatabaseManager jsonDB) {
        this.jsonDB = jsonDB;
    }

    public boolean markLessonCompleted(Student student, Course course, String lessonId, boolean quizPassed) {
        Lesson lesson = null;
        for (Lesson l : course.getLessons()) {
            if (l.getLessonId().equals(lessonId)) {
                lesson = l;
                break;
            }
        }


        if (lesson == null) return false;

        if (lesson.getQuiz() != null && !quizPassed) return false;

        if (!student.getProgressForCourse(course.getCourseId()).contains(lessonId)) {
            student.markLessonCompleted(course.getCourseId(), lessonId);
            saveStudent(student);
        }

        if (isCourseCompleted(student, course)) {
            if (!student.getCompletedCourses().contains(course.getCourseId())) {
                student.getCompletedCourses().add(course.getCourseId());
            }

            generateCertificate(student, course);
            saveStudent(student);
            return true;
        }

        return false;
    }

    public boolean isCourseCompleted(Student student, Course course) {
        for (Lesson lesson : course.getLessons()) {
            if (!student.getProgressForCourse(course.getCourseId()).contains(lesson.getLessonId())) {
                return false;
            }
            if (lesson.getQuiz() != null && !student.hasPassedQuiz(course.getCourseId(), lesson.getLessonId())) {
                return false;
            }
        }
        return true;
    }


    public Certificate generateCertificate(Student student, Course course) {
        for (Certificate c : student.getCertificates()) {
            if (c.getCourseId().equals(course.getCourseId())) {
                return c; // Already exists
            }
        }

        String certificateId = "CERT" + System.currentTimeMillis();
        String issueDate = java.time.LocalDate.now().toString();

        Certificate newCert = new Certificate(
                certificateId,
                course.getCourseId(),
                student.getUserId(),
                issueDate
        );

        student.getCertificates().add(newCert);
        saveStudent(student);

        return newCert;
    }

    private void saveStudent(Student student) {
        List<User> users = jsonDB.loadUsers();
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            if (u instanceof Student s && s.getUserId().equals(student.getUserId())) {
                users.set(i, student);
                break;
            }
        }
        jsonDB.saveUsers(users);
    }
}
