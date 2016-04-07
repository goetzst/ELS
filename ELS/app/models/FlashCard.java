package models;

/**
 * @author Stefan
 * class for depicting database table: flashcards entries for Forms and attribute matching
 */
public class FlashCard {

    //attributes
    private int     flashCardID;
    private int     tagID;
    private String  userEmail;
    private String  flashCardName;
    private int     docID;

    //constructor

    public FlashCard(int flashCardID, int tagID, String userEmail, String flashCardName, int docID) {
        this.flashCardID    = flashCardID;
        this.tagID          = tagID;
        this.userEmail      = userEmail;
        this.flashCardName  = flashCardName;
        this.docID          = docID;
    }

    //getter and setter
    public int getTagID() {
        return tagID;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public int getFlashCardID() {
        return flashCardID;
    }

    public String getFlashCardName() {
        return flashCardName;
    }

    public void setFlashCardName(String flashCardName) {
        this.flashCardName = flashCardName;
    }

    public int getDocID() {
        return docID;
    }

    public void setDocID(int docID) {
        this.docID = docID;
    }
}
