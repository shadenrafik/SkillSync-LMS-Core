package gui;

import backend.CourseManager;
import model.Course;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList; // ⭐️ Import ArrayList
import java.util.List;
import java.util.stream.Collectors;

public class ViewEnrolledStudentsPanel extends JPanel {

    private JComboBox<String> courseSelector;
    private JTable table;
    private DefaultTableModel model;
    private final CourseManager cmanager;
    private final User loggedUser;

    public ViewEnrolledStudentsPanel(CourseManager cmanager, User loggedUser) {
        this.loggedUser = loggedUser;
        this.cmanager = cmanager;
        setLayout(new BorderLayout(8, 8));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        courseSelector = new JComboBox<>();
        JButton loadBtn = new JButton("Load");
        top.add(new JLabel("Course:"));
        top.add(courseSelector);
        top.add(loadBtn);
        add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"Student ID"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadBtn.addActionListener(e -> loadStudents());
        loadCoursesIntoSelector();
    }


    private void loadCoursesIntoSelector() {
        courseSelector.removeAllItems();
        List<Course> allCourses = cmanager.getAllCourses();
        List<Course> instructorCourses = allCourses.stream()
                .filter(c -> loggedUser.getUserId().equals(c.getInstructorId()))
                .collect(Collectors.toList());

        for (Course c : instructorCourses) {
            courseSelector.addItem(c.getCourseId() + " - " + c.getTitle());
        }

        if (courseSelector.getItemCount() > 0) { // Auto-load if courses exist
            loadStudents();
        }
    }

    private void loadStudents() {
        model.setRowCount(0);
        String sel = (String) courseSelector.getSelectedItem();
        if (sel == null) return;
        String cid = sel.split(" - ")[0];

        Course chosen = cmanager.getCourseById(cid);

        if (chosen == null) return;

        // ⭐️ FIX: Use ArrayList<String> to match the Course model's getter
        ArrayList<String> st = chosen.getStudents();
        if (st == null || st.isEmpty()) return;

        for (String sid : st) {
            model.addRow(new Object[]{sid});
        }
    }
}