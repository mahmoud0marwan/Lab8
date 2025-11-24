import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class StudentDashboard extends JPanel {
    SkillForgeApp app;
    DefaultListModel<String> listModel;
    JList<String> courseList;

    public StudentDashboard(SkillForgeApp app) {
        this.app = app;
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Student Dashboard - " + app.currentUser.getUsername());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        header.add(titleLabel, BorderLayout.WEST);

        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> app.logout());
        header.add(logout, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
        listModel = new DefaultListModel<>();
        courseList = new JList<>(listModel);
        add(new JScrollPane(courseList), BorderLayout.CENTER);
        JPanel footer = new JPanel();
        JButton browseBtn = new JButton("Browse & Enroll");
        JButton openBtn = new JButton("Open Course");
        JButton myCertsBtn = new JButton("My Certificates");

        footer.add(browseBtn);
        footer.add(openBtn);
        footer.add(myCertsBtn);
        add(footer, BorderLayout.SOUTH);

        browseBtn.addActionListener(e -> browse());
        openBtn.addActionListener(e -> openCourse());
        myCertsBtn.addActionListener(e -> showCertificates());

        refreshList();
    }

    private void refreshList() {
        listModel.clear();
        Student s = (Student) app.currentUser;
        for (Course c : s.viewEnrolledCourses()) {
            listModel.addElement(c.getCourseId() + ": " + c.getTitle());
        }
    }

    private void browse() {
        List<Course> approved = app.studentManager.browseCourses();

        Object[] options = approved.stream().map(c -> c.getCourseId() + " - " + c.getTitle()).toArray();
        String selected = (String) JOptionPane.showInputDialog(this, "Select Course:", "Browse",
                JOptionPane.PLAIN_MESSAGE, null, options, null);

        if (selected != null) {
            String cId = selected.split(" - ")[0];
            app.studentManager.enrollInCourse(app.currentUser.getUserId(), cId);
            refreshList();
            JOptionPane.showMessageDialog(this, "Enrolled!");
        }
    }

    private void openCourse() {
        String selection = courseList.getSelectedValue();
        if (selection == null) return;
        String courseId = selection.split(":")[0];
        Course c = app.courseManager.getCourseById(courseId);

        JDialog d = new JDialog(app, "Course: " + c.getTitle(), true);
        d.setSize(400, 400);
        d.setLayout(new GridLayout(0, 1));

        for (Lesson l : c.getLessons()) {
            JButton lBtn = new JButton(l.getTitle());
            lBtn.addActionListener(e -> takeLesson(c, l));
            d.add(lBtn);
        }

        JButton certBtn = new JButton("CLAIM CERTIFICATE");
        certBtn.setBackground(Color.ORANGE);
        certBtn.addActionListener(e -> {
            Student s = (Student) app.currentUser;
            if (s.hasCompletedCourse(c)) {
                CertificateGenerator gen = new CertificateGenerator();
                Certificate cert = gen.generateCertificate(s, c);

                app.dbManager.saveUsers(app.dbManager.loadUsers());

                JOptionPane.showMessageDialog(d, "Certificate Generated!\nID: " + cert.getCertificateId());
            } else {
                JOptionPane.showMessageDialog(d, "Complete all quizzes first.");
            }
        });
        d.add(certBtn);

        d.setVisible(true);
    }

    private void takeLesson(Course c, Lesson l) {
        String content = "Content: " + l.getContent() + "\nResources: " + l.getResources();
        JOptionPane.showMessageDialog(this, content);

        if (l.getQuiz() != null) {
            int choice = JOptionPane.showConfirmDialog(this, "Start Quiz?", "Quiz", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                runQuiz(c, l);
            }
        } else {
            app.studentManager.markLessonCompleted(app.currentUser.getUserId(), c.getCourseId(), l.getLessonId());
        }
    }

    private void runQuiz(Course c, Lesson l) {
        Quiz q = l.getQuiz();
        List<Integer> answers = new ArrayList<>();

        for (QuizQuestion ques : q.getQuestions()) {
            String[] opts = ques.getChoices().toArray(new String[0]);
            int ans = JOptionPane.showOptionDialog(this, ques.getQuestionText(), "Question",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opts, opts[0]);
            answers.add(ans);
        }

        int score = ((Student)app.currentUser).takeQuiz(l.getLessonId(), q, answers);
        JOptionPane.showMessageDialog(this, "Score: " + score + " (Pass: " + q.getPassingScore() + ")");

        if (score >= q.getPassingScore()) {
            app.studentManager.markLessonCompleted(app.currentUser.getUserId(), c.getCourseId(), l.getLessonId());
        }
    }

    private void showCertificates() {
        Student s = (Student) app.currentUser;
        List<String> certs = s.getEarnedCertificates();
        JOptionPane.showMessageDialog(this, "My Certificates:\n" + certs);
    }
}