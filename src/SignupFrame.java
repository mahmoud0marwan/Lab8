import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SignupFrame extends JFrame {
    private final AuthManager auth;
    private final JsonDatabaseManager db;
    private final CourseManager courseManager;
    private final StudentManager studentManager;

    private final JTextField usernameField = new JTextField(20);
    private final JTextField emailField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JComboBox<String> roleCombo = new JComboBox<>(new String[] {"Student", "Instructor", "Admin"});

    public SignupFrame(AuthManager auth, JsonDatabaseManager db, CourseManager cm, StudentManager sm) {
        super("SkillForge - Signup");
        this.auth = auth;
        this.db = db;
        this.courseManager = cm;
        this.studentManager = sm;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 260);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel form = new JPanel(new GridLayout(5,2,6,6));
        form.add(new JLabel("Username:"));
        form.add(usernameField);
        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(new JLabel("Password:"));
        form.add(passwordField);
        form.add(new JLabel("Role:"));
        form.add(roleCombo);

        JButton signupBtn = new JButton("Signup");
        JButton backBtn = new JButton("Back to Login");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER,10,0));
        buttons.add(signupBtn);
        buttons.add(backBtn);

        root.add(form, BorderLayout.CENTER);
        root.add(buttons, BorderLayout.SOUTH);
        setContentPane(root);

        signupBtn.addActionListener(this::onSignup);
        backBtn.addActionListener(e -> {
            LoginFrame lf = new LoginFrame(auth, db, courseManager, studentManager);
            lf.setVisible(true);
            dispose();
        });
    }

    private void onSignup(ActionEvent ev) {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String pw = new String(passwordField.getPassword());
        String role = (String) roleCombo.getSelectedItem();

        if (username.isEmpty() || email.isEmpty() || pw.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if ("Instructor".equalsIgnoreCase(role)) {
                Instructor ins = new Instructor();
                ins.setUsername(username);
                ins.setEmail(email);
                ins.setPasswordHash(AuthManager.hashPassword(pw));
                ins.setRole("Instructor");
                boolean ok = auth.signup(ins);
                if (!ok) { JOptionPane.showMessageDialog(this, "Email already registered", "Signup", JOptionPane.ERROR_MESSAGE); return; }
            } else if ("Admin".equalsIgnoreCase(role)) {
                Admin a = new Admin(null, username, email, pw);
                // Admin class constructor sets password hash; but our signup requires passwordHash set
                a.setPasswordHash(AuthManager.hashPassword(pw));
                a.setRole("Admin");
                boolean ok = auth.signup(a);
                if (!ok) { JOptionPane.showMessageDialog(this, "Email already registered", "Signup", JOptionPane.ERROR_MESSAGE); return; }
            } else {
                Student s = new Student();
                s.setUsername(username);
                s.setEmail(email);
                s.setPasswordHash(AuthManager.hashPassword(pw));
                s.setRole("Student");
                boolean ok = auth.signup(s);
                if (!ok) { JOptionPane.showMessageDialog(this, "Email already registered", "Signup", JOptionPane.ERROR_MESSAGE); return; }
            }

            JOptionPane.showMessageDialog(this, "Signup successful. Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
            LoginFrame lf = new LoginFrame(auth, db, courseManager, studentManager);
            lf.setVisible(true);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Signup error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
