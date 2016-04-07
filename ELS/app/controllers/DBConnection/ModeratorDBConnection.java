package controllers.DBConnection;

import play.db.DB;
import models.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Stefan
 * class for all database access related to moderators
 */
public class ModeratorDBConnection {

    //selects
    /**
     *
     * @param docID ID of doc which moderators are requested
     * @return list of Users who are moderators of document
     */
    public List<User> getModeratorsOfDocument(int docID) {

        Connection      connection          = DB.getConnection();
        List<User>      moderatorList       = new LinkedList<>();

        try{
            String              sql         = "SELECT users.email, users.surName, users.firstName, users.role, " +
                                              "users.password " +
                                              "FROM  users JOIN moderators ON users.email = moderators.email " +
                                              "JOIN documents ON moderators.docID = documents.docID " +
                                              "WHERE documents.docID = ?";
            PreparedStatement   stmt        = connection.prepareStatement(sql);
            ResultSet rs;

            //preparing, executing and analyzing resultSet
            String              dID         = Integer.toString(docID);
            stmt.setString(1, dID);
            rs = stmt.executeQuery();
            while(rs.next()) {
                //creating User based on resultSet data
                //adding User to list
                String          email       = rs.getString("email");
                String          surName     = rs.getString("surName");
                String          firstName   = rs.getString("firstName");
                int             role        = rs.getInt("role");
                String          password    = rs.getString("password");
                User            tempUser    = new User(email, surName, firstName, role, password);
                moderatorList.add(tempUser);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return moderatorList;
    }


    //inserts
    /**
     * creates connection between user/moderator and document
     * @param email ID of user who will be promoted
     * @param docID ID of doc in which user will be moderator
     */
    public void addModeratorToDocument(String email, int docID) {

        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "INSERT INTO moderators " +
                                          "(email, docID) " +
                                          "VALUES (?, ?) ";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              dID     = Integer.toString(docID);
            stmt.setString(1, email);
            stmt.setString(2, dID);
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


    //deletes
    /**
     * deletes connection between referred User/moderator and document
     * @param email ID of user who will be demoted
     * @param docID ID of doc in which user will be demoted
     */
    public void deleteModeratorOfDocument(String email, int docID) {

        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "DELETE " +
                                          "FROM moderators " +
                                          "WHERE moderators.email = ?" +
                                          "AND moderators.docID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              dID     = Integer.toString(docID);
            stmt.setString(1, email);
            stmt.setString(2, dID);
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
