package gui;

import backend.AuthManager;
import backend.JsonDatabaseManager;
import model.User;
import model.Student;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;


public class LoginPanel extends JPanel implements ActionListener {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton, exitButton, registerButton;

    private AuthManager auth;

    public LoginPanel() {

        auth = new AuthManager(); // Use backend

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("SkillForge Login");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++; gbc.gridx = 0;
        add(new JLabel("Email:"), gbc);

        emailField = new JTextField(15);
        gbc.gridx = 1;
        add(emailField, gbc);

        gbc.gridy++; gbc.gridx = 0;
        add(new JLabel("Password:"), gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        add(passwordField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        add(loginButton, gbc);

        gbc.gridx = 1;
        exitButton = new JButton("Exit");
        exitButton.addActionListener(this);
        add(exitButton, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        registerButton = new JButton("Register");
        registerButton.addActionListener(this);
        add(registerButton, gbc);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == loginButton) {

            String email = emailField.getText().trim();
            String pass = new String(passwordField.getPassword()).trim();

            if (email.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter email and password.");
                return;
            }

            String role = auth.login(email, pass);

            if (role == null) {
                JOptionPane.showMessageDialog(this, "Invalid login!");
                return;
            }


            JsonDatabaseManager db = new JsonDatabaseManager();
            List<User> users = db.loadUsers();
            User loggedUser = null;
            for (User u : users) {
                if (u.getEmail() != null && u.getEmail().equalsIgnoreCase(email)) {
                    loggedUser = u;
                    break;
                }
            }

            if (loggedUser == null) {
                JOptionPane.showMessageDialog(this, "Login succeeded but user record not found. Check users.json.", "Warning", JOptionPane.WARNING_MESSAGE);
            }

            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (parent == null) {
                parent = new JFrame("SkillForge");
                parent.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                parent.setSize(900, 600);
                parent.setLocationRelativeTo(null);
            } else {
                parent.getContentPane().removeAll();
            }

            if (role.equalsIgnoreCase("instructor")) {
                parent.add(new InstructorDashboard(loggedUser));
            } else {
                if (loggedUser instanceof Student) {
                    parent.add(new StudentDashboard(loggedUser));
                } else {
                    JOptionPane.showMessageDialog(this, "Student record not available, opening minimal student dashboard.", "Warning", JOptionPane.WARNING_MESSAGE);
                    parent.add(new StudentDashboard(null));
                }
            }

            parent.revalidate();
            parent.repaint();
            parent.setVisible(true);
        }

        else if (e.getSource() == registerButton) {
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (parent != null) {
                parent.getContentPane().removeAll();
                parent.add(new SignupPanel());
                parent.revalidate();
                parent.repaint();
            } else {
                JFrame f = new JFrame("Register");
                f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                f.setSize(500, 400);
                f.setLocationRelativeTo(null);
                f.setContentPane(new SignupPanel());
                f.setVisible(true);
            }

        }

        else if (e.getSource() == exitButton) {
            System.exit(0);
        }
    }


    public static void main(String[] args) {
        JFrame f = new JFrame("Login Test");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(900, 600);
        f.add(new LoginPanel());
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
