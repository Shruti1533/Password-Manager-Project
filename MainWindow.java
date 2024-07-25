import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MainWindow extends JFrame {
    private int userId;
    private JTextField appNameField, appPasswordField, searchField, deleteAppField;
    private JTextArea passwordsArea;
    private JLabel statusLabel;

    public MainWindow(int userId) {
        this.userId = userId;

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Password Manager Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Status Panel
        statusLabel = new JLabel("Welcome to the Password Manager!", JLabel.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(statusLabel, BorderLayout.NORTH);

        // Main Panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        add(mainPanel, BorderLayout.CENTER);

        // Store Password Panel
        JPanel storePanel = createModernPanel("Store Password");
        appNameField = new JTextField(15);  // Adjusted to avoid overlap
        appPasswordField = new JTextField(15);  // Adjusted to avoid overlap
        JButton addButton = new JButton("Add Password");
        styleButton(addButton);
        addButton.addActionListener(e -> {
            applyButtonEffect(addButton);
            addPassword();
        });
        addComponentsToPanel(storePanel, new JLabel("App Name:"), appNameField, new JLabel("Password:"), appPasswordField, addButton);

        // Delete Password Panel
        JPanel deletePanel = createModernPanel("Delete Password");
        deleteAppField = new JTextField(15);  // Adjusted to avoid overlap
        JButton deleteButton = new JButton("Delete Password");
        styleButton(deleteButton);
        deleteButton.addActionListener(e -> {
            applyButtonEffect(deleteButton);
            deletePassword();
        });
        addComponentsToPanel(deletePanel, new JLabel("App Name:"), deleteAppField, deleteButton);

        // Search Password Panel
        JPanel searchPanel = createModernPanel("Search Password");
        searchField = new JTextField(15);  // Adjusted to avoid overlap
        passwordsArea = new JTextArea();
        passwordsArea.setEditable(false);
        passwordsArea.setLineWrap(true);
        passwordsArea.setWrapStyleWord(true);
        passwordsArea.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(passwordsArea);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        JButton searchButton = new JButton("Search");
        styleButton(searchButton);
        searchButton.addActionListener(e -> {
            applyButtonEffect(searchButton);
            searchPasswords();
        });
        addComponentsToPanel(searchPanel, searchField, scrollPane, searchButton);

        // Layout for main panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.5;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Adding panels to main panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(storePanel, gbc);

        gbc.gridy = 1;
        mainPanel.add(deletePanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        mainPanel.add(searchPanel, gbc);

        // Initially display all passwords
        searchPasswords();
    }

    private JPanel createModernPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setBackground(Color.WHITE);
        return panel;
    }

    private void addComponentsToPanel(JPanel panel, Component... components) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        for (Component component : components) {
            panel.add(component, gbc);
            gbc.gridy++;
        }
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.PINK);
        button.setBackground(Color.BLACK);
        button.setFocusPainted(false);
    }

    private void applyButtonEffect(JButton button) {
        button.setBackground(Color.GRAY);
        Timer timer = new Timer(100, e -> button.setBackground(Color.BLACK));
        timer.setRepeats(false);
        timer.start();
    }

    private void addPassword() {
        String appName = appNameField.getText();
        String appPassword = appPasswordField.getText();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/password_manager", "root", "new_password");
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO app_passwords (user_id, app_name, app_password) VALUES (?, ?, ?)")) {
            stmt.setInt(1, userId);
            stmt.setString(2, appName);
            stmt.setString(3, appPassword);
            stmt.executeUpdate();
            statusLabel.setText("Password for " + appName + " stored successfully.");
            searchPasswords();  // Refresh the displayed passwords
        } catch (SQLException ex) {
            ex.printStackTrace();
            statusLabel.setText("Failed to store password.");
        }
    }

    private void searchPasswords() {
        String searchQuery = searchField.getText();
        passwordsArea.setText("");  // Clear the text area

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/password_manager", "root", "new_password");
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM app_passwords WHERE user_id = ? AND app_name LIKE ?")) {
            stmt.setInt(1, userId);
            stmt.setString(2, "%" + searchQuery + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // Append each password to the text area
                passwordsArea.append("App: " + rs.getString("app_name") + " - Password: " + rs.getString("app_password") + "\n");
            }
            statusLabel.setText("Search complete.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            statusLabel.setText("Failed to search passwords.");
        }
    }

    private void deletePassword() {
        String appName = deleteAppField.getText();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/password_manager", "root", "new_password");
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM app_passwords WHERE user_id = ? AND app_name = ?")) {
            stmt.setInt(1, userId);
            stmt.setString(2, appName);
            stmt.executeUpdate();
            statusLabel.setText("Password for " + appName + " deleted successfully.");
            searchPasswords();  // Refresh the displayed passwords
        } catch (SQLException ex) {
            ex.printStackTrace();
            statusLabel.setText("Failed to delete password.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow(1).setVisible(true));
    }
}
