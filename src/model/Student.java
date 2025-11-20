
package model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student extends User{
    private List<String>enrolledCourses;
    private Map<String,List<String>>progress;

    public Student(){
        super("","","","","student");
        this.enrolledCourses=new ArrayList<>();
        this.progress=new HashMap<>();
    }
    public Student(String userId,String username,String email,String passwordHash){
        super(userId,username,email,passwordHash,"student");
        this.enrolledCourses=new ArrayList<>();
        this.progress=new HashMap<>();
    }
    public void enrollInCourse(String courseId){
        if (!enrolledCourses.contains(courseId)){
            enrolledCourses.add(courseId);
            progress.put(courseId,new ArrayList<>());
        }
    }
    public List<String>getProgressForCourse(String courseId){
        return progress.getOrDefault(courseId,new ArrayList<>());
    }
    public void markLessonCompleted(String courseId,String lessonId){
        progress.putIfAbsent(courseId,new ArrayList<>());
        List<String>completedLessons=progress.get(courseId);
        if (!completedLessons.contains(lessonId)){
            completedLessons.add(lessonId);
        }
    }
    public List<String>getEnrolledCourses(){ return enrolledCourses;}
    public void setEnrolledCourses(List<String>enrolledCourses){this.enrolledCourses=enrolledCourses;}
    public Map<String,List<String>>getProgress(){ return progress;}
    public void setProgress(Map<String,List<String>>progress){this.progress=progress;}

    @Override
    public String toString(){
        return "Student{"+"userId='"+userId+'\''+",username='"+username+'\''+",email='"+email+'\''+",enrolledCourses="+enrolledCourses+",progress="+progress+'}';
    }

}
