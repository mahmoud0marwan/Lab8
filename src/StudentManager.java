import java.util.List;

public class StudentManager {
    public final JsonDatabaseManager db;
    private final CourseManager courseManager;

    public StudentManager(JsonDatabaseManager db, CourseManager courseManager) {
        this.db = db;
        this.courseManager = courseManager;
    }

    public List<Course> browseCourses() {
        return db.loadCourses();
    }

    public void enrollInCourse(String studentId, String courseId) {
        System.out.println("Enrolling student " + studentId + " in course " + courseId);

        User user = db.getUserById(studentId);
        if (user == null || !(user instanceof Student)) {
            throw new RuntimeException("Student not found: " + studentId);
        }
        Student student = (Student) user;

        Course course = courseManager.getCourseById(courseId);
        if (course == null) {
            throw new RuntimeException("Course not found: " + courseId);
        }
        student.enrollCourse(courseId);
        course.enrollStudent(studentId);

        List<User> users = db.loadUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId().equals(studentId)) {
                users.set(i, student);
                break;
            }
        }
        db.saveUsers(users);

        courseManager.updateCourse(course);

        System.out.println("Enrollment completed successfully");
    }

    public void markLessonCompleted(String studentId, String courseId, String lessonId) {
        System.out.println("Marking lesson completed: student=" + studentId + ", course=" + courseId + ", lesson=" + lessonId);

        User user = db.getUserById(studentId);
        if (user == null || !(user instanceof Student)) {
            throw new RuntimeException("Student not found");
        }
        Student student = (Student) user;

        Course course = courseManager.getCourseById(courseId);
        if (course == null) {
            throw new RuntimeException("Course not found");
        }

        if (!student.getEnrolledCourses().contains(courseId)) {
            System.out.println("Student enrolled courses: " + student.getEnrolledCourses());
            throw new RuntimeException("Student not enrolled in this course. Enroll first.");
        }

        Lesson lesson = course.getLessonById(lessonId);
        if (lesson == null) {
            throw new RuntimeException("Lesson not found: " + lessonId);
        }

        List<Lesson> lessons = course.getLessons();
        int lessonIndex = -1;
        for (int i = 0; i < lessons.size(); i++) {
            if (lessons.get(i).getLessonId().equals(lessonId)) {
                lessonIndex = i + 1; // Progress is 1-based
                break;
            }
        }

        if (lessonIndex != -1) {
            student.updateProgress(courseId, lessonIndex);

            List<User> users = db.loadUsers();
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getUserId().equals(studentId)) {
                    users.set(i, student);
                    break;
                }
            }
            db.saveUsers(users);
            System.out.println("Progress saved successfully");
        } else {
            throw new RuntimeException("Lesson index not found for: " + lessonId);
        }
    }
}