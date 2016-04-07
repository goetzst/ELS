package controllers.DBConnection;

import models.*;
import play.db.DB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Stefan
 */
public class FlashCardDBConnectionSenpai {
    //selects

    /**
     * @param email of user
     * @return list of all FlashCards of given User, empty list if there are none
     */
    public List<FlashCard> getFlashCardsOfUser(String email) {

        Connection connection = DB.getConnection();
        List<FlashCard> cardList = new LinkedList<>();

        try {
            String sql = "SELECT * " +
                    "FROM  flashcard " +
                    "WHERE flashcard.email = ? ";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs;

            //preparing, executing and analyzing resultSet
            stmt.setString(1, email);
            rs = stmt.executeQuery();
            while (rs.next()) {
                //creating FlashCard based on resultSet data
                //adding FlashCard to list
                int flashCardID = rs.getInt("flashCardID");
                int tagID = rs.getInt("tagID");
                String name = rs.getString("flashCardName");
                int docID = rs.getInt("docID");
                FlashCard tempCard = new FlashCard(flashCardID, tagID, email, name, docID);
                cardList.add(tempCard);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cardList;
    }

    /**
     * @param flashCardID ID of requested flashCard
     * @return FlashCard referred to by ID, null if there is none
     */
    public FlashCard getFlashCardByID(int flashCardID) {

        Connection connection = DB.getConnection();
        FlashCard flashCard = null;

        try {
            String sql = "SELECT * " +
                    "FROM flashcard " +
                    "WHERE flashCardID = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs;

            //executing and analyzing resultSet
            String fID = Integer.toString(flashCardID);
            stmt.setString(1, fID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                //creating FlashCard based on resultSet data
                int tagID = rs.getInt("tagID");
                String email = rs.getString("email");
                String name = rs.getString("flashCardName");
                int docID = rs.getInt("docID");
                flashCard = new FlashCard(flashCardID, tagID, email, name, docID);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return flashCard;
    }

    /**
     * @param flashCardID ID of flashCard
     * @return List of all Chapters used in referred FlashCard, empty list if there are none
     */
    public List<ChapterChan> getChaptersForFlashCard(int flashCardID) {

        Connection connection = DB.getConnection();
        List<ChapterChan> chapterList = new LinkedList<>();

        try {
            String sql = "SELECT flashcardchapter.* " +
                    "FROM  flashcard JOIN flashcardchapter ON flashcard.flashCardID = " +
                    "flashcardchapter.flashCardID " +
                    "WHERE flashcard.flashCardID = ?" +
                    "ORDER BY flashcardchapter.sequence ASC";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs;

            //preparing, executing and analyzing resultSet
            String fID = Integer.toString(flashCardID);
            stmt.setString(1, fID);
            rs = stmt.executeQuery();
            while (rs.next()) {
                //creating comment based on resultSet data
                //adding comment to list
                int flashCardChapterID = rs.getInt("flashCardChapterID");
                String title = rs.getString("title");
                int sqc = rs.getInt("sequence");
                int chapterID = rs.getInt("chapterID");
                ChapterChan tempChapter = new ChapterChan(flashCardChapterID, sqc, title, chapterID);
                chapterList.add(tempChapter);
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
     * @param fChapterID ID of the chapter
     * @param sequence   Sequence to which the sequence of the specified Chapter will be changed
     */
    public void changeFlashCardChapterSequence(int fChapterID, int sequence) {
        Connection connection = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String sql = "UPDATE flashcardchapter " +
                    "SET flashcardchapter.sequence = ?" +
                    "WHERE flashcardchapter.flashCardChapterID = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            String seq = Integer.toString(sequence);
            String cID = Integer.toString(fChapterID);

            stmt.setString(1, seq);
            stmt.setString(2, cID);
            stmt.executeUpdate();
            //committing changes
            connection.commit();
            stmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param flashCardID ID of flashcard
     * @return List of all ContentHeadlines to FlashCard, empty list if there are none
     */
    public List<ContentHeadline> getContentHeadlineForFlashCard(int flashCardID) {
        Connection connection = DB.getConnection();
        List<ContentHeadline> contentHList = new LinkedList<>();

        try {
            String sql = "SELECT flashcardheadline.* " +
                    "FROM  flashcard JOIN flashcardheadline ON flashcard.flashCardID = " +
                    "flashcardheadline.flashCardID " +
                    "WHERE flashcard.flashCardID = ?" +
                    "ORDER BY flashcardheadline.sequence ASC";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs;

            //preparing, executing and analyzing resultSet
            String fID = Integer.toString(flashCardID);
            stmt.setString(1, fID);
            rs = stmt.executeQuery();
            while (rs.next()) {
                //creating comment based on resultSet data
                //adding comment to list
                int flashCardHeadlineID = rs.getInt("flashCardHeadlineID");
                String title = rs.getString("title");
                int sqc = rs.getInt("sequence");
                int flashCardChapterID = rs.getInt("chapterID");
                int headlineID = rs.getInt("headlineID");
                ContentHeadline tempChapter = new ContentHeadline(flashCardHeadlineID, flashCardChapterID, title, sqc, headlineID);
                contentHList.add(tempChapter);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contentHList;
    }

    /**
     * @param fContentHeadlineID ID of the headline
     * @param sequence           sequence to which the old one will be changed
     */
    public void changeFlashCardContentHeadlineSequence(int fContentHeadlineID, int sequence) {
        Connection connection = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String sql = "UPDATE flashcardheadline " +
                    "SET flashcardheadline.sequence = ? " +
                    "WHERE flashcardheadline.flashCardHeadlineID = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            String chID = Integer.toString(fContentHeadlineID);
            String seq = Integer.toString(sequence);

            stmt.setString(1, seq);
            stmt.setString(2, chID);
            stmt.executeUpdate();
            //committing changes
            connection.commit();
            stmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param fContentSubHeadlineID ID of the subheadline
     * @param sequence              sequence to which the old one will be changed
     */
    public void changeFlashCardContentSubHeadlineSequence(int fContentSubHeadlineID, int sequence) {
        Connection connection = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String sql = "UPDATE flashcardsubheadline " +
                    "SET flashcardsubheadline.sequence = ? " +
                    "WHERE flashcardsubheadline.flashCardSubHeadlineID = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            String cshID = Integer.toString(fContentSubHeadlineID);
            String seq = Integer.toString(sequence);

            stmt.setString(1, seq);
            stmt.setString(2, cshID);
            stmt.executeUpdate();
            //committing changes
            connection.commit();
            stmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param fcontentID ID of the content
     * @param sequence   sequence to which the old one will be changed
     */
    public void changeFlashCardContentSequence(int fcontentID, int sequence) {
        Connection connection = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String sql = "UPDATE flashcardcontent " +
                    "SET flashcardcontent.sequence = ? " +
                    "WHERE flashcardcontent.flashCardContentID = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            String cshID = Integer.toString(fcontentID);
            String seq = Integer.toString(sequence);

            stmt.setString(1, seq);
            stmt.setString(2, cshID);
            stmt.executeUpdate();
            //committing changes
            connection.commit();
            stmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param flashCardID ID of flashcard
     * @return List of all ContentSubHeadlines used in referred FlashCard
     */
    public List<ContentSubHeadline> getContentSubHeadlineForFlashCard(int flashCardID) {
        Connection connection = DB.getConnection();
        List<ContentSubHeadline> contentHList = new LinkedList<>();

        try {
            String sql = "SELECT flashcardsubheadline.* " +
                    "FROM  flashcard JOIN flashcardsubheadline ON flashcard.flashCardID = " +
                    "flashcardsubheadline.flashCardID " +
                    "WHERE flashcard.flashCardID = ?" +
                    "ORDER BY flashcardsubheadline.sequence ASC";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs;

            //preparing, executing and analyzing resultSet
            String fID = Integer.toString(flashCardID);
            stmt.setString(1, fID);
            rs = stmt.executeQuery();
            while (rs.next()) {
                //creating comment based on resultSet data
                //adding comment to list
                int flashCardSubHeadlineID = rs.getInt("flashCardSubHeadlineID");
                String title = rs.getString("title");
                int sqc = rs.getInt("sequence");
                int flashCardHeadlineID = rs.getInt("flashCardHeadlineID");
                int subHeadlineID = rs.getInt("subHeadlineID");
                ContentSubHeadline tempSubHeadline = new ContentSubHeadline(flashCardSubHeadlineID, flashCardHeadlineID, title, sqc, subHeadlineID);
                contentHList.add(tempSubHeadline);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contentHList;
    }

    /**
     * @param flashCardChapterID ID of the chapter
     * @return List of the content to a chapter
     */
    public List<Content> getContentToChapterForFlashCard(int flashCardChapterID) {
        Connection connection = DB.getConnection();
        List<Content> contentList = new LinkedList<>();

        try {
            String sql = "SELECT flashcardcontent.* " +
                    "FROM  flashcardchapter JOIN flashcardcontent ON flashcardchapter.flashcardchapterID = " +
                    "flashcardcontent.flashcardchapterID " +
                    "WHERE flashcardchapter.flashCardChapterID = ?" +
                    "ORDER BY flashcardcontent.sequence ASC";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs;

            //preparing, executing and analyzing resultSet
            String fID = Integer.toString(flashCardChapterID);
            stmt.setString(1, fID);
            rs = stmt.executeQuery();
            while (rs.next()) {
                //creating comment based on resultSet data
                //adding comment to list
                int flashCardContentID = rs.getInt("flashCardContentID");
                String content = rs.getString("content");
                int sqc = rs.getInt("sequence");
                int type = rs.getInt("type");
                int contentID = rs.getInt("contentID");
                Content tmpContent = new Content(flashCardContentID, flashCardChapterID, -1, -1, content, type, sqc, contentID);
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
     * @param flashCardHeadlineID ID of the headline
     * @return List of the content to a headline
     */
    public List<Content> getContentToHeadlineForFlashCard(int flashCardHeadlineID) {
        Connection connection = DB.getConnection();
        List<Content> contentList = new LinkedList<>();

        try {
            String sql = "SELECT flashcardcontent.* " +
                    "FROM  flashcardheadline JOIN flashcardcontent ON flashcardheadline.flashCardHeadlineID = " +
                    "flashcardcontent.flashCardHeadlineID " +
                    "WHERE flashcardheadline.flashCardHeadlineID = ?" +
                    "ORDER BY flashcardcontent.sequence ASC";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs;

            //preparing, executing and analyzing resultSet
            String fID = Integer.toString(flashCardHeadlineID);
            stmt.setString(1, fID);
            rs = stmt.executeQuery();
            while (rs.next()) {
                //creating comment based on resultSet data
                //adding comment to list
                int flashCardContentID = rs.getInt("flashCardContentID");
                String content = rs.getString("content");
                int sqc = rs.getInt("sequence");
                int type = rs.getInt("type");
                int contentID = rs.getInt("contentID");
                Content tmpContent = new Content(flashCardContentID, -1, flashCardHeadlineID, -1, content, type, sqc, contentID);
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
     * @param flashCardSubHeadlineID ID of the subheadline
     * @return List of the content to a subheadline
     */
    public List<Content> getContentToSubHeadlineForFlashCard(int flashCardSubHeadlineID) {
        Connection connection = DB.getConnection();
        List<Content> contentList = new LinkedList<>();

        try {
            String sql = "SELECT flashcardcontent.* " +
                    "FROM  flashcardsubheadline JOIN flashcardcontent ON flashcardsubheadline.flashCardSubHeadlineID = " +
                    "flashcardcontent.flashCardSubHeadlineID " +
                    "WHERE flashcardsubheadline.flashCardSubHeadlineID = ?" +
                    "ORDER BY flashcardcontent.sequence ASC";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs;

            //preparing, executing and analyzing resultSet
            String fID = Integer.toString(flashCardSubHeadlineID);
            stmt.setString(1, fID);
            rs = stmt.executeQuery();
            while (rs.next()) {
                //creating comment based on resultSet data
                //adding comment to list
                int flashCardContentID = rs.getInt("flashCardContentID");
                String content = rs.getString("content");
                int sqc = rs.getInt("sequence");
                int type = rs.getInt("type");
                int contentID = rs.getInt("contentID");
                Content tmpContent = new Content(flashCardContentID, -1, -1, flashCardSubHeadlineID, content, type, sqc, contentID);
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
     * @param flashCardChapterID ID of the chapter
     * @return List of headlines to a chapter
     */
    public List<ContentHeadline> getContentHeadlineToChapter(int flashCardChapterID) {
        Connection connection = DB.getConnection();
        List<ContentHeadline> contentHList = new LinkedList<>();

        try {
            String sql = "SELECT flashcardheadline.* " +
                    "FROM  flashcardheadline " +
                    "WHERE flashCardChapterID = ?" +
                    "ORDER BY flashcardheadline.sequence ASC";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs;

            //preparing, executing and analyzing resultSet
            String fID = Integer.toString(flashCardChapterID);
            stmt.setString(1, fID);
            rs = stmt.executeQuery();
            while (rs.next()) {
                //creating comment based on resultSet data
                //adding comment to list
                int flashCardHeadlineID = rs.getInt("flashCardHeadlineID");
                String title = rs.getString("title");
                int sqc = rs.getInt("sequence");
                int headlineID = rs.getInt("headlineID");
                ContentHeadline tempChapter = new ContentHeadline(flashCardHeadlineID, flashCardChapterID, title, sqc, headlineID);
                contentHList.add(tempChapter);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contentHList;
    }

    /**
     * @param flashCardHeadlineID ID of the chapter
     * @return List of subheadlines to a headline
     */
    public List<ContentSubHeadline> getContentSubHeadlineToHeadline(int flashCardHeadlineID) {
        Connection connection = DB.getConnection();
        List<ContentSubHeadline> contentHList = new LinkedList<>();

        try {
            String sql = "SELECT * " +
                    "FROM  flashcardsubheadline " +
                    "WHERE flashCardHeadlineID = ?" +
                    "ORDER BY sequence ASC";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs;

            //preparing, executing and analyzing resultSet
            String fID = Integer.toString(flashCardHeadlineID);
            stmt.setString(1, fID);
            rs = stmt.executeQuery();
            while (rs.next()) {
                //creating comment based on resultSet data
                //adding comment to list
                int flashCardSHeadlineID = rs.getInt("flashCardSubHeadlineID");
                String title = rs.getString("title");
                int sqc = rs.getInt("sequence");
                int subHeadlineID = rs.getInt("subHeadlineID");
                ContentSubHeadline tempChapter = new ContentSubHeadline(flashCardSHeadlineID, flashCardHeadlineID, title, sqc, subHeadlineID);
                contentHList.add(tempChapter);
            }
            //closing everything
            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contentHList;
    }

    /**
     * @param flashCardContentID ID of the content
     * @return List of comments to a content
     */
    public List<Comment> getCommentForContent(int flashCardContentID) {
        Connection connection = DB.getConnection();
        List<Comment> commentList = new LinkedList<>();

        try {
            String sql = "SELECT * " +
                    "FROM  flashcardcomment " +
                    "WHERE flashCardContentID = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs;

            //preparing, executing and analyzing resultSet
            String fID = Integer.toString(flashCardContentID);
            stmt.setString(1, fID);
            rs = stmt.executeQuery();
            while (rs.next()) {
                //creating comment based on resultSet data
                //adding comment to list
                int flashCardCommentID = rs.getInt("flashCardCommentID");
                String userMail = rs.getString("email");
                String content = rs.getString("content");
                int commentID = rs.getInt("commentID");
                Comment tmpComment = new Comment(flashCardCommentID, userMail, content, flashCardContentID, commentID);
                commentList.add(tmpComment);
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
     * @param flashCardCommentID ID of the comment
     * @return List of subcomments to a comment
     */
    public List<SubComment> getSubCommentForComment(int flashCardCommentID) {
        Connection connection = DB.getConnection();
        List<SubComment> commentList = new LinkedList<>();

        try {
            String sql = "SELECT * " +
                    "FROM  flashcardsubcomment " +
                    "WHERE flashCardCommentID = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs;

            //preparing, executing and analyzing resultSet
            String fID = Integer.toString(flashCardCommentID);
            stmt.setString(1, fID);
            rs = stmt.executeQuery();
            while (rs.next()) {
                //creating comment based on resultSet data
                //adding comment to list
                int flashCardSubCommentID = rs.getInt("flashCardSubCommentID");
                String userMail = rs.getString("email");
                String content = rs.getString("content");
                int subCommentID = rs.getInt("subCommentID");
                SubComment tmpComment = new SubComment(flashCardSubCommentID, userMail, content, flashCardCommentID, subCommentID);
                commentList.add(tmpComment);
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


    //inserts
    //ID will be returned for all classes with potential children

    /**
     * creates a FlashCard with given values
     *
     * @param tagID         ID of tag that is used for flashCard
     * @param email         ID of user creating the flashCard
     * @param flashCardName name of new flashCard
     * @param docID         ID of document on which flashCard will be based
     * @return ID of new flashCard
     */
    public int createFlashCard(int tagID, String email, String flashCardName, int docID, Connection connection) {

        int id = -1;//idea reasons

        try {
            String sql = "INSERT INTO flashcard " +
                    "(tagID, email, flashCardName, docID) " +
                    "VALUES (?, ?, ?, ?) ";

            PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

            //completing and executing statement
            String tID = Integer.toString(tagID);

            stmt.setString(1, tID);
            stmt.setString(2, email);
            stmt.setString(3, flashCardName);
            stmt.setInt(4, docID);
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next())
                id = keys.getInt(1);
            //closing
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    /**
     * clones comment into flashCardComment
     *
     * @param flashCardID        ID of flashCard
     * @param commentID          ID of comment to clone
     * @param flashCardContentID necessary ID of parent
     * @return flashCardCommentID of inserted flashCardComment
     */
    public int addFlashCardComment(int flashCardID, int commentID, int flashCardContentID, Connection connection) {
        CommentDBConnection commentDBConnection = new CommentDBConnection();
        Comment comment = commentDBConnection.getCommentByID(commentID);
        int id = -1;

        try {
            String sql = "INSERT INTO flashcardcomment " +
                    "(flashCardID, flashCardContentID, email, content, commentID) " +
                    "VALUES (?, ?, ?, ?, ?) ";
            PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

            //completing and executing statement
            String fID = Integer.toString(flashCardID);
            String fCID = Integer.toString(flashCardContentID);
            stmt.setString(1, fID);
            stmt.setString(2, fCID);
            stmt.setString(3, comment.getUserEmail());
            stmt.setString(4, comment.getContent());
            stmt.setInt(5, commentID);
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next())
                id = keys.getInt(1);
            //closing
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    /**
     * clones subComment into flashCardSubComment
     *
     * @param flashCardID        ID of flashCard
     * @param subCommentID       ID of subComment
     * @param flashCardCommentID necessary ID of parent
     */
    public void addFlashCardSubComment(int flashCardID, int subCommentID, int flashCardCommentID, Connection connection) {

        CommentDBConnection commentDBConnection = new CommentDBConnection();
        SubComment subComment = commentDBConnection.getSubCommentByID(subCommentID);

        try {
            String sql = "INSERT INTO flashcardsubcomment " +
                    "(flashCardID, flashCardCommentID, email, content, subCommentID) " +
                    "VALUES (?, ?, ?, ?, ?) ";
            PreparedStatement stmt = connection.prepareStatement(sql);

            //completing and executing statement
            String fID = Integer.toString(flashCardID);
            String fCID = Integer.toString(flashCardCommentID);
            stmt.setString(1, fID);
            stmt.setString(2, fCID);
            stmt.setString(3, subComment.getUserEmail());
            stmt.setString(4, subComment.getContent());
            stmt.setInt(5, subCommentID);
            stmt.executeUpdate();
            //closing
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * clones chapter into flashCardSubChapter
     *
     * @param flashCardID ID of flashCard
     * @param chapterID   ID of chapter to clone
     * @return flashCardChapterID of inserted flashCardChapter
     */
    public int addFlashCardChapter(int flashCardID, int chapterID, Connection connection) {

        ContentDBConnection contentDBConnection = new ContentDBConnection();
        ChapterChan chapter = contentDBConnection.getChapterByID(chapterID);
        int id = -1;

        try {
            String sql = "INSERT INTO flashcardchapter " +
                    "(flashCardID, sequence, title, chapterID) " +
                    "VALUES (?, ?, ?, ?) ";
            PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

            //completing and executing statement
            String fID = Integer.toString(flashCardID);
            String cID = Integer.toString(chapter.getSequence());
            stmt.setString(1, fID);
            stmt.setString(2, cID);
            stmt.setString(3, chapter.getTitle());
            stmt.setInt(4, chapterID);
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next())
                id = keys.getInt(1);
            //closing
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    /**
     * clones headline into flashCardHeadline
     *
     * @param flashCardID        ID of flashCard
     * @param cHeadlineID        ID of contentHeadline
     * @param flashCardChapterID necessary ID of parent
     * @return flashCardHeadlineID of inserted flashCardHeadline
     */
    public int addFlashCardContentHeadline(int flashCardID, int cHeadlineID, int flashCardChapterID, Connection connection) {
        ContentDBConnection contentDBConnection = new ContentDBConnection();
        ContentHeadline contentHeadline = contentDBConnection.getContentHeadLineByID(cHeadlineID);
        int id = -1;

        try {
            String sql = "INSERT INTO flashcardheadline " +
                    "(flashCardID, flashCardChapterID, title, sequence, headlineID) " +
                    "VALUES (?, ?, ?, ?, ?) ";
            PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

            //completing and executing statement
            String fID = Integer.toString(flashCardID);
            String fCID = Integer.toString(flashCardChapterID);
            String sqc = Integer.toString(contentHeadline.getSequence());
            stmt.setString(1, fID);
            stmt.setString(2, fCID);
            stmt.setString(3, contentHeadline.getTitle());
            stmt.setString(4, sqc);
            stmt.setInt(5, cHeadlineID);
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next())
                id = keys.getInt(1);
            //closing
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    /**
     * clones subHeadline into flashCardSubHeadline
     *
     * @param flashCardID         ID of flashCard
     * @param cSubHeadlineID      ID of subHeadline
     * @param flashCardHeadlineID necessary ID of parent
     * @return flashCardSubHeadlineID of inserted flashCardSubHeadline
     */
    public int addFlashCardContentSubHeadline(int flashCardID, int cSubHeadlineID, int flashCardHeadlineID, Connection connection) {
        ContentDBConnection contentDBConnection = new ContentDBConnection();
        ContentSubHeadline contentSubHeadline = contentDBConnection.getContentSubHeadLineByID(cSubHeadlineID);
        int id = -1;

        try {
            String sql = "INSERT INTO flashcardsubheadline " +
                    "(flashCardID, flashCardHeadlineID, title, sequence, subHeadlineID) " +
                    "VALUES (?, ?, ?, ?, ?) ";
            PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

            //completing and executing statement
            String fID = Integer.toString(flashCardID);
            String cID = Integer.toString(flashCardHeadlineID);
            String sqc = Integer.toString(contentSubHeadline.getSequence());
            stmt.setString(1, fID);
            stmt.setString(2, cID);
            stmt.setString(3, contentSubHeadline.getTitle());
            stmt.setString(4, sqc);
            stmt.setInt(5, cSubHeadlineID);
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next())
                id = keys.getInt(1);
            //closing
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    /**
     * clones content into flashCardContent
     *
     * @param flashCardID       ID of flashCard
     * @param contentID         ID of content
     * @param flashCardParentID ID of parent, type will be resolved via cloned content
     * @return ID of added Content
     */
    public int addFlashCardContent(int flashCardID, int contentID, int flashCardParentID, Connection connection) {
        ContentDBConnection contentDBConnection = new ContentDBConnection();
        Content content = contentDBConnection.getContentByID(contentID);
        int id = -1;

        try {
            String sql = "INSERT INTO flashcardcontent " +
                    "(flashCardID, flashCardChapterID, flashCardHeadlineID, flashCardSubHeadlineID, content, type, sequence, contentID) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?) ";
            PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

            //completing and executing statement
            String fID = Integer.toString(flashCardID);
            String cID = (content.getChapterID() != -1) ? Integer.toString(flashCardParentID) : null;
            String hID = (content.getcHeadLineID() != -1) ? Integer.toString(flashCardParentID) : null;
            String sID = (content.getcSubHeadLineID() != -1) ? Integer.toString(flashCardParentID) : null;
            String tp = Integer.toString(content.getType());
            String sqc = Integer.toString(content.getSequence());
            stmt.setString(1, fID);
            stmt.setString(2, cID);
            stmt.setString(3, hID);
            stmt.setString(4, sID);
            stmt.setString(5, content.getContent());
            stmt.setString(6, tp);
            stmt.setString(7, sqc);
            stmt.setInt(8, contentID);
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next())
                id = keys.getInt(1);
            //closing
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }


    //deletes

    /**
     * deletes referred flashCard
     *
     * @param flashCardID ID of flashCard to be deleted
     */
    public void deleteFlashCard(int flashCardID) {

        Connection connection = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String sql = "DELETE " +
                    "FROM flashcard " +
                    "WHERE flashcard.flashCardID = ? ";
            PreparedStatement stmt = connection.prepareStatement(sql);

            //completing and executing statement
            String fID = Integer.toString(flashCardID);
            stmt.setString(1, fID);
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
     * deletes flashCardComment
     *
     * @param flashCardCommentID ID of flashCardComment to be deleted
     */
    public void deleteFlashCardComment(int flashCardCommentID) {

        Connection connection = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String sql = "DELETE " +
                    "FROM flashcardcomment " +
                    "WHERE flashcardcomment.flashCardCommentID = ? ";
            PreparedStatement stmt = connection.prepareStatement(sql);

            //completing and executing statement
            String fID = Integer.toString(flashCardCommentID);
            stmt.setString(1, fID);
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
     * deletes flashCardSubComment
     *
     * @param flashCardSubCommentID ID of flashCardSubComment to be deleted
     */
    public void deleteFlashCardSubComment(int flashCardSubCommentID) {

        Connection connection = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String sql = "DELETE " +
                    "FROM flashcardsubcomment " +
                    "WHERE flashcardsubcomment.flashCardSubCommentID = ? ";
            PreparedStatement stmt = connection.prepareStatement(sql);

            //completing and executing statement
            String fID = Integer.toString(flashCardSubCommentID);
            stmt.setString(1, fID);
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
     * deletes flashCardChapter
     *
     * @param flashCardChapter ID of flashCardChapter to be deleted
     */
    public void deleteFlashCardChapter(int flashCardChapter) {

        Connection connection = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String sql = "DELETE " +
                    "FROM flashcardchapter " +
                    "WHERE flashCardChapterID = ? ";
            PreparedStatement stmt = connection.prepareStatement(sql);

            //completing and executing statement
            String fID = Integer.toString(flashCardChapter);
            stmt.setString(1, fID);
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
     * deletes flashCardContentHeadline
     *
     * @param flashCardContentHeadlineID ID of flashCardContent to be deleted
     */
    public void deleteFlashCardContentHeadline(int flashCardContentHeadlineID) {
        Connection connection = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String sql = "DELETE " +
                    "FROM flashcardheadline " +
                    "WHERE flashCardHeadlineID = ? ";
            PreparedStatement stmt = connection.prepareStatement(sql);

            //completing and executing statement
            String fID = Integer.toString(flashCardContentHeadlineID);
            stmt.setString(1, fID);
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
     * deletes flashCardSubHeadline
     *
     * @param flashCardSubHeadlineID ID of subheadline to be deleted
     */
    public void deleteFlashCardContentSubHeadline(int flashCardSubHeadlineID) {
        Connection connection = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String sql = "DELETE " +
                    "FROM flashcardsubheadline " +
                    "WHERE flashCardSubHeadlineID = ? ";
            PreparedStatement stmt = connection.prepareStatement(sql);

            //completing and executing statement
            String fID = Integer.toString(flashCardSubHeadlineID);
            stmt.setString(1, fID);
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
     * deletes
     *
     * @param flashCardContentID ID of content to be deleted
     */
    public void deleteFlashCardContent(int flashCardContentID) {
        Connection connection = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String sql = "DELETE " +
                    "FROM flashcardcontent " +
                    "WHERE flashCardContentID = ? ";
            PreparedStatement stmt = connection.prepareStatement(sql);

            //completing and executing statement
            String fID = Integer.toString(flashCardContentID);
            stmt.setString(1, fID);
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
     * changes FlashCard name of FlashCard referred to by flashCardID
     *
     * @param flashCardID ID of FlashCard to rename
     * @param newName     New name for the FlashCard
     */
    public void changeFlashCardName(int flashCardID, String newName) {
        Connection connection = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String sql = "UPDATE flashcard " +
                    "SET flashcard.flashCardName = ? " +
                    "WHERE flashcard.flashCardID = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);

            //completing and executing statement
            String fcID = Integer.toString(flashCardID);
            stmt.setString(1, newName);
            stmt.setString(2, fcID);
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
     * changes content of flashCardContent type can not be changed
     *
     * @param flashCardContentID ID of flashCardContent
     * @param content            new content
     */
    public void changeFlashCardContent(int flashCardContentID, String content) {
        Connection connection = DB.getConnection();

        try {
            connection.setAutoCommit(false);
            String sql = "UPDATE flashcardcontent " +
                    "SET content = ? " +
                    "WHERE flashCardContentID = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);

            //completing and executing statement
            String fID = Integer.toString(flashCardContentID);
            stmt.setString(1, content);
            stmt.setString(2, fID);
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
     * @param flashCardID ID of the flashcard
     * @param title       title of the new chapter
     * @param sequence    of the new chapter
     * @return ID of the chapter in the database
     */
    public int addNewChapter(int flashCardID, String title, int sequence) {
        Connection con = DB.getConnection();
        int id = -1;

        try {
            con.setAutoCommit(false);

            String sql2 = "INSERT INTO flashcardchapter (flashCardID, title, sequence) VALUES (?,?,?)";

            PreparedStatement stmt2 = con.prepareStatement(sql2, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt2.setInt(1, flashCardID);
            stmt2.setString(2, title);
            stmt2.setInt(3, sequence);
            stmt2.executeUpdate();

            ResultSet keys = stmt2.getGeneratedKeys();
            if (keys.next())
                id = keys.getInt(1);
            //committing changes
            con.commit();
            stmt2.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    /**
     * @param flashCardID        ID of the flashcard
     * @param flashCardChapterID id of the chapter
     * @param title              title of the new headline
     * @param sequence           sequence of the new headline
     * @return ID of the headline in the database
     */
    public int addNewContentHeadline(int flashCardID, int flashCardChapterID, String title, int sequence) {
        Connection con = DB.getConnection();
        int id = -1;

        try {
            con.setAutoCommit(false);

            String sql2 = "INSERT INTO flashcardheadline (flashCardID, flashCardChapterID, title, sequence) VALUES (?,?,?,?)";

            PreparedStatement stmt2 = con.prepareStatement(sql2, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt2.setInt(1, flashCardID);
            stmt2.setInt(2, flashCardChapterID);
            stmt2.setString(3, title);
            stmt2.setInt(4, sequence);
            stmt2.executeUpdate();

            ResultSet keys = stmt2.getGeneratedKeys();
            if (keys.next())
                id = keys.getInt(1);
            //committing changes
            con.commit();
            stmt2.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    /**
     * @param flashCardID         ID of the flashcard
     * @param flashCardHeadlineID id of the headline
     * @param title               title of the new subheadline
     * @param sequence            sequence of the new subheadline
     * @return ID of the subheadline in the database
     */
    public int addNewContentSubHeadline(int flashCardID, int flashCardHeadlineID, String title, int sequence) {
        Connection con = DB.getConnection();
        int id = -1;

        try {
            con.setAutoCommit(false);

            String sql2 = "INSERT INTO flashcardsubheadline (flashCardID, flashCardHeadlineID, title, sequence) VALUES (?,?,?,?)";

            PreparedStatement stmt2 = con.prepareStatement(sql2, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt2.setInt(1, flashCardID);
            stmt2.setInt(2, flashCardHeadlineID);
            stmt2.setString(3, title);
            stmt2.setInt(4, sequence);
            stmt2.executeUpdate();

            ResultSet keys = stmt2.getGeneratedKeys();
            if (keys.next())
                id = keys.getInt(1);
            //committing changes
            con.commit();
            stmt2.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();

        }
        return id;
    }

    /**
     * @param flashCardID  ID of the flashcard
     * @param parentID     id of the parent
     * @param sequence     sequence of the new content
     * @param content      content of the new content
     * @param type         type of the new content
     * @param typeOfParent type of the parent of new content (Either chapter[0], headline[1] or subheadline[2])
     * @return ID of the content in the database
     */
    public int addNewContent(int flashCardID, int parentID, int sequence, String content, int type, int typeOfParent) {
        Connection con = DB.getConnection();
        int id = -1;

        try {
            con.setAutoCommit(false);

            String sql2 = "INSERT INTO flashcardcontent (flashCardID, flashCardChapterID, flashCardHeadlineID, flashCardSubHeadlineID, content, type, sequence) VALUES (?,?,?,?,?,?,?)";

            PreparedStatement stmt2 = con.prepareStatement(sql2, PreparedStatement.RETURN_GENERATED_KEYS);

            stmt2.setInt(1, flashCardID);
            if (typeOfParent == 0) {
                stmt2.setInt(2, parentID);
                stmt2.setString(3, null);
                stmt2.setString(4, null);
            }
            if (typeOfParent == 1) {
                stmt2.setString(2, null);
                stmt2.setInt(3, parentID);
                stmt2.setString(4, null);
            }
            if (typeOfParent == 2) {
                stmt2.setString(2, null);
                stmt2.setString(3, null);
                stmt2.setInt(4, parentID);
            }
            stmt2.setString(5, content);
            stmt2.setInt(6, type);
            stmt2.setInt(7, sequence);
            stmt2.executeUpdate();

            ResultSet keys = stmt2.getGeneratedKeys();
            if (keys.next())
                id = keys.getInt(1);
            //committing changes
            con.commit();
            stmt2.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    /**
     * @param flashCardID        ID of the flashcard, can not be changed
     * @param flashCardChapterID id of the chapter, can not be changed
     * @param title              title of the changed chapter, can also be the same
     * @param sequence           sequence of the changed chapter, can also be the same
     */
    public void changeFlashCardChapter(int flashCardID, int flashCardChapterID, String title, int sequence) {
        Connection con = DB.getConnection();
        try {
            con.setAutoCommit(false);
            String sql = "UPDATE flashcardchapter SET title = ?, sequence = ? WHERE flashCardChapterID = ? AND flashCardID = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, title);
            stmt.setInt(2, sequence);
            stmt.setInt(3, flashCardChapterID);
            stmt.setInt(4, flashCardID);
            stmt.executeUpdate();
            //committing changes
            con.commit();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }


    /**
     * @param flashCardID         ID of the flashcard, can not be changed
     * @param flashCardHeadlineID ID of the headline, can not be changed
     * @param title               title of the changed headline, can also be the same
     * @param sequence            sequence of the changed headline, can also be the same
     */
    public void changeFlashCardHeadline(int flashCardID, int flashCardHeadlineID, String title, int sequence) {
        Connection con = DB.getConnection();
        try {
            con.setAutoCommit(false);
            String sql = "UPDATE flashcardheadline SET title = ?, sequence = ? WHERE flashCardHeadlineID = ? AND flashCardID = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, title);
            stmt.setInt(2, sequence);
            stmt.setInt(3, flashCardHeadlineID);
            stmt.setInt(4, flashCardID);
            stmt.executeUpdate();
            //committing changes
            con.commit();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param flashCardID            ID of the flashcard, can not be changed
     * @param flashCardSubHeadlineID ID of the subheadline, can not be changed
     * @param title                  title of the changed headline, can also be the same
     * @param sequence               sequence of the changed subheadline, can also be the same
     */
    public void changeFlashCardSubHeadline(int flashCardID, int flashCardSubHeadlineID, String title, int sequence) {
        Connection con = DB.getConnection();
        try {
            con.setAutoCommit(false);
            String sql = "UPDATE flashcardsubheadline SET title = ?, sequence = ? WHERE flashCardSubHeadlineID = ? AND flashCardID = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, title);
            stmt.setInt(2, sequence);
            stmt.setInt(3, flashCardSubHeadlineID);
            stmt.setInt(4, flashCardID);
            stmt.executeUpdate();
            //committing changes
            con.commit();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param flashCardID        ID of the flashcard, can not be changed
     * @param flashCardContentID ID of the content, can not be changed
     * @param content            content of the changed content, can also be the same
     * @param sequence           sequence of the changed content, can also be the same
     * @param type               type of the changed content, can also be the same
     */
    public void changeFlashCardContent(int flashCardID, int flashCardContentID, String content, int sequence, int type) {
        Connection con = DB.getConnection();
        try {
            con.setAutoCommit(false);
            String sql = "UPDATE flashcardcontent SET content = ?, sequence = ?, type = ? WHERE flashCardContentID = ? AND flashCardID = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, content);
            stmt.setInt(2, sequence);
            stmt.setInt(3, type);
            stmt.setInt(4, flashCardContentID);
            stmt.setInt(5, flashCardID);
            stmt.executeUpdate();
            //committing changes
            con.commit();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param flashCardChapterID ID of the Chapter
     * @return chapter with the flashCardChapterID
     */
    public ChapterChan getChapterByID(int flashCardChapterID) {
        Connection con = DB.getConnection();
        ChapterChan c = null;
        try {
            String sql = "SELECT * FROM flashcardchapter WHERE flashCardChapterID = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, flashCardChapterID);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int fid = rs.getInt("flashCardID");
            int fcid = rs.getInt("flashCardChapterID");
            int seq = rs.getInt("sequence");
            String title = rs.getString("title");

            c = new ChapterChan(fcid, fid, seq, title);
            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c;
    }

    /**
     * @param flashCardHeadlineID ID of the headline
     * @return headline with the flashCardHeadlineID
     */
    public ContentHeadline getHeadlineByID(int flashCardHeadlineID) {
        Connection con = DB.getConnection();
        ContentHeadline c = null;
        try {
            String sql = "SELECT * FROM flashcardheadline WHERE flashCardHeadlineID = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, flashCardHeadlineID);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int fhid = rs.getInt("flashCardHeadlineID");
            int fcid = rs.getInt("flashCardChapterID");
            int seq = rs.getInt("sequence");
            String title = rs.getString("title");
            int headlineID = rs.getInt("headlineID");

            c = new ContentHeadline(fhid, fcid, title, seq, headlineID);
            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c;
    }

    /**
     * @param flashCardSubHeadlineID ID of the subheadline
     * @return subheadline with the flashCardSubHeadlineID
     */
    public ContentSubHeadline getSubHeadlineByID(int flashCardSubHeadlineID) {
        Connection con = DB.getConnection();
        ContentSubHeadline c = null;
        try {
            String sql = "SELECT * FROM flashcardsubheadline WHERE flashCardSubHeadlineID = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, flashCardSubHeadlineID);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int fsid = rs.getInt("flashCardSubHeadlineID");
            int fhid = rs.getInt("flashCardHeadlineID");
            int seq = rs.getInt("sequence");
            String title = rs.getString("title");
            int subHeadlineID = rs.getInt("subHeadlineID");
            c = new ContentSubHeadline(fsid, fhid, title, seq, subHeadlineID);
            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c;
    }


    /**
     * @param flashCardContentID ID of the content
     * @return content with the flashCardContentID
     */
    public Content getContentByID(int flashCardContentID) {
        Connection con = DB.getConnection();
        Content c = null;
        try {
            String sql = "SELECT * FROM flashcardcontent WHERE flashCardContentID = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, flashCardContentID);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int fcoid = rs.getInt("flashCardContentID");
            int fcid = rs.getInt("flashCardChapterID");
            int fhid = rs.getInt("flashCardHeadlineID");
            int fsid = rs.getInt("flashCardSubHeadlineID");
            int seq = rs.getInt("sequence");
            int type = rs.getInt("type");
            String content = rs.getString("content");
            int flashCardID = rs.getInt("flashCardID");

            c = new Content(fcoid, fcid, fhid, fsid, flashCardID, content, type, seq);//this saves the flashCardID in the documentID attribute
            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c;
    }


    /**
     * @param fCardID ID of the flashcard
     * @return List of all content to the flashcard with the fCardID
     */
    public List<Content> getContentToFlashCard(int fCardID) {
        Connection con = DB.getConnection();
        List<Content> contents = new ArrayList<>();

        try {
            String sql = "SELECT * FROM flashcardcontent WHERE flashCardID = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, fCardID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int fcoid = rs.getInt("flashCardContentID");
                int fcid = rs.getInt("flashCardChapterID");
                int fhid = rs.getInt("flashCardHeadlineID");
                int fsid = rs.getInt("flashCardSubHeadlineID");
                int seq = rs.getInt("sequence");
                int type = rs.getInt("type");
                String content = rs.getString("content");
                int contentID = rs.getInt("contentID");
                contents.add(new Content(fcoid, fcid, fhid, fsid, content, type, seq, contentID));
            }

            rs.close();
            stmt.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contents;
    }

    public int getFChapterToChapter(int flashCardID, int chapterID) {
        Connection connection = DB.getConnection();
        int id = -1;
        try {
            String sql = "SELECT * " +
                    "FROM flashcardchapter " +
                    "WHERE flashCardID = ? " +
                    "AND chapterID = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, flashCardID);
            stmt.setInt(2, chapterID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                id = rs.getInt("flashCardChapterID");
            }
            rs.close();
            stmt.close();
            connection.close();
        } catch (Exception e) {

        }
        return id;
    }

    public int getFHeadlineToHeadline(int flashCardID, int headlineID) {
        Connection connection = DB.getConnection();
        int id = -1;
        try {
            String sql = "SELECT * " +
                    "FROM flashcardheadline " +
                    "WHERE flashCardID = ? " +
                    "AND headlineID = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, flashCardID);
            stmt.setInt(2, headlineID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                id = rs.getInt("flashCardHeadlineID");
            }
            rs.close();
            stmt.close();
            connection.close();
        } catch (Exception e) {

        }
        return id;
    }

    public int getFSubHeadlineToSubHeadline(int flashCardID, int subHeadlineID) {
        Connection connection = DB.getConnection();
        int id = -1;
        try {
            String sql = "SELECT * " +
                    "FROM flashcardsubheadline " +
                    "WHERE flashCardID = ? " +
                    "AND subHeadlineID = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, flashCardID);
            stmt.setInt(2, subHeadlineID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                id = rs.getInt("flashCardSubHeadlineID");
            }
            rs.close();
            stmt.close();
            connection.close();
        } catch (Exception e) {

        }
        return id;
    }

    public int getFContentToContent(int flashCardID, int contentID){
        Connection  connection  = DB.getConnection();
        int id  = -1;
        try {
            String sql                  = "SELECT * " +
                    "FROM flashcardcontent " +
                    "WHERE flashCardID = ? " +
                    "AND contentID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);
            stmt.setInt(1, flashCardID);
            stmt.setInt(2, contentID);
            ResultSet rs                = stmt.executeQuery();
            if(rs.next()){
                id  = rs.getInt("flashCardContentID");
            }
            rs.close();
            stmt.close();
            connection.close();
        }catch (Exception e){

        }
        return id;
    }

    public int getFCommentToComment(int flashCardID, int commentID){
        Connection  connection  = DB.getConnection();
        int id  = -1;
        try {
            String sql                  = "SELECT * " +
                    "FROM flashcardcomment " +
                    "WHERE flashCardID = ? " +
                    "AND commentID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);
            stmt.setInt(1, flashCardID);
            stmt.setInt(2, commentID);
            ResultSet rs                = stmt.executeQuery();
            if(rs.next()){
                id  = rs.getInt("flashCardCommentID");
            }
            rs.close();
            stmt.close();
            connection.close();
        }catch (Exception e){

        }
        return id;
    }

    public int getFSubCommentToSubComment(int flashCardID, int subCommentID){
        Connection  connection  = DB.getConnection();
        int id  = -1;
        try {
            String sql                  = "SELECT * " +
                    "FROM flashcardsubcomment " +
                    "WHERE flashCardID = ? " +
                    "AND headlineID = ?";
            PreparedStatement   stmt    = connection.prepareStatement(sql);
            stmt.setInt(1, flashCardID);
            stmt.setInt(2, subCommentID);
            ResultSet rs                = stmt.executeQuery();
            if(rs.next()){
                id  = rs.getInt("flashCardSubCommentID");
            }
            rs.close();
            stmt.close();
            connection.close();
        }catch (Exception e){

        }
        return id;
    }
}
