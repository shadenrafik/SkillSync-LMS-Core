package gui;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class HomePanel extends JPanel{

        public HomePanel() {

            setLayout(new BorderLayout());

            JLabel welcomePanel = new JLabel("Welcome to the Student Management System");
            welcomePanel.setHorizontalAlignment(JLabel.CENTER);
            welcomePanel.setFont(new Font("welcome Font", Font.BOLD, 24));
            add(welcomePanel, BorderLayout.CENTER);

        }


    }

