package models;

import java.time.LocalDateTime;

/**
 * @author Stefan
 */
public class Content extends AbstractVersion{
    private int     contentID;
    private int     chapterID;
    private int     cHeadLineID;
    private int     cSubHeadLineID;
    private String  content;
    //0:= Text, 1:= Image, 2:= Code
    private int     type;
    private int     sequence;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Content content1 = (Content) o;

        if (cHeadLineID != content1.cHeadLineID) return false;
        if (cSubHeadLineID != content1.cSubHeadLineID) return false;
        if (chapterID != content1.chapterID) return false;
        if (contentID != content1.contentID) return false;
        if (sequence != content1.sequence) return false;
        if (type != content1.type) return false;
        if (content != null ? !content.equals(content1.content) : content1.content != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = contentID;
        result = 31 * result + chapterID;
        result = 31 * result + cHeadLineID;
        result = 31 * result + cSubHeadLineID;
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + type;
        result = 31 * result + sequence;
        return result;
    }

    //constructor
    public Content () {
        //empty for Form
    }

    //foreign keys that have no value are depicted with -1
    public Content(int contentID, int chapterID, int cHeadLineID, int cSubHeadLineID, String content, int type, int sequence, int id) {
        this.contentID = contentID;
        this.chapterID = chapterID;
        this.cHeadLineID = cHeadLineID;
        this.cSubHeadLineID = cSubHeadLineID;
        this.content = content;
        this.type = type;
        this.sequence = sequence;
        this.id     = id;
    }

    public Content(int contentID, int chapterID, int cHeadLineID, int cSubHeadLineID, String content, int type, int sequence) {
        this.contentID = contentID;
        this.chapterID = chapterID;
        this.cHeadLineID = cHeadLineID;
        this.cSubHeadLineID = cSubHeadLineID;
        this.content = content;
        this.type = type;
        this.sequence = sequence;
    }

    public Content(int contentID, int chapterID, int cHeadLineID, int cSubHeadLineID, int docID, String content, int type, int sequence) {
        this.contentID = contentID;
        this.chapterID = chapterID;
        this.cHeadLineID = cHeadLineID;
        this.cSubHeadLineID = cSubHeadLineID;
        this.documentID = docID;
        this.content = content;
        this.type = type;
        this.sequence = sequence;
    }

    public Content(int vContentID, int docID, int contentID, String content, int type, String changeLog, String timeStamp, int sequence, int chapterID, int cHeadLineID, int cSubHeadLineID) {
        this.id         = vContentID;
        this.documentID = docID;
        this.contentID  = contentID;
        this.content    = content;
        this.type       = type;
        this.changeLog  = changeLog;
        this.timeStamp  = LocalDateTime.parse(timeStamp);
        this.sequence   = sequence;
        this.chapterID  = chapterID;
        this.cHeadLineID= cHeadLineID;
        this.cSubHeadLineID= cSubHeadLineID;
    }

    //getter, setter

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getContentID() {
        return contentID;
    }

    public void setContentID(int contentID) {
        this.contentID = contentID;
    }

    public int getChapterID() {
        return chapterID;
    }

    public void setChapterID(int chapterID) {
        this.chapterID = chapterID;
    }

    public int getcHeadLineID() {
        return cHeadLineID;
    }

    public void setcHeadLineID(int cHeadLineID) {
        this.cHeadLineID = cHeadLineID;
    }

    public int getcSubHeadLineID() {
        return cSubHeadLineID;
    }

    public void setcSubHeadLineID(int cSubHeadLineID) {
        this.cSubHeadLineID = cSubHeadLineID;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
