package gui;

import backend.CourseManager;
import model.Course;
import model.Student;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class AvailableCoursesPanel extends JPanel implements ActionListener {

    private StudentDashboard parentDashboard;
    private Student currentStudent;
    private DefaultListModel<Course> courseListModel;
    private JList<Course> courseList;
    private JButton enrollButton;
    private CourseManager courseManager;

    // FIX: Updated constructor
    public AvailableCoursesPanel(StudentDashboard parent, Student student) {
        this.parentDashboard = parent;
        this.currentStudent = student;
        this.courseManager = new CourseManager("src/database/courses.json");

        setLayout(new BorderLayout(10, 10));

        courseListModel = new DefaultListModel<>();
        courseList = new JList<>(courseListModel);
        courseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        courseList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Course) {
                    setText(((Course) value).getTitle());
                }
                return c;
            }
        });

        add(new JScrollPane(courseList), BorderLayout.CENTER);

        enrollButton = new JButton("Enroll in Selected Course");
        enrollButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        enrollButton.addActionListener(this);
        add(enrollButton, BorderLayout.SOUTH);

        refreshCourseList();
    }

    public void refreshCourseList() {
        List<String> enrolledIds = currentStudent.getEnrolledCourses();
        List<Course> allCourses = courseManager.getAllCourses();

        List<Course> availableCourses = new ArrayList<>();
        for (Course course : allCourses) {
            if(/*course.getStatus() == null || */!course.getStatus().equals("APPROVED")) {          //to skip non-approved courses
                continue; // Skip non-approved courses
            }
            if (!enrolledIds.contains(course.getCourseId())) {
                availableCourses.add(course);
            }
        }

        courseListModel.clear();
        availableCourses.forEach(courseListModel::addElement);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == enrollButton) {
            Course selectedCourse = courseList.getSelectedValue();
            if (selectedCourse == null) { /* ... */ return; }

            courseManager.enrollStudent(currentStudent.getUserId(), selectedCourse.getCourseId());

            currentStudent.enrollInCourse(selectedCourse.getCourseId());


            refreshCourseList();


            parentDashboard.refreshEnrolledPanel();

            JOptionPane.showMessageDialog(this, "Successfully enrolled!");
        }
    }
}