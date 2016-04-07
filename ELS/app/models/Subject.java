package models;

/**
 * @author Stefan
 * class for depicting database table: subjects entries for Forms and attribute matching
 */
public class Subject {

    //attributes
    private int     subjectID;
    private String  subjectName;
    private String  facultyName;

    //getter and setter
    public Subject(int subjectID, String subjectName, String facultyName) {
        this.subjectID = subjectID;
        this.subjectName = subjectName;
        this.facultyName = facultyName;
    }

    public int getSubjectID() {
        return subjectID;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }
}
