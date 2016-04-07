package controllers.DBConnection;

import models.Reference;
import play.db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * @author Thomas
 */
public class ReferenceDBConnection {

    /**
     * Get reference by id.
     * @param referenceID id of referece
     * @return Reference
     */
    public Reference getReferenceByID(int referenceID) {

        Connection connection      = DB.getConnection();
        Reference  reference     = new Reference();

        try{
            String sql =        "SELECT * " +
                                "FROM reference " +
                                "WHERE reference.referenceID = ?";

            PreparedStatement stmt    = connection.prepareStatement(sql);
            ResultSet rs;

            //executing and analyzing resultSet
            String  ref = Integer.toString(referenceID);
            stmt.setString(1, ref);
            rs = stmt.executeQuery();
            while(rs.next()) {
                int id              = rs.getInt("referenceID");
                int contentID       = rs.getInt("contentID");
                int referencesToID  = rs.getInt("referencesToID");
                int referencesToDocID= rs.getInt("referencesToDocID");
                String name         = rs.getString("name");
                reference           = new Reference(id, contentID, referencesToID, name, referencesToDocID);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reference;
    }

    /**
     * Get all Reference from a content.
     * @param contentID of the content
     * @return LinkedList of References from the content
     */
    public LinkedList<Reference> getReferencesToContent (int contentID) {

        Connection connection               = DB.getConnection();
        LinkedList<Reference>  reference    = new LinkedList<>();

        try{
            String sql =        "SELECT * " +
                                "FROM reference " +
                                "WHERE reference.contentID = ?";

            PreparedStatement stmt  = connection.prepareStatement(sql);
            ResultSet rs;

            //executing and analyzing resultSet
            stmt.setInt(1, contentID);
            rs = stmt.executeQuery();
            while(rs.next()) {
                int id              = rs.getInt("referenceID");
                int conID           = rs.getInt("contentID");
                int referencesToID  = rs.getInt("referencesToID");
                String name         = rs.getString("name");
                int referencesToDocID= rs.getInt("referencesToDocID");
                reference.add(new Reference(id, conID, referencesToID, name, referencesToDocID));
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reference;
    }

    /**
     * Adds a reference in database
     * @param contentID id of the content to which the reference belongs
     * @param referencesToID id of content which is referred by the reference
     * @param name of the reference
     */
    public void addReference (int contentID, int referencesToID, String name, int referencesToDocID) {

        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String sql =    "INSERT INTO reference " +
                            "(contentID, referencesToID, name, referencesToDocID) " +
                            "VALUES (?, ?, ?, ?) ";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            stmt.setInt(1, contentID);
            stmt.setInt(2, referencesToID);
            stmt.setString(3, name);
            stmt.setInt(4, referencesToDocID);
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
     * Deletes a reference from database
     * @param referenceID id of reference who should be deleted
     */
    public void deleteReference(int referenceID) {

        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String sql =    "DELETE " +
                            "FROM reference " +
                            "WHERE reference.referenceID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            stmt.setInt(1, referenceID);
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
