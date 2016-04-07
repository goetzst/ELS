package models;

/**
 * @author Stefan, Alex
 * class for depicting database table: subcomments entries for Forms and attribute matching
 */
public class SubComment {

    //attributes
    private int     id;
    private int     subCommentID;
    private String  userEmail;
    private String  content;
    private int     commentID;

    /**
     * Is a constructor of SubComment
     * @param subCommentID ID of SubComment in the DB
     * @param userEmail Email of User who created the SubComment
     * @param content comment of the SubComment
     * @param commentID ID of the related comment
     */
    //constructor
    public SubComment (int subCommentID, String userEmail, String content, int commentID, int id) {
        this.subCommentID   = subCommentID;
        this.userEmail      = userEmail;
        this.content        = content;
        this.commentID      = commentID;
        this.id             = id;
    }

    public SubComment(int subCommentID, String userEmail, String content, int commentID) {
        this.subCommentID = subCommentID;
        this.userEmail = userEmail;
        this.content = content;
        this.commentID = commentID;
    }

    public SubComment (int subCommentID, String userEmail, String content) {
        this.subCommentID   = subCommentID;
        this.userEmail      = userEmail;
        this.content        = content;
    }

    public SubComment(){

    }
    //getter and setter
    public String getUserEmail() {
        return userEmail;
    }

    public int getSubCommentID() {
        return subCommentID;
    }

    public String getContent() {
        return content;
    }

    public int getCommentID() {
        return this.commentID;
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
}
