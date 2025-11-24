import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {
    private final AuthManager auth;
    private final JsonDatabaseManager db;
    private final CourseManager courseManager;
    private final StudentManager studentManager;

    private final JTextField emailField = new JTextField(25);
    private final JPasswordField passwordField = new JPasswordField(25);

    public LoginFrame(AuthManager auth, JsonDatabaseManager db, CourseManager cm, StudentManager sm) {
        super("SkillForge - Login");
        this.auth = auth;
        this.db = db;
        this.courseManager = cm;
        this.studentManager = sm;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 200);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel form = new JPanel(new GridLayout(3,2,6,6));
        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(new JLabel("Password:"));
        form.add(passwordField);

        JButton loginBtn = new JButton("Login");
        JButton signupBtn = new JButton("Signup");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER,10,0));
        buttons.add(loginBtn);
        buttons.add(signupBtn);

        root.add(form, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);
        setContentPane(root);

        loginBtn.addActionListener(this::onLogin);
        signupBtn.addActionListener(e -> {
            SignupFrame s = new SignupFrame(auth, db, courseManager, studentManager);
            s.setVisible(true);
            dispose();
        });
    }

    private void onLogin(ActionEvent ev) {
        String email = emailField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter email and password", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            User u = auth.login(email, pass);
            if (u == null) {
                JOptionPane.showMessageDialog(this, "Wrong email or password", "Login failed", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String role = u.getRole();
            if (role == null) role = "";

            if (role.equalsIgnoreCase("Student")) {
                // refresh student object from DB to ensure managers set
                Student student = (Student) db.getUserById(u.getUserId());
                if (student != null) student.setCourseManager(courseManager);
                StudentDashboardFrame sFrame = new StudentDashboardFrame(student,db, courseManager, studentManager,auth);
                sFrame.setVisible(true);
                dispose();
            } else if (role.equalsIgnoreCase("Instructor")) {
                Instructor ins = (Instructor) db.getUserById(u.getUserId());
                if (ins != null) ins.setCourseManager(courseManager);
                InstructorDashboardFrame iFrame = new InstructorDashboardFrame(ins, db, courseManager, studentManager, auth);
                iFrame.setVisible(true);
                dispose();
            } else if (role.equalsIgnoreCase("Admin")) {
                Admin admin = (Admin) db.getUserById(u.getUserId());
                AdminDashboardFrame aFrame = new AdminDashboardFrame(admin, db, courseManager);
                aFrame.setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Unknown role: " + role, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Login error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

