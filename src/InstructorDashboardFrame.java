import javax.swing.*;
import java.awt.*;
import java.util.List;

public class InstructorDashboardFrame extends JFrame {
    private final Instructor instructor;
    private final JsonDatabaseManager db;
    private final CourseManager courseManager;
    private final StudentManager studentManager;
    private final AuthManager auth;

    private final DefaultListModel<Course> coursesModel = new DefaultListModel<>();
    private final JList<Course> coursesList = new JList<>(coursesModel);

    private final DefaultListModel<Lesson> lessonsModel = new DefaultListModel<>();
    private final JList<Lesson> lessonsList = new JList<>(lessonsModel);

    public InstructorDashboardFrame(Instructor instructor, JsonDatabaseManager db, CourseManager cm, StudentManager sm, AuthManager auth) {
        super("SkillForge - Instructor (" + instructor.getUsername() + ")");
        this.instructor = instructor;
        this.db = db;
        this.courseManager = cm;
        this.studentManager = sm;
        this.auth = auth;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 520);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // left: instructor courses
        JPanel left = new JPanel(new BorderLayout(6,6));
        left.add(new JLabel("Your courses:"), BorderLayout.NORTH);
        left.add(new JScrollPane(coursesList), BorderLayout.CENTER);
        JPanel courseBtns = new JPanel(new GridLayout(1,4,6,6));
        JButton createBtn = new JButton("Create");
        JButton editBtn = new JButton("Edit");
        JButton viewStudentsBtn = new JButton("Students");
        JButton viewChartBtn = new JButton("View Chart");
        courseBtns.add(createBtn); courseBtns.add(editBtn); courseBtns.add(viewStudentsBtn); courseBtns.add(viewChartBtn);
        left.add(courseBtns, BorderLayout.SOUTH);

        // center: lessons
        JPanel center = new JPanel(new BorderLayout(6,6));
        center.add(new JLabel("Lessons:"), BorderLayout.NORTH);
        center.add(new JScrollPane(lessonsList), BorderLayout.CENTER);
        JPanel lessonBtns = new JPanel(new GridLayout(1,3,6,6));
        JButton addLessonBtn = new JButton("Add Lesson");
        JButton removeLessonBtn = new JButton("Remove");
        JButton addQuizBtn = new JButton("Add Quiz");
        lessonBtns.add(addLessonBtn); lessonBtns.add(removeLessonBtn); lessonBtns.add(addQuizBtn);
        center.add(lessonBtns, BorderLayout.SOUTH);

        // top logout
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutBtn = new JButton("Logout");
        top.add(logoutBtn);

        root.add(top, BorderLayout.NORTH);
        root.add(left, BorderLayout.WEST);
        root.add(center, BorderLayout.CENTER);
        add(root);

        refreshCourses();

        coursesList.addListSelectionListener(e -> {
            Course c = coursesList.getSelectedValue();
            loadLessons(c);
        });

        // Course buttons
        createBtn.addActionListener(e -> {
            JTextField title = new JTextField();
            JTextField desc = new JTextField();
            Object[] msg = {"Title:", title, "Description:", desc};
            int option = JOptionPane.showConfirmDialog(this, msg, "Create course", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String cid = courseManager.createCourse(instructor.getUserId(), title.getText().trim(), desc.getText().trim());
                db.saveCourses(courseManager.getAllCourses());
                refreshCourses();
                JOptionPane.showMessageDialog(this, "Course created: " + cid);
            }
        });

        editBtn.addActionListener(e -> {
            Course c = coursesList.getSelectedValue();
            if (c == null) { JOptionPane.showMessageDialog(this, "Select a course", "Edit", JOptionPane.WARNING_MESSAGE); return; }
            JTextField title = new JTextField(c.getTitle());
            JTextField desc = new JTextField(c.getDescription());
            Object[] msg = {"Title:", title, "Description:", desc};
            int option = JOptionPane.showConfirmDialog(this, msg, "Edit course", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                c.setTitle(title.getText().trim());
                c.setDescription(desc.getText().trim());
                courseManager.updateCourse(c);
                db.saveCourses(courseManager.getAllCourses());
                refreshCourses();
            }
        });

        viewStudentsBtn.addActionListener(e -> {
            Course c = coursesList.getSelectedValue();
            if (c == null) { JOptionPane.showMessageDialog(this, "Select a course", "Students", JOptionPane.WARNING_MESSAGE); return; }
            StringBuilder sb = new StringBuilder();
            for (Student s : c.getStudents()) sb.append(s.getUsername()).append(" (").append(s.getUserId()).append(")\n");
            JOptionPane.showMessageDialog(this, sb.length()==0? "No students" : sb.toString(), "Enrolled students", JOptionPane.INFORMATION_MESSAGE);
        });

        viewChartBtn.addActionListener(e -> {
            Course c = coursesList.getSelectedValue();
            if (c == null) { JOptionPane.showMessageDialog(this, "Select a course", "Chart", JOptionPane.WARNING_MESSAGE); return; }
            ChartFrame chart = new ChartFrame(c, db);
            chart.setVisible(true);
        });

        // Lesson buttons
        addLessonBtn.addActionListener(e -> {
            Course c = coursesList.getSelectedValue();
            if (c == null) { JOptionPane.showMessageDialog(this, "Select course", "Add lesson", JOptionPane.WARNING_MESSAGE); return; }
            JTextField title = new JTextField();
            JTextArea content = new JTextArea(6,30);
            Object[] msg = {"Lesson title:", title, "Content:", new JScrollPane(content)};
            int option = JOptionPane.showConfirmDialog(this, msg, "Add lesson", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String lid = IdGenerator.generateLessonId();
                Lesson l = new Lesson(lid, title.getText().trim(), content.getText().trim());
                courseManager.addLesson(c.getCourseId(), l);
                db.saveCourses(courseManager.getAllCourses());
                loadLessons(c);
                JOptionPane.showMessageDialog(this, "Lesson added.");
            }
        });

        removeLessonBtn.addActionListener(e -> {
            Course c = coursesList.getSelectedValue();
            Lesson l = lessonsList.getSelectedValue();
            if (c == null || l == null) { JOptionPane.showMessageDialog(this, "Select lesson", "Remove", JOptionPane.WARNING_MESSAGE); return; }
            courseManager.removeLesson(c.getCourseId(), l.getLessonId());
            db.saveCourses(courseManager.getAllCourses());
            loadLessons(c);
        });

        addQuizBtn.addActionListener(e -> {
            Course c = coursesList.getSelectedValue();
            Lesson l = lessonsList.getSelectedValue();
            if (c == null || l == null) {
                JOptionPane.showMessageDialog(this, "Select a lesson", "Add Quiz", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JTextField passingScoreField = new JTextField();
            Object[] quizMsg = {"Passing Score:", passingScoreField};
            int quizOption = JOptionPane.showConfirmDialog(this, quizMsg, "Create Quiz", JOptionPane.OK_CANCEL_OPTION);
            if (quizOption != JOptionPane.OK_OPTION) return;

            int passingScore;
            try {
                passingScore = Integer.parseInt(passingScoreField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid passing score", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Quiz quiz = new Quiz(IdGenerator.generateUserId(), passingScore);

            while (true) {
                JTextField questionField = new JTextField();
                JTextField choicesField = new JTextField(); // comma separated
                JTextField correctField = new JTextField();

                Object[] qMsg = {
                        "Question text:", questionField,
                        "Choices (comma-separated):", choicesField,
                        "Correct answer index (0-based):", correctField
                };

                int qOption = JOptionPane.showConfirmDialog(this, qMsg, "Add Quiz Question", JOptionPane.OK_CANCEL_OPTION);
                if (qOption != JOptionPane.OK_OPTION) break;

                String questionText = questionField.getText().trim();
                String[] choicesArray = choicesField.getText().trim().split(",");
                int correctIndex;
                try {
                    correctIndex = Integer.parseInt(correctField.getText().trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid correct answer index", "Error", JOptionPane.ERROR_MESSAGE);
                    continue;
                }

                quiz.addQuestion(new QuizQuestion(questionText, List.of(choicesArray), correctIndex));

                int addMore = JOptionPane.showConfirmDialog(this, "Add another question?", "Quiz", JOptionPane.YES_NO_OPTION);
                if (addMore != JOptionPane.YES_OPTION) break;
            }

            l.setQuiz(quiz);
            courseManager.editLesson(c.getCourseId(), l);
            db.saveCourses(courseManager.getAllCourses());
            JOptionPane.showMessageDialog(this, "Quiz added to lesson.");
        });

        // Logout
        logoutBtn.addActionListener(e -> {
            auth.logout();
            LoginFrame lf = new LoginFrame(auth, db, courseManager, studentManager);
            lf.setVisible(true);
            dispose();
        });
    }

    private void refreshCourses() {
        coursesModel.clear();
        for (Course c : courseManager.getAllCourses()) {
            if (instructor.getUserId().equals(c.getInstructorId())) coursesModel.addElement(c);
        }
    }

    private void loadLessons(Course c) {
        lessonsModel.clear();
        if (c == null) return;
        for (Lesson l : c.getLessons()) lessonsModel.addElement(l);
    }
}
