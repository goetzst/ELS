package models;

import java.time.LocalDateTime;

/**
 * @author Stefan
 * class to be inherited by all models to be versioned
 */
public abstract class AbstractVersion implements Comparable<AbstractVersion>{
    protected   int             id;
    protected   String          changeLog;          //format: "type changed/deleted"
    protected   LocalDateTime   timeStamp;          //format: as LocalDateTime
    protected   int             documentID;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChangeLog() {
        return changeLog;
    }

    public void setChangeLog(String changeLog) {
        this.changeLog = changeLog;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getDocumentID() {
        return documentID;
    }

    public void setDocumentID(int documentID) {
        this.documentID = documentID;
    }

    @Override
    public int compareTo(AbstractVersion obj) {
        return (-1) * this.timeStamp.compareTo(obj.getTimeStamp());
    }
}
