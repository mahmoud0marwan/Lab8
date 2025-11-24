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
        for(int i=0;i<courses.size();i++)
        {
            if (courses.get(i).getCourseStatus().equalsIgnoreCase("PENDING"))
            {
                pendingCourses.add(courses.get(i));
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
            if (course.getCourseStatus().equalsIgnoreCase("APPROVED"))
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
            if (course.getCourseStatus().equalsIgnoreCase("REJECTED"))
            {
                rejectedCourses.add(course);
            }

        }
        return rejectedCourses;
    }
    public void approveCourse(Course course)
    {
        course.setCourseStatus("APPROVED");

    }
    public void rejectCourse(Course course)
    {
        course.setCourseStatus("REJECTED");
    }
}
