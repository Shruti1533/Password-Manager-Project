import javax.swing.*;
import java.awt.*;

class FirstScreen {
    JFrame frame;
    JLabel text = new JLabel("PASSWORD MANAGER");
    JLabel text2 = new JLabel("Developer: Shruti Chandan");
    JProgressBar progressBar = new JProgressBar();
    JLabel message = new JLabel();

    FirstScreen() {
        createGUI();
        addText();
        addProgressBar();
        addMessage();
        addTitle();
        runningPBar();
    }

    public void createGUI() {
        frame = new JFrame();
        frame.getContentPane().setLayout(null);
        frame.setUndecorated(true);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(Color.BLACK);
        frame.setVisible(true);
    }

    public void addText() {
        text.setFont(new Font("Segoe UI", Font.BOLD, 30));
        text.setBounds(0, 160, frame.getWidth(), 60);
        text.setHorizontalAlignment(SwingConstants.CENTER);
        text.setForeground(Color.WHITE);
        frame.add(text);
    }

    public void addTitle() {
        text2.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        text2.setBounds(0, frame.getHeight() - 30, frame.getWidth(), 20);
        text2.setHorizontalAlignment(SwingConstants.CENTER);
        text2.setForeground(new Color(173, 216, 230));
        frame.add(text2);
    }

    public void addMessage() {
        message.setBounds(0, 320, frame.getWidth(), 40);
        message.setForeground(Color.WHITE);
        message.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        message.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(message);
    }

    public void addProgressBar() {
        progressBar.setBounds(100, 280, 400, 30);
        progressBar.setBorderPainted(false);
        progressBar.setStringPainted(true);
        progressBar.setBackground(new Color(50, 50, 50));
        progressBar.setForeground(new Color(144, 238, 144));
        progressBar.setValue(0);
        frame.add(progressBar);
    }

    public void runningPBar() {
        int i = 0;
        while (i <= 100) {
            try {
                Thread.sleep(40);
                progressBar.setValue(i);
                message.setText("LOADING... (" + i + "%)");
                i++;
                if (i == 100)
                    frame.dispose();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        new LoginWindow().setVisible(true); // Show login window after splash screen
    }

    public static void main(String[] args) {
        new FirstScreen();
    }
}
