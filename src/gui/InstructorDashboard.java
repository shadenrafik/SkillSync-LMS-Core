package gui;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

import backend.*;
import model.User;

public class InstructorDashboard extends JPanel implements ActionListener {

    private JButton editCoursesButton, viewEnrolledStudentsButton, manageLessonsButton, logoutButton;
    private CardLayout contentLayout;
    private JPanel contentPanel;

    private ActionListener logoutListener;
    private CourseManager Manager;
    private User loggedUser;



    public InstructorDashboard(User loggedUser) {
        this.loggedUser = loggedUser;
        setLayout(new BorderLayout());
        Manager =new CourseManager("src/database/courses.json");
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

        navBar.add(editCoursesButton);
        navBar.add(viewEnrolledStudentsButton);
        navBar.add(manageLessonsButton);
        navBar.add(logoutButton);

        JButton[] buttons = { editCoursesButton, viewEnrolledStudentsButton, manageLessonsButton, logoutButton };
        for (JButton btn : buttons) {
            btn.addActionListener(this);
        }

        add(navBar, BorderLayout.NORTH);
    }

    private void initContentPanel() {
        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.add("Home", new HomePanel());
        contentPanel.add(new EditCoursesPanel(Manager, loggedUser), "EditCourses");
        contentPanel.add(new ViewEnrolledStudentsPanel(Manager, loggedUser), "ViewEnrolledStudents");
        contentPanel.add(new ManageLessonsPanel(Manager, loggedUser), "ManageLessons");
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
            contentLayout.show(contentPanel, "ViewEnrolledStudents");
        } else if (source == manageLessonsButton) {
            contentLayout.show(contentPanel, "ManageLessons");
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
