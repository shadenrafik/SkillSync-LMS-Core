package gui;

import backend.CourseManager;
import model.Course;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PendingCoursesPanel extends JPanel {

    private final CourseManager manager;
    private final DefaultTableModel model;
    private final JTable table;

    public PendingCoursesPanel(CourseManager manager) {
        this.manager = manager;
        setLayout(new BorderLayout(8,8));

        model = new DefaultTableModel(new Object[]{"Course ID", "Title", "Instructor ID", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRefresh = new JButton("Refresh");
        JButton btnDetails = new JButton("View Details");
        JButton btnApprove = new JButton("Approve");
        JButton btnReject = new JButton("Reject");

        bottom.add(btnRefresh);
        bottom.add(btnDetails);
        bottom.add(btnApprove);
        bottom.add(btnReject);
        add(bottom, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> refreshList());
        btnDetails.addActionListener(e -> showDetails());
        btnApprove.addActionListener(e -> doApprove());
        btnReject.addActionListener(e -> doReject());

        refreshList();
    }

    public void refreshList() {
        model.setRowCount(0);
        List<Course> pending = manager.getPendingCourses();
        for (Course c : pending) {
            model.addRow(new Object[]{c.getCourseId(), c.getTitle(), c.getInstructorId(), c.getStatus()});
        }
    }

    private Course getSelectedCourse() {
        int r = table.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Select a course first.");
            return null;
        }
        String cid = (String) model.getValueAt(r, 0);
        return manager.getCourseById(cid);
    }

    private void showDetails() {
        Course c = getSelectedCourse();
        if (c == null) return;
        StringBuilder sb = new StringBuilder();
        sb.append("Course ID: ").append(c.getCourseId()).append("\n");
        sb.append("Title: ").append(c.getTitle()).append("\n");
        sb.append("Description: ").append(c.getDescription()).append("\n");
        sb.append("Instructor ID: ").append(c.getInstructorId()).append("\n");
        sb.append("Status: ").append(c.getStatus()).append("\n");
        sb.append("Lessons: ").append(c.getLessons() == null ? "0" : c.getLessons().size()).append("\n");
        sb.append("Students enrolled: ").append(c.getStudents() == null ? "0" : c.getStudents().size()).append("\n");

        JOptionPane.showMessageDialog(this, sb.toString(), "Course Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void doApprove() {
        Course c = getSelectedCourse();
        if (c == null) return;
        //String note = JOptionPane.showInputDialog(this, "Optional approval note (leave empty if none):");
        boolean ok = manager.approveCourse(c.getCourseId());
        if (ok) {
            JOptionPane.showMessageDialog(this, "Course approved.");
            refreshList();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to approve.");
        }
    }

    private void doReject() {
        Course c = getSelectedCourse();
        if (c == null) return;
        boolean ok = manager.rejectCourse(c.getCourseId());
        if (ok) {
            JOptionPane.showMessageDialog(this, "Course rejected.");
            refreshList();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to reject.");
        }
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Pending Courses Test");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        CourseManager cm = new CourseManager("src/database/courses.json");
        PendingCoursesPanel pcp = new PendingCoursesPanel(cm);
        f.add(pcp);
        f.setSize(600,400);
        f.setVisible(true);
    }
}
