
package backend;
import com.google.gson.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import model.*;

public class JsonDatabaseManager{


    private static final String USERS_FILE="src/database/users.json";
    private static final String COURSES_FILE="src/database/courses.json";
    private final Gson gson;
    
    public JsonDatabaseManager() {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public List<User>loadUsers(){
        List<User>users=new ArrayList<>();
        try{
            if(!Files.exists(Paths.get(USERS_FILE))){
                Files.createFile(Paths.get(USERS_FILE));
                Files.writeString(Paths.get(USERS_FILE),"[]");
            }
            String json=Files.readString(Paths.get(USERS_FILE));
            JsonArray array=JsonParser.parseString(json).getAsJsonArray();
            for (JsonElement element : array) {
                JsonObject obj=element.getAsJsonObject();
                String role=obj.get("role").getAsString();
                if (role.equalsIgnoreCase("student")){
                    Student student=gson.fromJson(obj,Student.class);
                    users.add(student);
                }else if (role.equalsIgnoreCase("instructor")){
                    Instructor instructor=gson.fromJson(obj,Instructor.class);
                    users.add(instructor);
                }else if (role.equalsIgnoreCase("admin")){
                    Admin admin=gson.fromJson(obj,Admin.class);
                    users.add(admin);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return users;
    }
    public List<Student> loadAllStudents(){
        List<Student>students=new ArrayList<>();
        for (User user:loadUsers()){
            if (user instanceof Student student){
                students.add(student);
            }
        } return students;
    }

    public List<String> getEnrolledStudentIds(String courseId) {
        JsonArray coursesArray = loadCourses();
        for (JsonElement element : coursesArray) {
            JsonObject courseObj = element.getAsJsonObject();
            if (courseObj.get("courseId").getAsString().equals(courseId)) {
                if (courseObj.has("students") && courseObj.get("students").isJsonArray()) {
                    JsonArray studentsArray = courseObj.getAsJsonArray("students");
                    List<String> enrolledIds = new ArrayList<>();
                    for (JsonElement studentElement : studentsArray) {
                        enrolledIds.add(studentElement.getAsString());
                    }
                    return enrolledIds;
                }
            }
        }
        return Collections.emptyList();
    }
    public void saveUsers(List<User>users){
        try (Writer writer=new FileWriter(USERS_FILE)){
            gson.toJson(users,writer);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public String generateUserId(List<User>users){
        String id;
        Random rand=new Random();
        boolean unique;
        do{
            id="U"+(rand.nextInt(9000)+1000);
            unique=true;
            for(User u:users){
                if (u.getUserId().equals(id)){
                    unique=false;
                    break;
                }
            }
        }while(!unique);
        return id;
    }

    public boolean isEmailDuplicate(List<User>users,String email){
        for (User u :users){
            if (u.getEmail().equalsIgnoreCase(email)){
                return true;
            }
        }
        return false;
    }


    public JsonArray loadCourses(){
        try {
            if (!Files.exists(Paths.get(COURSES_FILE))){
                Files.createFile(Paths.get(COURSES_FILE));
                Files.writeString(Paths.get(COURSES_FILE),"[]");
            }
            String json=Files.readString(Paths.get(COURSES_FILE));
            return JsonParser.parseString(json).getAsJsonArray();
        }catch (IOException e){
            e.printStackTrace();
        }
        return new JsonArray();
    }

    public void saveCourses(JsonArray courses){
        try (Writer writer=new FileWriter(COURSES_FILE)){
            gson.toJson(courses,writer);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void updateInstructorCourseList(String instructorId, String courseId, String action) {
        List<User> users = loadUsers();
        for (User u : users) {
            if (u.getUserId().equals(instructorId) && u instanceof Instructor instructor) {
                if ("add".equalsIgnoreCase(action)) {
                    instructor.addCreatedCourse(courseId);
                } else if ("remove".equalsIgnoreCase(action)) {
                    instructor.getCreatedCourses().remove(courseId);
                }
                saveUsers(users);
                return;
            }
        }
    }
    public void updateStudentEnrollment(String studentId, String courseId, String action) {
        List<User> users = loadUsers();
        for (User u : users) {
            if (u.getUserId().equals(studentId) && u instanceof Student student) {
                if ("enroll".equalsIgnoreCase(action)) {
                    student.enrollInCourse(courseId);
                }
                saveUsers(users);
                return;
            }
        }
    }
    public void updateStudentProgress(String studentId, String courseId, String lessonId, StudentQuizAttempt attempt) {
        List<User> users = loadUsers();
        for (User u : users) {
            if (u.getUserId().equals(studentId) && u instanceof Student student) {
                student.getQuizAttempts().computeIfAbsent(courseId, k -> new HashMap<>()).put(lessonId, attempt);
                if (attempt.isPassed()) {
                    student.markLessonCompleted(courseId, lessonId);
                }
                saveUsers(users);
                return;
            }
        }
    }
}


