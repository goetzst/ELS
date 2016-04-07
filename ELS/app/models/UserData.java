package models;


/**
 * @author Sabrina
 * class for userData parsing purposes
 */
public class UserData {

        //attributes
        private String  firstName;
        private String  surName;
        private String  password;
        private String  oldPassword;


        //constructor
        public UserData(){

        }
        public UserData( String surName, String firstName, String password, String oldPassword) {

            this.surName    = surName;
            this.firstName  = firstName;
            this.password   = password;
            this.oldPassword= oldPassword;
        }

        //getter and setter

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getOldPassword() {
            return oldPassword;
         }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
         }

        public void setSurName(String surName) {
            this.surName = surName;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getSurName() {

            return surName;
        }

        public String getPassword() {
            return password;
        }

        public String getFirstName() {

            return firstName;
        }
    }

