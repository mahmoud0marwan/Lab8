import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                String usersPath = "database/users.json";
                String coursesPath = "database/courses.json";

                JsonDatabaseManager db = new JsonDatabaseManager(usersPath, coursesPath);
                CourseManager courseManager = new CourseManager(db);
                StudentManager studentManager = new StudentManager(db, courseManager);
                AuthManager auth = new AuthManager(db);

                // ensure courseManager references are set in DB and existing users
                db.setCourseManager(courseManager);

                // Wire managers into loaded users (JsonDatabaseManager already tries to set courseManager on load)
                // But do it explicitly for safety:
                for (User u : db.loadUsers()) {
                    if (u instanceof Student) ((Student) u).setCourseManager(courseManager);
                    if (u instanceof Instructor) ((Instructor) u).setCourseManager(courseManager);
                }

                LoginFrame login = new LoginFrame(auth, db, courseManager, studentManager);
                login.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to start: " + ex.getMessage(), "Startup error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

