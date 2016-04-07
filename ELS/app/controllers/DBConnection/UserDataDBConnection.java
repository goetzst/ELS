package controllers.DBConnection;

import models.User;
import play.db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Stefan
 * class for all database access related to Users
 */
public class UserDataDBConnection {

    //select

    /**
     * all users
     * @return List of all Users
     */
    public List<User> getUsers() {
        Connection      connection          = DB.getConnection();
        List<User>      userList            = new LinkedList<>();

        try{
            String              sql         = "SELECT * " +
                                              "FROM users " +
                                              "ORDER BY surName ";
            PreparedStatement   stmt        = connection.prepareStatement(sql);
            ResultSet           rs;

            //executing and analyzing resultSet
            rs = stmt.executeQuery();
            while(rs.next()) {
                //creating document based on resultSet data
                //adding document to list
                String          email       = rs.getString("email");
                String          surName     = rs.getString("surName");
                String          firstName   = rs.getString("firstName");
                int             role        = rs.getInt("role");
                String          pass        = rs.getString("password");
                User            tempUser        = new User(email, surName, firstName, role, pass);
                userList.add(tempUser);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userList;
    }

    /**
     * specific user
     * @param email ID of user
     * @return user referred to by email, null if there is none
     */
    public User getUserByEmail(String email) {

        Connection  connection  = DB.getConnection();
        User        user        = null;

        try{
            String              sql         = "SELECT * " +
                                              "FROM users " +
                                              "WHERE email = ?";
            PreparedStatement   stmt        = connection.prepareStatement(sql);
            ResultSet           rs;

            //executing and analyzing resultSet
            stmt.setString(1, email);
            rs = stmt.executeQuery();
            if(rs.next()) {
                //creating User based on resultSet data
                String          surName     = rs.getString("surname");
                String          firstName   = rs.getString("firstName");
                int             role        = rs.getInt("role");
                String          password    = rs.getString("password");
                user                        = new User(email, surName, firstName, role, password);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }


    //insert
    /**
     * creates User with given values
     * @param surName surName of new user
     * @param firsName firstName of new user
     * @param role role of new user
     * @param password password hash
     * @param email ID of user
     */
    public void createUser(String email, String surName, String firsName, int role, String password) {

        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "INSERT INTO users " +
                                          "(email, surName, firstName, role, password) " +
                                          "VALUES (?, ?, ?, ?, ?) ";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              rl     = Integer.toString(role);
            stmt.setString(1, email);
            stmt.setString(2, surName);
            stmt.setString(3, firsName);
            stmt.setString(4, rl);
            stmt.setString(5, password);
            stmt.executeUpdate();
            //committing changes
            connection.commit();
            //closing
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //update
    /**
     * changes the surname of user referred to by given ID
     * @param email ID of user
     * @param surName new surName
     */
    public void changeUserSurName(String email, String surName) {

        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "UPDATE users " +
                                          "SET users.surName = ?" +
                                          "WHERE users.email = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            stmt.setString(1, surName);
            stmt.setString(2, email);
            stmt.executeUpdate();
            //committing changes
            connection.commit();
            //closing
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * changes the firstname of the User referred to by given ID
     * @param email ID of user
     * @param firstName new firstName
     */
    public void changeUserFirstName(String email, String firstName) {

        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "UPDATE users " +
                    "SET users.firstName = ?" +
                    "WHERE users.email = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            stmt.setString(1, firstName);
            stmt.setString(2, email);
            stmt.executeUpdate();
            //committing changes
            connection.commit();
            //closing
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * changes role of user referred to by given ID
     * @param email ID of user
     * @param role changed role
     */
    public void changeUserRole(String email, int role) {

        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "UPDATE users " +
                                          "SET users.role = ?" +
                                          "WHERE users.email = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              rl      = Integer.toString(role);
            stmt.setString(1, rl);
            stmt.setString(2, email);
            stmt.executeUpdate();
            //committing changes
            connection.commit();
            //closing
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
            //TODO exception conventions
        }
    }

    /**
     * changes password of user referred to by given ID
     * @param email ID of user
     * @param password new hash
     */
    public void changeUserPassword(String email, String password) {

        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "UPDATE users " +
                                          "SET users.password = ?" +
                                          "WHERE users.email = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            stmt.setString(1, password);
            stmt.setString(2, email);
            stmt.executeUpdate();
            //committing changes
            connection.commit();
            //closing
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
            //TODO exception conventions
        }
    }


    //delete
    /**
     * deletes user referred to by email
     * cascades tags via constraints
     * @param email ID of user subject for deletion
     */
    public void deleteUser(String email) {
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "DELETE " +
                                          "FROM users " +
                                          "WHERE users.email = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            stmt.setString(1, email);
            stmt.executeUpdate();
            //committing changes
            connection.commit();
            //closing
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
