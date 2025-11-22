package model;

public class Certificate {
    private String certificateId;
    private String courseId;
    private String studentId;
    private String issueDate;
    public Certificate(String certificateId, String courseId, String studentID, String issueDate) {
        this.certificateId = certificateId;
        this.courseId = courseId;
        this.studentId = studentId;
        this.issueDate = issueDate;
    }
    public String getCertificateId() {
        return certificateId;
    }
    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }
    public String getCourseId() {
        return courseId;
    }
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
    public String getStudentID() {
        return studentId;
    }
    public void setStudentID(String studentID) {
        this.studentId = studentID;
    }
    public String getIssueDate() {
        return issueDate;
    }
    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }
}