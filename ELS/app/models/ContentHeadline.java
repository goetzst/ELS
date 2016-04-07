package models;

import java.time.LocalDateTime;

/**
 * @author Stefan
 * class for depicting database table: contentheadline entries for Forms and attribute matching
 */
public class ContentHeadline extends AbstractVersion{
    private int     cHeadLineID;
    private int     chapterID;
    private String  title;
    private int     sequence;

    //constructor
    public ContentHeadline() {
        //empty for Form
    }

    public ContentHeadline(int cHeadLineID, int chapterID, String title, int sequence, int id) {
        this.cHeadLineID    = cHeadLineID;
        this.chapterID      = chapterID;
        this.title          = title;
        this.sequence       = sequence;
        this.id             = id;
    }

    public ContentHeadline(int chapterID, int cHeadLineID, String title, int sequence) {
        this.chapterID = chapterID;
        this.cHeadLineID = cHeadLineID;
        this.title = title;
        this.sequence = sequence;
    }

    public ContentHeadline (int vHeadlineID, int docID, int cHeadLineID, String title, String changeLog, String timeStamp, int sequence, int chapterID) {
        this.id             = vHeadlineID;
        this.documentID     = docID;
        this.cHeadLineID    = cHeadLineID;
        this.title          = title;
        this.changeLog      = changeLog;
        this.timeStamp      = LocalDateTime.parse(timeStamp);
        this.sequence       = sequence;
        this.chapterID      = chapterID;
    }

    //getter, setter

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getcHeadLineID() {
        return cHeadLineID;
    }

    public void setcHeadLineID(int cHeadLineID) {
        this.cHeadLineID = cHeadLineID;
    }

    public int getChapterID() {
        return chapterID;
    }

    public void setChapterID(int chapterID) {
        this.chapterID = chapterID;
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

        ContentHeadline that = (ContentHeadline) o;

        if (cHeadLineID != that.cHeadLineID) return false;
        if (chapterID != that.chapterID) return false;
        if (sequence != that.sequence) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = cHeadLineID;
        result = 31 * result + chapterID;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + sequence;
        return result;
    }
}
