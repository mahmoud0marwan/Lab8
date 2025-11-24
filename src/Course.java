import java.util.ArrayList;
import java.util.List;

public class Course {
    private String courseId;
    private String instructorId;
    private String title;
    private String description;
    private List<Lesson> lessons;
    private List<Student> students;
    private String courseStatus;
    private transient StudentManager studentManager;

    public Course(String courseId, String instructorId, String title, String description, StudentManager studentManager) {
        this.courseId = courseId;
        this.instructorId = instructorId;
        this.title = title;
        this.description = description;
        this.lessons = new ArrayList<>();
        this.students = new ArrayList<>();
        this.studentManager = studentManager;
        this.courseStatus = "PENDING";
    }

    public void setStudentManager(StudentManager studentManager) {
        this.studentManager = studentManager;
    }

    public void addLesson(Lesson lesson) {
        lessons.add(lesson);
    }

    public void updateLesson(Lesson lesson) {
        for (int i = 0; i < lessons.size(); i++) {
            if (lessons.get(i).getLessonId().equals(lesson.getLessonId())) {
                lessons.set(i, lesson);
                return;
            }
        }
    }

    public void deleteLesson(String lessonId) {
        lessons.removeIf(lesson -> lesson.getLessonId().equals(lessonId));
    }

    public void enrollStudent(String studentId) {
        for (Student s : students) {
            if (s.getUserId().equals(studentId)) return;
        }
        User user = studentManager.db.getUserById(studentId);
        if (user instanceof Student) {
            students.add((Student) user);
        } else {
            throw new IllegalArgumentException("User is not a student: " + studentId);
        }
    }

    public Lesson getLessonById(String lessonId) {
        for (Lesson lesson : lessons) {
            if (lesson.getLessonId().equals(lessonId))
                return lesson;
        }
        return null;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<Lesson> getLessons() {
        return new ArrayList<>(lessons);
    }

    public List<Student> getStudents() {
        return new ArrayList<>(students);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnrolled(String studentId) {
        for (Student student : students) {
            if (student.getUserId().equals(studentId))
                return true;
        }
        return false;
    }

    public void setCourseStatus(String courseStatus) {
        this.courseStatus = courseStatus;
    }

    public String getCourseStatus() {
        return courseStatus;
    }
}