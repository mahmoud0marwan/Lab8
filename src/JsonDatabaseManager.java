import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonDatabaseManager {
    private final String usersFile;
    private final String coursesFile;
    private final Gson gson;
    private CourseManager courseManager;

    public JsonDatabaseManager(String usersFilePath, String coursesFilePath) {
        this.usersFile = usersFilePath;
        this.coursesFile = coursesFilePath;

        JsonDeserializer<User> userDeserializer = (json, typeOfT, context) -> {
            JsonObject jsonObject = json.getAsJsonObject();
            JsonElement roleElement = jsonObject.get("role");

            if (roleElement == null) {
                throw new JsonParseException("User object missing 'role' field.");
            }

            String role = roleElement.getAsString();

            if ("Student".equalsIgnoreCase(role)) {
                return context.deserialize(json, Student.class);
            } else if ("Instructor".equalsIgnoreCase(role)) {
                return context.deserialize(json, Instructor.class);
            } else if ("Admin".equalsIgnoreCase(role)) {  // <--- ADD THIS BLOCK
                return context.deserialize(json, Admin.class);
            } else {
                throw new JsonParseException("Unknown role: " + role);
            }
        };

        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(User.class, userDeserializer)
                .create();

        createFileIfMissing(usersFile);
        createFileIfMissing(coursesFile);
    }
    public void setCourseManager(CourseManager courseManager) {
        this.courseManager = courseManager;
    }

    private void createFileIfMissing(String filePath) {
        try {
            java.io.File file = new java.io.File(filePath);
            if (!file.exists()) {
                java.io.File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                file.createNewFile();
                try (FileWriter writer = new FileWriter(filePath)) {
                    writer.write("[]");
                }
                System.out.println("Created file: " + filePath);
            }
        } catch (Exception e) {
            System.err.println("Error creating file " + filePath + ": " + e.getMessage());
        }
    }

    public List<User> loadUsers() {
        try (FileReader reader = new FileReader(usersFile)) {
            Type listType = new TypeToken<List<User>>(){}.getType();
            List<User> users = gson.fromJson(reader, listType);

            if (users == null) {
                return new ArrayList<>();
            }

            if (courseManager != null) {
                for (User user : users) {
                    if (user instanceof Student) {
                        ((Student) user).setCourseManager(courseManager);
                    } else if (user instanceof Instructor) {
                        ((Instructor) user).setCourseManager(courseManager);
                    }
                }
            }

            return users;
        } catch (Exception e) {
            System.err.println("Warning: Could not load users (" + e.getMessage() + "). Starting with empty list.");
            return new ArrayList<>();
        }
    }

    public void saveUsers(List<User> users) {
        try (FileWriter writer = new FileWriter(usersFile)) {
            gson.toJson(users, writer);
        } catch (Exception e) {
            System.err.println("Error saving users to " + usersFile + ": " + e.getMessage());
        }
    }

    public boolean isUserIdUnique(String userId) {
        List<User> users = loadUsers();
        for (User u : users) {
            if (u.getUserId() != null && u.getUserId().equals(userId)) {
                return false;
            }
        }
        return true;
    }

    public List<Course> loadCourses() {
        try (FileReader reader = new FileReader(coursesFile)) {
            Type listType = new TypeToken<List<Course>>(){}.getType();
            List<Course> courses = gson.fromJson(reader, listType);
            return courses != null ? courses : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Error loading courses: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public void saveCourses(List<Course> courses) {
        try (FileWriter writer = new FileWriter(coursesFile)) {
            gson.toJson(courses, writer);
        } catch (Exception e) {
            System.err.println("Error saving courses: " + e.getMessage());
        }
    }

    public User getUserById(String userId) {
        List<User> users = loadUsers();
        for (User u : users) {
            if (u.getUserId() != null && u.getUserId().equals(userId)) {
                return u;
            }
        }
        return null;
    }

    public User getUserByEmail(String email) {
        List<User> users = loadUsers();
        for (User u : users) {
            if (u.getEmail() != null && u.getEmail().equalsIgnoreCase(email)) {
                return u;
            }
        }
        return null;
    }
}