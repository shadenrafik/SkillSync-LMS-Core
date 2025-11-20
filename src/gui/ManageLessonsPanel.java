package gui;

import backend.CourseManager;
import backend.JsonDatabaseManager;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import gui.Validation;

public class ManageLessonsPanel extends JPanel {

    private JComboBox<String> courseSelector;
    private JTable table;
    private DefaultTableModel model;

    private CourseManager manager;
    private User loggedUser;

 public ManageLessonsPanel(CourseManager manager, User loggedUser) {
        this.manager = manager;
        this.loggedUser = loggedUser;

        setLayout(new BorderLayout(10,10));

        JPanel top = new JPanel(new FlowLayout());
        courseSelector = new JComboBox<>();
        JButton loadBtn = new JButton("Load Lessons");

        top.add(new JLabel("Select Course:"));
        top.add(courseSelector);
        top.add(loadBtn);

        add(top, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"Lesson ID", "Title"}, 0){
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Add Lesson");
        JButton editBtn = new JButton("Edit Lesson");
        JButton delBtn = new JButton("Delete Lesson");

        bottom.add(addBtn);
        bottom.add(editBtn);
        bottom.add(delBtn);

        add(bottom, BorderLayout.SOUTH);

        loadBtn.addActionListener(e -> loadLessons());
        addBtn.addActionListener(e -> addLesson());
        editBtn.addActionListener(e -> editLesson());
        delBtn.addActionListener(e -> deleteLesson());

        loadCoursesIntoSelector();
     if (courseSelector.getItemCount() > 0) {
         loadLessons();
     }
    }

    private void loadCoursesIntoSelector() {
        
        courseSelector.removeAllItems();
        List<Course> allCourses = manager.getAllCourses();
        List<Course> instructorCourses = allCourses.stream()
                .filter(c -> loggedUser.getUserId().equals(c.getInstructorId()))
                .collect(Collectors.toList());

        for (Course c : instructorCourses) {
            courseSelector.addItem(c.getCourseId());
        }
    }

    private void loadLessons() {
        model.setRowCount(0);
        String selectedId = (String) courseSelector.getSelectedItem();
        if (selectedId == null) return;

        Course course = manager.getCourseById(selectedId);
        if (course == null) return;

        List<Lesson> lessons = course.getLessons();
        if (lessons == null) return;

        for (Lesson L : lessons) {
            model.addRow(new Object[]{L.getLessonId(), L.getTitle()});
        }
    }

    private void addLesson() {
        String courseId = (String) courseSelector.getSelectedItem();
        if (courseId == null) return;

        JTextField idField = new JTextField();
        JTextField titleField = new JTextField();
        JTextArea contentArea = new JTextArea(5, 20);

        Object[] form = {
                "Lesson ID:", idField,
                "Title:", titleField,
                "Content:", new JScrollPane(contentArea)
        };

        int result = JOptionPane.showConfirmDialog(this, form, "Add Lesson", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        String lid = idField.getText().trim();
        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();

        Course course = manager.getCourseById(courseId);
        if (course == null) { JOptionPane.showMessageDialog(this, "Course not found"); return; }

        if (!Validation.isValidLessonId(lid, manager, courseId)) {
            if (Validation.isEmpty(lid)) {
                JOptionPane.showMessageDialog(this, "Lesson ID cannot be empty.");
            } else {
                JOptionPane.showMessageDialog(this, "Lesson ID must be unique within the course.");
            }
            return;
        }

        if (Validation.isEmpty(title)) {
            JOptionPane.showMessageDialog(this, "Lesson title cannot be empty");
            return;
        }
        if (course.getLessons() == null) course.setLessons(new ArrayList<>());


        Lesson newLesson = new Lesson(lid, title, content, new ArrayList<>());        

        manager.addLesson(courseId, newLesson);
        loadLessons();
    }

     private void editLesson() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a lesson first"); return; }

        String lid = (String) table.getValueAt(row, 0);
        String courseId = (String) courseSelector.getSelectedItem();

        Course course = manager.getCourseById(courseId);
        if (course == null) { JOptionPane.showMessageDialog(this, "Course not found"); return; }

        Lesson target = null;
        if (course.getLessons() != null) {
            for (Lesson L : course.getLessons()) {
                if (L.getLessonId().equals(lid)) { target = L; break; }
            }
        }
        if (target == null) { JOptionPane.showMessageDialog(this, "Lesson not found"); return; }

        JTextField titleField = new JTextField(target.getTitle());
        JTextArea contentArea = new JTextArea(target.getContent(), 5, 20);

        Object[] form = {
                "Title:", titleField,
                "Content:", new JScrollPane(contentArea)
        };

        int result = JOptionPane.showConfirmDialog(this, form, "Edit Lesson", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

         String newTitle = titleField.getText().trim();
         if(Validation.isEmpty(newTitle)) {
             JOptionPane.showMessageDialog(this, "Lesson title cannot be empty");
             return;
         }
        Lesson updated = new Lesson(target.getLessonId(), titleField.getText().trim(), contentArea.getText().trim(), target.getResources());


        manager.editLesson(courseId, lid, updated);
        loadLessons();
    }

    private void deleteLesson() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a lesson first"); return; }

        String lid = (String) table.getValueAt(row, 0);
        String courseId = (String) courseSelector.getSelectedItem();

       manager.deleteLesson(courseId, lid);
        loadLessons();
    }
}
