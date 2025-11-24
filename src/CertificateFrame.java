import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CertificateFrame extends JFrame {
    private final Student student;
    private final JsonDatabaseManager db;
    private final CourseManager courseManager;

    public CertificateFrame(Student student, JsonDatabaseManager db, CourseManager courseManager) {
        super("Certificates - " + student.getUsername());
        this.student = student;
        this.db = db;
        this.courseManager = courseManager;

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        DefaultListModel<String> model = new DefaultListModel<>();
        for (String cid : student.getEarnedCertificates()) model.addElement(cid);
        JList<String> list = new JList<>(model);
        add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton gen = new JButton("Generate certificate (if eligible)");
        JButton view = new JButton("View");
        bottom.add(gen); bottom.add(view);
        add(bottom, BorderLayout.SOUTH);

        gen.addActionListener(e -> {
            // attempt generation for all enrolled courses he has completed
            boolean any = false;
            for (String courseId : student.getEnrolledCourses()) {
                Course c = courseManager.getCourseById(courseId);
                if (c == null) continue;
                CertificateGenerator cg = new CertificateGenerator();
                Certificate cert = cg.generateCertificate(student, c);
                if (cert != null) {
                    JOptionPane.showMessageDialog(this, "Certificate generated: " + cert.getCertificateId());
                    model.addElement(cert.getCertificateId());
                    any = true;
                }
            }
            if (!any) JOptionPane.showMessageDialog(this, "No completed courses found.");
        });

        view.addActionListener(e -> {
            String sel = list.getSelectedValue();
            if (sel == null) { JOptionPane.showMessageDialog(this, "Select a certificate id", "View", JOptionPane.WARNING_MESSAGE); return; }
            // simple viewer: read certificates.json and show matching entry (file format is simple appended JSON)
            try {
                java.nio.file.Path p = java.nio.file.Paths.get("certificates.json");
                if (!java.nio.file.Files.exists(p)) { JOptionPane.showMessageDialog(this, "No certificates file", "View", JOptionPane.ERROR_MESSAGE); return; }
                String content = new String(java.nio.file.Files.readAllBytes(p));
                JOptionPane.showMessageDialog(this, content, "Certificates file", JOptionPane.PLAIN_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error reading certs: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

