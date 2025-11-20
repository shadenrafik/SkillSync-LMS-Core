package gui;

import model.Course;
import model.Lesson;
import model.Student;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class LessonViewerPanel extends JPanel implements ListSelectionListener, ActionListener {

    private StudentDashboard parentDashboard;
    private Course currentCourse;

    private JList<Lesson> lessonList;
    private DefaultListModel<Lesson> lessonListModel;
    private JTextArea lessonContentArea;
    private JButton markCompletedButton;
    private JButton backButton; // To go back to the enrolled courses
    private JLabel progressLabel;
    private Student currentStudent;

    public LessonViewerPanel(StudentDashboard parent) {
        this.parentDashboard = parent;

        setLayout(new BorderLayout(10, 10));


        JPanel topPanel = new JPanel(new BorderLayout());
        backButton = new JButton("<- Back to My Courses");
        backButton.addActionListener(this);

        progressLabel = new JLabel();
        progressLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(progressLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);


        lessonListModel = new DefaultListModel<>();
        lessonList = new JList<>(lessonListModel);
        lessonList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        lessonList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Lesson) {
                    setText(((Lesson) value).getTitle());
                }
                return c;
            }
        });

        lessonList.addListSelectionListener(this);

        JScrollPane lessonListScrollPane = new JScrollPane(lessonList);
        lessonListScrollPane.setPreferredSize(new Dimension(250, 0));
        add(lessonListScrollPane, BorderLayout.WEST);


        lessonContentArea = new JTextArea("Select a lesson to view its content.");
        lessonContentArea.setEditable(false);
        lessonContentArea.setLineWrap(true);
        lessonContentArea.setWrapStyleWord(true);
        lessonContentArea.setFont(new Font("SansSerif", Font.PLAIN, 16));
        add(new JScrollPane(lessonContentArea), BorderLayout.CENTER);


        markCompletedButton = new JButton("Mark Selected Lesson as Completed");
        markCompletedButton.addActionListener(this);
        add(markCompletedButton, BorderLayout.SOUTH);
    }


    public void loadCourse(Student student, Course course) {
        this.currentStudent = student;
        this.currentCourse = course;


        List<Lesson> lessons = currentCourse.getLessons();
        lessonListModel.clear();
        lessons.forEach(lessonListModel::addElement);


        lessonContentArea.setText("Select a lesson to view its content.");


        updateProgress();
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            Lesson selectedLesson = lessonList.getSelectedValue();
            if (selectedLesson != null) {
                lessonContentArea.setText(selectedLesson.getContent());
            } else {
                lessonContentArea.setText("Select a lesson to view its content.");
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == markCompletedButton) {
            Lesson selectedLesson = lessonList.getSelectedValue();
            if (selectedLesson == null) {
                JOptionPane.showMessageDialog(this, "Please select a lesson to mark as complete.", "No Lesson Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            currentStudent.markLessonCompleted(currentCourse.getCourseId(), selectedLesson.getLessonId());
            updateProgress();
            JOptionPane.showMessageDialog(this, "Lesson '" + selectedLesson.getTitle() + "' marked as complete!");

        } else if (e.getSource() == backButton) {
            // Tell the parent dashboard to go back
            parentDashboard.showPanel("ViewEnrolledCourses");
        }
    }

    private void updateProgress() {
        List<String> completed = currentStudent.getProgressForCourse(currentCourse.getCourseId());
        int completedCount = completed.size();
        int totalCount = currentCourse.getLessons().size();
        progressLabel.setText("Progress: " + completedCount + " / " + totalCount + " completed   ");
    }
}