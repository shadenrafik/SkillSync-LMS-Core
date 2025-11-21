package backend;
import model.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CourseManager {
    private JsonDatabaseManager jsonDB;
    private ArrayList<Course> courses;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Type courseListType = new TypeToken<List<Course>>() {}.getType();

    public CourseManager(String fileName) {
        this.jsonDB=new JsonDatabaseManager();
        this.courses=loadCoursesFromDB();
    }

    private ArrayList<Course> loadCoursesFromDB() {
        JsonArray arr = jsonDB.loadCourses();
        if (arr == null || arr.size() == 0) {
            return new ArrayList<>();
        }

        List<Course> list = gson.fromJson(arr, courseListType);

        return list == null ? new ArrayList<>() : new ArrayList<>(list);
    }

    private void saveAllCourses() {
        JsonElement tree = gson.toJsonTree(this.courses);
        JsonArray arr = tree.isJsonArray() ? tree.getAsJsonArray() : new JsonArray();
        jsonDB.saveCourses(arr);
    }
    public Course getCourseById(String id) {
        for (Course c : courses) {
            if (c.getCourseId().equals(id))
                return c;
        }
        return null;
    }
    public void insertCourse(Course course) {
        if (getCourseById(course.getCourseId()) == null) {
            courses.add(course);
            saveAllCourses();
            System.out.println("Course added.");

            jsonDB.updateInstructorCourseList(course.getInstructorId(), course.getCourseId(), "add");
        } else {
            System.out.println("Course already exists.");
        }
    }
    // In CourseManager.java

    public void deleteCourse(String id) {
        Iterator<Course> it =courses.iterator();
        while (it.hasNext()) {
            Course c = it.next();
            if (c.getCourseId().equals(id)) {
                String instructorId = c.getInstructorId();
                it.remove();
                saveAllCourses();

                jsonDB.updateInstructorCourseList(instructorId, id, "remove");

                System.out.println("Course deleted.");
                return;
            }
        }
        System.out.println("Course not found.");
    }
    public void editCourse(String courseId, String newTitle, String newDescription, String newInstructorId) {
        Course course = getCourseById(courseId);
        if(course!=null){
            course.setTitle(newTitle);
            course.setDescription(newDescription);
            course.setInstructorId(newInstructorId);
            saveAllCourses();
            System.out.println("Course edited.");

        }
        else {
            System.out.println("Course not found.");

        }}

    public void enrollStudent(String studentId, String courseId) {
        Course course = getCourseById(courseId);
        if (course != null) {
            ArrayList<String> students = course.getStudents();

            if (!students.contains(studentId)) {
                students.add(studentId);
                saveAllCourses();
                jsonDB.updateStudentEnrollment(studentId, courseId, "enroll");
                System.out.println("Student enrolled.");
            } else {
                System.out.println("Student already enrolled.");
            }
        }
    }

        public void removeStudent(String studentId, String courseId) {
        Course course = getCourseById(courseId);
        if (course != null) {
            ArrayList<String> students = course.getStudents();

            if (students.remove(studentId)) {
                saveAllCourses();
                System.out.println("Student removed.");
            } else {
                System.out.println("Student not enrolled in course");
            }

        } else {
            System.out.println("Course not found.");
        }
    }

    public ArrayList<Course> getAllCourses() {
        return new ArrayList<>(courses);
    }
    public void addLesson(String courseId, Lesson newLesson) {
        Course course = getCourseById(courseId);
        if (course == null) {
            System.out.println("Course not found.");
            return;
        }

        ArrayList<Lesson> lessons = course.getLessons();
        for (Lesson lesson : lessons) {
            if (lesson.getLessonId().equals(newLesson.getLessonId())) {
                System.out.println("Lesson already exists.");
                return;
            }
        }

        lessons.add(newLesson);
        saveAllCourses();
        System.out.println("Lesson added successfully.");
    }

    public void deleteLesson(String courseId, String lessonId) {
        Course course = getCourseById(courseId);
        if (course == null) {
            System.out.println("Course not found.");
            return;
        }

        ArrayList<Lesson> lessons = course.getLessons();
        Iterator<Lesson> it = lessons.iterator();
        while (it.hasNext()) {
            Lesson lesson = it.next();
            if (lesson.getLessonId().equals(lessonId)) {
                it.remove();
                saveAllCourses();
                System.out.println("Lesson deleted successfully.");
                return;
            }
        }

        System.out.println("Lesson not found.");
    }
    public void editLesson(String courseId, String lessonId, Lesson newLesson) {
        Course course = getCourseById(courseId);
        if (course == null) {
            System.out.println("Course not found.");
            return;
        }
        for (Lesson lesson : course.getLessons()) {
            if (lesson.getLessonId().equals(lessonId)) {
                lesson.setTitle(newLesson.getTitle());
                lesson.setContent(newLesson.getContent());
                lesson.setResources(newLesson.getResources());
                saveAllCourses();
                System.out.println("Lesson edited successfully.");
                return;
            }
        }
        System.out.println("Lesson not found.");
    }


    public List<Course> getPendingCourses() {                   //add to admin role
        List<Course> pendingCourses = new ArrayList<>();
        for (Course c : courses) {
            if ("PENDING".equals(c.getStatus())) {
                pendingCourses.add(c);
            }
        }
        return pendingCourses;
    }

    public boolean approveCourse(String courseId) {                   //add to admin role
        Course course = getCourseById(courseId);
        if (course != null) {
            course.setStatus("APPROVED");
            saveAllCourses();
            System.out.println("Course approved.");
            return true;
        } else {
            System.out.println("Course not found.");
            return false;
        }
    }

    public boolean rejectCourse(String courseId) {                   //add to admin role
        Course course = getCourseById(courseId);
        if (course != null) {
            course.setStatus("REJECTED");
            saveAllCourses();
            System.out.println("Course rejected.");
            return true;
        } else {
            System.out.println("Course not found.");
            return false;
        }
    }
}
