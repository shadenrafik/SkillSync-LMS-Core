
package model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student extends User{
    private List<String>enrolledCourses;
    private Map<String,List<String>>progress;
    private Map<String, Map<String, StudentQuizAttempt>> quizAttempts;
private List<String > completedCourses;
private List<Certificate>certificates;
    public Student(){
        super("","","","","student");
        this.enrolledCourses=new ArrayList<>();
        this.progress=new HashMap<>();
        this.quizAttempts=new HashMap<>();
        this.completedCourses=new ArrayList<>();
        this.certificates=new ArrayList<>();
    }
    public Student(String userId,String username,String email,String passwordHash){
        super(userId,username,email,passwordHash,"student");
        this.enrolledCourses=new ArrayList<>();
        this.progress=new HashMap<>();
        this.quizAttempts=new HashMap<>();
    }
    public void enrollInCourse(String courseId){
        if (!enrolledCourses.contains(courseId)){
            enrolledCourses.add(courseId);
            progress.put(courseId,new ArrayList<>());
            quizAttempts.put(courseId, new HashMap<>());
        }
    }
    public List<String>getProgressForCourse(String courseId){
        return progress.getOrDefault(courseId,new ArrayList<>());
    }
    public boolean hasPassedQuiz(String courseId, String lessonId) {
        Map<String, StudentQuizAttempt> courseAttempts = quizAttempts.get(courseId);
        if (courseAttempts != null) {
            StudentQuizAttempt attempt = courseAttempts.get(lessonId);
            return attempt != null && attempt.isPassed();
        }
        return false;
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

    public Map<String, Map<String, StudentQuizAttempt>> getQuizAttempts() {
        return quizAttempts;
    }

    public void setQuizAttempts(Map<String, Map<String, StudentQuizAttempt>> quizAttempts) {
        this.quizAttempts = quizAttempts;
    }

    public List<String> getCompletedCourses() {
        return completedCourses;
    }

    public void setCompletedCourses(List<String> completedCourses) {
        this.completedCourses = completedCourses;
    }

    public List<Certificate> getCertificates() {
        return certificates;
    }

    public void setCertificates(List<Certificate> certificates) {
        this.certificates = certificates;
    }

    @Override
    public String toString(){
        return "Student{"+"userId='"+userId+'\''+",username='"+username+'\''+",email='"+email+'\''+",enrolledCourses="+enrolledCourses+",progress="+progress+",quizAttempts="+quizAttempts+'}';
    }

}
