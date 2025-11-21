package gui;

import backend.CourseManager;
import model.Course;
import model.Lesson;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import model.User;
import java.util.stream.Collectors;


public class EditCoursesPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    public JButton btnCreate;
    private JButton btnEdit, btnDelete;
    private final User loggedUser;
    private final CourseManager cmanager;


    public EditCoursesPanel(CourseManager cmanager,User loggedUser) {
        this.loggedUser = loggedUser;
        this.cmanager=cmanager;
        setLayout(new BorderLayout(6,6));
        model = new DefaultTableModel(new Object[]{"CourseId","Title","InstructorId"}, 0) {
            @Override public boolean isCellEditable(int r,int c){ return false; }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnCreate = new JButton("Create");
        btnEdit = new JButton("Edit");
        btnDelete = new JButton("Delete");
        bottom.add(btnCreate); bottom.add(btnEdit); bottom.add(btnDelete);
        add(bottom, BorderLayout.SOUTH);

        btnCreate.addActionListener(e -> onCreate());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());

        refreshCourses();
    }


    public void refreshCourses() {
        model.setRowCount(0);
        List<Course> allCourses = cmanager.getAllCourses();

        List<Course> instructorCourses = allCourses.stream()
                .filter(c -> loggedUser.getUserId().equals(c.getInstructorId()))
                .collect(Collectors.toList());

        for (Course c : instructorCourses) {
            model.addRow(new Object[]{c.getCourseId(), c.getTitle(), c.getInstructorId()});
        }
    }

    public void onCreate() {
        JTextField id = new JTextField();
        JTextField title = new JTextField();
        JTextField desc = new JTextField();
        JTextField instr = new JTextField();

        Object[] form = {
                "Course ID:", id,
                "Title:", title,
                "Description:", desc,
        };

        if (JOptionPane.showConfirmDialog(this, form, "Create Course", JOptionPane.OK_CANCEL_OPTION)
                != JOptionPane.OK_OPTION) return;

        try {
            String cid = id.getText().trim();
            String t = title.getText().trim();
            String d = desc.getText().trim();
            String iid = loggedUser.getUserId();


            ArrayList<Lesson> lessons = new ArrayList<>();
            ArrayList<String> students = new ArrayList<>();
            Course newC = new Course(cid, t, d, iid, lessons, students);

            cmanager.insertCourse(newC);
            refreshCourses();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void onEdit() {
        int r = table.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this,"Select a course"); return; }

        String cid = (String) model.getValueAt(r,0);

        Course target = cmanager.getCourseById(cid);
        if (target == null) return;
        if (!target.getInstructorId().equals(loggedUser.getUserId())) {
            JOptionPane.showMessageDialog(this, "You can only edit your own courses.");
            return;
        }
        JTextField idField = new JTextField(target.getCourseId());
        idField.setEditable(false);
        JTextField title = new JTextField(target.getTitle());
        JTextField desc = new JTextField(target.getDescription());


        Object[] form = {"Course ID", idField,"Title:", title,"Description:", desc};

        if (JOptionPane.showConfirmDialog(this, form, "Edit Course", JOptionPane.OK_CANCEL_OPTION)
                != JOptionPane.OK_OPTION) return;


        cmanager.editCourse(cid, title.getText().trim(), desc.getText().trim(), target.getInstructorId());

        refreshCourses();
    }

    private void onDelete() {
        int r = table.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this,"Select a course"); return; }
        String cid = (String) model.getValueAt(r,0);
        Course target = cmanager.getCourseById(cid);

        if (target == null) return;

        if (!target.getInstructorId().equals(loggedUser.getUserId())) {
            JOptionPane.showMessageDialog(this, "You can only delete your own courses.");
            return;
        }

        if (JOptionPane.showConfirmDialog(this,"Delete "+cid+" ?", "Confirm",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        cmanager.deleteCourse(cid);
        refreshCourses();
    }
}
