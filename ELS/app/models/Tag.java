package models;

/**
 * @author Stefan
 * class for depicting database table: tags entries for Forms and attribute matching
 */
public class Tag {

    //attributes
    private int     tagID;
    private String  tagName;
    private boolean global;
    private int     docID;

    //constructor
    public Tag(int tagID, String tagName, boolean global, int docID) {
        this.tagID      = tagID;
        this.tagName    = tagName;
        this.global     = global;
        this.docID      = docID;
    }

    public Tag() {

    }

    //getter and setter
    public int getTagID() {
        return tagID;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public boolean isGlobal() {
        return global;
    }

    public void setGlobal(boolean global) {
        this.global = global;
    }

    public int getDocID() {
        return docID;
    }

    public void setDocID(int docID) {
        this.docID = docID;
    }
}
