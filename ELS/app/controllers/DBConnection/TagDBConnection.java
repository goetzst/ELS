package controllers.DBConnection;

import models.*;
import play.db.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Stefan
 * class for all database access related to Tags
 */
public class TagDBConnection {

    //selects

    /**
     * tag with referred ID
     * @param tagID id of tag to get
     * @return requested tag empty tag if there is none
     */
    public Tag getTagByID(int tagID) {
        Connection  connection  = DB.getConnection();
        Tag         tag         = new Tag();

        try{
            String              sql         = "SELECT * " +
                                              "FROM tags " +
                                              "WHERE tags.tagID = ?";
            PreparedStatement   stmt        = connection.prepareStatement(sql);
            ResultSet           rs;

            //executing and analyzing resultSet
            String              tID         = Integer.toString(tagID);
            stmt.setString(1, tID);
            rs = stmt.executeQuery();
            if(rs.next()) {
                //creating tag based on resultSet data
                //adding tag to list
                String      tagName         = rs.getString("tagName");
                boolean     global          = rs.getBoolean("global");
                int         docID           = rs.getInt("docID");
                            tag             = new Tag(tagID, tagName, global, docID);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tag;
    }
    /**
     * all tags of referred user
     * @param email ID of user of whom tags are requested
     * @return list of all Tags for one user
     */
    public List<Tag> getTagsOfUser(String email) {

        Connection              connection  = DB.getConnection();
        List<Tag>               tagList     = new LinkedList<>();

        try{
            String              sql         = "SELECT tags.* " +
                                              "FROM  users JOIN usertags ON users.email = usertags.email " +
                                              "JOIN tags ON usertags.tagID = tags.tagID " +
                                              "WHERE users.email = ?";
            PreparedStatement   stmt        = connection.prepareStatement(sql);
            ResultSet           rs;

            //executing and analyzing resultSet
            stmt.setString(1, email);
            rs = stmt.executeQuery();
            while(rs.next()) {
                //creating tag based on resultSet data
                //adding tag to list
                int         tagID           = rs.getInt("tagID");
                String      tagName         = rs.getString("tagName");
                boolean         global      = rs.getBoolean("global");
                int         docID           = rs.getInt("docID");
                Tag         tempTag         = new Tag(tagID, tagName, global, docID);
                tagList.add(tempTag);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tagList;
    }

    /**
     * all tags of referred user + all global tags of document
     * @param email ID of user
     * @param docID ID of document
     * @return list of all Tags
     */
    public List<Tag> getTagsOfUserGlobal(String email, int docID){
        List<Tag>   tagsOfUser  = getTagsOfUser(email);

        Connection  connection  = DB.getConnection();

        try{
            connection.setAutoCommit(false);
            String  sql =   "SELECT * " +
                            "FROM tags " +
                            "WHERE docID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);
            stmt.setInt(1, docID);
            ResultSet   rs  = stmt.executeQuery();
            while(rs.next()){
                //creating tag based on resultSet data
                //adding tag to list
                int         tagID           = rs.getInt("tagID");
                String      tagName         = rs.getString("tagName");
                boolean     global          = rs.getBoolean("global");
                Tag         tempTag         = new Tag(tagID, tagName, global, docID);
                boolean     newOne          = true;
                for(Tag tag: tagsOfUser){
                    if(tag.getTagID() == tagID) {
                        newOne = false;
                        break;
                    }
                }
                if(newOne)
                    tagsOfUser.add(tempTag);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return tagsOfUser;
    }

    public List<Tag> getTagsOfUserGlobal(String email){
        List<Tag>   tagsOfUser  = getTagsOfUser(email);

        Connection  connection  = DB.getConnection();

        try{
            connection.setAutoCommit(false);
            String  sql =   "SELECT * " +
                            "FROM tags " +
                            "WHERE global = true";
            PreparedStatement   stmt    = connection.prepareStatement(sql);
            ResultSet   rs  = stmt.executeQuery();
            while(rs.next()){
                //creating tag based on resultSet data
                //adding tag to list
                int         tagID           = rs.getInt("tagID");
                String      tagName         = rs.getString("tagName");
                boolean     global          = rs.getBoolean("global");
                int         docID           = rs.getInt("docID");
                Tag         tempTag         = new Tag(tagID, tagName, global, docID);
                boolean     newOne          = true;
                for(Tag tag: tagsOfUser){
                    if(tag.getTagID() == tagID) {
                        newOne = false;
                        break;
                    }
                }
                if(newOne)
                    tagsOfUser.add(tempTag);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return tagsOfUser;
    }

    /**
     * all chapterchan tagged with referred tag
     * @param tagID ID of tag
     * @return list of all tagged chapterChan
     */
    public List<ChapterChan> getTaggedChapterChan(int tagID) {
        Connection              connection  = DB.getConnection();
        List<ChapterChan>       idList      = new LinkedList<>();

        try{
            String              sql         = "SELECT chapter.* " +
                                              "FROM  tags JOIN tagchaptersan ON tags.tagID = tagchaptersan.tagID " +
                                              "JOIN chapter ON tagchaptersan.chapterID = chapter.chapterID " +
                                              "WHERE tags.tagID = ?";

            PreparedStatement   stmt        = connection.prepareStatement(sql);
            ResultSet           rs;

            //executing and analyzing resultSet
            String              tID         = Integer.toString(tagID);
            stmt.setString(1, tID);
            rs = stmt.executeQuery();
            while(rs.next()) {
                //getting ID and adding it to List
                int             chapterID   = rs.getInt("chapterID");
                int             docID       = rs.getInt("docID");
                String          title       = rs.getString("title");
                int             sequence    = rs.getInt("sequence");
                idList.add(new ChapterChan(chapterID, docID, sequence, title));
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
     * all comments tagged with referred tag
     * @param tagID ID of tag
     * @return List of tagged CommentIDs
     */
    public List<Comment> getTaggedComments(int tagID) {

        Connection              connection  = DB.getConnection();
        List<Comment>           idList      = new LinkedList<>();

        try{
            String              sql         = "SELECT comments.* " +
                                              "FROM  tags JOIN tagcomments ON tags.tagID = tagcomments.tagID " +
                                              "JOIN comments ON tagcomments.commentID = comments.commentID " +
                                              "WHERE tags.tagID = ?";

            PreparedStatement   stmt        = connection.prepareStatement(sql);
            ResultSet           rs;

            //executing and analyzing resultSet
            String              tID         = Integer.toString(tagID);
            stmt.setString(1, tID);
            rs = stmt.executeQuery();
            while(rs.next()) {
                //getting ID and adding it to List
                int             commentID   = rs.getInt("commentID");
                String          email       = rs.getString("email");
                String          content     = rs.getString("content");
                int             contentID   = rs.getInt("contentID");
                idList.add(new Comment(commentID, email, content, contentID));
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
     * all subComments tagged with referred tag
     * @param tagID ID of tag
     * @return list of tagged SubCommentIDs
     */
    public List<SubComment> getTaggedSubComments(int tagID) {

        Connection              connection  = DB.getConnection();
        List<SubComment>        idList      = new LinkedList<>();

        try{
            String              sql         = "SELECT subcomments.* " +
                                              "FROM  tags JOIN tagsubcomments ON tags.tagID = tagsubcomments.tagID " +
                                              "JOIN subcomments ON tagsubcomments.subCID = subcomments.subCID " +
                                              "WHERE tags.tagID = ?";

            PreparedStatement   stmt        = connection.prepareStatement(sql);
            ResultSet           rs;

            //executing and analyzing resultSet
            String              tID         = Integer.toString(tagID);
            stmt.setString(1, tID);
            rs = stmt.executeQuery();
            while(rs.next()) {
                //getting ID and adding it to List
                int             sCommentID  = rs.getInt("subCID");
                int             commentID   = rs.getInt("commentID");
                String          email       = rs.getString("email");
                String          content     = rs.getString("content");
                idList.add(new SubComment(sCommentID, email, content, commentID));
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
     * all ContentHeadlines tagged with referred tag
     * @param tagID ID of tag
     * @return List of tagged contentHeadlines
     */
    public List<ContentHeadline> getTaggedContentHeadlines(int tagID) {
        Connection              connection  = DB.getConnection();
        List<ContentHeadline>        idList      = new LinkedList<>();

        try{
            String              sql         = "SELECT contentheadline.* " +
                                              "FROM  tags JOIN tagcontentheadline ON tags.tagID = tagcontentheadline.tagID " +
                                              "JOIN contentheadline ON contentheadline.cheadlineID = tagcontentheadline.cheadlineID " +
                                              "WHERE tags.tagID = ?";

            PreparedStatement   stmt        = connection.prepareStatement(sql);
            ResultSet           rs;

            //executing and analyzing resultSet
            String              tID         = Integer.toString(tagID);
            stmt.setString(1, tID);
            rs = stmt.executeQuery();
            while(rs.next()) {
                //getting ID and adding it to List
                int             cHeadlineID = rs.getInt("cheadlineID");
                int             chapterID   = rs.getInt("chapterID");
                String          title       = rs.getString("title");
                int             sequence    = rs.getInt("sequence");
                idList.add(new ContentHeadline(cHeadlineID, chapterID, title, sequence));
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
     * all ContentSubHeadlines tagged with referred Tag
     * @param tagID ID of tag
     * @return List of all tagged contentSubHeadlines
     */
    public List<ContentSubHeadline> getTaggedContentSubHeadline(int tagID) {
        Connection              connection  = DB.getConnection();
        List<ContentSubHeadline>        idList      = new LinkedList<>();

        try{
            String              sql         = "SELECT contentsubheadline.* " +
                                              "FROM  tags JOIN tagcontentsubheadline ON tags.tagID = tagcontentsubheadline.tagID " +
                                              "JOIN contentsubheadline ON contentsubheadline.csubheadlineID = tagcontentsubheadline.csubheadlineID " +
                                              "WHERE tags.tagID = ?";

            PreparedStatement   stmt        = connection.prepareStatement(sql);
            ResultSet           rs;

            //executing and analyzing resultSet
            String              tID         = Integer.toString(tagID);
            stmt.setString(1, tID);
            rs = stmt.executeQuery();
            while(rs.next()) {
                //getting ID and adding it to List
                int             cSubHeadlineID = rs.getInt("csubheadlineID");
                int             cHeadlineID   = rs.getInt("cheadlineID");
                String          title       = rs.getString("title");
                int             sequence    = rs.getInt("sequence");
                idList.add(new ContentSubHeadline(cSubHeadlineID, cHeadlineID, title, sequence));
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
     * all tags to referred chapterChan
     * @param chapterID ID of chapterChan
     * @param email email of user
     * @return list of tags on specific chapterChan to specific user
     */
    public List<Tag> getTagsToChapterChan(int chapterID, String email) {
        Connection              connection  = DB.getConnection();
        List<Tag>               tagList      = new LinkedList<>();

        try{
            String              sql         = "SELECT tags.* " +
                                              "FROM  tags JOIN tagchaptersan ON tags.tagID = tagchaptersan.tagID " +
                                              "JOIN chapter ON tagchaptersan.chapterID = chapter.chapterID " +
                                              "JOIN usertags ON tags.tagID = usertags.tagID " +
                                              "WHERE (usertags.email = ? OR tags.global)" +
                                              "AND chapter.chapterID = ?";

            PreparedStatement   stmt        = connection.prepareStatement(sql);
            ResultSet           rs;

            //executing and analyzing resultSet
            String              cID         = Integer.toString(chapterID);
            stmt.setString(1, email);
            stmt.setString(2, cID);
            rs = stmt.executeQuery();
            while(rs.next()) {
                //getting ID and adding it to List
                String          tagName     = rs.getString("tags.tagName");
                int             tagID       = rs.getInt("tags.tagID");
                boolean         global      = rs.getBoolean("tags.global");
                int             docID       = rs.getInt("tags.docID");
                tagList.add(new Tag(tagID, tagName, global, docID));
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tagList;

    }

    /**
     * all tags to referred comment
     * @param commentID specific comment
     * @param email user to whom tags belong
     * @return List of Tags on specific Comment to specific user, empty list if there are none
     */
    public List<Tag> getTagsToComment(int commentID, String email) {
        Connection              connection  = DB.getConnection();
        List<Tag>               tagList      = new LinkedList<>();

        try{
            String              sql         = "SELECT tags.* " +
                                              "FROM  tags JOIN tagcomments ON tags.tagID = tagcomments.tagID " +
                                              "JOIN comments ON tagcomments.commentID = comments.commentID " +
                                              "JOIN usertags ON tags.tagID = usertags.tagID " +
                                              "WHERE (usertags.email = ? OR tags.global)" +
                                              "AND comments.commentID = ?";

            PreparedStatement   stmt        = connection.prepareStatement(sql);
            ResultSet           rs;

            //executing and analyzing resultSet
            String              cID         = Integer.toString(commentID);
            stmt.setString(1, email);
            stmt.setString(2, cID);
            rs = stmt.executeQuery();
            while(rs.next()) {
                //getting ID and adding it to List
                String          tagName     = rs.getString("tags.tagName");
                int             tagID       = rs.getInt("tags.tagID");
                boolean         global      = rs.getBoolean("tags.global");
                int         docID           = rs.getInt("tags.docID");
                tagList.add(new Tag(tagID, tagName, global, docID));
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tagList;
    }

    /**
     * all tags to referred subComment
     * @param subCommentID specific subcomment
     * @param email user to whom tags belong
     * @return list of tags to specific subcomment of specific user, empty list if there are none
     */
    public List<Tag> getTagsToSubComment(int subCommentID, String email) {
        Connection              connection  = DB.getConnection();
        List<Tag>               tagList      = new LinkedList<>();

        try{
            String              sql         = "SELECT tags.* " +
                                              "FROM  tags JOIN tagsubcomments ON tags.tagID = tagsubcomments.tagID " +
                                              "JOIN subcomments ON tagsubcomments.subCID = subcomments.subCID " +
                                              "JOIN usertags ON tags.tagID = usertags.tagID " +
                                              "WHERE (usertags.email = ? OR tags.global) " +
                                              "AND subcomments.subCID = ?";

            PreparedStatement   stmt        = connection.prepareStatement(sql);
            ResultSet           rs;

            //executing and analyzing resultSet
            String              cID         = Integer.toString(subCommentID);
            stmt.setString(1, email);
            stmt.setString(2, cID);
            rs = stmt.executeQuery();
            while(rs.next()) {
                //getting ID and adding it to List
                String          tagName     = rs.getString("tags.tagName");
                int             tagID       = rs.getInt("tags.tagID");
                boolean         global      = rs.getBoolean("tags.global");
                int         docID           = rs.getInt("tags.docID");
                tagList.add(new Tag(tagID, tagName, global, docID));
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tagList;
    }

    /**
     * all tags to referred contentHeadline of specific user
     * @param cHeadlineID ID of contentHeadline
     * @param email user to whom tags belong
     * @return list of tags to referred contentHeadline of user
     */
    public List<Tag> getTagsToContentHeadline(int cHeadlineID, String email) {
        Connection              connection  = DB.getConnection();
        List<Tag>               tagList      = new LinkedList<>();

        try{
            String              sql         = "SELECT tags.* " +
                                              "FROM  tags JOIN tagcontentheadline ON tags.tagID = tagcontentheadline.tagID " +
                                              "JOIN contentheadline ON tagcontentheadline.cheadlineID = contentheadline.cheadlineID " +
                                              "JOIN usertags ON tags.tagID = usertags.tagID " +
                                              "WHERE (usertags.email = ? OR tags.global) " +
                                              "AND contentheadline.cheadlineID = ?";

            PreparedStatement   stmt        = connection.prepareStatement(sql);
            ResultSet           rs;

            //executing and analyzing resultSet
            String              cID         = Integer.toString(cHeadlineID);
            stmt.setString(1, email);
            stmt.setString(2, cID);
            rs = stmt.executeQuery();
            while(rs.next()) {
                //getting ID and adding it to List
                String          tagName     = rs.getString("tags.tagName");
                int             tagID       = rs.getInt("tags.tagID");
                boolean         global      = rs.getBoolean("tags.global");
                int         docID           = rs.getInt("tags.docID");
                tagList.add(new Tag(tagID, tagName, global, docID));
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tagList;
    }

    /**
     * all tags to referred contentSubHeadline of specific user
     * @param cSubHeadlineID ID of contentSubHeadline
     * @param email user to whom tags belong
     * @return list of tags to referred contentSubHeadline of user
     */
    public List<Tag> getTagsToContentSubHeadline(int cSubHeadlineID, String email) {
        Connection              connection  = DB.getConnection();
        List<Tag>               tagList      = new LinkedList<>();

        try{
            String              sql         = "SELECT tags.* " +
                                              "FROM  tags JOIN tagcontentsubheadline ON tags.tagID = tagcontentsubheadline.tagID " +
                                              "JOIN contentsubheadline ON tagcontentsubheadline.csubheadlineID = contentsubheadline.csubheadlineID " +
                                              "JOIN usertags ON tags.tagID = usertags.tagID " +
                                              "WHERE (usertags.email = ? OR tags.global) " +
                                              "AND contentsubheadline.csubheadlineID = ?";

            PreparedStatement   stmt        = connection.prepareStatement(sql);
            ResultSet           rs;

            //executing and analyzing resultSet
            String              cID         = Integer.toString(cSubHeadlineID);
            stmt.setString(1, email);
            stmt.setString(2, cID);
            rs = stmt.executeQuery();
            while(rs.next()) {
                //getting ID and adding it to List
                String          tagName     = rs.getString("tags.tagName");
                int             tagID       = rs.getInt("tags.tagID");
                boolean         global      = rs.getBoolean("tags.global");
                int         docID           = rs.getInt("tags.docID");
                tagList.add(new Tag(tagID, tagName, global, docID));
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tagList;
    }


    //inserts
    /**
     * creates new tag to user
     * has to be sure user exists
     * @param tagName name the new tag will have
     * @param userMail user who created the new tag
     * @return ID of the new tag
     */
    public int createTag(String tagName, String userMail, boolean global, int docID) {

        Connection  connection  = DB.getConnection();
        int                     id   = -1;//idea reasons

        try {
            connection.setAutoCommit(false);

            String              sql     = "INSERT INTO tags " +
                                          "(tagName, global, docID) " +
                                          "VALUES (?, ?, ?) ";

            String              sql2    = "INSERT INTO usertags " +
                                          "(email, tagID) " +
                                          "VALUES (?, ?) ";
            PreparedStatement   stmt    = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            PreparedStatement   stmt2   = connection.prepareStatement(sql2);

            //completing and executing statement
            if(global)
                stmt.setInt(3, docID);
            else
                stmt.setString(3, null);
            stmt.setBoolean(2, global);
            stmt.setString(1, tagName);
            stmt.executeUpdate();

            ResultSet   keys    = stmt.getGeneratedKeys();
            if(keys.next()){
                id = keys.getInt(1);
                stmt2.setString(1, userMail);
                stmt2.setInt(2, id);

                stmt2.executeUpdate();
            }
            //committing changes
            connection.commit();
            //closing
            stmt.close();
            stmt2.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }

    /**
     * adds connection between tagged chapterChan and tag via IDs
     * @param tagID ID of tag
     * @param chapterID ID of chapterChan
     */
    public void addChapterChanTag(int tagID, int chapterID) {

        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "INSERT INTO tagchaptersan " +
                                          "(tagID, chapterID) " +
                                          "VALUES (?, ?) ";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              tID     = Integer.toString(tagID);
            String              cID     = Integer.toString(chapterID);
            stmt.setString(1, tID);
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
     * adds connection between tagged comment and tag via IDs
     * @param tagID ID of tag
     * @param commentID ID of comment
     */
    public void addCommentTag(int tagID, int commentID) {

        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "INSERT INTO tagcomments " +
                                          "(tagID, commentID) " +
                                          "VALUES (?, ?) ";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              tID     = Integer.toString(tagID);
            String              cID     = Integer.toString(commentID);
            stmt.setString(1, tID);
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
     * adds connection between tagged SubComment and tag via IDs
     * @param tagID ID of tag
     * @param subCommentID ID of subComment
     */
    public void addSubCommentTag(int tagID, int subCommentID) {

        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "INSERT INTO tagsubcomments " +
                                          "(tagID, subCID) " +
                                          "VALUES (?, ?) ";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              tID     = Integer.toString(tagID);
            String              cID     = Integer.toString(subCommentID);
            stmt.setString(1, tID);
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
     * adds connection between tagged contentHeadline and tag via IDs
     * @param tagID ID of tag
     * @param contentHeadlineID ID of contentHeadline
     */
    public void addContentHeadlineTag(int tagID, int contentHeadlineID) {
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "INSERT INTO tagcontentheadline " +
                                          "(cheadlineID, tagID) " +
                                          "VALUES (?, ?) ";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              tID     = Integer.toString(tagID);
            String              cID     = Integer.toString(contentHeadlineID);
            stmt.setString(2, tID);
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
     * adds connection between tagged contentSubHeadline and tag via IDs
     * @param tagID ID of tag
     * @param contentSubHeadlineID ID of contentSub Headline
     */
    public void addContentSubHeadlineTag(int tagID, int contentSubHeadlineID) {
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "INSERT INTO tagcontentsubheadline " +
                                          "(csubheadlineID, tagID) " +
                                          "VALUES (?, ?) ";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              tID     = Integer.toString(tagID);
            String              cID     = Integer.toString(contentSubHeadlineID);
            stmt.setString(2, tID);
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


    //updates
    /**
     * changes name of tag referred to by ID
     * @param tagID ID of tag
     * @param tagName new name
     */
    public void changeTagName(int tagID, String tagName) {

        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "UPDATE tags " +
                                          "SET tags.tagName = ? " +
                                          "WHERE tags.tagID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              tID     = Integer.toString(tagID);
            stmt.setString(1, tagName);
            stmt.setString(2, tID);
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
     * deletes tag referred to by ID
     * cascades usertags via constraints
     * @param tagID ID of tag
     */
    public void deleteTag(int tagID) {

        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "DELETE " +
                                          "FROM tags " +
                                          "WHERE tags.tagID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              tID     = Integer.toString(tagID);
            stmt.setString(1, tID);
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
     * deletes connection between chapterChan and tag
     * @param tagID ID of tag
     * @param chapterID ID of chapterChan
     */
    public void deleteChapterChanTag(int tagID, int chapterID) {
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "DELETE " +
                                          "FROM tagchaptersan " +
                                          "WHERE tagchaptersan.tagID = ?" +
                                          "AND tagchaptersan.chapterID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              tID     = Integer.toString(tagID);
            String              cID     = Integer.toString(chapterID);
            stmt.setString(1, tID);
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
     * deletes connection between comment and tag
     * @param tagID ID of tag
     * @param commentID ID of comment
     */
    public void deleteCommentTag(int tagID, int commentID) {

        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "DELETE " +
                                          "FROM tagcomments " +
                                          "WHERE tagcomments.tagID = ?" +
                                          "AND tagcomments.commentID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              tID     = Integer.toString(tagID);
            String              cID     = Integer.toString(commentID);
            stmt.setString(1, tID);
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
     * deletes connection between subComment and tag
     * @param tagID ID of tag
     * @param subCommentID ID of subComment
     */
    public void deleteSubCommentTag(int tagID, int subCommentID) {

        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "DELETE " +
                                          "FROM tagsubcomments " +
                                          "WHERE tagsubcomments.tagID = ?" +
                                          "AND tagsubcomments.subCID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              tID     = Integer.toString(tagID);
            String              sID     = Integer.toString(subCommentID);
            stmt.setString(1, tID);
            stmt.setString(2, sID);
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
     * deletes connection between contentHeadline and tag
     * @param tagID ID of tag
     * @param contentHeadlineID ID of contentHeadline
     */
    public void deleteContentHeadlineTag(int tagID, int contentHeadlineID) {
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "DELETE " +
                                          "FROM tagcontentheadline " +
                                          "WHERE tagID = ?" +
                                          "AND cheadlineID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              tID     = Integer.toString(tagID);
            String              cID     = Integer.toString(contentHeadlineID);
            stmt.setString(1, tID);
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
     * deletes connection between contentSubHeadline and tag
     * @param tagID ID of tag
     * @param contentSubHeadlineID ID of contentSubHeadline
     */
    public void deleteContentSubHeadlineTag(int tagID, int contentSubHeadlineID) {
        Connection  connection  = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String              sql     = "DELETE " +
                                          "FROM tagcontentsubheadline " +
                                          "WHERE tagID = ?" +
                                          "AND csubheadlineID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);

            //completing and executing statement
            String              tID     = Integer.toString(tagID);
            String              cID     = Integer.toString(contentSubHeadlineID);
            stmt.setString(1, tID);
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
}
