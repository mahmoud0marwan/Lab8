import javax.swing.*;
import java.awt.*;
import java.util.List;

public class InstructorDashboard extends JPanel {
    SkillForgeApp app;
    JPanel insightsPanel;

    public InstructorDashboard(SkillForgeApp app) {
        this.app = app;
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Instructor Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        header.add(titleLabel, BorderLayout.WEST);

        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> app.logout());
        header.add(logout, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        JPanel controls = new JPanel();
        JButton createBtn = new JButton("Create Course");
        JButton viewInsightsBtn = new JButton("View Analytics");
        controls.add(createBtn);
        controls.add(viewInsightsBtn);
        add(controls, BorderLayout.SOUTH);

        insightsPanel = new JPanel(new GridLayout(0, 1));
        add(new JScrollPane(insightsPanel), BorderLayout.CENTER);

        createBtn.addActionListener(e -> createCourse());
        viewInsightsBtn.addActionListener(e -> showInsights());
    }

    private void createCourse() {
        String title = JOptionPane.showInputDialog("Enter Course Title:");
        String desc = JOptionPane.showInputDialog("Enter Description:");
        if (title != null && desc != null) {
            Instructor inst = (Instructor) app.currentUser;
            inst.createCourse(title, desc);
            JOptionPane.showMessageDialog(this, "Course created (Pending Approval)");
        }
    }

    private void showInsights() {
        insightsPanel.removeAll();
        Instructor inst = (Instructor) app.currentUser;
        List<String> myCourses = inst.getCreatedCourses();

        for (String cId : myCourses) {
            Course c = app.courseManager.getCourseById(cId);
            if (c == null) continue;

            JPanel chart = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(Color.WHITE);
                    g.fillRect(0, 0, getWidth(), getHeight());

                    List<Student> students = c.getStudents();
                    if (students.isEmpty()) {
                        g.setColor(Color.BLACK);
                        g.drawString("No students enrolled", 20, 20);
                        return;
                    }

                    int barWidth = 40;
                    int x = 20;
                    int maxH = 100;

                    g.setColor(Color.BLACK);
                    g.drawString("Progress for: " + c.getTitle(), 10, 15);

                    for (Student s : students) {
                        int progressIdx = s.getProgress().getOrDefault(cId, 0);
                        int total = c.getLessons().size();
                        int pct = (total == 0) ? 0 : (int)((progressIdx / (double)total) * maxH);

                        g.setColor(Color.BLUE);
                        g.fillRect(x, 120 - pct, barWidth, pct);

                        g.setColor(Color.BLACK);
                        g.drawRect(x, 120 - maxH, barWidth, maxH);
                        g.drawString(s.getUsername(), x, 135);
                        g.drawString(pct + "%", x, 120 - pct - 5);

                        x += barWidth + 20;
                    }
                }
            };
            chart.setPreferredSize(new Dimension(400, 150));
            chart.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            insightsPanel.add(chart);
        }
        insightsPanel.revalidate();
        insightsPanel.repaint();
    }
}