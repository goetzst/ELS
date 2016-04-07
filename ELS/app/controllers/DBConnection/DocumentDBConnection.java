package controllers.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import play.db.DB;
import models.*;

/**
 * @author Stefan
 * class for all database access related to Document
 * this includes Chapter related access too
 */
public class DocumentDBConnection {

    //document related methods
    //selects
    /**
     * @return list of all Documents, empty list if there are none
     */
    public List<Document> getDocuments() {

        Connection      connection          = DB.getConnection();
        List<Document>  documentList        = new LinkedList<>();

        try{
            String              sql         = "SELECT * " +
                                              "FROM documents " +
                                              "ORDER BY documentName ";
            PreparedStatement   stmt        = connection.prepareStatement(sql);
            ResultSet           rs;

            //executing and analyzing resultSet
            rs = stmt.executeQuery();
            while(rs.next()) {
                //creating document based on resultSet data
                //adding document to list
                int         documentID      = rs.getInt("docID");
                String      documentName    = rs.getString("documentName");
                String      dozentEmail     = rs.getString("email");
                int         subjectID       = rs.getInt("subjectID");
                boolean     visible         = rs.getBoolean("visible");
                Document    tempDoc         = new Document(documentID, subjectID, documentName, dozentEmail, visible);
                documentList.add(tempDoc);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return documentList;
    }

    /**
     * @param userEmail ID of user which bookmarked docs are requested
     * @return List of Documents bookmarked by user, empty list if there are none
     */
    public List<Document> getBookmarkedDocuments(String userEmail) {

        Connection      connection      = DB.getConnection();
        List<Document>   documentList   = new LinkedList<>();

        try{
            String              sql     = "SELECT * " +
                                          "FROM  users JOIN bookmarks ON users.email = bookmarks.email " +
                                          "JOIN documents ON bookmarks.docID = documents.docID " +
                                          "WHERE users.email = ? " +
                                          "ORDER BY documents.documentName";
            PreparedStatement   stmt    = connection.prepareStatement(sql);
            ResultSet           rs;

            //executing and analyzing resultSet
            stmt.setString(1, userEmail);
            rs = stmt.executeQuery();
            while(rs.next()) {
                //creating document based on resultSet data
                //adding document to list
                int         documentID      = rs.getInt("docID");
                String      documentName    = rs.getString("documentName");
                String      dozentEmail     = rs.getString("email");
                int         subjectID       = rs.getInt("subjectID");
                boolean     visible         = rs.getBoolean("visible");
                Document    tempDoc         = new Document(documentID, subjectID, documentName, dozentEmail, visible);
                documentList.add(tempDoc);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return documentList;

    }

    /**
     *
     * @param docID ID of requested doc
     * @return document referred to by ID, null if there is no document
     */
    public Document getDocumentByID(int docID) {

        Connection      connection  = DB.getConnection();
        Document        document    = null;

        try{
            String              sql     = "SELECT * " +
                                          "FROM documents " +
                                          "WHERE docID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);
            ResultSet           rs;

            //executing and analyzing resultSet
            String              dID     = Integer.toString(docID);
            stmt.setString(1, dID);
            rs = stmt.executeQuery();
            if(rs.next()) {
                //creating document based on resultSet data
                int         documentID      = rs.getInt("docID");
                int         subjID          = rs.getInt("subjectID");
                String      documentName    = rs.getString("documentName");
                String      dozentEmail     = rs.getString("email");
                boolean     visible         = rs.getBoolean("visible");
                document                    = new Document(documentID, subjID, documentName, dozentEmail, visible);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return document;
    }


    //insert
    /**
     * creates a Document with given values
     * @param subjectID ID of subject in which doc will be created
     * @param email ID of creator
     * @param documentName name of doc
     */
    public void createDocument(int subjectID, String email, String documentName) {

        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "INSERT INTO documents " +
                                          "(subjectID, email, documentName) " +
                                          "VALUES (?, ?, ?) ";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              sID     = Integer.toString(subjectID);
            stmt.setString(1, sID);
            stmt.setString(2, email);
            stmt.setString(3, documentName);
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
     * changes document name of document referred to by docID
     * @param docID ID of doc stc
     * @param documentName new name
     */
    public void changeDocumentName(int docID, String documentName) {
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "UPDATE documents " +
                                          "SET documents.documentName = ? " +
                                          "WHERE documents.docID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              dID     = Integer.toString(docID);
            stmt.setString(1, documentName);
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


    //delete
    /**
     * deletes referred Document
     * cascades comments, subcomments via constraints
     * @param docID ID of doc staged for deletion
     */
    public void deleteDocument(int docID) {
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "DELETE " +
                                          "FROM documents " +
                                          "WHERE documents.docID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              dID     = Integer.toString(docID);
            stmt.setString(1, dID);
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

    //bookmark
    //insert
    /**
     * creates bookmark connecting user and document
     * @param email ID of user who created bookmark
     * @param docID ID of bookmarked doc
     */
    public void createBookmark(String email, int docID) {

        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "INSERT INTO bookmarks " +
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

    //delete
    /**
     * deletes referred bookmark
     * @param email ID of user who bookmarked doc
     * @param docID ID of bookmarked doc
     */
    public void deleteBookmark(String email, int docID) {

        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "DELETE " +
                                          "FROM bookmarks " +
                                          "WHERE bookmarks.email = ? " +
                                          "AND bookmarks.docID = ?";
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

    /**
     * changes visibility of document
     * @param docID ID of document
     */
    public void toggleVisibility(int docID){
        Connection  connection  = DB.getConnection();

        try{
            connection.setAutoCommit(false);
            String              sql =   "UPDATE documents " +
                                        "SET visible = NOT(visible) " +
                                        "WHERE docID = ?";
            PreparedStatement   stmt= connection.prepareStatement(sql);

            //completing and executing statement
            stmt.setInt(1, docID);
            stmt.executeUpdate();
            //committing
            connection.commit();
            //closing
            stmt.close();
            connection.close();

        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    /**
     * Get all documents which are owned by a specific dozent
     * @param emailOfDozent Email of Dozent
     * @return list of Documents for referred email, empty list if there is no document
     */
    public List<Document> getDocumentsDozent(String emailOfDozent) {

        Connection      connection          = DB.getConnection();
        List<Document>  documentList        = new LinkedList<>();

        try{
            String              sql         = "SELECT * " +
                                              "FROM documents " +
                                              "WHERE documents.email = ? " +
                                              "ORDER BY documentName ";
            PreparedStatement   stmt        = connection.prepareStatement(sql);
            ResultSet           rs;

            //executing and analyzing resultSet
            stmt.setString(1, emailOfDozent);
            rs = stmt.executeQuery();
            while(rs.next()) {
                //creating document based on resultSet data
                //adding document to list
                int         documentID      = rs.getInt("docID");
                String      documentName    = rs.getString("documentName");
                String      dozentEmail     = rs.getString("email");
                int         subjectID       = rs.getInt("subjectID");
                boolean     visible         = rs.getBoolean("visible");
                Document    tempDoc         = new Document(documentID, subjectID, documentName, dozentEmail, visible);
                documentList.add(tempDoc);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return documentList;
    }

    /**
     * Get all documents for a given student-email who is a moderator for this documents
     * @param emailOfModerator Email of Student
     * @return list of Documents for referred email, empty list if there is no document
     */
    public List<Document> getDocumentsModerator(String emailOfModerator) {

        Connection      connection          = DB.getConnection();
        List<Document>  documentList        = new LinkedList<>();

        try{
            String              sql         =   "SELECT * " +
                                                "FROM moderators " +
                                                "WHERE moderators.email = ? ";
            PreparedStatement   stmt        = connection.prepareStatement(sql);
            ResultSet           rs;

            //executing and analyzing resultSet
            stmt.setString(1, emailOfModerator);
            rs = stmt.executeQuery();
            while(rs.next()) {
                //creating document based on resultSet data
                //adding document to list
                int         documentID      = rs.getInt("docID");
                Document    tempDoc         = getDocumentByID(documentID);
                documentList.add(tempDoc);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return documentList;
    }


}
