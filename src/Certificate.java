public class Certificate {

    private String certificateId;
    private String courseId;
    private String studentId;
    private String issueDate;

    public Certificate(String certificateId, String courseId, String studentId, String issueDate) {
        this.certificateId = certificateId;
        this.courseId = courseId;
        this.studentId = studentId;
        this.issueDate = issueDate;
    }

    public String getCertificateId() { return certificateId; }
    public String getCourseId() { return courseId; }
    public String getStudentId() { return studentId; }
    public String getIssueDate() { return issueDate; }

    public String toJson() {
        return "{\n" +
                "  \"certificateId\": \"" + certificateId + "\",\n" +
                "  \"courseId\": \"" + courseId + "\",\n" +
                "  \"studentId\": \"" + studentId + "\",\n" +
                "  \"issueDate\": \"" + issueDate + "\"\n" +
                "}";
    }
}
