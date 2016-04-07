package controllers;

import controllers.DBConnection.ContentDBConnection;
import controllers.DBConnection.DocumentDBConnection;
import controllers.DBConnection.VersionControlDBConnection;
import play.mvc.Security;
import controllers.Security.*;
import models.*;
import controllers.routes;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;
import views.html.versionOldContent;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Stefan
 * class for VC
 */
public class VersionController extends Controller {

    /**
     * displays all available versions and rollback possibilities for document
     * @param docID ID of document
     * @return redirect
     */
    @Security.Authenticated(SecurityDozent.class)
    public static Result versionPage(int docID) {
        VersionControlDBConnection  versionControlDBConnection  = new VersionControlDBConnection();
        DocumentDBConnection documentDBConnection = new DocumentDBConnection();
        Document doc = documentDBConnection.getDocumentByID(docID);

        //getting all changes
        List<Document>              docVersions                 = versionControlDBConnection.getVersionDocumentToDoc(docID);
        List<ChapterChan>           chapterVersions             = versionControlDBConnection.getVersionChapterToDoc(docID);
        List<Content>               contentVersions             = versionControlDBConnection.getVersionContentToDoc(docID);
        List<ContentHeadline>       headlineVersions            = versionControlDBConnection.getVersionContentHeadlineToDoc(docID);
        List<ContentSubHeadline>    subHeadlineVersions         = versionControlDBConnection.getVersionContentSubHeadlineToDoc(docID);

        //adding all changes to single list
        List<AbstractVersion>       versionList                 = new LinkedList<>();
        versionList.addAll(docVersions);
        versionList.addAll(chapterVersions);
        versionList.addAll(contentVersions);
        versionList.addAll(headlineVersions);
        versionList.addAll(subHeadlineVersions);
        //sorting list by timestamp
        Collections.sort(versionList);

        //getting user information
        String email    = session().get("email");
        int role        = Integer.parseInt(session().get("role"));
        return ok(versionPage.render(versionList, doc, email, role));
    }

    /**
     * showcases old Version of Type
     * @param id ID of Version to be showcased
     * @param type 0: chapter, 1: content, 2: document (not used), 3: headline, 4: subHeadline  getType by looking at changeLog
     * @param docID ID of document
     * @return redirect
     */
    @Security.Authenticated(SecurityDozent.class)
    public static Result showCaseVersion(int id, int type, int docID) {
        String  email    = session().get("email");
        int     role     = Integer.parseInt(session().get("role"));
        DocumentDBConnection documentDBConnection = new DocumentDBConnection();
        Document doc = documentDBConnection.getDocumentByID(docID);

        VersionControlDBConnection  versionControlDBConnection  = new VersionControlDBConnection();
        switch (type) {
            case 0: //case for chapter
                //Get VersionChapter and contents to the chapter
                ChapterChan chapterForVersionChapter  = versionControlDBConnection.getVersionChapterByID(id);
                List<Integer> contentListIDsToChapter = versionControlDBConnection.getVersionContentIDsToChapter(chapterForVersionChapter.getChapterID());
                List<Content> contentListToChapter = contentListIDsToChapter.stream().map(versionControlDBConnection::getVersionContentByID).collect(Collectors.toCollection(LinkedList::new));

                //Get all VersionHeadlinesID and create empty List for all Headlines und empty HashMap for Contents to the headlines
                LinkedList<ContentHeadline> headlinesForVersionChapter = new LinkedList<>();
                HashMap<Integer, LinkedList<Content>> contentListToHeadlinesForVersionChapter = new HashMap<>();
                List<Integer> versionHeadlineIDs = versionControlDBConnection.getVersionHeadlineIDsToChapter(chapterForVersionChapter.getChapterID());

                //Create empty List for all SubHeadlines und empty HashMap for Contents to the subHeadlines
                HashMap<Integer, LinkedList<ContentSubHeadline>> subHeadlinesForVersionChapter = new HashMap<>();
                HashMap<Integer, LinkedList<Content>> contentListToSubHeadlinesForVersionChapter = new HashMap<>();

                //for all headlines
                for(Integer versionHeadlineID : versionHeadlineIDs){
                    ContentHeadline tempHeadline = versionControlDBConnection.getVersionHeadlineByID(versionHeadlineID);
                    List<Integer> versionContentIDs = versionControlDBConnection.getVersionContentIDsToHeadline(tempHeadline.getcHeadLineID());
                    LinkedList<Content> versionContents = new LinkedList<>();
                    for(Integer versionContentID : versionContentIDs){
                        Content tempVersionContent = versionControlDBConnection.getVersionContentByID(versionContentID);
                        versionContents.add(tempVersionContent);
                    }

                    //Add Headline and contents to List/HashMap
                    headlinesForVersionChapter.add(tempHeadline);
                    contentListToHeadlinesForVersionChapter.put(tempHeadline.getcHeadLineID(), versionContents);

                    //SubHeadlines
                    subHeadlinesForVersionChapter = new HashMap<>();
                    contentListToSubHeadlinesForVersionChapter = new HashMap<>();

                    List<Integer> versionSubHeadlineIDs = versionControlDBConnection.getVersionSubHeadlineIDsToHeadline(tempHeadline.getcHeadLineID());
                    LinkedList<ContentSubHeadline> subHeadlinesForOneHeadline = new LinkedList<>();

                    //for all subHeadlines
                    for(Integer versionSubHeadlineID : versionSubHeadlineIDs){
                        ContentSubHeadline tempSubHeadline = versionControlDBConnection.getVersionSubHeadlineByID(versionSubHeadlineID);
                        List<Integer> versionContentIDsToSubHeadlines = versionControlDBConnection.getVersionContentIDsToSubHeadline(tempSubHeadline.getcSubHeadLineID());
                        LinkedList<Content> versionContentsToSubHeadline = new LinkedList<>();
                        for(Integer versionContentID : versionContentIDsToSubHeadlines){
                            Content tempVersionContent = versionControlDBConnection.getVersionContentByID(versionContentID);
                            versionContentsToSubHeadline.add(tempVersionContent);
                        }

                        //Add SubHeadline and contents to List/HashMap
                        subHeadlinesForOneHeadline.add(tempSubHeadline);
                        contentListToSubHeadlinesForVersionChapter.put(tempSubHeadline.getcSubHeadLineID(), versionContentsToSubHeadline);
                    }

                    //Put SubHeadline in HasMap
                    subHeadlinesForVersionChapter.put(tempHeadline.getcHeadLineID(), subHeadlinesForOneHeadline);

                }

                return ok(versionOldChapter.render(chapterForVersionChapter, contentListToChapter, headlinesForVersionChapter, contentListToHeadlinesForVersionChapter, subHeadlinesForVersionChapter, contentListToSubHeadlinesForVersionChapter,  doc, email, role));
            case 1://case for content
                Content     tmpContent  = versionControlDBConnection.getVersionContentByID(id);
                return ok(versionOldContent.render(tmpContent, doc, email, role));

            case 3: //case for headline
                //Get VersionHeadline and Contents
                ContentHeadline tmpHeadline = versionControlDBConnection.getVersionHeadlineByID(id);
                List<Integer> contentListIDsToHeadline = versionControlDBConnection.getVersionContentIDsToHeadline(tmpHeadline.getcHeadLineID());
                List<Content> contentListToHeadline = contentListIDsToHeadline.stream().map(versionControlDBConnection::getVersionContentByID).collect(Collectors.toCollection(LinkedList::new));

                LinkedList<ContentSubHeadline> subHeadlines = new LinkedList<>();
                HashMap<Integer, LinkedList<Content>> contentListToSubHeadlines = new HashMap<>();

                //Get all SubHeadlines and Contents to SubHeadlines
                List<Integer> versionSubHeadlineIDs = versionControlDBConnection.getVersionSubHeadlineIDsToHeadline(tmpHeadline.getcHeadLineID());
                for(Integer versionSubHeadlineID : versionSubHeadlineIDs){
                    ContentSubHeadline tempSubHeadline = versionControlDBConnection.getVersionSubHeadlineByID(versionSubHeadlineID);
                    List<Integer> versionContentIDs = versionControlDBConnection.getVersionContentIDsToSubHeadline(tempSubHeadline.getcSubHeadLineID());
                    LinkedList<Content> versionContents = new LinkedList<>();
                    for(Integer versionContentID : versionContentIDs){
                        Content tempVersionContent = versionControlDBConnection.getVersionContentByID(versionContentID);
                        versionContents.add(tempVersionContent);
                    }

                    //Add SubHeadline and Contents to List/HashMap
                    subHeadlines.add(tempSubHeadline);
                    contentListToSubHeadlines.put(tempSubHeadline.getcSubHeadLineID(), versionContents);
                }

                return ok(versionOldHeadline.render(tmpHeadline, contentListToHeadline, subHeadlines, contentListToSubHeadlines, doc, email, role));

            case 4: //case for subHeadline
                //Get VersionSubHeadline and Contents
                ContentSubHeadline  tmpSubHeadline  = versionControlDBConnection.getVersionSubHeadlineByID(id);
                List<Integer> versionContentIDs = versionControlDBConnection.getVersionContentIDsToSubHeadline(tmpSubHeadline.getcSubHeadLineID());
                List<Content> versionContents = versionContentIDs.stream().map(versionControlDBConnection::getVersionContentByID).collect(Collectors.toCollection(LinkedList::new));
                return ok(versionOldSubHeadline.render(tmpSubHeadline, versionContents, doc, email, role));
        }
        return badRequest("Something went terribly wrong here!");
    }

    //following methods are used to create versions of Objects
    /**
     * creates a versionChapter for changedChapter
     * @param chapter changedChapter
     */
    @Security.Authenticated(SecurityDozent.class)
    public static void chapterChanged(ChapterChan chapter) {
        VersionControlDBConnection  versionControlDBConnection  = new VersionControlDBConnection();

        String   localDateTime   = LocalDateTime.now().toString();
        versionControlDBConnection.createVersionChapter(chapter.getDocID(), chapter.getChapterID(), chapter.getTitle(), "chapter changed" + " " + chapter.getTitle(), localDateTime, chapter.getSequence());
    }

    /**
     * creates a versionChapter for deletedChapter
     * @param chapter chapter to be deleted
     */
    @Security.Authenticated(SecurityDozent.class)
    public static void chapterDeleted(ChapterChan chapter) {
        VersionControlDBConnection  versionControlDBConnection  = new VersionControlDBConnection();
        ContentDBConnection         contentDBConnection         = new ContentDBConnection();

        //making snapshots of all children
        List<Content>               contentChildren             = contentDBConnection.getContentToChapter(chapter.getChapterID());
        List<ContentHeadline>       headlineChildren            = contentDBConnection.getContentHeadLinesToChapter(chapter.getChapterID());

        contentChildren.forEach(controllers.VersionController::contentDeleted);
        headlineChildren.forEach(controllers.VersionController::headlineDeleted);

        String   localDateTime   = LocalDateTime.now().toString();
        versionControlDBConnection.createVersionChapter(chapter.getDocID(), chapter.getChapterID(), chapter.getTitle(), "chapter deleted" + " " + chapter.getTitle(), localDateTime, chapter.getSequence());
    }

    /**
     * creates a versionContent for changedContent
     * @param content changedContent
     */
    @Security.Authenticated(SecurityDozent.class)
    public static void contentChanged(Content content) {
        VersionControlDBConnection  versionControlDBConnection  = new VersionControlDBConnection();
        ContentDBConnection         contentDBConnection         = new ContentDBConnection();
        int                         docID;

        if(content.getChapterID() != -1) {//checks what kind of parent the content has and resolves docID accordingly
            docID = contentDBConnection.getChapterByID(content.getChapterID()).getDocID();
        }else if(content.getcHeadLineID() != -1){
            docID = contentDBConnection.getChapterByID((contentDBConnection.getContentHeadLineByID(content.getcHeadLineID()).getChapterID())).getDocID();
        }else{
            docID = contentDBConnection.getChapterByID(contentDBConnection.getContentHeadLineByID(contentDBConnection.getContentSubHeadLineByID(content.getcSubHeadLineID()).getCheadlineID()).getChapterID()).getDocID();
        }

        String  localDateTime   = LocalDateTime.now().toString();
        versionControlDBConnection.createVersionContent(docID, content.getContentID(), content.getContent(), content.getType(), "content changed", localDateTime, content.getSequence(), content.getChapterID(), content.getcHeadLineID(), content.getcSubHeadLineID());
    }

    /**
     * creates a versionContent for deleted content
     * @param content content do be deleted
     */
    @Security.Authenticated(SecurityDozent.class)
    public static void contentDeleted(Content content) {
        VersionControlDBConnection  versionControlDBConnection  = new VersionControlDBConnection();
        ContentDBConnection         contentDBConnection         = new ContentDBConnection();
        int                         docID;

        if(content.getChapterID() != -1) {//checks what kind of parent the content has and resolves docID accordingly
            docID = contentDBConnection.getChapterByID(content.getChapterID()).getDocID();
        }else if(content.getcHeadLineID() != -1){
            docID = contentDBConnection.getChapterByID((contentDBConnection.getContentHeadLineByID(content.getcHeadLineID()).getChapterID())).getDocID();
        }else{
            docID = contentDBConnection.getChapterByID(contentDBConnection.getContentHeadLineByID(contentDBConnection.getContentSubHeadLineByID(content.getcSubHeadLineID()).getCheadlineID()).getChapterID()).getDocID();
        }

        String  localDateTime   = LocalDateTime.now().toString();
        versionControlDBConnection.createVersionContent(docID, content.getContentID(), content.getContent(), content.getType(), "content deleted", localDateTime, content.getSequence(), content.getChapterID(), content.getcHeadLineID(), content.getcSubHeadLineID());
    }

    /**
     * creates a versionDocument for changed document. Not used in current version.
     * @param doc changedDocument
     */
    @Security.Authenticated(SecurityDozent.class)
    public static void documentChanged(Document doc) {
        VersionControlDBConnection  versionControlDBConnection  = new VersionControlDBConnection();

        String   localDateTime   = LocalDateTime.now().toString();
        versionControlDBConnection.createVersionDocument(doc.getDocumentID(), doc.getDocumentName(), "document changed", localDateTime);
    }
    //no document deleted because if parent is deleted children are not rollback-able so there is no real reason to rollback a deletion of a document

    /**
     * creates a versionHeadline for changedHeadline
     * @param contentHeadline changedHeadline
     */
    @Security.Authenticated(SecurityDozent.class)
    public static void headlineChanged(ContentHeadline contentHeadline) {
        VersionControlDBConnection  versionControlDBConnection  = new VersionControlDBConnection();
        ContentDBConnection         contentDBConnection         = new ContentDBConnection();
        int     docID                                           = contentDBConnection.getChapterByID(contentHeadline.getChapterID()).getDocID();

        String  localDateTime   = LocalDateTime.now().toString();
        versionControlDBConnection.createVersionHeadline(docID, contentHeadline.getcHeadLineID(), contentHeadline.getTitle(), "headline changed" + " " + contentHeadline.getTitle(), localDateTime, contentHeadline.getSequence(), contentHeadline.getChapterID());
    }

    /**
     * creates a versionHeadline of deleted headline
     * @param contentHeadline headline to be deleted
     */
    @Security.Authenticated(SecurityDozent.class)
    public static void headlineDeleted(ContentHeadline contentHeadline) {
        VersionControlDBConnection  versionControlDBConnection  = new VersionControlDBConnection();
        ContentDBConnection         contentDBConnection         = new ContentDBConnection();
        int     docID           = contentDBConnection.getChapterByID(contentHeadline.getChapterID()).getDocID();

        //due to cascades in db when deleting all children of headline have to be saved as well
        List<Content>               contentChildren             = contentDBConnection.getContentToContentHeadline(contentHeadline.getcHeadLineID());
        List<ContentSubHeadline>    subHeadlineChildren         = contentDBConnection.getContentSubHeadLinesToHeadline(contentHeadline.getcHeadLineID());

        //creates versions forEach child
        contentChildren.forEach(controllers.VersionController::contentDeleted);
        subHeadlineChildren.forEach(controllers.VersionController::subHeadlineDeleted);

        String  localDateTime   = LocalDateTime.now().toString();
        versionControlDBConnection.createVersionHeadline(docID, contentHeadline.getcHeadLineID(), contentHeadline.getTitle(), "headline deleted" + " " + contentHeadline.getTitle(), localDateTime, contentHeadline.getSequence(), contentHeadline.getChapterID());
    }

    /**
     * creates a versionSubHeadline for changed subHeadline
     * @param contentSubHeadline changedSubHeadline
     */
    @Security.Authenticated(SecurityDozent.class)
    public static void subHeadlineChanged(ContentSubHeadline contentSubHeadline) {
        VersionControlDBConnection  versionControlDBConnection  = new VersionControlDBConnection();
        ContentDBConnection         contentDBConnection         = new ContentDBConnection();
        int     docID           = contentDBConnection.getChapterByID(contentDBConnection.getContentHeadLineByID(contentSubHeadline.getCheadlineID()).getChapterID()).getDocID();

        String  localDateTime   = LocalDateTime.now().toString();
        versionControlDBConnection.createVersionSubHeadline(docID, contentSubHeadline.getcSubHeadLineID(), contentSubHeadline.getTitle(), "subheadline changed" + " " + contentSubHeadline.getTitle(), localDateTime, contentSubHeadline.getSequence(), contentSubHeadline.getCheadlineID());
    }

    /**
     * creates a versionSubHeadline for deleted subHeadline
     * @param contentSubHeadline subHeadline to be deleted
     */
    @Security.Authenticated(SecurityDozent.class)
    public static void subHeadlineDeleted(ContentSubHeadline contentSubHeadline) {
        VersionControlDBConnection  versionControlDBConnection  = new VersionControlDBConnection();
        ContentDBConnection         contentDBConnection         = new ContentDBConnection();
        int     docID           = contentDBConnection.getChapterByID(contentDBConnection.getContentHeadLineByID(contentSubHeadline.getCheadlineID()).getChapterID()).getDocID();

        //due to cascades in database when deleting a subHeadline children have to be saved as well
        List<Content>               contentChildren             = contentDBConnection.getContentToContentSubHeadline(contentSubHeadline.getcSubHeadLineID());

        //creating version forEach child
        contentChildren.forEach(controllers.VersionController::contentDeleted);

        String  localDateTime   = LocalDateTime.now().toString();
        versionControlDBConnection.createVersionSubHeadline(docID, contentSubHeadline.getcSubHeadLineID(), contentSubHeadline.getTitle(), "subheadline deleted" + " " + contentSubHeadline.getTitle(), localDateTime, contentSubHeadline.getSequence(), contentSubHeadline.getCheadlineID());
    }

    /**
     * view for rollback of Chapter
     * @param versionChapterID ID of versionChapter
     * @param docID ID of related document
     * @return redirect
     */
    @Security.Authenticated(SecurityDozent.class)
    public static Result rollbackChapterPublic(int versionChapterID, int docID){
        // rollback chapter works always, because when doc does not exist -> no rollback for chapter possible
        rollbackChapter(versionChapterID);
        return redirect(routes.VersionController.versionPage(docID));
    }

    /**
     * restores chapter that was stored in version and deletes version, also restores all children that were deleted as a result of chapterDeletion if it was one
     * @param versionChapterID ID of versionChapter to be committed to document
     */
    @Security.Authenticated(SecurityDozent.class)
    private static void rollbackChapter(int versionChapterID){
        VersionControlDBConnection  versionControlDBConnection  = new VersionControlDBConnection();

        ChapterChan                 chapterToRollback           = versionControlDBConnection.getVersionChapterByID(versionChapterID);
        //changeLog format: chapter changed/deleted
        String                      rollbackKind                = chapterToRollback.getChangeLog().split(" ")[1];

        if(rollbackKind.equals("changed")){ //simply revert title and sequence to what it was
            ContentController.changeChapter(versionChapterID);
        }else{ //have to rebuild from scratch
            ContentController.addChapter(chapterToRollback);

            List<Integer>   contentIDs  = versionControlDBConnection.getVersionContentIDsToChapter(chapterToRollback.getChapterID());
            List<Integer>   headlineIDs = versionControlDBConnection.getVersionHeadlineIDsToChapter(chapterToRollback.getChapterID());

            HashMap<Integer, Content>         contentAgeCheckHMap   = new HashMap<>(); //depicts formerContentID -> latest version
            HashMap<Integer, ContentHeadline> headlineAgeCheckHMap  = new HashMap<>(); //depicts formerHeadlineID -> latest version

            for(int i: contentIDs){ //checks all contentVersions and puts latest into hashMap
                Content tmpC            = versionControlDBConnection.getVersionContentByID(i);
                int     contentID       = tmpC.getContentID();
                if(Math.abs(Duration.between(tmpC.getTimeStamp(), chapterToRollback.getTimeStamp()).toMillis()) < 500)//impossible to have multiple contentRollbacks within this short time
                    contentAgeCheckHMap.put(contentID, tmpC);
            }
            for(int i: headlineIDs){ //checks all headlineVersions and puts latest into hashMap
                ContentHeadline tmpCH           = versionControlDBConnection.getVersionHeadlineByID(i);
                int             headlineID       = tmpCH.getcHeadLineID();
                if(Math.abs(Duration.between(tmpCH.getTimeStamp(), chapterToRollback.getTimeStamp()).toMillis()) < 500)//impossible to have multiple headlineRollbacks within this short time
                    headlineAgeCheckHMap.put(headlineID, tmpCH);
            }
            //Rollback for contentChildren
            Set<Integer> contentKeys            = contentAgeCheckHMap.keySet();
            for(Integer i: contentKeys){
                rollbackContent(contentAgeCheckHMap.get(i).getId());
            }
            //Rollback for headlineChildren
            Set<Integer> headlineKeys           = headlineAgeCheckHMap.keySet();
            for(Integer i: headlineKeys){
                rollbackHeadline(headlineAgeCheckHMap.get(i).getId());
            }
            versionControlDBConnection.deleteVersionChapter(versionChapterID);
        }
    }

    /**
     * view for rollback of Headline
     * @param versionHeadlineID ID of versionHeadline
     * @param docID ID of related document
     * @return redirect
     */
    @Security.Authenticated(SecurityDozent.class)
    public static Result rollbackHeadlinePublic(int versionHeadlineID, int docID){
        boolean success = rollbackHeadline(versionHeadlineID);
        if(!success){
            flash("danger", "Rollback failed. Please make sure that parent exists.");
        }
        return redirect(routes.VersionController.versionPage(docID));
    }

    /**
     * restores headline stored in versionHeadline and deletes version, also restores all children that were deleted as a result of headlineDeletion if it was one
     * @param versionHeadlineID ID of versionHeadline to be committed to document
     */
    @Security.Authenticated(SecurityDozent.class)
    private static boolean rollbackHeadline(int versionHeadlineID){
        boolean success;
        VersionControlDBConnection  versionControlDBConnection  = new VersionControlDBConnection();
        ContentDBConnection         contentDBConnection         = new ContentDBConnection();

        ContentHeadline             headlineToRollback          = versionControlDBConnection.getVersionHeadlineByID(versionHeadlineID);
        //changeLog format: headline changed/deleted
        String                      rollbackKind                = headlineToRollback.getChangeLog().split(" ")[1];

        if(!(contentDBConnection.getChapterByID(headlineToRollback.getChapterID()) == null)) { //checks if parent exists if not it was deleted together or after headline
            if (rollbackKind.equals("changed")) {//simply revert title and sequence to what it was
                ContentController.changeContentHeadline(versionHeadlineID);
            } else { //have to rebuild from scratch
                ContentController.addContentHeadline(headlineToRollback);

                List<Integer> contentIDs        = versionControlDBConnection.getVersionContentIDsToHeadline(headlineToRollback.getcHeadLineID());
                List<Integer> subHeadlineIDs    = versionControlDBConnection.getVersionSubHeadlineIDsToHeadline(headlineToRollback.getcHeadLineID());

                HashMap<Integer, Content>               contentAgeCheckHMap     = new HashMap<>(); //depicts formerContentID -> latest version
                HashMap<Integer, ContentSubHeadline>    subHeadlineAgeCheckHMap = new HashMap<>(); //depicts formerSubHeadlineID -> latest version

                for(int i: contentIDs){ //checks all contentVersions and puts latest into hashMap
                    Content tmpC            = versionControlDBConnection.getVersionContentByID(i);
                    int     contentID       = tmpC.getContentID();
                    if(Math.abs(Duration.between(tmpC.getTimeStamp(), headlineToRollback.getTimeStamp()).toMillis()) < 500)//impossible to have multiple contentRollbacks within this short time
                        contentAgeCheckHMap.put(contentID, tmpC);
                }
                for(int i: subHeadlineIDs){ //checks all headlineVersions and puts latest into hashMap
                    ContentSubHeadline  tmpCH               = versionControlDBConnection.getVersionSubHeadlineByID(i);
                    int                 subHeadlineID       = tmpCH.getcSubHeadLineID();
                    if(Math.abs(Duration.between(tmpCH.getTimeStamp(), headlineToRollback.getTimeStamp()).toMillis()) < 500)//impossible to have multiple subHeadlineRollbacks within this short time
                        subHeadlineAgeCheckHMap.put(subHeadlineID, tmpCH);
                }

                //Rollback for contentChildren
                Set<Integer> contentKeys            = contentAgeCheckHMap.keySet();
                for(Integer i: contentKeys){
                    rollbackContent(contentAgeCheckHMap.get(i).getId());
                }

                //Rollback for subHeadlineChildren
                Set<Integer> subHeadlineKeys        = subHeadlineAgeCheckHMap.keySet();
                for (Integer i : subHeadlineKeys) {
                    rollbackSubHeadline(subHeadlineAgeCheckHMap.get(i).getId());
                }
                versionControlDBConnection.deleteVersionHeadline(versionHeadlineID);
            }
            success = true;
        }else{
            success = false;
            System.out.println("Rollback of SubContent failed -> Parent missing");
        }
        return success;
    }

    /**
     * view for rollback of subHeadline
     * @param versionSubHeadlineID ID of versionSubHeadline
     * @param docID ID of related document
     * @return redirect
     */
    @Security.Authenticated(SecurityDozent.class)
    public static Result rollbackSubHeadlinePublic(int versionSubHeadlineID, int docID){
        boolean success = rollbackSubHeadline(versionSubHeadlineID);
        if(!success){
            flash("danger", "Rollback failed. Please make sure that parent exists.");
        }
        return redirect(routes.VersionController.versionPage(docID));
    }

    /**
     * restores subHeadline stored in versionSubHeadline and deletes version, also restores all children that were deleted as a result of subHeadlineDeletion if it was one
     * @param versionSubHeadlineID ID of subHeadlineVersion
     */
    @Security.Authenticated(SecurityDozent.class)
    private static boolean rollbackSubHeadline(int versionSubHeadlineID){
        boolean success;
        VersionControlDBConnection  versionControlDBConnection  = new VersionControlDBConnection();
        ContentDBConnection         contentDBConnection         = new ContentDBConnection();

        ContentSubHeadline          subHeadlineToRollback       = versionControlDBConnection.getVersionSubHeadlineByID(versionSubHeadlineID);

        //changeLog format: subHeadline changed/deleted
        String                      rollbackKind                = subHeadlineToRollback.getChangeLog().split(" ")[1];

        if(!(contentDBConnection.getContentHeadLineByID(subHeadlineToRollback.getCheadlineID()) == null)) {
            if (rollbackKind.equals("changed")) {//simply revert title and sequence to what it was
                ContentController.changeContentSubHeadline(versionSubHeadlineID);
            } else { //have to rebuild from scratch
                ContentController.addContentSubHeadline(subHeadlineToRollback);

                List<Integer> contentIDs = versionControlDBConnection.getVersionContentIDsToSubHeadline(subHeadlineToRollback.getcSubHeadLineID());

                HashMap<Integer, Content>               contentAgeCheckHMap     = new HashMap<>(); //depicts formerContentID -> latest version

                for(int i: contentIDs){ //checks all contentVersions and puts latest into hashMap
                    Content tmpC            = versionControlDBConnection.getVersionContentByID(i);
                    int     contentID       = tmpC.getContentID();
                    if(Math.abs(Duration.between(tmpC.getTimeStamp(), subHeadlineToRollback.getTimeStamp()).toMillis()) < 500)//impossible to have multiple contentRollbacks within this short time
                        contentAgeCheckHMap.put(contentID, tmpC);
                }

                //Rollback for contentChildren
                Set<Integer> contentKeys            = contentAgeCheckHMap.keySet();
                for(Integer i: contentKeys){
                    rollbackContent(contentAgeCheckHMap.get(i).getId());
                }
                versionControlDBConnection.deleteVersionSubHeadline(versionSubHeadlineID);
            }
            success = true;
        }else{
            success = false;
            System.out.println("Rollback of SubContent failed -> Parent missing");
        }
        return success;
    }

    /**
     * view for rollback of content
     * @param versionContentID ID of versionContent
     * @param docID ID of related document
     * @return redirect
     */
    @Security.Authenticated(SecurityDozent.class)
    public static Result rollbackContentPublic(int versionContentID, int docID){
        boolean success = rollbackContent(versionContentID);
        if(!success){
            flash("danger", "Rollback failed. Please make sure that parent exists.");
        }
        return redirect(routes.VersionController.versionPage(docID));
    }

    /**
     * restores content stored in versionContent and deletes version
     * @param versionContentID ID of versionContent
     */
    @Security.Authenticated(SecurityDozent.class)
    private static boolean rollbackContent(int versionContentID){
        boolean success;
        VersionControlDBConnection  versionControlDBConnection  = new VersionControlDBConnection();
        ContentDBConnection         contentDBConnection         = new ContentDBConnection();

        Content contentToRollback = versionControlDBConnection.getVersionContentByID(versionContentID);
        //changeLog format: content changed/deleted
        String rollbackKind = contentToRollback.getChangeLog().split(" ")[1];

        if (!(contentDBConnection.getChapterByID(contentToRollback.getChapterID()) == null &&
                contentDBConnection.getContentHeadLineByID(contentToRollback.getcHeadLineID()) == null &&
                contentDBConnection.getContentSubHeadLineByID(contentToRollback.getcSubHeadLineID()) == null)) { //checks if any parent exists if not it was deleted together or after content
            if (rollbackKind.equals("changed")) {//simply revert content, type and sequence
                ContentController.changeContent(versionContentID);
            } else {
                ContentController.addContent(contentToRollback);
            }
            versionControlDBConnection.deleteVersionContent(versionContentID);
            success = true;
        } else {
            success = false;
            System.out.println("Content not rollback -> parent is missing.");
        }
        return success;
    }
}
