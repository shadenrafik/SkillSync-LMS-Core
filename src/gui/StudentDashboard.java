package gui;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import model.*;

public class StudentDashboard extends JPanel implements ActionListener {

    private JButton viewAvailableCoursesButton, enrolledCoursesButton, logoutButton,homeButton;
    private CardLayout contentLayout;
    private JPanel contentPanel;
    private Student currentStudent;

    private ActionListener logoutListener;
    private HomePanel homePanel;
    private AvailableCoursesPanel availableCoursesPanel;
    private EnrolledCoursesPanel enrolledCoursesPanel;
    private LessonViewerPanel lessonViewerPanel;

    public StudentDashboard(User user) {
        this.currentStudent = (Student) user;
        setLayout(new BorderLayout());
        initializeNavBar();
        initContentPanel();
    }

    private void initializeNavBar() {

        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JLabel titleLabel = new JLabel("Student Dashboard");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        navBar.add(titleLabel);


        homeButton = createNavBtn("Home");
        viewAvailableCoursesButton = createNavBtn("View Available Courses");
        enrolledCoursesButton = createNavBtn("View Enrolled Courses");
        logoutButton = createNavBtn("Logout");



        navBar.add(viewAvailableCoursesButton);
        navBar.add(enrolledCoursesButton);
        navBar.add(logoutButton);

        JButton[] buttons = {
                viewAvailableCoursesButton, enrolledCoursesButton, logoutButton,homeButton};
        for (JButton btn : buttons) {
            btn.addActionListener(this);
        }

        add(navBar, BorderLayout.NORTH);
    }

    private void initContentPanel() {
        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);

        homePanel = new HomePanel();
        availableCoursesPanel = new AvailableCoursesPanel(this,currentStudent);
        enrolledCoursesPanel = new EnrolledCoursesPanel(this, currentStudent);
        lessonViewerPanel = new LessonViewerPanel(this);
        contentPanel.add(homePanel, "Home");
        contentPanel.add(availableCoursesPanel, "ViewAvailableCourses");
        contentPanel.add(enrolledCoursesPanel, "ViewEnrolledCourses");
        contentPanel.add(lessonViewerPanel, "LessonViewer");

        add(contentPanel, BorderLayout.CENTER);;

        contentLayout.show(contentPanel, "Home");
    }
    public void showLessonViewer(Student student, Course course) {
        lessonViewerPanel.loadCourse(student, course);
        contentLayout.show(contentPanel, "LessonViewer");
    }

    public void showPanel(String panelName) {
        contentLayout.show(contentPanel, panelName);

    }
    public void refreshEnrolledPanel() {
        enrolledCoursesPanel.refreshCourseList();
    }
    private JButton createNavBtn(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 35));
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return button;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == viewAvailableCoursesButton) {
            contentLayout.show(contentPanel, "ViewAvailableCourses");
        } else if (source == homeButton) {
            contentLayout.show(contentPanel, "Home");}
        else if (source == enrolledCoursesButton) {
            contentLayout.show(contentPanel, "ViewEnrolledCourses");
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


    public void setLogoutAction(ActionListener listener) {
        this.logoutListener = listener;
    }

}