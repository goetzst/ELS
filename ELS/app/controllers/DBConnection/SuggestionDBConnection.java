package controllers.DBConnection;

import models.Suggestion;
import play.db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 *@author Stefan
 * class for DB access related to suggestions
 */
public class SuggestionDBConnection {

    /**
     * creates a new suggestion for parent with content from flashCardContent
     * @param parentID ID of parent to which suggestion is added
     * @param content the suggested content
     * @param type type of content
     * @param userMail ID of user that suggested content
     * @param kind indicates what kind of parent the suggestion has 0:= chapter, 1:= headline, 2:= subHeadline
     */
    public void createSuggestion(int parentID, String content, int type, String userMail, int kind){
        Connection connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String sql              = "INSERT INTO suggestions " +
                                      "(parentID, content, email, type, parentKind) " +
                                      "VALUES (?, ?, ?, ?, ?) ";
            PreparedStatement stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            stmt.setInt(1, parentID);
            stmt.setString(2, content);
            stmt.setString(3, userMail);
            stmt.setInt(4, type);
            stmt.setInt(5, kind);
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
     * deletes specific suggestion from database
     * @param suggestionID ID of suggestion
     */
    public void deleteSuggestion(int suggestionID){
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "DELETE " +
                    "FROM suggestions " +
                    "WHERE suggestionID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            stmt.setInt(1, suggestionID);
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
     * list of all suggestions made by specific user
     * @param email ID of user
     * @return list of all suggestions, empty list if there are none
     */
    public List<Suggestion> getSuggestionsOfUser(String email){
        Connection          connection      = DB.getConnection();
        List<Suggestion>    suggestionList  = new LinkedList<>();

        try{
            String              sql     = "SELECT * " +
                                          "FROM suggestions " +
                                          "WHERE email = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);
            ResultSet rs;

            //executing and analyzing resultSet
            stmt.setString(1, email);
            rs = stmt.executeQuery();
            while(rs.next()) {
                //creating suggestion based on resultSet data
                int         id              = rs.getInt("suggestionID");
                int         parentID        = rs.getInt("parentID");
                String      content         = rs.getString("content");
                int         type            = rs.getInt("type");
                int         parentKind      = rs.getInt("parentKind");
                suggestionList.add(new Suggestion(id, parentID, content, email, type, parentKind));
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return suggestionList;
    }

    /**
     * list of all suggestion to specific Chapter
     * @param chapterID ID of chapter
     * @return list of all suggestions for chapter, empty list if there are none
     */
    public List<Suggestion> getSuggestionsToChapter(int chapterID){
        Connection          connection      = DB.getConnection();
        List<Suggestion>    suggestionList  = new LinkedList<>();

        try{
            String              sql     = "SELECT * " +
                    "FROM suggestions " +
                    "WHERE parentID = ? AND parentKind = 0";
            PreparedStatement   stmt    = connection.prepareStatement(sql);
            ResultSet rs;

            //executing and analyzing resultSet
            stmt.setInt(1, chapterID);
            rs = stmt.executeQuery();
            while(rs.next()) {
                //creating suggestion based on resultSet data
                int         id              = rs.getInt("suggestionID");
                String      content         = rs.getString("content");
                int         type            = rs.getInt("type");
                String      email           = rs.getString("email");
                suggestionList.add(new Suggestion(id, chapterID, content, email, type, 0));
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return suggestionList;
    }

    /**
     * list of all suggestions to specific headline
     * @param headlineID ID of headline
     * @return list of all suggestions fo headline, empty list if there are none
     */
    public List<Suggestion> getSuggestionsToHeadline(int headlineID){
        Connection          connection      = DB.getConnection();
        List<Suggestion>    suggestionList  = new LinkedList<>();

        try{
            String              sql     = "SELECT * " +
                    "FROM suggestions " +
                    "WHERE parentID = ? AND parentKind = 1";
            PreparedStatement   stmt    = connection.prepareStatement(sql);
            ResultSet rs;

            //executing and analyzing resultSet
            stmt.setInt(1, headlineID);
            rs = stmt.executeQuery();
            while(rs.next()) {
                //creating suggestion based on resultSet data
                int         id              = rs.getInt("suggestionID");
                String      content         = rs.getString("content");
                int         type            = rs.getInt("type");
                String      email           = rs.getString("email");
                suggestionList.add(new Suggestion(id, headlineID, content, email, type, 0));
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return suggestionList;
    }

    /**
     * list of all suggestions to specific subHeadline
     * @param subHeadlineID ID of subHeadline
     * @return list of all suggestions fo subHeadline, empty list if there are none
     */
    public List<Suggestion> getSuggestionsToSubHeadline(int subHeadlineID){
        Connection          connection      = DB.getConnection();
        List<Suggestion>    suggestionList  = new LinkedList<>();

        try{
            String              sql     = "SELECT * " +
                    "FROM suggestions " +
                    "WHERE parentID = ? AND parentKind = 2";
            PreparedStatement   stmt    = connection.prepareStatement(sql);
            ResultSet rs;

            //executing and analyzing resultSet
            stmt.setInt(1, subHeadlineID);
            rs = stmt.executeQuery();
            while(rs.next()) {
                //creating suggestion based on resultSet data
                int         id              = rs.getInt("suggestionID");
                String      content         = rs.getString("content");
                int         type            = rs.getInt("type");
                String      email           = rs.getString("email");
                suggestionList.add(new Suggestion(id, subHeadlineID, content, email, type, 0));
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return suggestionList;
    }

    public Suggestion getSuggestionByID(int suggestionID){
        Connection          connection  = DB.getConnection();
        Suggestion          suggestion  = null;

        try{
            String              sql     = "SELECT * " +
                    "FROM suggestions " +
                    "WHERE suggestionID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);
            ResultSet           rs;

            //executing and analyzing resultSet
            stmt.setInt(1, suggestionID);
            rs = stmt.executeQuery();
            if(rs.next()) {
                //creating comment based on resultSet data
                int         parentID        = rs.getInt("parentID");
                String      email           = rs.getString("email");
                int         kind            = rs.getInt("parentKind");
                String      content         = rs.getString("content");
                int         type            = rs.getInt("type");
                suggestion                  = new Suggestion(suggestionID, parentID, content, email, type, kind);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return suggestion;
    }
}
