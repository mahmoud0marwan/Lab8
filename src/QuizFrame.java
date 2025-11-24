import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class QuizFrame extends JFrame {
    private final Student student;
    private final JsonDatabaseManager db;
    private final CourseManager courseManager;
    private final StudentManager studentManager;
    private final Course course;
    private final Lesson lesson;
    private final Quiz quiz;

    private final List<ButtonGroup> groups = new ArrayList<>();

    public QuizFrame(Student student, JsonDatabaseManager db, CourseManager cm, StudentManager sm, Course course, Lesson lesson) {
        super("Quiz - " + lesson.getTitle());
        this.student = student;
        this.db = db;
        this.courseManager = cm;
        this.studentManager = sm;
        this.course = course;
        this.lesson = lesson;
        this.quiz = lesson.getQuiz();

        if (quiz == null) throw new IllegalArgumentException("No quiz");

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(8,8));
        JPanel questionsPanel = new JPanel();
        questionsPanel.setLayout(new BoxLayout(questionsPanel, BoxLayout.Y_AXIS));

        int qIndex = 0;
        for (QuizQuestion q : quiz.getQuestions()) {
            JPanel qp = new JPanel(new BorderLayout());
            qp.setBorder(BorderFactory.createTitledBorder("Question " + (qIndex+1)));
            JLabel qLabel = new JLabel("<html>" + q.getQuestionText() + "</html>");
            qp.add(qLabel, BorderLayout.NORTH);

            JPanel opts = new JPanel(new GridLayout(q.getChoices().size(), 1));
            ButtonGroup bg = new ButtonGroup();
            int optIndex = 0;
            for (String opt : q.getChoices()) {
                JRadioButton rb = new JRadioButton(opt);
                rb.setActionCommand(String.valueOf(optIndex));
                bg.add(rb);
                opts.add(rb);
                optIndex++;
            }
            groups.add(bg);
            qp.add(opts, BorderLayout.CENTER);
            questionsPanel.add(qp);
            qIndex++;
        }

        JScrollPane sc = new JScrollPane(questionsPanel);
        root.add(sc, BorderLayout.CENTER);
        JButton submit = new JButton("Submit quiz");
        root.add(submit, BorderLayout.SOUTH);

        add(root);

        submit.addActionListener(e -> {
            List<Integer> answers = new ArrayList<>();
            for (ButtonGroup bg : groups) {
                ButtonModel sel = bg.getSelection();
                if (sel == null) {
                    answers.add(-1); // unanswered
                } else {
                    answers.add(Integer.parseInt(sel.getActionCommand()));
                }
            }
            int score = quiz.grade(answers);
            student.recordQuizScore(lesson.getLessonId(), score);
            // Save student score to users.json
            List<User> users = db.loadUsers();
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getUserId().equals(student.getUserId())) {
                    users.set(i, student);
                    break;
                }
            }
            db.saveUsers(users);

            JOptionPane.showMessageDialog(this, "You scored: " + score + " out of " + quiz.getQuestions().size());
            // optionally mark lesson completed if passing
            if (score >= quiz.getPassingScore()) {
                studentManager.markLessonCompleted(student.getUserId(), course.getCourseId(), lesson.getLessonId());
                JOptionPane.showMessageDialog(this, "Quiz passed — lesson marked completed.");
            } else {
                JOptionPane.showMessageDialog(this, "Quiz not passed. Try again later.");
            }
            dispose();
        });
    }
}
