import java.util.ArrayList;
import java.util.List;

public class Instructor extends User {
    private List<String> createdCourses = new ArrayList<>();
    private transient CourseManager courseManager;

    public Instructor() {
        this.role = "Instructor";
    }

    public Instructor(String userId, String username, String email, String password, CourseManager courseManager) {
        this();
        this.courseManager = courseManager;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.passwordHash = AuthManager.hashPassword(password);
    }

    public void setCourseManager(CourseManager courseManager) {
        this.courseManager = courseManager;
    }

    public String createCourse(String title, String description) {
        if (courseManager == null) {
            throw new IllegalStateException("CourseManager not set for instructor");
        }
        String courseId = courseManager.createCourse(this.userId, title, description);
        if (courseId != null) {
            createdCourses.add(courseId);
            System.out.println("Instructor " + this.userId + " created course: " + courseId);

            if (courseManager.j != null) {
                List<User> users = courseManager.j.loadUsers();
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i).getUserId().equals(this.userId)) {
                        users.set(i, this);
                        courseManager.j.saveUsers(users);
                        break;
                    }
                }
            }
        }
        return courseId;
    }

    public void editCourse(String courseId, String title, String description) {
        if (courseManager == null) {
            throw new IllegalStateException("CourseManager not set for instructor");
        }
        Course course = courseManager.getCourseById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("Course not found");
        }
        if (!course.getInstructorId().equals(this.userId)) {
            throw new IllegalArgumentException("You cannot edit this course");
        }

        if (title != null && !title.trim().isEmpty()) {
            course.setTitle(title.trim());
        }
        if (description != null && !description.trim().isEmpty()) {
            course.setDescription(description.trim());
        }

        courseManager.updateCourse(course);
    }

    public void addLesson(String courseId, Lesson lesson) {
        if (courseManager == null) {
            throw new IllegalStateException("CourseManager not set for instructor");
        }
        courseManager.addLesson(courseId, lesson);
    }

    public void editLesson(String courseId, Lesson updatedLesson) {
        if (courseManager == null) {
            throw new IllegalStateException("CourseManager not set for instructor");
        }
        courseManager.editLesson(courseId, updatedLesson);
    }

    public void deleteLesson(String courseId, String lessonId) {
        if (courseManager == null) {
            throw new IllegalStateException("CourseManager not set for instructor");
        }
        courseManager.removeLesson(courseId, lessonId);
    }

    public List<Student> viewEnrolledStudents(String courseId) {
        if (courseManager == null) {
            throw new IllegalStateException("CourseManager not set for instructor");
        }
        Course course = courseManager.getCourseById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("Course not found");
        }
        if (!course.getInstructorId().equals(this.userId)) {
            throw new IllegalArgumentException("You cannot view students for this course");
        }
        return course.getStudents();
    }

    public List<String> getCreatedCourses() {
        System.out.println("Instructor " + this.userId + " has created " + createdCourses.size() + " courses");
        return new ArrayList<>(createdCourses);
    }
}