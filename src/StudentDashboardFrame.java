import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StudentDashboardFrame extends JFrame {
    private final Student student;
    private final JsonDatabaseManager db;
    private final CourseManager courseManager;
    private final StudentManager studentManager;
    private final AuthManager auth;

    private final DefaultListModel<Course> allCoursesModel = new DefaultListModel<>();
    private final JList<Course> allCoursesList = new JList<>(allCoursesModel);

    private final DefaultListModel<Course> enrolledModel = new DefaultListModel<>();
    private final JList<Course> enrolledList = new JList<>(enrolledModel);

    private final DefaultListModel<Lesson> lessonsModel = new DefaultListModel<>();
    private final JList<Lesson> lessonsList = new JList<>(lessonsModel);

    public StudentDashboardFrame(Student student, JsonDatabaseManager db, CourseManager cm, StudentManager sm, AuthManager auth) {
        super("SkillForge - Student (" + student.getUsername() + ")");
        this.student = student;
        this.db = db;
        this.courseManager = cm;
        this.studentManager = sm;
        this.auth = auth;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 520);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // Left: All approved courses
        JPanel left = new JPanel(new BorderLayout(6,6));
        left.add(new JLabel("Available courses (approved):"), BorderLayout.NORTH);
        allCoursesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        left.add(new JScrollPane(allCoursesList), BorderLayout.CENTER);
        JButton enrollBtn = new JButton("Enroll");
        left.add(enrollBtn, BorderLayout.SOUTH);

        // Center: Enrolled courses
        JPanel center = new JPanel(new BorderLayout(6,6));
        center.add(new JLabel("Your enrolled courses:"), BorderLayout.NORTH);
        enrolledList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        center.add(new JScrollPane(enrolledList), BorderLayout.CENTER);

        // Right: Lessons and actions
        JPanel right = new JPanel(new BorderLayout(6,6));
        right.add(new JLabel("Lessons:"), BorderLayout.NORTH);
        lessonsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        right.add(new JScrollPane(lessonsList), BorderLayout.CENTER);
        JPanel rhs = new JPanel(new GridLayout(3,1,6,6));
        JButton openLessonBtn = new JButton("Open lesson (view content)");
        JButton takeQuizBtn = new JButton("Take quiz (if exists)");
        JButton certBtn = new JButton("Certificates");
        rhs.add(openLessonBtn);
        rhs.add(takeQuizBtn);
        rhs.add(certBtn);
        right.add(rhs, BorderLayout.SOUTH);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutBtn = new JButton("Logout");
        top.add(logoutBtn);

        root.add(top, BorderLayout.NORTH);
        root.add(left, BorderLayout.WEST);
        root.add(center, BorderLayout.CENTER);
        root.add(right, BorderLayout.EAST);
        add(root);

        refreshAllCourses();
        refreshEnrolled();

        enrolledList.addListSelectionListener(e -> {
            Course selected = enrolledList.getSelectedValue();
            loadLessonsForCourse(selected);
        });

        enrollBtn.addActionListener(e -> {
            Course selected = allCoursesList.getSelectedValue();
            if (selected == null) { JOptionPane.showMessageDialog(this, "Select a course", "Enroll", JOptionPane.WARNING_MESSAGE); return; }
            try {
                studentManager.enrollInCourse(student.getUserId(), selected.getCourseId());
                refreshEnrolled();
                db.saveUsers(db.loadUsers());
                db.saveCourses(db.loadCourses());
                JOptionPane.showMessageDialog(this, "Enrolled successfully", "Enroll", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Enroll failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        openLessonBtn.addActionListener(e -> {
            Course c = enrolledList.getSelectedValue();
            Lesson l = lessonsList.getSelectedValue();
            if (c == null || l == null) { JOptionPane.showMessageDialog(this, "Select course and lesson", "Open", JOptionPane.WARNING_MESSAGE); return; }
            JTextArea ta = new JTextArea(l.getContent());
            ta.setLineWrap(true);
            ta.setWrapStyleWord(true);
            ta.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(ta), "Lesson: " + l.getTitle(), JOptionPane.PLAIN_MESSAGE);
        });

        takeQuizBtn.addActionListener(e -> {
            Course c = enrolledList.getSelectedValue();
            Lesson l = lessonsList.getSelectedValue();
            if (c == null || l == null) { JOptionPane.showMessageDialog(this, "Select course and lesson", "Quiz", JOptionPane.WARNING_MESSAGE); return; }
            Quiz q = l.getQuiz();
            if (q == null) { JOptionPane.showMessageDialog(this, "No quiz for this lesson", "Quiz", JOptionPane.INFORMATION_MESSAGE); return; }
            QuizFrame qf = new QuizFrame(student, db, courseManager, studentManager, c, l);
            qf.setVisible(true);
        });

        certBtn.addActionListener(e -> {
            CertificateFrame cf = new CertificateFrame(student, db, courseManager);
            cf.setVisible(true);
        });

        logoutBtn.addActionListener(e -> {
            auth.logout();
            LoginFrame lf = new LoginFrame(auth, db, courseManager, studentManager);
            lf.setVisible(true);
            dispose();
        });
    }

    private void refreshAllCourses() {
        allCoursesModel.clear();
        List<Course> all = studentManager.browseCourses();
        for (Course c : all) allCoursesModel.addElement(c);
    }

    private void refreshEnrolled() {
        enrolledModel.clear();
        for (String cid : student.getEnrolledCourses()) {
            Course c = courseManager.getCourseById(cid);
            if (c != null) enrolledModel.addElement(c);
        }
    }

    private void loadLessonsForCourse(Course c) {
        lessonsModel.clear();
        if (c == null) return;
        for (Lesson l : c.getLessons()) lessonsModel.addElement(l);
    }
}
