import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AdminDashboardFrame extends JFrame {

    private final Admin admin;
    private final JsonDatabaseManager db;
    private final CourseManager courseManager;

    private final DefaultListModel<Course> pendingModel = new DefaultListModel<>();
    private final JList<Course> pendingList = new JList<>(pendingModel);

    public AdminDashboardFrame(Admin admin, JsonDatabaseManager db, CourseManager cm) {
        super("SkillForge - Admin (" + admin.getUsername() + ")");
        this.admin = admin;
        this.db = db;
        this.courseManager = cm;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 520);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        pendingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        root.add(new JScrollPane(pendingList), BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton approveBtn = new JButton("Approve");
        JButton rejectBtn = new JButton("Reject");
        JButton refreshBtn = new JButton("Refresh");
        btns.add(approveBtn);
        btns.add(rejectBtn);
        btns.add(refreshBtn);

        root.add(btns, BorderLayout.SOUTH);
        add(root);

        refreshPending();

        approveBtn.addActionListener(e -> approveSelectedCourse());
        rejectBtn.addActionListener(e -> rejectSelectedCourse());
        refreshBtn.addActionListener(e -> refreshPending());
    }

    private void refreshPending() {
        pendingModel.clear();

        List<Course> pending = Admin.viewPendingCourses(db);

        for (Course c : pending) {
            pendingModel.addElement(c);
        }

        if (pending.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No pending courses at the moment.",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void approveSelectedCourse() {
        Course selected = pendingList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a course first.");
            return;
        }

        // Update status
        admin.approveCourse(selected);

        // Update CourseManager with the modified course
        courseManager.updateCourse(selected);

        // Save to JSON
        db.saveCourses(courseManager.getAllCourses());

        JOptionPane.showMessageDialog(this,
                "Course approved successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        refreshPending();
    }

    private void rejectSelectedCourse() {
        Course selected = pendingList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a course first.");
            return;
        }

        // Update status
        admin.rejectCourse(selected);

        // Update CourseManager with the modified course
        courseManager.updateCourse(selected);

        // Save to JSON
        db.saveCourses(courseManager.getAllCourses());

        JOptionPane.showMessageDialog(this,
                "Course rejected successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        refreshPending();
    }
}
