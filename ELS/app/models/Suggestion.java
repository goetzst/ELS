package models;

/**
 * @author Stefan
 * class for depicting suggestions
 */
public class Suggestion {
    private int     id;
    private int     parentID;
    private String  content;
    private int     type;
    private String  email;
    private int     parentKind;

    public int getParentKind() {
        return parentKind;
    }

    public void setParentKind(int parentKind) {
        this.parentKind = parentKind;
    }

    public Suggestion(int id, int parentID, String content, String email, int type, int parentKind) {
        this.id = id;
        this.parentID = parentID;
        this.content = content;
        this.email = email;
        this.type = type;
        this.parentKind = parentKind;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentID() {
        return parentID;
    }

    public void setParentID(int parentID) {
        this.parentID = parentID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
