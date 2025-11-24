package gui;

import backend.CourseManager;
import model.Course;
import javax.swing.*;
import java.awt.*;


public class InstructorInsightsFrame extends JFrame {
    private CourseManager courseManager;

    public InstructorInsightsFrame(String courseId, String instructorId) {
        courseManager = new CourseManager("src/database/courses.json");
        Course course = courseManager.getCourseById(courseId);

        if (course == null) {
            JOptionPane.showMessageDialog(this, "Course not found");
            dispose();
            return;
        }

        if(instructorId != null && !course.getInstructorId().equals(instructorId)) {
            JOptionPane.showMessageDialog(this, "You do not own this course");
            dispose();
            return;
        }

        new ChartFrame(course);
        dispose();
    }
}