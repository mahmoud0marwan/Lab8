import java.util.ArrayList;
import java.util.List;

public class Admin  extends  User{


    public Admin(String userId, String username, String email, String password) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.passwordHash = AuthManager.hashPassword(password);
    }

    public static List<Course> viewPendingCourses(JsonDatabaseManager j)
    {
      List<Course> courses=j.loadCourses();
        List<Course> pendingCourses=new ArrayList<>();
        for(Course course :courses)
        {
            if (course.getCourseStatus().equals("Pending"))
            {
                pendingCourses.add(course);
            }

        }
        return pendingCourses;
    }
    public static List<Course> viewApprovedCourses(JsonDatabaseManager j)
    {
        List<Course> courses=j.loadCourses();
        List<Course> approvedCourses=new ArrayList<>();
        for(Course course :courses)
        {
            if (course.getCourseStatus().equals("Approved"))
            {
                approvedCourses.add(course);
            }

        }
        return approvedCourses;
    }
    public static List<Course> viewRejectedCourses(JsonDatabaseManager j)
    {
        List<Course> courses=j.loadCourses();
        List<Course> rejectedCourses=new ArrayList<>();
        for(Course course :courses)
        {
            if (course.getCourseStatus().equals("Rejected"))
            {
                rejectedCourses.add(course);
            }

        }
        return rejectedCourses;
    }
    private void approveCourse(Course course)
    {
        course.setCourseStatus("Approved");
    }
    private void rejectCourse(Course course)
    {
        course.setCourseStatus("Rejected");
    }
}
