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

public class EnrolledCoursesPanel extends JPanel implements ActionListener {

    private StudentDashboard parentDashboard;
    private Student currentStudent;
    private DefaultListModel<Course> courseListModel;
    private JList<Course> courseList;
    private JButton viewLessonsButton;
    private CourseManager courseManager;

    public EnrolledCoursesPanel(StudentDashboard parent, Student student) {
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

        viewLessonsButton = new JButton("View Lessons for Selected Course");
        viewLessonsButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        viewLessonsButton.addActionListener(this);
        add(viewLessonsButton, BorderLayout.SOUTH);

        refreshCourseList();
    }

    public void refreshCourseList() {

        List<String> enrolledIds = currentStudent.getEnrolledCourses();

        List<Course> allCourses = courseManager.getAllCourses();

        List<Course> enrolledCourses = new ArrayList<>();
        for (Course course : allCourses) {
            if (enrolledIds.contains(course.getCourseId())) {
                enrolledCourses.add(course);
            }
        }

        courseListModel.clear();
        enrolledCourses.forEach(courseListModel::addElement);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == viewLessonsButton) {
            Course selectedCourse = courseList.getSelectedValue();

            if (selectedCourse == null) {
                JOptionPane.showMessageDialog(this, "Please select one of your courses to view.", "No Course Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }

            parentDashboard.showLessonViewer(currentStudent, selectedCourse);
        }
    }
}