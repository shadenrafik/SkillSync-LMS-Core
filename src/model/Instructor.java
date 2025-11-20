
package model;
import java.util.ArrayList;
import java.util.List;

public class Instructor extends User{
    private List<String>createdCourses;

    public Instructor(){
        super("","","","","instructor");
        this.createdCourses = new ArrayList<>();
    }
    public Instructor(String userId,String username,String email,String passwordHash){
        super(userId,username,email,passwordHash,"instructor");
        this.createdCourses=new ArrayList<>();
    }
    public void addCreatedCourse(String courseId){
        if (!createdCourses.contains(courseId)){
            createdCourses.add(courseId);
        }
    }
    public List<String>getCreatedCourses(){ return createdCourses;}
    public void setCreatedCourses(List<String>createdCourses){
        this.createdCourses=createdCourses;
    }

    public String toString(){
        return "Instructor{"+"userId='"+userId+'\''+",username='"+username+'\''+",email='"+email+'\''+",createdCourses=" + createdCourses +'}';
    }
}
