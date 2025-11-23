package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

import backend.*;
import model.Course;

public class ManageCoursesPanelAdmin extends JPanel {
    private CourseManager manager;
    private DefaultTableModel model;
    private JTable table;

    private JPanel bottomPanel;
    private JButton refreshBtn, editBtn, deleteBtn;


    public ManageCoursesPanelAdmin(CourseManager manager) {
        this.manager = manager;
        setLayout(new BorderLayout(10,10));

        model = new DefaultTableModel(new Object[]{"Course ID", "Title", "Instructor ID", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);


        bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshBtn = new JButton("Refresh");
        editBtn = new JButton("Edit Course");
        deleteBtn = new JButton("Delete Course");

        bottomPanel.add(refreshBtn);
        bottomPanel.add(editBtn);
        bottomPanel.add(deleteBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> refreshCourseList());
        editBtn.addActionListener(e -> onEditCourse());
        deleteBtn.addActionListener(e -> deleteSelectedCourse());

        refreshCourseList();
    }
    
    private void refreshCourseList() {
        model.setRowCount(0);
        for (var c : manager.getAllCourses()) {
            model.addRow(new Object[]{c.getCourseId(), c.getTitle(), c.getInstructorId(), (c.getStatus()==null?"PENDING":c.getStatus())});
        }
    }

    private Course getSelectedCourse() {
        int r = table.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Select a course first.");
            return null;
        }
        String courseId = (String) model.getValueAt(r, 0);
        return manager.getCourseById(courseId);
    }

    private void onEditCourse() {
        Course c = getSelectedCourse();
        if (c == null) return;

        JTextField titleField = new JTextField(c.getTitle());
        JTextField descField = new JTextField(c.getDescription());
        JTextField instrField = new JTextField(c.getInstructorId());

        Object[] message = {
            "Title:", titleField,
            "Description:", descField,
            "Instructor ID:", instrField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "View/Edit Course", JOptionPane.OK_CANCEL_OPTION);
        if (option != JOptionPane.OK_OPTION) return;

        String newTitle = titleField.getText().trim();
        String newDesc = descField.getText().trim(); 
        String newInstrId = instrField.getText().trim();

        if(newTitle.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title cannot be empty.");
            return;
        }
        if(newInstrId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Instructor ID cannot be empty.");
            return;
        }

        manager.editCourse(c.getCourseId(), newTitle, newDesc, newInstrId);
        refreshCourseList();
        JOptionPane.showMessageDialog(this, "Course updated.");
    }

    private void deleteSelectedCourse() {
        Course c = getSelectedCourse();
        if (c == null) return;

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete course " + c.getCourseId() + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        manager.deleteCourse(c.getCourseId());
        refreshCourseList();
        JOptionPane.showMessageDialog(this, "Course deleted.");
    }
    

}
