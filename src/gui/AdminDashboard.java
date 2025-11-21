package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import backend.CourseManager;
import model.Admin;
import model.User;

public class AdminDashboard extends JPanel implements ActionListener {

    private CourseManager manager;
    private User loggedUser;

    private JButton pendingCoursesButton,manageCoursesButton, logoutButton;
    private CardLayout contentLayout;
    private JPanel contentPanel;

    private ActionListener logoutListener;

    public AdminDashboard(User loggedUser) {
        this.loggedUser = loggedUser;
        setLayout(new BorderLayout());
        manager = new CourseManager("src/database/courses.json");
        initializeNavBar();
        initContentPanel();
    }

    private void initializeNavBar() {
        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        navBar.add(titleLabel);

        JLabel userLabel = new JLabel("Logged in as: " + loggedUser.getUsername());
        userLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        navBar.add(userLabel);

        pendingCoursesButton = createNavBtn("Pending Courses");
        manageCoursesButton = createNavBtn("Manage Courses");
        logoutButton = createNavBtn("Logout");

        navBar.add(pendingCoursesButton);
        navBar.add(manageCoursesButton);
        navBar.add(logoutButton);

        JButton[] buttons = { pendingCoursesButton, manageCoursesButton, logoutButton };
        for (JButton btn : buttons) {
            btn.addActionListener(this);
        }

        add(navBar, BorderLayout.NORTH);
    }

    private JButton createNavBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 16));
        return btn;
    }

    private void initContentPanel() {
        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.add(new HomePanel(), "Home");
        contentPanel.add(new PendingCoursesPanel(manager), "PendingCoursesPanel");
        //contentPanel.add(new ManageCoursesPanel(manager), "ManageCourses");
        add(contentPanel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == pendingCoursesButton) {
            contentLayout.show(contentPanel, "PendingCoursesPanel");
        } else if (src == manageCoursesButton) {
            contentLayout.show(contentPanel, "ManageCourses");
        } else if (src == logoutButton) {
        if (logoutListener != null) {
            logoutListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "logout"));
            return;
            }
        //  logout → return to LoginPanel
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

    

    // public static void main(String[] args) {
    //     JFrame frame = new JFrame("Admin Dashboard Test");
    //     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //     frame.setSize(800, 600);
    //     Admin testUser = new Admin ("A1", "admin1","A@A.com","adminpass");
    //     AdminDashboard adminDashboard = new AdminDashboard(testUser);
    //     frame.add(adminDashboard);
    //     frame.setVisible(true);
    // }
}
