package controllers.DBConnection;

import models.ChapterChan;
import models.Content;
import models.ContentHeadline;
import models.ContentSubHeadline;
import play.db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Stefan
 * class for all content related database access
 */
public class ContentDBConnection {

    //chapter related
    /**
     *
     * @param docID ID of referred document
     * @return List of all childChapters, empty list if there are none
     */
    public List<ChapterChan> getChaptersToDoc (int docID) {
        Connection connection  = DB.getConnection();
        List<ChapterChan>  chapterList  = new LinkedList<>();

        try{
            String      sql             = "SELECT * " +
                                          "FROM chapter " +
                                          "WHERE docID = ?" +
                                          "ORDER BY sequence ";
            PreparedStatement stmt    = connection.prepareStatement(sql);
            ResultSet rs;

            //executing and analyzing resultSet
            String              dID     = Integer.toString(docID);
            stmt.setString(1,dID);
            rs = stmt.executeQuery();
            while(rs.next()) {
                //creating chapter based on resultSet data
                //adding chapter it to list
                int         chapterID   = rs.getInt("chapterID");
                String      title       = rs.getString("title");
                int         sequence    = rs.getInt("sequence");
                ChapterChan tempChap    = new ChapterChan(chapterID, docID, sequence, title);
                chapterList.add(tempChap);
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
     *
     * @param chapterID ID of referred chapter
     * @return chapter, null if there is none
     */
    public ChapterChan getChapterByID(int chapterID) {
        Connection      connection  = DB.getConnection();
        ChapterChan     chapter     = null;

        try{
            String      sql             = "SELECT * " +
                                          "FROM chapter " +
                                          "WHERE chapterID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);
            ResultSet rs;

            //executing and analyzing resultSet
            String              dID     = Integer.toString(chapterID);
            stmt.setString(1,dID);
            rs = stmt.executeQuery();
            if(rs.next()) {
                //creating chapter based on resultSet data
                String      title    = rs.getString("title");
                int         sequence    = rs.getInt("sequence");
                int         docID       = rs.getInt("docID");
                chapter                 = new ChapterChan(chapterID, docID, sequence, title);
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

    //inserts
    /**
     * creates chapter in database
     * @param docID ID of parentDoc
     * @param sequence sequence
     * @param title title of chapter
     */
    public void createChapter(int docID, int sequence, String title) {
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "INSERT INTO chapter " +
                                          "(docID, title, sequence) " +
                                          "VALUES (?, ?, ?) ";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              dID     = Integer.toString(docID);
            String              sqc     = Integer.toString(sequence);
            stmt.setString(1, dID);
            stmt.setString(2, title);
            stmt.setString(3, sqc);
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
     * creates chapter which id is already known
     * @param chapterID ID of chapter
     * @param docID ID of parentDoc
     * @param sequence sequence
     * @param title title of chapter
     */
    public void createChapter(int chapterID, int docID, int sequence, String title){
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "INSERT INTO chapter " +
                    "(chapterID, docID, title, sequence) " +
                    "VALUES (?, ?, ?, ?) ";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              dID     = Integer.toString(docID);
            String              sqc     = Integer.toString(sequence);
            String              cID     = Integer.toString(chapterID);
            stmt.setString(1, cID);
            stmt.setString(2, dID);
            stmt.setString(3, title);
            stmt.setString(4, sqc);
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
     * changes chapterTitle
     * @param chapterID ID of chapter stc
     * @param title new title of chapter
     */
    public void changeChapterTitle(int chapterID, String title) {
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "UPDATE chapter " +
                                          "SET chapter.title = ? " +
                                          "WHERE chapter.chapterID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              cID     = Integer.toString(chapterID);
            stmt.setString(1, title);
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

    /**
     * changes sequence number of chapter
     * @param chapterID ID of chapter stc
     * @param sequence new sequence number
     */
    public void changeChapterSequence(int chapterID, int sequence) {
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "UPDATE chapter " +
                                          "SET chapter.sequence = ? " +
                                          "WHERE chapter.chapterID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              seq     = Integer.toString(sequence);
            String              cID     = Integer.toString(chapterID);
            stmt.setString(1, seq);
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
     * deletes referred chapter
     * @param chapterID ID of referred chapter
     */
    public void deleteChapter(int chapterID) {
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "DELETE " +
                                          "FROM chapter " +
                                          "WHERE chapter.chapterID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              cID     = Integer.toString(chapterID);
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


    //contentheadline related
    /**
     *
     * @param chapterID ID of parentChapter
     * @return List of contentHeadlines
     */
    public List<ContentHeadline> getContentHeadLinesToChapter(int chapterID) {
        Connection connection               = DB.getConnection();
        List<ContentHeadline>  headLineList = new LinkedList<>();

        try{
            String      sql                 = "SELECT * " +
                                              "FROM contentheadline " +
                                              "WHERE chapterID = ? " +
                                              "ORDER BY sequence";
            PreparedStatement stmt          = connection.prepareStatement(sql);
            ResultSet rs;

            //executing and analyzing resultSet
            String              cID     = Integer.toString(chapterID);
            stmt.setString(1,cID);
            rs = stmt.executeQuery();
            while(rs.next()) {
                //creating chapter based on resultSet data
                //adding chapter it to list
                int         cHeadLineID = rs.getInt("cheadlineID");
                String      title       = rs.getString("title");
                int         sequence    = rs.getInt("sequence");
                ContentHeadline tempH   = new ContentHeadline(cHeadLineID, chapterID, title, sequence);
                headLineList.add(tempH);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return headLineList;
    }

    /**
     *
     * @param cHeadLineID ID of referred contentHeadline
     * @return contentHeadline, null if there is none
     */
    public ContentHeadline getContentHeadLineByID(int cHeadLineID) {
        Connection      connection  = DB.getConnection();
        ContentHeadline cHeadLine   = null;

        try{
            String      sql             = "SELECT * " +
                                          "FROM contentheadline " +
                                          "WHERE cheadlineID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);
            ResultSet rs;

            //executing and analyzing resultSet
            String              cID     = Integer.toString(cHeadLineID);
            stmt.setString(1,cID);
            rs = stmt.executeQuery();
            if(rs.next()) {
                //creating chapter based on resultSet data
                String      title       = rs.getString("title");
                int         chapterID   = rs.getInt("chapterID");
                int         sequence    = rs.getInt("sequence");
                cHeadLine               = new ContentHeadline(cHeadLineID, chapterID, title, sequence);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cHeadLine;
    }

    //inserts
    /**
     * creates a new contentHeadline based on input values
     * @param chapterID ID of parentChapter
     * @param title title of contentHeadline
     * @param sequence sequence
     */
    public void createContentHeadLine(int chapterID, String title, int sequence) {
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "INSERT INTO contentheadline " +
                                          "(chapterID, title, sequence) " +
                                          "VALUES (?, ?, ?) ";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              cID     = Integer.toString(chapterID);
            String              sq      = Integer.toString(sequence);
            stmt.setString(1, cID);
            stmt.setString(2, title);
            stmt.setString(3, sq);
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
     * creates headline which ID is known
     * @param headlineID ID of headline
     * @param chapterID ID of parentChapter
     * @param title title of headline
     * @param sequence sequence
     */
    public void createContentHeadLine(int headlineID, int chapterID, String title, int sequence) {
        Connection connection = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String sql = "INSERT INTO contentheadline " +
                    "(cheadlineID, chapterID, title, sequence) " +
                    "VALUES (?, ?, ?, ?) ";
            PreparedStatement stmt = connection.prepareStatement(sql);

            //completing and executing statement
            String cID = Integer.toString(chapterID);
            String sq = Integer.toString(sequence);
            String hID = Integer.toString(headlineID);
            stmt.setString(1, hID);
            stmt.setString(2, cID);
            stmt.setString(3, title);
            stmt.setString(4, sq);
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
     * changes title of referred contentHeadline
     * @param cHeadLineID ID of contentHeadline stc
     * @param title new title
     */
    public void changeContentHeadLineTitle(int cHeadLineID, String title) {
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "UPDATE contentheadline " +
                                          "SET title = ? " +
                                          "WHERE cheadlineID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              cID     = Integer.toString(cHeadLineID);
            stmt.setString(1, title);
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

    /**
     * changes sequence of contentHeadline
     * @param cHeadLineID ID of referred headline
     * @param sequence new sequence
     */
    public void changeContentHeadLineSequence(int cHeadLineID, int sequence) {
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "UPDATE contentheadline " +
                                          "SET sequence = ? " +
                                          "WHERE cheadlineID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              seq     = Integer.toString(sequence);
            String              cID     = Integer.toString(cHeadLineID);
            stmt.setString(1, seq);
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

    //deletes
    /**
     * deletes referred contentHeadline
     * @param cHeadLineID ID of referred contentHeadline
     */
    public void deleteContentHeadLine(int cHeadLineID) {
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "DELETE " +
                                          "FROM contentheadline " +
                                          "WHERE cheadlineID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              cID     = Integer.toString(cHeadLineID);
            stmt.setString(1, cID);
            stmt.executeUpdate();
            //committing chagnes
            connection.commit();
            //closing
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //contentsubheadline related
    /**
     *
     * @param cHeadLineID ID of parentHeadline
     * @return List of contentSubHeadlines
     */
    public List<ContentSubHeadline> getContentSubHeadLinesToHeadline(int cHeadLineID) {
        Connection connection                   = DB.getConnection();
        List<ContentSubHeadline>  headLineList  = new LinkedList<>();

        try{
            String      sql                     = "SELECT * " +
                                                  "FROM contentsubheadline " +
                                                  "WHERE cheadlineID = ? "+
                                                  "ORDER BY sequence";
            PreparedStatement stmt              = connection.prepareStatement(sql);
            ResultSet rs;

            //executing and analyzing resultSet
            String              cID     = Integer.toString(cHeadLineID);
            stmt.setString(1,cID);
            rs = stmt.executeQuery();
            while(rs.next()) {
                //creating chapter based on resultSet data
                //adding chapter it to list
                int         cSubHeadlineID  = rs.getInt("csubheadlineID");
                String      title           = rs.getString("title");
                int         sequence        = rs.getInt("sequence");
                ContentSubHeadline tempH    = new ContentSubHeadline(cSubHeadlineID, cHeadLineID, title, sequence);
                headLineList.add(tempH);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return headLineList;
    }

    /**
     *
     * @param cSubHeadLineID ID of referred contentSubHeadline
     * @return contentSubHeadline, null if there is none
     */
    public ContentSubHeadline getContentSubHeadLineByID(int cSubHeadLineID) {
        Connection      connection      = DB.getConnection();
        ContentSubHeadline cSubHeadLine = null;

        try{
            String      sql             = "SELECT * " +
                                          "FROM contentsubheadline " +
                                          "WHERE csubheadlineID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);
            ResultSet rs;

            //executing and analyzing resultSet
            String              cID     = Integer.toString(cSubHeadLineID);
            stmt.setString(1,cID);
            rs = stmt.executeQuery();
            if(rs.next()) {
                //creating chapter based on resultSet data
                String      title       = rs.getString("title");
                int         cHeadlineID = rs.getInt("cheadlineID");
                int         sequence    = rs.getInt("sequence");
                cSubHeadLine            = new ContentSubHeadline(cSubHeadLineID, cHeadlineID, title, sequence);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cSubHeadLine;
    }

    //inserts
    /**
     * creates a new contentSubHeadline based on input values
     * @param cHeadlineID ID of parentHeadliner
     * @param title title of contentSubHeadline
     * @param sequence sequence
     */
    public void createContentSubHeadLine(int cHeadlineID, String title, int sequence) {
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "INSERT INTO contentsubheadline " +
                                          "(cheadlineID, title, sequence) " +
                                          "VALUES (?, ?, ?) ";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              cID     = Integer.toString(cHeadlineID);
            String              sq      = Integer.toString(sequence);
            stmt.setString(1, cID);
            stmt.setString(2, title);
            stmt.setString(3, sq);
            stmt.executeUpdate();
            //committing chagnes
            connection.commit();
            //closing
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * creates contentSubHeadline which ID is known
     * @param subHeadlineID ID of subheadline
     * @param cHeadlineID ID of parentHeadliner
     * @param title title of contentSubHeadline
     * @param sequence sequence
     */
    public void createContentSubHeadLine(int subHeadlineID, int cHeadlineID, String title, int sequence){
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "INSERT INTO contentsubheadline " +
                    "(csubheadlineID, cheadlineID, title, sequence) " +
                    "VALUES (?, ?, ?, ?) ";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              cID     = Integer.toString(cHeadlineID);
            String              sq      = Integer.toString(sequence);
            String              csubID  = Integer.toString(subHeadlineID);
            stmt.setString(1, csubID);
            stmt.setString(2, cID);
            stmt.setString(3, title);
            stmt.setString(4, sq);
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
     * changes title of referred contentSubHeadline
     * @param cSubHeadLineID ID of contentSubHeadline stc
     * @param title new title
     */
    public void changeContentSubHeadLineTitle(int cSubHeadLineID, String title) {
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "UPDATE contentsubheadline " +
                                          "SET title = ? " +
                                          "WHERE csubheadlineID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              cID     = Integer.toString(cSubHeadLineID);
            stmt.setString(1, title);
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

    /**
     * changes sequence of referred contentSubHeadline
     * @param cSubHeadLineID ID of subHeadline
     * @param sequence new sequence
     */
    public void changeContentSubHeadLineSequence(int cSubHeadLineID, int sequence) {
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "UPDATE contentsubheadline " +
                                          "SET sequence = ? " +
                                          "WHERE csubheadlineID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              seq     = Integer.toString(sequence);
            String              cID     = Integer.toString(cSubHeadLineID);
            stmt.setString(1, seq);
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

    //deletes
    /**
     * deletes referred contentSubHeadline
     * @param cSubHeadLineID ID of referred contentSubHeadline
     */
    public void deleteContentSubHeadLine(int cSubHeadLineID) {
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "DELETE " +
                                          "FROM contentsubheadline " +
                                          "WHERE csubheadlineID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              cID     = Integer.toString(cSubHeadLineID);
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


    //content related
    //selects
    /**
     *
     * @param chapterID ID of parentChapter
     * @return List of content, empty List if there is none
     */
    public List<Content> getContentToChapter(int chapterID) {
        Connection      connection  = DB.getConnection();
        List<Content>   contentList = new LinkedList<>();

        try{
            String      sql         = "SELECT * " +
                                      "FROM content " +
                                      "WHERE chapterID = ? " +
                                      "ORDER BY sequence";
            PreparedStatement stmt              = connection.prepareStatement(sql);
            ResultSet rs;

            //executing and analyzing resultSet
            String              cID     = Integer.toString(chapterID);
            stmt.setString(1,cID);
            rs = stmt.executeQuery();
            while(rs.next()) {
                //creating chapter based on resultSet data
                //adding chapter it to list
                int         contentID   = rs.getInt("contentID");
                String      content     = rs.getString("content");
                int         type        = rs.getInt("type");
                int         sequence    = rs.getInt("sequence");
                Content     tempC       = new Content(contentID, chapterID, -1, -1, content, type, sequence);
                contentList.add(tempC);
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
     *
     * @param cHeadLineID ID of parentHeadline
     * @return List of content, empty List if there are none
     */
    public List<Content> getContentToContentHeadline(int cHeadLineID) {
        Connection      connection  = DB.getConnection();
        List<Content>   contentList = new LinkedList<>();

        try{
            String      sql         = "SELECT * " +
                                      "FROM content " +
                                      "WHERE cheadlineID = ? " +
                                      "ORDER BY sequence";
            PreparedStatement stmt              = connection.prepareStatement(sql);
            ResultSet rs;

            //executing and analyzing resultSet
            String              cID     = Integer.toString(cHeadLineID);
            stmt.setString(1,cID);
            rs = stmt.executeQuery();
            while(rs.next()) {
                //creating chapter based on resultSet data
                //adding chapter it to list
                int         contentID   = rs.getInt("contentID");
                String      content     = rs.getString("content");
                int         type        = rs.getInt("type");
                int         sequence    = rs.getInt("sequence");
                Content     tempC       = new Content(contentID, -1, cHeadLineID, -1, content, type, sequence);
                contentList.add(tempC);
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
     *
     * @param cSubHeadLineID ID of parentSubHeadLIne
     * @return List of content, empty List if there are none
     */
    public List<Content> getContentToContentSubHeadline(int cSubHeadLineID) {
        Connection      connection  = DB.getConnection();
        List<Content>   contentList = new LinkedList<>();

        try{
            String      sql         = "SELECT * " +
                                      "FROM content " +
                                      "WHERE csubheadlineID = ? " +
                                      "ORDER BY sequence";
            PreparedStatement stmt              = connection.prepareStatement(sql);
            ResultSet rs;

            //executing and analyzing resultSet
            String              cID     = Integer.toString(cSubHeadLineID);
            stmt.setString(1,cID);
            rs = stmt.executeQuery();
            while(rs.next()) {
                //creating chapter based on resultSet data
                //adding chapter it to list
                int         contentID   = rs.getInt("contentID");
                String      content     = rs.getString("content");
                int         type        = rs.getInt("type");
                int         sequence    = rs.getInt("sequence");
                Content     tempC       = new Content(contentID, -1, -1, cSubHeadLineID, content, type, sequence);
                contentList.add(tempC);
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
     *
     * @param contentID ID of referred content
     * @return content, null if there is none
     */
    public Content getContentByID(int contentID) {
        Connection  connection          = DB.getConnection();
        Content     content             = null;

        try{
            String      sql             = "SELECT * " +
                                          "FROM content " +
                                          "WHERE contentID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);
            ResultSet rs;

            //executing and analyzing resultSet
            String              cID     = Integer.toString(contentID);
            stmt.setString(1,cID);
            rs = stmt.executeQuery();
            if(rs.next()) {
                //creating content based on resultSet data
                int             chapterID   = (rs.getInt("chapterID") == 0)? -1: rs.getInt("chapterID");
                int             cHeadLineID = (rs.getInt("cheadlineID") == 0)? -1: rs.getInt("cheadlineID");
                int         cSubHeadLineID  = (rs.getInt("csubheadlineID") == 0)? -1: rs.getInt("csubheadlineID");
                String      contentS        = rs.getString("content");
                int         type            = rs.getInt("type");
                int         sequence    = rs.getInt("sequence");
                int         docID       = getDocIdToContent(contentID);
                content                     = new Content(contentID, chapterID, cHeadLineID, cSubHeadLineID, docID, contentS, type, sequence);
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

    //inserts
    /**
     * creates content with given values
     * @param chapterID ID of parentChapter if there is none -1
     * @param cHeadLineID ID of parentHeadline if there is none -1
     * @param cSubHeadLineID ID of parentSubHeadline if there is none -1
     * @param content content
     * @param typ type of content 0:= text, 1:= image, 2:= code
     * @param sequence sequence
     */
    public void createContent(int chapterID, int cHeadLineID, int cSubHeadLineID, String content, int typ, int sequence) {
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "INSERT INTO content " +
                                          "(chapterID, cheadlineID, csubheadlineID, content, type, sequence) " +
                                          "VALUES (?, ?, ?, ?, ?, ?) ";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              cID     = (chapterID != -1) ? Integer.toString(chapterID) : null;
            String              cHID    = (cHeadLineID != -1) ? Integer.toString(cHeadLineID) : null;
            String              cSHID   = (cSubHeadLineID != -1) ? Integer.toString(cSubHeadLineID) : null;
            String              tp      = Integer.toString(typ);
            String              sq      = Integer.toString(sequence);
            stmt.setString(1, cID);
            stmt.setString(2, cHID);
            stmt.setString(3, cSHID);
            stmt.setString(4, content);
            stmt.setString(5, tp);
            stmt.setString(6, sq);
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
     * creates content which id is known
     * @param contentID ID of content
     * @param chapterID ID of parentChapter if there is none -1
     * @param cHeadLineID ID of parentHeadline if there is none -1
     * @param cSubHeadLineID ID of parentSubHeadline if there is none -1
     * @param content content
     * @param typ type of content 0:= text, 1:= image, 2:= code
     * @param sequence sequence
     */
    public void createContent(int contentID, int chapterID, int cHeadLineID, int cSubHeadLineID, String content, int typ, int sequence){
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "INSERT INTO content " +
                    "(contentID, chapterID, cheadlineID, csubheadlineID, content, type, sequence) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?) ";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              cID     = (chapterID != -1) ? Integer.toString(chapterID) : null;
            String              cHID    = (cHeadLineID != -1) ? Integer.toString(cHeadLineID) : null;
            String              cSHID   = (cSubHeadLineID != -1) ? Integer.toString(cSubHeadLineID) : null;
            String              tp      = Integer.toString(typ);
            String              sq      = Integer.toString(sequence);
            String              conID   = Integer.toString(contentID);
            stmt.setString(1, conID);
            stmt.setString(2, cID);
            stmt.setString(3, cHID);
            stmt.setString(4, cSHID);
            stmt.setString(5, content);
            stmt.setString(6, tp);
            stmt.setString(7, sq);
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
     * changes content
     * @param contentID ID of referred content
     * @param content new content
     * @param type new type of content
     */
    public void changeContent(int contentID, String content, int type) {
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "UPDATE content " +
                                          "SET content = ?, type = ? " +
                                          "WHERE contentID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              cID     = Integer.toString(contentID);
            String              tp      = Integer.toString(type);
            stmt.setString(1, content);
            stmt.setString(2, tp);
            stmt.setString(3, cID);
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
     * changes sequence of referred content
     * @param contentID ID of referred content
     * @param sequence new sequence
     */
    public void changeContentSequence(int contentID, int sequence) {
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "UPDATE content " +
                                          "SET sequence = ? " +
                                          "WHERE contentID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              seq     = Integer.toString(sequence);
            String              cID     = Integer.toString(contentID);
            stmt.setString(1, seq);
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

    //deletes
    /**
     * deletes referred content
     * @param contentID ID of referred content
     */
    public void deleteContent(int contentID) {
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "DELETE " +
                                          "FROM content " +
                                          "WHERE contentID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              cID     = Integer.toString(contentID);
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

    /**
     * Get docID the a content
     * @param contentID ID of content
     * @return docID
     */
    public int getDocIdToContent(int contentID){
        int result = -1;

        Connection  connection  = DB.getConnection();

        try {
            String              sql     =   "SELECT * "+
                                            "FROM "+
                    "(SELECT c.contentID,  cap.docID "+
                    "FROM content AS c JOIN chapter AS cap ON c.`chapterID`=cap.`chapterID` "+
                    "UNION "+
                    "SELECT c.contentID,  cap.docID "+
                    "FROM content AS c JOIN contentheadline AS h ON c.`cHeadlineID`=h.`cHeadlineID` "+
                        "JOIN chapter AS cap ON h.`chapterID`=cap.`chapterID` "+
                    "UNION "+
                    "SELECT c.contentID,  cap.docID "+
                    "FROM content AS c JOIN contentsubheadline AS s ON c.`cSubHeadlineID`=s.`cSubHeadlineID` "+
                        "JOIN contentheadline AS h ON h.cheadlineID=s.cheadlineID "+
                        "JOIN chapter AS cap ON h.`chapterID`=cap.`chapterID`) AS conToDoc "+
                    "WHERE conToDoc.contentID = ? ";

            PreparedStatement   stmt    = connection.prepareStatement(sql);
            ResultSet rs;

            //executing and analyzing resultSet
            stmt.setInt(1, contentID);
            rs = stmt.executeQuery();
            if(rs.next()) {
                //creating content based on resultSet data
                result =  rs.getInt("docID");

            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }
}
