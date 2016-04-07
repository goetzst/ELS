package models;

import java.time.LocalDateTime;

/**
 * @author Stefan
 * class for depicting database table: documents entries for Forms and attribute matching
 */
public class Document extends AbstractVersion{

    //attributes
    private int     documentID;
    private int     subjectID;
    private String  documentName;
    private String  dozentEmail;
    private boolean visible;

    //constructor
    public Document(int documentID, int subjectID, String documentName, String dozentEmail, boolean visible) {
        this.documentID     = documentID;
        this.subjectID      = subjectID;
        this.documentName   = documentName;
        this.dozentEmail    = dozentEmail;
        this.visible        = visible;
    }

    public Document(int vDocID, int docID, String documentName, String changeLog, String timeStamp) {
        this.id             = vDocID;
        this.documentID     = docID;
        this.documentName   = documentName;
        this.changeLog      = changeLog;
        this.timeStamp      = LocalDateTime.parse(timeStamp);
    }

    public Document() {
        this.documentID = 0;
        this.subjectID = 0;
        this.documentName = "";
        this.dozentEmail = "";
    }
    //getter and setter
    public int getDocumentID() {
        return documentID;
    }

    public String getDocumentName() {
        return documentName;
    }

    public int getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(int subjectID) {
        this.subjectID = subjectID;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDozentEmail() {
        return dozentEmail;
    }

    public void setDozentEmail(String dozentEmail) {
        this.dozentEmail = dozentEmail;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
