import java.util.ArrayList;
import java.util.List;

public class CourseManager {

    List<Course> courses;
    JsonDatabaseManager j;
    private StudentManager studentManager;

    public CourseManager(JsonDatabaseManager j) {
        this.j = j;
        this.courses = j.loadCourses();
    }

    public CourseManager(JsonDatabaseManager j, StudentManager studentManager) {
        this.j = j;
        this.studentManager = studentManager;
        this.courses = j.loadCourses();

        for (Course course : courses) {
            course.setStudentManager(studentManager);
        }
    }

    public void setStudentManager(StudentManager studentManager) {
        this.studentManager = studentManager;

        for (Course course : courses) {
            course.setStudentManager(studentManager);
        }
    }

    public Course getCourseById(String id) {
        for (Course c : courses) {
            if (c.getCourseId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    public List<Course> getAllCourses() {
        return new ArrayList<>(courses);
    }

    public String createCourse(String instructorId, String title, String description) {
        String courseId = IdGenerator.generateCourseId();
        if (getCourseById(courseId) != null) {
            return null;
        }

        Course newCourse = new Course(courseId, instructorId, title, description, studentManager);
        courses.add(newCourse);
        j.saveCourses(courses);
        return courseId;
    }

    public boolean updateCourse(Course updatedCourse) {
        for (int i=0 ; i<courses.size() ; i++){
            if (courses.get(i).getCourseId().equals(updatedCourse.getCourseId())) {
            courses.set(i,updatedCourse);
                j.saveCourses(courses);
            return true;
            }

        }
        return false;
        }

        public boolean deleteCourse(String courseId) {

        return courses.removeIf(c -> c.getCourseId().equals(courseId));
        }

    public void addLesson(String courseId, Lesson lesson) {
        Course course = getCourseById(courseId);
        if (course != null) {
            course.addLesson(lesson);
            j.saveCourses(courses);
        } else {
            throw new IllegalArgumentException("Course not found: " + courseId);
        }
    }

        public void editLesson(String courseId, Lesson lesson) {
            Course course = getCourseById(courseId);
            if (course != null) {
                course.updateLesson(lesson);
                j.saveCourses(courses);
            }
        }

        public void removeLesson(String courseId, String lessonId) {
            Course course = getCourseById(courseId);
            if (course != null) {
                course.deleteLesson(lessonId);
                j.saveCourses(courses);
            }
        }
    public void addCourse(Course course)
    {
        this.courses.add(course);
        j.saveCourses(courses);
    }
    public StudentManager getStudentManager() {
        return studentManager;
    }
    }
