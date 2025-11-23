import java.io.FileWriter;
import java.time.LocalDate;
import java.util.UUID;

public class CertificateGenerator {

    public Certificate generateCertificate(Student s, Course c) {

        if (!s.hasCompletedCourse(c))
            return null;

        String certId = UUID.randomUUID().toString();
        String date = LocalDate.now().toString();

        Certificate certificate = new Certificate(
                certId,
                c.getCourseId(),
                s.getId(),
                date
        );

        s.addCertificate(certId);

        saveCertificate(certificate);

        return certificate;
    }

    private void saveCertificate(Certificate certificate) {
        try {
            FileWriter fw = new FileWriter("certificates.json", true);
            fw.write(certificate.toJson());
            fw.write(",\n");
            fw.close();
        } catch (Exception e) {
            System.out.println("Error saving certificate: " + e.getMessage());
        }
    }
}