import javax.swing.*;
import java.awt.*;

public class SkillForgeApp extends JFrame {
    public JsonDatabaseManager dbManager;
    public CourseManager courseManager;
    public StudentManager studentManager;
    public AuthManager authManager;
    public User currentUser;

    public CardLayout cardLayout;
    public JPanel mainPanel;

    public SkillForgeApp() {
        dbManager = new JsonDatabaseManager("users.json", "courses.json");
        courseManager = new CourseManager(dbManager);
        studentManager = new StudentManager(dbManager, courseManager);
        authManager = new AuthManager(dbManager);

        courseManager.setStudentManager(studentManager);

        setTitle("Skill Forge Learning System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createLoginPanel(), "LOGIN");

        add(mainPanel);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel title = new JLabel("Skill Forge Login");
        title.setFont(new Font("Arial", Font.BOLD, 28));

        JTextField emailField = new JTextField(20);
        JPasswordField passField = new JPasswordField(20);
        JButton loginBtn = new JButton("Login");

        loginBtn.addActionListener(e -> {
            String email = emailField.getText();
            String pass = new String(passField.getPassword());
            try {
                currentUser = authManager.login(email, pass);

                if (currentUser instanceof Student) {
                    ((Student) currentUser).setCourseManager(courseManager);
                } else if (currentUser instanceof Instructor) {
                    ((Instructor) currentUser).setCourseManager(courseManager);
                }

                routeUserToDashboard();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Login Failed: " + ex.getMessage());
            }
        });

        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2; panel.add(title, gbc);
        gbc.gridwidth=1; gbc.gridy=1; panel.add(new JLabel("Email:"), gbc);
        gbc.gridx=1; panel.add(emailField, gbc);
        gbc.gridx=0; gbc.gridy=2; panel.add(new JLabel("Password:"), gbc);
        gbc.gridx=1; panel.add(passField, gbc);
        gbc.gridx=0; gbc.gridy=3; gbc.gridwidth=2; panel.add(loginBtn, gbc);

        return panel;
    }

    public void routeUserToDashboard() {
        if (currentUser instanceof Admin) {
            mainPanel.add(new AdminDashboard(this), "ADMIN");
            cardLayout.show(mainPanel, "ADMIN");
        } else if (currentUser instanceof Instructor) {
            mainPanel.add(new InstructorDashboard(this), "INSTRUCTOR");
            cardLayout.show(mainPanel, "INSTRUCTOR");
        } else if (currentUser instanceof Student) {
            mainPanel.add(new StudentDashboard(this), "STUDENT");
            cardLayout.show(mainPanel, "STUDENT");
        }
    }

    public void logout() {
        currentUser = null;
        cardLayout.show(mainPanel, "LOGIN");
    }

    public static void main(String[] args) {
        // 1. Initialize DB and Auth
        JsonDatabaseManager db = new JsonDatabaseManager("users.json", "courses.json");
        AuthManager auth = new AuthManager(db);

        // 2. Create Default Admin if not exists
        if (db.getUserByEmail("admin@skillforge.com") == null) {
            System.out.println("Creating default Admin...");
            // Admin Constructor: userId, username, email, password
            Admin admin = new Admin("temp", "Super Admin", "admin@skillforge.com", "password");
            admin.setRole("Admin"); // Important for JSON logic
            auth.signup(admin);
        }

        // 3. Create Default Instructor if not exists
        if (db.getUserByEmail("inst@skillforge.com") == null) {
            System.out.println("Creating default Instructor...");
            // Instructor Constructor: userId, username, email, password, courseManager (null for now)
            Instructor inst = new Instructor("temp", "Dr. Java", "inst@skillforge.com", "password", null);
            inst.setRole("Instructor");
            auth.signup(inst);
        }

        // 4. Create Default Student if not exists
        if (db.getUserByEmail("student@skillforge.com") == null) {
            System.out.println("Creating default Student...");
            // Student Constructor: userId, username, email, password, courseManager (null for now)
            Student student = new Student("temp", "John Student", "student@skillforge.com", "password", null);
            student.setRole("Student");
            auth.signup(student);
        }

        // 5. Launch App
        SwingUtilities.invokeLater(() -> new SkillForgeApp().setVisible(true));
    }
}