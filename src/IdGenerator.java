import java.util.UUID;

public class IdGenerator {
    public static String generateUserId() {
        return "U-" + UUID.randomUUID().toString().substring(0, 8);
    }
    public static String generateCourseId() {
        return "C-" + UUID.randomUUID().toString().substring(0, 8);
    }
    public static String generateLessonId() {
        return "L-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
