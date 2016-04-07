package models;

import java.time.LocalDateTime;

/**
 * @author Stefan
 * class for depicting database table: chapter entries for Forms and attribute matching
 */
public class ChapterChan extends AbstractVersion{
    private int     chapterID;
    private int     docID;
    private int     sequence;
    private String  title;

    //constructor
    public ChapterChan () {
        //empty for Form
    }

    public ChapterChan (int chapterID, int docID, int sequence, String title) {
        this.chapterID  = chapterID;
        this.docID      = docID;
        this.documentID = docID;
        this.sequence   = sequence;
        this.title      = title;
    }

    public ChapterChan(int chapterID, int sequence, String title, int id){
        this.chapterID  = chapterID;
        this.sequence   = sequence;
        this.title      = title;
        this.id         = id;
    }

    public ChapterChan(int chapterID, int sequence, String title) {
        this.chapterID = chapterID;
        this.sequence = sequence;
        this.title = title;
    }

    public ChapterChan (int vChapterID, int docID, int chapterID, String title, String changeLog, String timeStamp, int sequence) {
        this.id         = vChapterID;
        this.chapterID  = chapterID;
        this.documentID = docID;
        this.docID      = docID;
        this.title      = title;
        this.changeLog  = changeLog;
        this.timeStamp  = LocalDateTime.parse(timeStamp);
        this.sequence   = sequence;
    }

    //getter setter

    public int getDocID() {
        return docID;
    }

    public void setDocID(int docID) {
        this.docID = docID;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getChapterID() {
        return chapterID;
    }

    public void setChapterID(int chapterID) {
        this.chapterID = chapterID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChapterChan that = (ChapterChan) o;

        if (chapterID != that.chapterID) return false;
        if (docID != that.docID) return false;
        if (sequence != that.sequence) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = chapterID;
        result = 31 * result + docID;
        result = 31 * result + sequence;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        return result;
    }
}
