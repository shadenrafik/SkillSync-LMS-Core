package gui;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

import backend.*;
import model.User;

public class InstructorDashboard extends JPanel implements ActionListener {

    private JButton editCoursesButton, viewEnrolledStudentsButton, manageLessonsButton, logoutButton,insightsButton;
    private CardLayout contentLayout;
    private JPanel contentPanel;

    private ActionListener logoutListener;
    private CourseManager Manager;
    private User loggedUser;

    private EditCoursesPanel editCoursesPanel ;
    private ViewEnrolledStudentsPanel viewEnrolledStudentsPanel ;
    private ManageLessonsPanel manageLessonsPanel ;



    public InstructorDashboard(User loggedUser) {
        this.loggedUser = loggedUser;
        setLayout(new BorderLayout());
        Manager =new CourseManager("src/database/courses.json");
        editCoursesPanel = new EditCoursesPanel(Manager, loggedUser);
        viewEnrolledStudentsPanel = new ViewEnrolledStudentsPanel(Manager, loggedUser);
        manageLessonsPanel = new ManageLessonsPanel(Manager, loggedUser);
        initializeNavBar();
        initContentPanel();
    }

    private void initializeNavBar() {

        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JLabel titleLabel = new JLabel("Instructor Dashboard");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        navBar.add(titleLabel);
        JLabel userLabel = new JLabel("Logged in as: " + loggedUser.getUsername());
        userLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        navBar.add(userLabel);
        editCoursesButton = createNavBtn("Edit Courses");
        viewEnrolledStudentsButton = createNavBtn("View Enrolled Students");
        manageLessonsButton = createNavBtn("Manage Lessons");
        logoutButton = createNavBtn("Logout");
        insightsButton = createNavBtn("Insights");

        navBar.add(editCoursesButton);
        navBar.add(viewEnrolledStudentsButton);
        navBar.add(manageLessonsButton);
        navBar.add(insightsButton);
        navBar.add(logoutButton);

        JButton[] buttons = { editCoursesButton, viewEnrolledStudentsButton, manageLessonsButton, logoutButton, insightsButton };
        for (JButton btn : buttons) {
            btn.addActionListener(this);
        }

        add(navBar, BorderLayout.NORTH);
    }

    private void initContentPanel() {
        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.add(new HomePanel(), "Home");

        contentPanel.add(editCoursesPanel, "EditCourses");
        contentPanel.add(viewEnrolledStudentsPanel, "ViewEnrolledStudents");
        contentPanel.add(manageLessonsPanel, "ManageLessons");
        add(contentPanel, BorderLayout.CENTER);
        contentLayout.show(contentPanel, "Home");

    }

    private JButton createNavBtn(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(80, 35));
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == editCoursesButton) {
            contentLayout.show(contentPanel, "EditCourses");
        } else if (source == viewEnrolledStudentsButton) {
            viewEnrolledStudentsPanel.refreshCourses();
            contentLayout.show(contentPanel, "ViewEnrolledStudents");
        } else if (source == manageLessonsButton) {
            manageLessonsPanel.refreshCourses();
            contentLayout.show(contentPanel, "ManageLessons");
        } else if (source == insightsButton) {
            openInsightsWindow();
        } else if (source == logoutButton) {

        if (logoutListener != null) {
            logoutListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "logout"));
            return;
        }

        Window window = SwingUtilities.getWindowAncestor(this);

        if (window instanceof JFrame frame) {
            frame.getContentPane().removeAll();
            frame.setContentPane(new LoginPanel());
            frame.revalidate();
            frame.repaint();
        }
    }
    }
    private void openInsightsWindow() {
        String courseId=JOptionPane.showInputDialog(this, "Enter Course ID to view Insights:","View Insights",JOptionPane.PLAIN_MESSAGE);
        if (courseId != null && !courseId.trim().isEmpty()) {
            new InstructorInsightsFrame(courseId.trim(), loggedUser.getUserId());
        }
    }
    public void setLogoutAction(ActionListener listener) {
        this.logoutListener = listener;
    }
}
