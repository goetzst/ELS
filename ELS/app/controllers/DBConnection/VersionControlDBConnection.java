package controllers.DBConnection;

import play.db.DB;
import models.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
/**
 * @author Stefan
 * DB connection for versionControl access
 */
public class VersionControlDBConnection {

    /**
     * gets all chapterChanges occurred within referred document
     * @param docID ID of document
     * @return list of ChapterChan in Version format
     */
    public List<ChapterChan> getVersionChapterToDoc(int docID) {
        Connection          connection          = DB.getConnection();
        List<ChapterChan>   chapterList         = new LinkedList<>();

        try{
            String              sql         = "SELECT * " +
                                              "FROM versionchapter " +
                                              "WHERE docID = ? "+
                                              "ORDER BY timestamp";
            PreparedStatement   stmt        = connection.prepareStatement(sql);
            String              dID         = Integer.toString(docID);
            stmt.setString(1, dID);
            ResultSet           rs;

            //executing and analyzing resultSet
            rs = stmt.executeQuery();
            while(rs.next()) {
                //creating chapter based on resultSet data
                //adding chapter to list
                int     vChapterID          = rs.getInt("vchapterID");
                int     chapterID           = rs.getInt("chapterID");
                String  title               = rs.getString("title");
                String  changeLog           = rs.getString("changelog");
                String  timeStamp           = rs.getString("timestamp");
                int     sequence            = rs.getInt("sequence");
                ChapterChan tmpChapter      = new ChapterChan(vChapterID, docID, chapterID, title, changeLog, timeStamp, sequence);
                chapterList.add(tmpChapter);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return chapterList;
    }

    /**
     * gets all contentChanges occurred within referred document
     * @param docID ID of document
     * @return list of Content in Version format
     */
    public List<Content> getVersionContentToDoc(int docID) {
        Connection          connection          = DB.getConnection();
        List<Content>       contentList         = new LinkedList<>();

        try{
            String              sql         = "SELECT * " +
                                              "FROM versioncontent " +
                                              "WHERE docID = ? "+
                                              "ORDER BY timestamp";
            PreparedStatement   stmt        = connection.prepareStatement(sql);
            String              dID         = Integer.toString(docID);
            stmt.setString(1, dID);
            ResultSet           rs;

            //executing and analyzing resultSet
            rs = stmt.executeQuery();
            while(rs.next()) {
                //creating content based on resultSet data
                //adding content to list
                int     vContentID          = rs.getInt("vcontentID");
                int     contentID           = rs.getInt("contentID");
                String  content             = rs.getString("content");
                int     type                = rs.getInt("type");
                String  changeLog           = rs.getString("changelog");
                String  timeStamp           = rs.getString("timestamp");
                int     sequence            = rs.getInt("sequence");
                int     chapterID           = rs.getInt("chapterID");
                int     cHeadlineID         = rs.getInt("cheadlineID");
                int     cSubHeadlineID      = rs.getInt("csubheadlineID");
                Content tmpContent          = new Content(vContentID, docID, contentID, content, type, changeLog, timeStamp, sequence, (chapterID == 0) ? -1: chapterID,
                        (cHeadlineID == 0) ? -1: cHeadlineID, (cSubHeadlineID == 0) ? -1: cSubHeadlineID);
                contentList.add(tmpContent);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contentList;
    }

    /**
     * gets all documentChanges occurred within referred document
     * @param docID ID of document
     * @return list of Document in Version format
     */
    public List<Document> getVersionDocumentToDoc(int docID) {
        Connection          connection          = DB.getConnection();
        List<Document>      documentList        = new LinkedList<>();

        try{
            String              sql         = "SELECT * " +
                                              "FROM versiondocument " +
                                              "WHERE docID = ? "+
                                              "ORDER BY timestamp";
            PreparedStatement   stmt        = connection.prepareStatement(sql);
            String              dID         = Integer.toString(docID);
            stmt.setString(1, dID);
            ResultSet           rs;

            //executing and analyzing resultSet
            rs = stmt.executeQuery();
            while(rs.next()) {
                //creating chapter based on resultSet data
                //adding chapter to list
                int     vDocID              = rs.getInt("vdocID");
                String  documentName        = rs.getString("documentName");
                String  changeLog           = rs.getString("changelog");
                String  timeStamp           = rs.getString("timestamp");
                Document tmpDoc             = new Document(vDocID, docID, documentName, changeLog, timeStamp);
                documentList.add(tmpDoc);
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
     * gets all headlineChanges occurred within referred document
     * @param docID ID of document
     * @return list of ContentHeadline in Version format
     */
    public List<ContentHeadline> getVersionContentHeadlineToDoc(int docID) {
        Connection              connection          = DB.getConnection();
        List<ContentHeadline>   contentHeadlineList = new LinkedList<>();

        try{
            String              sql         = "SELECT * " +
                                              "FROM versionheadline " +
                                              "WHERE docID = ? "+
                                              "ORDER BY timestamp";
            PreparedStatement   stmt        = connection.prepareStatement(sql);
            String              dID         = Integer.toString(docID);
            stmt.setString(1, dID);
            ResultSet           rs;

            //executing and analyzing resultSet
            rs = stmt.executeQuery();
            while(rs.next()) {
                //creating headline based on resultSet data
                //adding headline to list
                int     vHeadlineID         = rs.getInt("vheadlineID");
                int     cheadlineID         = rs.getInt("cheadlineID");
                String  title               = rs.getString("title");
                String  changeLog           = rs.getString("changelog");
                String  timeStamp           = rs.getString("timestamp");
                int     sequence            = rs.getInt("sequence");
                int     chapterID           = rs.getInt("chapterID");
                ContentHeadline tmpHeadline = new ContentHeadline(vHeadlineID, docID, cheadlineID, title, changeLog, timeStamp, sequence, chapterID);
                contentHeadlineList.add(tmpHeadline);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contentHeadlineList;
    }

    /**
     * gets all subheadlineChanges occured within referred document
     * @param docID ID of document
     * @return list of ContentSubHeadline in Version format
     */
    public List<ContentSubHeadline> getVersionContentSubHeadlineToDoc(int docID) {
        Connection              connection          = DB.getConnection();
        List<ContentSubHeadline>   contentHeadlineList = new LinkedList<>();

        try{
            String              sql         = "SELECT * " +
                    "FROM versionsubheadline " +
                    "WHERE docID = ? "+
                    "ORDER BY timestamp";
            PreparedStatement   stmt        = connection.prepareStatement(sql);
            String              dID         = Integer.toString(docID);
            stmt.setString(1, dID);
            ResultSet           rs;

            //executing and analyzing resultSet
            rs = stmt.executeQuery();
            while(rs.next()) {
                //creating headline based on resultSet data
                //adding headline to list
                int     vHeadlineID         = rs.getInt("vsubheadlineID");
                int     csubheadlineID         = rs.getInt("csubheadlineID");
                String  title               = rs.getString("title");
                String  changeLog           = rs.getString("changelog");
                String  timeStamp           = rs.getString("timestamp");
                int     sequence            = rs.getInt("sequence");
                int     cheadlineID         = rs.getInt("cheadlineID");
                ContentSubHeadline tmpHeadline = new ContentSubHeadline(vHeadlineID, docID, csubheadlineID, title, changeLog, timeStamp, sequence, cheadlineID);
                contentHeadlineList.add(tmpHeadline);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contentHeadlineList;
    }

    /**
     * gets all IDs of former HeadlineChildren of Chapter
     * @param chapterID ID of chapter
     * @return list of IDs
     */
    public List<Integer> getVersionHeadlineIDsToChapter(int chapterID){
        Connection      connection          = DB.getConnection();
        List<Integer>   idList              = new LinkedList<>();

        try{
            String              sql         = "SELECT vheadlineID " +
                                              "FROM versionheadline " +
                                              "WHERE chapterID = ? "+
                                              "ORDER BY sequence";
            PreparedStatement   stmt        = connection.prepareStatement(sql);
            String              cID         = Integer.toString(chapterID);
            stmt.setString(1, cID);
            ResultSet           rs;

            //executing and analyzing resultSet
            rs = stmt.executeQuery();
            while(rs.next()) {
                //creating headline based on resultSet data
                //adding headline to list
                int     vHeadlineID         = rs.getInt("vheadlineID");
                idList.add(vHeadlineID);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return idList;
    }

    /**
     * gets all IDs of former ContentChildren of Chapter
     * @param chapterID ID of chapter
     * @return list of IDs
     */
    public List<Integer> getVersionContentIDsToChapter(int chapterID){
        Connection      connection          = DB.getConnection();
        List<Integer>   idList              = new LinkedList<>();

        try{
            String              sql         = "SELECT vcontentID " +
                    "FROM versioncontent " +
                    "WHERE chapterID = ? "+
                    "ORDER BY sequence";
            PreparedStatement   stmt        = connection.prepareStatement(sql);
            String              cID         = Integer.toString(chapterID);
            stmt.setString(1, cID);
            ResultSet           rs;

            //executing and analyzing resultSet
            rs = stmt.executeQuery();
            while(rs.next()) {
                //creating headline based on resultSet data
                //adding headline to list
                int     vcontentID         = rs.getInt("vcontentID");
                idList.add(vcontentID);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return idList;
    }

    /**
     * gets all IDs of former SubHeadlineChildren of Headline
     * @param headlineID ID of headline
     * @return list of IDs
     */
    public List<Integer> getVersionSubHeadlineIDsToHeadline(int headlineID){
        Connection      connection          = DB.getConnection();
        List<Integer>   idList              = new LinkedList<>();

        try{
            String              sql         = "SELECT vsubheadlineID " +
                    "FROM versionsubheadline " +
                    "WHERE cheadlineID = ? "+
                    "ORDER BY sequence";
            PreparedStatement   stmt        = connection.prepareStatement(sql);
            String              cID         = Integer.toString(headlineID);
            stmt.setString(1, cID);
            ResultSet           rs;

            //executing and analyzing resultSet
            rs = stmt.executeQuery();
            while(rs.next()) {
                //creating headline based on resultSet data
                //adding headline to list
                int     vsubheadlineID         = rs.getInt("vsubheadlineID");
                idList.add(vsubheadlineID);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return idList;
    }

    /**
     * gets all IDs of former ContentChildren of Headline
     * @param headlineID ID of headline
     * @return list of IDs
     */
    public List<Integer> getVersionContentIDsToHeadline(int headlineID){
        Connection      connection          = DB.getConnection();
        List<Integer>   idList              = new LinkedList<>();

        try{
            String              sql         = "SELECT vcontentID " +
                    "FROM versioncontent " +
                    "WHERE cheadlineID = ? "+
                    "ORDER BY sequence";
            PreparedStatement   stmt        = connection.prepareStatement(sql);
            String              cID         = Integer.toString(headlineID);
            stmt.setString(1, cID);
            ResultSet           rs;

            //executing and analyzing resultSet
            rs = stmt.executeQuery();
            while(rs.next()) {
                //creating headline based on resultSet data
                //adding headline to list
                int     vcontentID         = rs.getInt("vcontentID");
                idList.add(vcontentID);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return idList;
    }

    /**
     * gets all IDs of former ContentChildren of SubHeadline
     * @param subHeadlineID ID of subHeadline
     * @return list of IDs
     */
    public List<Integer> getVersionContentIDsToSubHeadline(int subHeadlineID){
        Connection      connection          = DB.getConnection();
        List<Integer>   idList              = new LinkedList<>();

        try{
            String              sql         = "SELECT vcontentID " +
                    "FROM versioncontent " +
                    "WHERE csubheadlineID = ? "+
                    "ORDER BY sequence";
            PreparedStatement   stmt        = connection.prepareStatement(sql);
            String              cID         = Integer.toString(subHeadlineID);
            stmt.setString(1, cID);
            ResultSet           rs;

            //executing and analyzing resultSet
            rs = stmt.executeQuery();
            while(rs.next()) {
                //creating headline based on resultSet data
                //adding headline to list
                int     vcontentID         = rs.getInt("vcontentID");
                idList.add(vcontentID);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return idList;
    }

    /**
     * gets chapter based on ID
     * @param id ID of versionChapter
     * @return chapterChan in Version format
     */
    public ChapterChan getVersionChapterByID(int id) {
        Connection  connection  = DB.getConnection();
        ChapterChan chapter     = null;

        try{
            String              sql         = "SELECT * " +
                                              "FROM versionchapter " +
                                              "WHERE vchapterID = ?";
            PreparedStatement   stmt        = connection.prepareStatement(sql);
            ResultSet           rs;

            //executing and analyzing resultSet
            String      cID           = Integer.toString(id);
            stmt.setString(1, cID);
            rs = stmt.executeQuery();
            if(rs.next()) {
                //creating Chapter based on resultSet data
                int             docID       = rs.getInt("docID");
                int             chapterID   = rs.getInt("chapterID");
                String          title       = rs.getString("title");
                String          changeLog   = rs.getString("changelog");
                String          timeStamp   = rs.getString("timestamp");
                int             sequence    = rs.getInt("sequence");
                chapter                     = new ChapterChan(id, docID, chapterID, title, changeLog, timeStamp, sequence);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return chapter;
    }

    /**
     * gets content based on ID
     * @param id ID of versionContent
     * @return content in Version format
     */
    public Content getVersionContentByID(int id) {
        Connection  connection  = DB.getConnection();
        Content     content     = null;

        try{
            String              sql         = "SELECT * " +
                    "FROM versioncontent " +
                    "WHERE vcontentID = ?";
            PreparedStatement   stmt        = connection.prepareStatement(sql);
            ResultSet           rs;

            //executing and analyzing resultSet
            String      cID           = Integer.toString(id);
            stmt.setString(1, cID);
            rs = stmt.executeQuery();
            if(rs.next()) {
                //creating User based on resultSet data
                int             docID       = rs.getInt("docID");
                int             contentID   = rs.getInt("contentID");
                String          scontent    = rs.getString("content");
                int             type        = rs.getInt("type");
                String          changeLog   = rs.getString("changelog");
                String          timeStamp   = rs.getString("timestamp");
                int     sequence            = rs.getInt("sequence");
                int     chapterID           = rs.getInt("chapterID");
                int     cHeadlineID         = rs.getInt("cheadlineID");
                int     cSubHeadlineID      = rs.getInt("csubheadlineID");
                content                     = new Content(id, docID, contentID, scontent, type, changeLog, timeStamp, sequence, (chapterID == 0) ? -1: chapterID,
                        (cHeadlineID == 0) ? -1: cHeadlineID, (cSubHeadlineID == 0) ? -1: cSubHeadlineID);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return content;
    }

    /**
     * gets document based on ID
     * @param id ID of versionDocument
     * @return document in Version format
     */
    public Document getVersionDocumentByID(int id) {
        Connection  connection  = DB.getConnection();
        Document    doc     = null;

        try{
            String              sql         = "SELECT * " +
                    "FROM versiondocument " +
                    "WHERE vdocID = ?";
            PreparedStatement   stmt        = connection.prepareStatement(sql);
            ResultSet           rs;

            //executing and analyzing resultSet
            String      cID           = Integer.toString(id);
            stmt.setString(1, cID);
            rs = stmt.executeQuery();
            if(rs.next()) {
                //creating User based on resultSet data
                int             docID       = rs.getInt("docID");
                String          documentName= rs.getString("documentName");
                String          changeLog   = rs.getString("changelog");
                String          timeStamp   = rs.getString("timestamp");
                doc                         = new Document(id, docID, documentName, changeLog, timeStamp);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return doc;
    }

    /**
     * gets headline based on ID
     * @param id ID of versionHeadline
     * @return headline in Version format
     */
    public ContentHeadline getVersionHeadlineByID(int id) {
        Connection      connection  = DB.getConnection();
        ContentHeadline headline    = null;

        try{
            String              sql         = "SELECT * " +
                    "FROM versionheadline " +
                    "WHERE vheadlineID = ?";
            PreparedStatement   stmt        = connection.prepareStatement(sql);
            ResultSet           rs;

            //executing and analyzing resultSet
            String      cID           = Integer.toString(id);
            stmt.setString(1, cID);
            rs = stmt.executeQuery();
            if(rs.next()) {
                //creating User based on resultSet data
                int             docID       = rs.getInt("docID");
                int             cheadlineID = rs.getInt("cheadlineID");
                String          title       = rs.getString("title");
                String          changeLog   = rs.getString("changelog");
                String          timeStamp   = rs.getString("timestamp");
                int             sequence    = rs.getInt("sequence");
                int             chapterID   = rs.getInt("chapterID");
                headline                     = new ContentHeadline(id, docID, cheadlineID, title, changeLog, timeStamp, sequence, chapterID);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return headline;
    }

    /**
     * gets subHeadline based on ID
     * @param id ID of versionSubHeadline
     * @return subHeadline in Version format
     */
    public ContentSubHeadline getVersionSubHeadlineByID(int id) {
        Connection  connection  = DB.getConnection();
        ContentSubHeadline subHeadline     = null;

        try{
            String              sql         = "SELECT * " +
                    "FROM versionsubheadline " +
                    "WHERE vsubheadlineID = ?";
            PreparedStatement   stmt        = connection.prepareStatement(sql);
            ResultSet           rs;

            //executing and analyzing resultSet
            String      cID           = Integer.toString(id);
            stmt.setString(1, cID);
            rs = stmt.executeQuery();
            if(rs.next()) {
                //creating User based on resultSet data
                int             docID       = rs.getInt("docID");
                int             csubheadlineID= rs.getInt("csubheadlineID");
                String          title       = rs.getString("title");
                String          changeLog   = rs.getString("changelog");
                String          timeStamp   = rs.getString("timestamp");
                int             sequence    = rs.getInt("sequence");
                int             cheadlineID = rs.getInt("cheadlineID");
                subHeadline                 = new ContentSubHeadline(id, docID, csubheadlineID, title, changeLog, timeStamp, sequence, cheadlineID);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return subHeadline;
    }

    /**
     * creates version of chapter
     * @param docID ID of document in which change occurred
     * @param chapterID ID of changed chapter
     * @param title old title
     * @param changeLog changeLog message
     * @param timestamp time of occurrence
     * @param sequence old sequence
     */
    public void createVersionChapter(int docID, int chapterID, String title, String changeLog, String timestamp, int sequence) {
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "INSERT INTO versionchapter " +
                    "(docID, chapterID, title, changelog, timestamp, sequence) " +
                    "VALUES (?, ?, ?, ?, ?, ?) ";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String  dID         = Integer.toString(docID);
            String  cID         = Integer.toString(chapterID);
            String  sqc         = Integer.toString(sequence);
            stmt.setString(1, dID);
            stmt.setString(2, cID);
            stmt.setString(3, title);
            stmt.setString(4, changeLog);
            stmt.setString(5, timestamp);
            stmt.setString(6, sqc);
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
     * creates version of content
     * @param docID ID of document in which change occurred
     * @param contentID ID of changed content
     * @param content old content
     * @param type old type of content
     * @param changeLog changeLog message
     * @param timestamp time of occurrence
     * @param sequence old sequence
     * @param chapterID ID of chapter parent
     * @param cHeadlineID ID of headline parent
     * @param cSubHeadlineID ID of subHeadline parent
     */
    public void createVersionContent(int docID, int contentID, String content, int type, String changeLog, String timestamp, int sequence, int chapterID, int cHeadlineID, int cSubHeadlineID) {
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "INSERT INTO versioncontent " +
                    "(docID, contentID, content, type, changelog, timestamp, sequence, chapterID, cheadlineID, csubheadlineID) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            ContentDBConnection contentDBConnection = new ContentDBConnection();
            docID = contentDBConnection.getDocIdToContent(contentID);

            //completing and executing statement
            String  cID         = Integer.toString(contentID);
            String  t           = Integer.toString(type);
            String  sqc         = Integer.toString(sequence);
            String  chID        = (chapterID != -1) ? Integer.toString(chapterID) : null;
            String  hID         = (cHeadlineID != -1) ? Integer.toString(cHeadlineID) : null;
            String  sHID        = (cSubHeadlineID != -1) ? Integer.toString(cSubHeadlineID) : null;
            stmt.setInt(1, docID);
            stmt.setString(2, cID);
            stmt.setString(3, content);
            stmt.setString(4, t);
            stmt.setString(5, changeLog);
            stmt.setString(6, timestamp);
            stmt.setString(7, sqc);
            stmt.setString(8, chID);
            stmt.setString(9, hID);
            stmt.setString(10, sHID);
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
     * creates version of document
     * @param docID ID of document which has been changed
     * @param documentName old name
     * @param changeLog changeLog message
     * @param timestamp time of occurrence
     */
    public void createVersionDocument(int docID, String documentName, String changeLog, String timestamp) {
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "INSERT INTO versiondocument " +
                    "(docID, documentName, changelog, timestamp) " +
                    "VALUES (?, ?, ?, ?) ";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String  dID         = Integer.toString(docID);
            stmt.setString(1, dID);
            stmt.setString(2, documentName);
            stmt.setString(3, changeLog);
            stmt.setString(4, timestamp);
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
     * creates version of headline
     * @param docID ID of document in which change occurred
     * @param cHeadlineID ID of changed headline
     * @param title old title
     * @param changeLog changeLog message
     * @param timestamp time of occurrence
     * @param sequence old sequence
     * @param chapterID ID of chapter
     */
    public void createVersionHeadline(int docID, int cHeadlineID, String title, String changeLog, String timestamp, int sequence, int chapterID) {
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "INSERT INTO versionheadline " +
                    "(docID, cheadlineID, title, changelog, timestamp, sequence, chapterID) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?) ";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String  dID         = Integer.toString(docID);
            String  cID         = Integer.toString(cHeadlineID);
            String  sqc         = Integer.toString(sequence);
            String  chID        = Integer.toString(chapterID);
            stmt.setString(1, dID);
            stmt.setString(2, cID);
            stmt.setString(3, title);
            stmt.setString(4, changeLog);
            stmt.setString(5, timestamp);
            stmt.setString(6, sqc);
            stmt.setString(7, chID);
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
     * creates version of subHeadline
     * @param docID ID of document in which change occurred
     * @param cSubHeadlineID ID of changed subHeadline
     * @param title old title
     * @param changeLog changeLog message
     * @param timestamp time of occurrence
     * @param sequence old sequence
     * @param cheadlineID ID of headline
     */
    public void createVersionSubHeadline(int docID, int cSubHeadlineID, String title, String changeLog, String timestamp, int sequence, int cheadlineID) {
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "INSERT INTO versionsubheadline " +
                    "(docID, csubheadlineID, title, changelog, timestamp, sequence, cheadlineID) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?) ";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String  dID         = Integer.toString(docID);
            String  cID         = Integer.toString(cSubHeadlineID);
            String  sqc         = Integer.toString(sequence);
            String  chID        = Integer.toString(cheadlineID);
            stmt.setString(1, dID);
            stmt.setString(2, cID);
            stmt.setString(3, title);
            stmt.setString(4, changeLog);
            stmt.setString(5, timestamp);
            stmt.setString(6, sqc);
            stmt.setString(7, chID);
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
     * deletes versionChapter from database
     * @param vChapterID ID of versionChapter
     */
    public void deleteVersionChapter(int vChapterID){
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "DELETE " +
                                          "FROM versionchapter " +
                                          "WHERE vchapterID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            stmt.setInt(1, vChapterID);
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
     * deletes versionContent from database
     * @param vContentID ID of versionContent
     */
    public void deleteVersionContent(int vContentID){
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "DELETE " +
                    "FROM versioncontent " +
                    "WHERE vcontentID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            stmt.setInt(1, vContentID);
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
     * deletes versionDocument from database
     * @param vDocID ID of versionDocument
     */
    public void deleteVersionDocument(int vDocID){
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "DELETE " +
                    "FROM versiondocument " +
                    "WHERE vdocID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            stmt.setInt(1, vDocID);
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
     * deletes versionHeadline from database
     * @param vheadlineID ID of versionHeadline
     */
    public void deleteVersionHeadline(int vheadlineID){
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "DELETE " +
                    "FROM versionheadline " +
                    "WHERE vheadlineID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            stmt.setInt(1, vheadlineID);
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
     * deletes versionSubHeadline from database
     * @param vSubHeadlineID ID of versionSubHeadline
     */
    public void deleteVersionSubHeadline(int vSubHeadlineID){
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "DELETE " +
                    "FROM versionsubheadline " +
                    "WHERE vsubheadlineID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            stmt.setInt(1, vSubHeadlineID);
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
