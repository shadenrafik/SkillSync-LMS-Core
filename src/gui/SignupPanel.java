package gui;

import backend.AuthManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SignupPanel extends JPanel implements ActionListener {

    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> roleComboBox;
    private JButton signupButton;
    private JButton backToLoginButton;
    private JLabel errorLabel;

    private AuthManager auth;

    public SignupPanel() {
        this.auth = new AuthManager();

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); 
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Create New Account");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++; gbc.gridx = 0;
        add(new JLabel("Username:"), gbc);

        usernameField = new JTextField(15);
        gbc.gridx = 1;
        add(usernameField, gbc);

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

        gbc.gridy++; gbc.gridx = 0;
        add(new JLabel("Confirm Password:"), gbc);

        confirmPasswordField = new JPasswordField(15);
        gbc.gridx = 1;
        add(confirmPasswordField, gbc);

        gbc.gridy++; gbc.gridx = 0;
        add(new JLabel("Role:"), gbc);

        String[] roles = {"student", "instructor"};
        roleComboBox = new JComboBox<>(roles);
        gbc.gridx = 1;
        add(roleComboBox, gbc);

        gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 2;
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        add(errorLabel, gbc);

        gbc.gridy++; gbc.gridwidth = 1;
        gbc.gridx = 0;
        signupButton = new JButton("Sign Up");
        signupButton.addActionListener(this);
        add(signupButton, gbc);

        gbc.gridx = 1;
        backToLoginButton = new JButton("Back to Login");
        backToLoginButton.addActionListener(this);
        add(backToLoginButton, gbc);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == signupButton) {
            handleSignup();
        } else if (source == backToLoginButton) {
            // Switch back to the LoginPanel
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
            parent.getContentPane().removeAll();
            parent.add(new LoginPanel());
            parent.revalidate();
            parent.repaint();
        }
    }

    private void handleSignup() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
        String role = (String) roleComboBox.getSelectedItem();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorLabel.setText("All fields are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match.");
            return;
        }

        if(!Validation.isValidEmail(email)) {
            errorLabel.setText("Invalid email format.");
            return;
        }

        if(!Validation.isValidName(username)) {
            errorLabel.setText("Invalid username. Only letters and spaces allowed.");
            return;
        }
        

        boolean in = auth.signup(username, email, password, role);

        if (in) {
            JOptionPane.showMessageDialog(this, "Registration successful! Please log in.");
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
            parent.getContentPane().removeAll();
            parent.add(new LoginPanel());
            parent.revalidate();
            parent.repaint();
        } else {
            errorLabel.setText("Signup failed. Email may be invalid or already taken.");
        }
    }
}