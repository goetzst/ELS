package models;

/**
 * @author Thomas
 */
public class Reference {

    //a reference with "id" is part of a content with "contentID" and references to a content with "referencesToID"
    //name can be something like "vorwissen", "verwandter inhalt", ...

    private int     id;
    private int     contentID;
    private int     referencesToID;
    private int     referencesToDocID;
    private String  name;

    public Reference(){

    }

    public Reference(int id, int contentID, int referencesToID, String name, int referencesToDocID) {
        this.id                 = id;
        this.contentID          = contentID;
        this.referencesToID     = referencesToID;
        this.name               = name;
        this.referencesToDocID  = referencesToDocID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getContentID() {
        return contentID;
    }

    public void setContentID(int contentID) {
        this.contentID = contentID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getReferencesToID() {
        return referencesToID;
    }

    public void setReferencesToID(int referencesToID) {
        this.referencesToID = referencesToID;
    }

    public int getReferencesToDocID() {
        return referencesToDocID;
    }

    public void setReferencesToDocID(int referencesToDocID) {
        this.referencesToDocID = referencesToDocID;
    }
}
