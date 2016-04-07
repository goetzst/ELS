package models;

/**
 * @author Stefan
 * class to depict data used for login in Forms
 */
public class LoginData {

    //attributes
    private String  email;
    private String  password;


     //only getter
     //setter are not needed because this class is just for parsing purposes
    public String getMail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
