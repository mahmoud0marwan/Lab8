import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JPanel {
    SkillForgeApp app;
    DefaultTableModel tableModel;
    JTable table;

    public AdminDashboard(SkillForgeApp app) {
        this.app = app;
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Admin Dashboard - Pending Approvals");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        header.add(titleLabel, BorderLayout.WEST);

        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> app.logout());
        header.add(logout, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        String[] cols = {"Course ID", "Title", "Instructor", "Status"};
        tableModel = new DefaultTableModel(cols, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel footer = new JPanel();
        JButton approveBtn = new JButton("Approve");
        JButton rejectBtn = new JButton("Reject");
        JButton refreshBtn = new JButton("Refresh");

        footer.add(approveBtn);
        footer.add(rejectBtn);
        footer.add(refreshBtn);
        add(footer, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadData());
        approveBtn.addActionListener(e -> processCourse(true));
        rejectBtn.addActionListener(e -> processCourse(false));

        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Course> pending = Admin.viewPendingCourses(app.dbManager);
        for (Course c : pending) {
            tableModel.addRow(new Object[]{c.getCourseId(), c.getTitle(), c.getInstructorId(), c.getCourseStatus()});
        }
    }

    private void processCourse(boolean approve) {
        int row = table.getSelectedRow();
        if (row == -1) return;

        String courseId = (String) tableModel.getValueAt(row, 0);
        Course c = app.courseManager.getCourseById(courseId);

        if (c != null && app.currentUser instanceof Admin) {
            Admin admin = (Admin) app.currentUser;
            if (approve) admin.approveCourse(c);
            else admin.rejectCourse(c);

            app.courseManager.updateCourse(c);
            JOptionPane.showMessageDialog(this, "Success!");
            loadData();
        }
    }
}