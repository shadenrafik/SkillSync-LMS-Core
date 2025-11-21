import javax.swing.*;
import gui.LoginPanel;

public class main {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("SkillForge");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 600);
            frame.setLocationRelativeTo(null);

            frame.add(new LoginPanel());  

            frame.setVisible(true);
        });

    }
}