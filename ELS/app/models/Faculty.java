package models;

/**
 * @author Stefan
 * class for depicting database table: faculties entries for Forms and attribute matching
 */
public class Faculty {

    //attributes
    private String  facultyName;

    //getter and setter

    public Faculty(String facultyName) {
        this.facultyName = facultyName;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }
}
