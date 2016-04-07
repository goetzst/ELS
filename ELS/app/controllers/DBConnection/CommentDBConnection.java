package controllers.DBConnection;

import models.Comment;
import models.SubComment;
import play.db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Stefan
 * class for all database access related to Comments
 * this includes SubComment related access too
 */
public class CommentDBConnection {

    //comment related methods
    //selects
    /**
     * gets all comments of a content
     * @param contentID ID of content
     * @return list of comments to content empty if there are none
     */
    public List<Comment> getCommentsOfContent(int contentID) {
        Connection      connection      = DB.getConnection();
        List<Comment>   commentList     = new LinkedList<>();

        try{
            String              sql     = "SELECT comments.commentID, comments.email, comments.content " +
                    "FROM  comments " +
                    "WHERE comments.contentID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);
            ResultSet           rs;

            //executing and analyzing resultSet
            String  cID                 = Integer.toString(contentID);
            stmt.setString(1, cID);
            rs = stmt.executeQuery();
            while(rs.next()) {
                //creating comment based on resultSet data
                //adding comment to list
                int         commentID   = rs.getInt("commentID");
                String      userEmail   = rs.getString("email");
                String      content     = rs.getString("content");
                Comment     tempComm    = new Comment(commentID, userEmail, content);
                commentList.add(tempComm);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return commentList;
    }
    /**
     *
     * @param commentID ID of requested comment
     * @return Comment with commentID, null if there is none
     */
    public Comment getCommentByID(int commentID) {

        Connection          connection  = DB.getConnection();
        Comment             comment     = null;

        try{
            String              sql     = "SELECT * " +
                                          "FROM comments " +
                                          "WHERE commentID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);
            ResultSet           rs;

            //executing and analyzing resultSet
            String              cID     = Integer.toString(commentID);
            stmt.setString(1, cID);
            rs = stmt.executeQuery();
            if(rs.next()) {
                //creating comment based on resultSet data
                String      email           = rs.getString("email");
                String      content         = rs.getString("content");
                int         contentID       = rs.getInt("contentID");
                comment                     = new Comment(commentID, email, content, contentID);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return comment;
    }

    //insert
    /**
     * creates chapter with given values
     * @param email ID of user who created comment
     * @param contentID ID of content in which comment is created
     * @param content content of comment
     */
    public void createComment(String email, int contentID, String content) {

        Connection  connection  = DB.getConnection();
        try {
            connection.setAutoCommit(false);
            String              sql     = "INSERT INTO comments " +
                                          "(email, contentID, content) " +
                                          "VALUES (?, ?, ?) ";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              cID     = Integer.toString(contentID);
            stmt.setString(1, email);
            stmt.setString(2, cID);
            stmt.setString(3, content);
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


    //updates
    /**
     * changes current content of referred comment
     * @param commentID ID of comment stc
     * @param content changed content
     */
    public void changeComment(int commentID, String content) {

        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "UPDATE comments " +
                                          "SET comments.content = ? " +
                                          "WHERE comments.commentID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              cID     = Integer.toString(commentID);
            stmt.setString(1, content);
            stmt.setString(2, cID);
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


    //delete
    /**
     * deletes referred comment
     * cascades subcomments via constraints
     * cascades tagcomments via constraints
     * @param commentID ID of comment staged for deletion
     */
    public void deleteComment(int commentID) {

        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "DELETE " +
                                          "FROM comments " +
                                          "WHERE comments.commentID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              cID     = Integer.toString(commentID);
            stmt.setString(1, cID);
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



    //subComment related methods
    //selects
    /**
     *
     * @param commentID ID of comment to which subComments are requested
     * @return list of subComments for referred commentID, empty list if there are none
     */
    public List<SubComment> getSubCommentsOfComment(int commentID) {

        Connection      connection      = DB.getConnection();
        List<SubComment> commentList    = new LinkedList<>();

        try{
            String              sql     = "SELECT subcomments.subCID, subcomments.email, subcomments.content " +
                                          "FROM  subcomments " +
                                          "WHERE subcomments.commentID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);
            ResultSet           rs;

            //executing and analyzing resultSet
            String  cID                 = Integer.toString(commentID);
            stmt.setString(1, cID);
            rs = stmt.executeQuery();
            while(rs.next()) {
                //creating SubComment based on resultSet data
                //adding SubComment to list
                int         subCommentID= rs.getInt("subCID");
                String      userEmail   = rs.getString("email");
                String      content     = rs.getString("content");
                SubComment  tempSubComm = new SubComment(subCommentID, userEmail, content);
                commentList.add(tempSubComm);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return commentList;
    }

    /**
     *
     * @param subCommentID ID of requested subComment
     * @return SubComment referred to by , null if there is none
     */
    public SubComment getSubCommentByID(int subCommentID) {
        Connection          connection  = DB.getConnection();
        SubComment          subComment  = null;

        try{
            String              sql     = "SELECT * " +
                                          "FROM subcomments " +
                                          "WHERE subCID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);
            ResultSet           rs;

            //executing and analyzing resultSet
            String              sCID    = Integer.toString(subCommentID);
            stmt.setString(1, sCID);
            rs = stmt.executeQuery();
            if(rs.next()) {
                //creating comment based on resultSet data
                String      email           = rs.getString("email");
                String      content         = rs.getString("content");
                int         commentID       = rs.getInt("commentID");
                subComment                  = new SubComment(subCommentID, email, content, commentID);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
            //TODO exception conventions
        }

        return subComment;
    }


    //insert
    /**
     * creates a SubComment with given values
     * @param email ID of user who created subComment
     * @param commentID ID of comment in which subComment is created
     * @param content content of subComment
     */
    public void createSubComment(String email, int commentID, String content) {

        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "INSERT INTO subcomments " +
                                          "(email, commentID, content) " +
                                          "VALUES (?, ?, ?) ";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              cID     = Integer.toString(commentID);
            stmt.setString(1, email);
            stmt.setString(2, cID);
            stmt.setString(3, content);
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
     * changes current content of referred subComment
     * @param subCommentID ID of subComment stc
     * @param content new content
     */
    public void changeSubComment(int subCommentID, String content) {

        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "UPDATE subcomments " +
                                          "SET subcomments.content = ? " +
                                          "WHERE subcomments.subCID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              cID     = Integer.toString(subCommentID);
            stmt.setString(1, content);
            stmt.setString(2, cID);
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


    //delete
    /**
     * deletes subComment referred to by subCommentID
     * cascades tagsubcomments via constraints
     * @param subCommentID ID of subComment staged for deletion
     */
    public void deleteSubComment(int subCommentID) {
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "DELETE " +
                                          "FROM subcomments " +
                                          "WHERE subcomments.subCID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              cID     = Integer.toString(subCommentID);
            stmt.setString(1, cID);
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
