import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student extends User {
    private List<String> enrolledCourses = new ArrayList<>();
    private Map<String, Integer> progress = new HashMap<>();
    private transient CourseManager courseManager;
    private Map<String , Integer> quizScores;

    public Student() {
        this.role = "Student";
    }

    public Student(String userId, String username, String email, String password, CourseManager courseManager) {
        this();
        this.courseManager = courseManager;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.passwordHash = AuthManager.hashPassword(password);
        this.quizScores = new HashMap<>();
    }

    public int takeQuiz(String lessonId , Quiz quiz , List<Integer> answers) {
        int score = quiz.grade(answers);
        quizScores.put(lessonId, score);
        return score;
    }

    int getQuizScore(String lessonId) {
        return quizScores.getOrDefault(lessonId , -1);
    }

    public void setCourseManager(CourseManager courseManager) {
        this.courseManager = courseManager;
    }

    public void enrollCourse(String courseId) {
        if (courseId == null || courseId.trim().isEmpty()) {
            throw new IllegalArgumentException("Course ID is invalid");
        }
        if (enrolledCourses.contains(courseId)) {
            return;
        }
        enrolledCourses.add(courseId);
        progress.put(courseId, 0);
        System.out.println("Student " + this.userId + " enrolled in course: " + courseId);
    }

    public void updateProgress(String courseId, int lessonIndex) {
        if (courseId == null || courseId.trim().isEmpty()) {
            throw new IllegalArgumentException("Course ID cannot be null or empty.");
        }
        if (!enrolledCourses.contains(courseId)) {
            throw new IllegalArgumentException("Student is not enrolled in this course: " + courseId);
        }
        if (lessonIndex < 0) {
            throw new IllegalArgumentException("Lesson index cannot be negative.");
        }

        Integer currentProgress = progress.get(courseId);
        if (currentProgress == null) {
            progress.put(courseId, lessonIndex);
        } else if (lessonIndex > currentProgress) {
            progress.put(courseId, lessonIndex);
        }
        System.out.println("Student " + this.userId + " progress updated: " + courseId + " -> lesson " + lessonIndex);
    }

    public List<Course> viewEnrolledCourses() {
        if (courseManager == null) {
            throw new IllegalStateException("CourseManager not set for student");
        }
        List<Course> allCourses = courseManager.getAllCourses();
        List<Course> enrolled = new ArrayList<>();
        for (Course course : allCourses) {
            if (enrolledCourses.contains(course.getCourseId())) {
                enrolled.add(course);
            }
        }
        System.out.println("Student " + this.userId + " is enrolled in " + enrolled.size() + " courses");
        return enrolled;
    }

    public List<String> getEnrolledCourses() {
        System.out.println("Getting enrolled courses for student " + this.userId + ": " + enrolledCourses.size() + " courses");
        return new ArrayList<>(enrolledCourses);
    }

    public Map<String, Integer> getProgress() {
        return new HashMap<>(progress);
    }
}