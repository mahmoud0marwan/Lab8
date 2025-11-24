import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class ChartFrame extends JFrame {
    private final Course course;
    private final JsonDatabaseManager db;

    public ChartFrame(Course course, JsonDatabaseManager db) {
        super("Insights - " + course.getTitle());
        this.course = course;
        this.db = db;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 400);
        setLocationRelativeTo(null);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new ChartPanel(), BorderLayout.CENTER);
    }

    private class ChartPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // compute average quiz score per lesson across all students
            List<User> users = db.loadUsers();
            int nLessons = course.getLessons().size();
            if (nLessons == 0) {
                g.drawString("No lessons", 20, 20);
                return;
            }
            double[] averages = new double[nLessons];
            int[] counts = new int[nLessons];

            for (User u : users) {
                if (u instanceof Student) {
                    Student s = (Student) u;
                    Map<String,Integer> scores = s.getQuizScores();
                    for (int i = 0; i < nLessons; i++) {
                        Lesson L = course.getLessons().get(i);
                        Integer sc = scores.get(L.getLessonId());
                        if (sc != null) {
                            averages[i] += sc;
                            counts[i]++;
                        }
                    }
                }
            }
            for (int i = 0; i < nLessons; i++) {
                if (counts[i] > 0) averages[i] /= counts[i];
            }

            // draw simple bar chart
            int w = getWidth(), h = getHeight();
            int margin = 40;
            int chartW = w - margin*2;
            int chartH = h - margin*2;
            int barW = Math.max(10, chartW / (nLessons * 2));
            int maxVal = 0;
            for (int i = 0; i < nLessons; i++) {
                int val = (int)Math.ceil(averages[i]);
                if (val > maxVal) maxVal = val;
            }
            if (maxVal == 0) maxVal = 1;

            g.drawRect(margin, margin, chartW, chartH);
            for (int i = 0; i < nLessons; i++) {
                int x = margin + i * 2 * barW + barW/2;
                int barH = (int)((averages[i] / maxVal) * (chartH - 20));
                g.setColor(Color.BLUE);
                g.fillRect(x, margin + (chartH - barH), barW, barH);
                g.setColor(Color.BLACK);
                g.drawString(String.format("%.1f", averages[i]), x, margin + (chartH - barH) - 5);
                g.drawString("L" + (i+1), x, margin + chartH + 15);
            }
        }
    }
}

