package backend;

import model.Student;
import model.StudentQuizAttempt;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatisticsManager {
    private JsonDatabaseManager db;
    public StatisticsManager(){db=new JsonDatabaseManager();}
    public double getLessonAverage(String courseId,String lessonId){
        List<Student>students=db.loadAllStudents();
        double total=0;
        int count=0;
        for (Student s:students){
            StudentQuizAttempt attempt=s.getQuizAttempts().getOrDefault(courseId,null)!=null?s.getQuizAttempts().get(courseId).get(lessonId):null;
            if(attempt!=null){
                total+=attempt.getScore();
                count++;
            }
        } return count==0?0:total/count;
    }
    public double getCourseAverage(String courseId){
        List<Student> students=db.loadAllStudents();
        double total=0;
        int count=0;
        for (Student s:students){
            Map<String,StudentQuizAttempt> attempts=s.getQuizAttempts().get(courseId);
            if(attempts!=null){
                for (StudentQuizAttempt a:attempts.values()){
                    total+=a.getScore();
                    count++;
                }
            }
        } return count==0?0:total/count;
    }

    public double getLessonCompletionRate(String courseId, String lessonId) {
        List<String> enrolledStudentIds = db.getEnrolledStudentIds(courseId);
        List<Student> allStudents = db.loadAllStudents();

        List<Student> enrolledStudents = allStudents.stream()
                .filter(s -> enrolledStudentIds.contains(s.getUserId()))
                .collect(Collectors.toList());

        int passed = 0;
        for (Student s : enrolledStudents) {
            if (s.hasPassedQuiz(courseId, lessonId)) {
                passed++;
            }
        }
        return enrolledStudents.isEmpty() ? 0 : (passed * 100.0 / enrolledStudents.size());
    }

    public double getCourseCompletionRate(String courseId, int totalLessons) {
        // 1. Get IDs of students enrolled in this course
        List<String> enrolledIds = db.getEnrolledStudentIds(courseId);

        // 2. Load all student objects and filter to only include enrolled ones
        List<Student> enrolledStudents = db.loadAllStudents().stream()
                .filter(s -> enrolledIds.contains(s.getUserId()))
                .collect(java.util.stream.Collectors.toList());

        int completed = 0;
        for (Student s : enrolledStudents) {
            List<String> completedLessons = s.getProgressForCourse(courseId);
            if (completedLessons.size() == totalLessons) {
                completed++;
            }
        }
        return enrolledStudents.isEmpty() ? 0 : (completed * 100.0 / enrolledStudents.size());
    }
    public void recordQuizAttempt(String studentId,String courseId,String lessonId,double score,boolean passed){
        StudentQuizAttempt attempt=new StudentQuizAttempt(score,passed);
        db.updateStudentProgress(studentId,courseId,lessonId,attempt);
    }

}