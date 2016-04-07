package models;

/**
 * @author Stefan
 * class for depicting database table: users entries for Forms and attribute matching
 */
public class User {
    //attributes
    private String  email;
    private String  firstName;
    private String  surName;
    private String  password;
    private int     role;

    //constructor
    public User(){

    }
    public User(String email, String surName, String firstName, int role, String password) {

        this.email      = email;
        this.surName    = surName;
        this.firstName  = firstName;
        this.role       = role;
        this.password   = password;
    }

    //getter and setter

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getSurName() {

        return surName;
    }

    public String getPassword() {
        return password;
    }

    public int getRole() {
        return role;
    }

    public String getFirstName() {

        return firstName;
    }
}
