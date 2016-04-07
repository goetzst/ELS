package models;

import java.time.LocalDateTime;

/**
 * @author Stefan
 */
public class ContentSubHeadline extends AbstractVersion{
    private int     cSubHeadLineID;
    private int     cheadlineID;
    private String  title;
    private int     sequence;

    //constructor
    public ContentSubHeadline (){
        //empty for Form
    }

    public ContentSubHeadline(int cSubHeadLineID, int cheadlineID, String title, int sequence, int id) {
        this.cSubHeadLineID = cSubHeadLineID;
        this.cheadlineID = cheadlineID;
        this.title = title;
        this.sequence = sequence;
        this.id         = id;
    }

    public ContentSubHeadline(int cSubHeadLineID, int cheadlineID, String title, int sequence) {
        this.cSubHeadLineID = cSubHeadLineID;
        this.cheadlineID = cheadlineID;
        this.title = title;
        this.sequence = sequence;
    }

    public ContentSubHeadline(int vSubHeadlineID, int docID, int cSubHeadlineID, String title, String changeLog, String timeStamp, int sequence, int cheadlineID) {
        this.id             = vSubHeadlineID;
        this.documentID     = docID;
        this.cSubHeadLineID = cSubHeadlineID;
        this.title          = title;
        this.changeLog      = changeLog;
        this.timeStamp      = LocalDateTime.parse(timeStamp);
        this.sequence       = sequence;
        this.cheadlineID    = cheadlineID;
    }

    //getter, setter

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getcSubHeadLineID() {
        return cSubHeadLineID;
    }

    public void setcSubHeadLineID(int cSubHeadLineID) {
        this.cSubHeadLineID = cSubHeadLineID;
    }

    public int getCheadlineID() {
        return cheadlineID;
    }

    public void setCheadlineID(int cheadlineID) {
        this.cheadlineID = cheadlineID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContentSubHeadline that = (ContentSubHeadline) o;

        if (cSubHeadLineID != that.cSubHeadLineID) return false;
        if (cheadlineID != that.cheadlineID) return false;
        if (sequence != that.sequence) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = cSubHeadLineID;
        result = 31 * result + cheadlineID;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + sequence;
        return result;
    }
}
