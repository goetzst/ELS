package controllers.DBConnection;

import models.Faculty;
import play.db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Verena
 */
public class FacultyDBConnection {

    /**
     * Get a List of all faculties
     * @return List of all faculties
     */
    public List<Faculty> getFaculties() {

        Connection          connection  = DB.getConnection();
        ArrayList<Faculty>  fac         = new ArrayList<>();

        try {
            String              sql     = "SELECT *"
                                        + "FROM faculties;";
            PreparedStatement   stmt    = connection.prepareStatement(sql);
            ResultSet           rs;

            rs = stmt.executeQuery();

            while(rs.next()) {
                String facultyName = rs.getString("facultyName");
                fac.add(new Faculty(facultyName));
            }

            //closing
            rs.close();
            stmt.close();
            connection.close();

        } catch(SQLException e) {
            e.printStackTrace();
        }
        return fac;
    }

    /**
     * creates a new faculty in db
     * @param name name of new faculty
     */
    public void addFaculty (String name) {
        Connection connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String sql              = "INSERT INTO faculties " +
                                      "(facultyName) " +
                                      "VALUES (?) ";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            stmt.setString(1, name);
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
     * deletes referred faculty
     * @param name name of faculty staged for deletion
     */
    public void removeFaculty (String name) {
        Connection connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String sql              = "DELETE " +
                                      "FROM faculties " +
                                      "WHERE faculties.facultyName = ?";
            PreparedStatement stmt  = connection.prepareStatement(sql);

            //completing and executing statement
            stmt.setString(1, name);
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
     * changes name of faculty
     * @param oldName old name
     * @param newName new name of faculty
     */
    public void editFaculty (String oldName, String newName) {
        Connection connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String sql                  = "UPDATE faculties " +
                                          "SET faculties.facultyName = ? " +
                                          "WHERE faculties.facultyName = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            stmt.setString(1, newName);
            stmt.setString(2, oldName);
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
