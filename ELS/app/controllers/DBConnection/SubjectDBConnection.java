package controllers.DBConnection;

import models.Subject;
import play.db.DB;

import java.sql.*;
import java.util.*;

/**
 * @author Verena
 */
public class SubjectDBConnection {

    /**
     *
     * @param name name of requested faculty
     * @return List of faculties with referred name
     */
    public List<Subject> getSubjectsByFacultyName(String name) {

        Connection connection = DB.getConnection();
        ArrayList<Subject> subs = new ArrayList<>();

        try {
            String              sql     = "SELECT * "
                                        + "FROM subjects "
                                        + "WHERE facultyName = ? "
                                        + "ORDER BY subjectName";
            PreparedStatement   stmt    = connection.prepareStatement(sql);
            ResultSet rs;

            stmt.setString(1,  name);
            rs = stmt.executeQuery();
            while(rs.next()) {
                int     subjectID   = rs.getInt("subjectID");
                String  subjectName = rs.getString("subjectName");
                String  facultyName = rs.getString("facultyName");
                subs.add(new Subject(subjectID, subjectName, facultyName));
            }
            connection.close();
        }catch(SQLException e) {
            e.printStackTrace();
        }

        return subs;
    }


    /**
     * adds subject to faculty
     * @param subName name of subject
     * @param facName name of faculty
     */
    public void addSubject (String subName, String facName) {
        Connection connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String sql              = "INSERT INTO subjects " +
                                      "(facultyName, subjectName) " +
                                      "VALUES (?, ?) ";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            stmt.setString(1, facName);
            stmt.setString(2, subName);
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
     * removes subject
     * @param subjectId ID of subject
     */
    public void removeSubject (int subjectId) {
        Connection connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String sql              = "DELETE " +
                                      "FROM subjects " +
                                      "WHERE subjects.subjectID = ?";
            PreparedStatement stmt  = connection.prepareStatement(sql);

            //completing and executing statement
            stmt.setInt(1, subjectId);
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
     * renames subject
     * @param subjectId ID of subject
     * @param newName new name
     */
    public void editSubject (int subjectId, String newName) {
        Connection connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String sql                  = "UPDATE subjects " +
                                          "SET subjects.subjectName = ? " +
                                          "WHERE subjects.subjectID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            stmt.setString(1, newName);
            stmt.setInt(2, subjectId);
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
