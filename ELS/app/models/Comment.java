package models;

/**
 * @author Stefan, Alex
 * class for depicting database table: comments entries for Forms and attribute matching
 */
public class Comment {

    //attributes
    private int     id;
    private int     commentID;
    private String  userEmail;
    private String  content;
    private int     contentID;

    /**
     * constructor of Comment
     * @param commentID commentID in the DB
     * @param userEmail Email of the User who created the Comment
     * @param content content of the comment
     */
    //constructor
    public Comment(int commentID, String userEmail, String content) {
        this.commentID  = commentID;
        this.userEmail  = userEmail;
        this.content    = content;
    }

    public Comment(int commentID, String userEmail, String content, int contentID) {
        this.commentID = commentID;
        this.userEmail = userEmail;
        this.content = content;
        this.contentID = contentID;
    }

    public Comment(int commentID, String userEmail, String content, int contentID, int id){
        this.commentID  = commentID;
        this.userEmail  = userEmail;
        this.content    = content;
        this.contentID  = contentID;
        this.id         = id;
    }

    public Comment() {
    }
    //getter and setter
    public int getCommentID() {
        return commentID;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getContent() {
        return content;
    }

    public int getContentID() {
        return this.contentID;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comment comment = (Comment) o;

        if (commentID != comment.commentID) return false;
        if (contentID != comment.contentID) return false;
        if (content != null ? !content.equals(comment.content) : comment.content != null) return false;
        if (userEmail != null ? !userEmail.equals(comment.userEmail) : comment.userEmail != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = commentID;
        result = 31 * result + (userEmail != null ? userEmail.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + contentID;
        return result;
    }
}
