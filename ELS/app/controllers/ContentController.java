package controllers;

import controllers.DBConnection.*;
import models.*;
import play.data.Form;
import controllers.routes;
import controllers.Security.*;
import play.mvc.Controller;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import play.mvc.Http;
import play.mvc.Security;
import play.mvc.Result;
import views.html.addReferencePage;
import views.html.changeContentPageNEU;


/**
 * @author Stefan, Thomas
 * class for Content and Parents
 */
public class ContentController extends Controller{

    //Chapter related
    /**
     * creates and adds new chapter to referred doc
     * @param docID ID of doc
     * @return redirect
     */
    @Security.Authenticated(SecurityModerator.class)
    public static Result addChapter(int docID) {
        ContentDBConnection contentDBConnection = new ContentDBConnection();

        ChapterChan         chapterChan         = Form.form(ChapterChan.class).bindFromRequest().get();
        chapterChan.setDocID(docID);

        List<ChapterChan>   chapterList         = contentDBConnection.getChaptersToDoc(docID);
        chapterList.stream().filter(chapter -> chapter.getSequence() >= chapterChan.getSequence()).forEach(chapter -> contentDBConnection.changeChapterSequence(chapter.getChapterID(), (chapter.getSequence() + 1)));

        contentDBConnection.createChapter(chapterChan.getDocID(), chapterChan.getSequence(), chapterChan.getTitle());

        return redirect(controllers.routes.DocumentControllerSama.documentPage(docID, true));
    }

    /**
     * adds rollback chapter to database
     * @param chapterChan rollbackChapter
     */
    public static void addChapter(ChapterChan chapterChan){
        ContentDBConnection contentDBConnection = new ContentDBConnection();

        List<ChapterChan>   chapterList         = contentDBConnection.getChaptersToDoc(chapterChan.getDocID());
        chapterList.stream().filter(chapter -> chapter.getSequence() >= chapterChan.getSequence()).forEach(chapter -> contentDBConnection.changeChapterSequence(chapter.getChapterID(), (chapter.getSequence() + 1)));

        contentDBConnection.createChapter(chapterChan.getChapterID(), chapterChan.getDocID(), chapterChan.getSequence(), chapterChan.getTitle());
    }

    /**
     * changes referred chapter
     * @param docID ID of doc
     * @param chapterID ID of chapter
     * @return redirect
     */
    @Security.Authenticated(SecurityModerator.class)
    public static Result changeChapter(int docID, int chapterID) {
        ContentDBConnection contentDBConnection = new ContentDBConnection();

        ChapterChan         oldChapter      = contentDBConnection.getChapterByID(chapterID);
        VersionController.chapterChanged(oldChapter);
        List<ChapterChan>   chapterList     = contentDBConnection.getChaptersToDoc(docID);

        ChapterChan         changedChapter  = Form.form(ChapterChan.class).bindFromRequest().get();
        changedChapter.setDocID(docID);
        changedChapter.setChapterID(chapterID);

        changeChapter(changedChapter, oldChapter, chapterList);

        return redirect(controllers.routes.DocumentControllerSama.documentPage(docID, true));
    }

    /**
     * changes chapter to rollback
     * sequence will remain the same
     * @param vChapterID ID of rollbackChapter
     */
    public static void changeChapter(int vChapterID){
        ContentDBConnection         contentDBConnection         = new ContentDBConnection();
        VersionControlDBConnection  versionControlDBConnection  = new VersionControlDBConnection();

        ChapterChan         changedChapter  = versionControlDBConnection.getVersionChapterByID(vChapterID);
        ChapterChan         oldChapter      = contentDBConnection.getChapterByID(changedChapter.getChapterID());
        changedChapter.setSequence(oldChapter.getSequence());
        VersionController.chapterChanged(oldChapter);
        List<ChapterChan>   chapterList     = contentDBConnection.getChaptersToDoc(oldChapter.getDocID());

        changeChapter(changedChapter, oldChapter, chapterList);
    }

    /**
     * changes oldChapter into changedChapter and resolves sequence issues with chapterList
     * @param changedChapter chapter to be committed
     * @param oldChapter chapter to be changed
     * @param chapterList list for sequence resolve
     */
    private static void changeChapter(ChapterChan changedChapter, ChapterChan oldChapter, List<ChapterChan> chapterList) {
        ContentDBConnection contentDBConnection = new ContentDBConnection();
        //change headline
        if (!changedChapter.getTitle().equals(oldChapter.getTitle())) {
            contentDBConnection.changeChapterTitle(changedChapter.getChapterID(), changedChapter.getTitle());
        }
        //change sequence
        if (changedChapter.getSequence() !=  oldChapter.getSequence()) {
            if (changedChapter.getSequence() <  oldChapter.getSequence()) {
                for(int i=changedChapter.getSequence(); i<oldChapter.getSequence(); i++ ) {
                    contentDBConnection.changeChapterSequence(chapterList.get(i - 1).getChapterID(), chapterList.get(i - 1).getSequence() + 1);
                }
                contentDBConnection.changeChapterSequence(oldChapter.getChapterID(), changedChapter.getSequence());
            } else if (changedChapter.getSequence() >  oldChapter.getSequence()) {
                for(int i=oldChapter.getSequence()+1; i<changedChapter.getSequence(); i++ ) {
                    contentDBConnection.changeChapterSequence(chapterList.get(i - 1).getChapterID(), chapterList.get(i - 1).getSequence() - 1);
                }
                contentDBConnection.changeChapterSequence(oldChapter.getChapterID(), changedChapter.getSequence()-1);
            }
        }
    }

    /**
     * deletes chapter
     * @param chapterID ID of chapter to be deleted
     * @param docID ID of parent
     * @return redirect
     */
    @Security.Authenticated(SecurityModerator.class)
    public static Result deleteChapter(int chapterID, int docID) {
        ContentDBConnection contentDBConnection         = new ContentDBConnection();

        ChapterChan         chapterToBeDeleted  = contentDBConnection.getChapterByID(chapterID);
        VersionController.chapterDeleted(chapterToBeDeleted);
        List<ChapterChan>   chaptersToDoc       = contentDBConnection.getChaptersToDoc(docID);

        chaptersToDoc.stream().filter(chapter -> chapter.getSequence() > chapterToBeDeleted.getSequence()).forEach(chapter -> contentDBConnection.changeChapterSequence(chapter.getChapterID(), chapter.getSequence() - 1));

        contentDBConnection.deleteChapter(chapterID);

        return redirect(controllers.routes.DocumentControllerSama.documentPage(docID, true));
    }


    //ContentHeadline related
    /**
     * adds contentHeadline to chapter
     * @param chapterID ID of referred chapter
     * @return redirect
     */
    @Security.Authenticated(SecurityModerator.class)
    public static Result addContentHeadline(int chapterID) {
        ContentDBConnection contentDBConnection = new ContentDBConnection();

        ContentHeadline     newContentHeadline  = Form.form(ContentHeadline.class).bindFromRequest().get();
        List<ContentHeadline> contentHList = contentDBConnection.getContentHeadLinesToChapter(chapterID);
        contentHList.stream().filter(contentH -> contentH.getSequence() >= newContentHeadline.getSequence()).forEach(contentH -> contentDBConnection.changeContentHeadLineSequence(contentH.getcHeadLineID(), (contentH.getSequence() + 1)));
        contentDBConnection.createContentHeadLine(chapterID, newContentHeadline.getTitle(), newContentHeadline.getSequence());

        int docID = contentDBConnection.getChapterByID(chapterID).getDocID();
        return redirect(controllers.routes.DocumentControllerSama.documentPage(docID, true));
    }

    /**
     * adds rollbackHeadline to database
     * @param newContentHeadline rollbackHeadline
     */
    public static void addContentHeadline(ContentHeadline newContentHeadline) {
        ContentDBConnection contentDBConnection = new ContentDBConnection();

        List<ContentHeadline> contentHList = contentDBConnection.getContentHeadLinesToChapter(newContentHeadline.getChapterID());
        contentHList.stream().filter(contentH -> contentH.getSequence() >= newContentHeadline.getSequence()).forEach(contentH -> contentDBConnection.changeContentHeadLineSequence(contentH.getcHeadLineID(), (contentH.getSequence() + 1)));
        contentDBConnection.createContentHeadLine(newContentHeadline.getcHeadLineID(), newContentHeadline.getChapterID(), newContentHeadline.getTitle(), newContentHeadline.getSequence());
    }

    /**
     * changes contentHeadline
     * @param cHeadlineID ID of contentHeadline stc
     * @param chapterID ID of chapter parent
     * @return redirect
     */
    @Security.Authenticated(SecurityModerator.class)
    public static Result changeContentHeadline(int cHeadlineID, int chapterID) {
        ContentDBConnection contentDBConnection = new ContentDBConnection();

        ContentHeadline         oldContentHeadline      = contentDBConnection.getContentHeadLineByID(cHeadlineID);
        VersionController.headlineChanged(oldContentHeadline);
        List<ContentHeadline>   contentHeadlines        = contentDBConnection.getContentHeadLinesToChapter(chapterID);


        ContentHeadline         changedContentHeadline  = Form.form(ContentHeadline.class).bindFromRequest().get();
        changedContentHeadline.setcHeadLineID(cHeadlineID);
        changedContentHeadline.setChapterID(chapterID);

        changeContentHeadline(changedContentHeadline, oldContentHeadline, contentHeadlines);

        //TODO
        int docID = contentDBConnection.getChapterByID(chapterID).getDocID();
        return redirect(controllers.routes.DocumentControllerSama.documentPage(docID, true));
    }

    /**
     * changes contentHeadline back to old version
     * sequence will remain the same
     * @param vContentHeadlineID rollbackHeadline
     */
    public static void changeContentHeadline(int vContentHeadlineID){
        ContentDBConnection contentDBConnection = new ContentDBConnection();
        VersionControlDBConnection  versionControlDBConnection  = new VersionControlDBConnection();

        ContentHeadline         changedContentHeadline  = versionControlDBConnection.getVersionHeadlineByID(vContentHeadlineID);
        ContentHeadline         oldContentHeadline      = contentDBConnection.getContentHeadLineByID(changedContentHeadline.getcHeadLineID());
        changedContentHeadline.setSequence(oldContentHeadline.getSequence());
        VersionController.headlineChanged(oldContentHeadline);
        List<ContentHeadline>   contentHeadlines        = contentDBConnection.getContentHeadLinesToChapter(changedContentHeadline.getChapterID());

        changeContentHeadline(changedContentHeadline, oldContentHeadline, contentHeadlines);
    }

    /**
     * changes contentHeadline back to old version
     * @param changedContentHeadline versionHeadline
     * @param oldContentHeadline headline that was stored in db before
     * @param contentHeadlines list of all Headlines of chapter
     */
    private static void changeContentHeadline(ContentHeadline changedContentHeadline, ContentHeadline oldContentHeadline, List<ContentHeadline> contentHeadlines){
        ContentDBConnection contentDBConnection = new ContentDBConnection();
        //change headline
        if (!changedContentHeadline.getTitle().equals(oldContentHeadline.getTitle())) {
            contentDBConnection.changeContentHeadLineTitle(changedContentHeadline.getcHeadLineID(), changedContentHeadline.getTitle());
        }
        //change sequence
        if (changedContentHeadline.getSequence() !=  oldContentHeadline.getSequence()) {
            if (changedContentHeadline.getSequence() <  oldContentHeadline.getSequence()) {
                for(int i=changedContentHeadline.getSequence(); i<oldContentHeadline.getSequence(); i++ ) {
                    contentDBConnection.changeContentHeadLineSequence(contentHeadlines.get(i - 1).getcHeadLineID(), contentHeadlines.get(i - 1).getSequence() + 1);
                }
                contentDBConnection.changeChapterSequence(oldContentHeadline.getChapterID(), changedContentHeadline.getSequence());
            } else if (changedContentHeadline.getSequence() >  oldContentHeadline.getSequence()) {
                for(int i=oldContentHeadline.getSequence()+1; i<changedContentHeadline.getSequence(); i++ ) {
                    contentDBConnection.changeContentHeadLineSequence(contentHeadlines.get(i - 1).getcHeadLineID(), contentHeadlines.get(i - 1).getSequence() - 1);
                }
                contentDBConnection.changeContentHeadLineSequence(oldContentHeadline.getcHeadLineID(), changedContentHeadline.getSequence() - 1);
            }
        }
    }

    /**
     * deletes contentHeadline
     * @param cHeadlineID ID of headline to be deleted
     * @param chapterID ID of parent
     * @return redirect
     */
    @Security.Authenticated(SecurityModerator.class)
    public static Result deleteContentHeadline(int cHeadlineID, int chapterID) {
        ContentDBConnection contentDBConnection         = new ContentDBConnection();

        ContentHeadline  headlineToBeDeleted      = contentDBConnection.getContentHeadLineByID(cHeadlineID);
        VersionController.headlineDeleted(headlineToBeDeleted);
        List<ContentHeadline>    contentHeadlines = contentDBConnection.getContentHeadLinesToChapter(chapterID);

        contentHeadlines.stream().filter(cHeadline -> cHeadline.getSequence() > headlineToBeDeleted.getSequence()).forEach(cHeadline -> contentDBConnection.changeContentSubHeadLineSequence(cHeadline.getcHeadLineID(), cHeadline.getSequence() - 1));
        contentDBConnection.deleteContentHeadLine(cHeadlineID);
        //TODO
        int docID = contentDBConnection.getChapterByID(chapterID).getDocID();
        return redirect(controllers.routes.DocumentControllerSama.documentPage(docID, true));    }


    //ContentSubHeadline related

    /**
     * creates subHeadline child to referred parent
     * @param cHeadLineID ID of parent Headline
     * @return redirect
     */
    @Security.Authenticated(SecurityModerator.class)
    public static Result addContentSubHeadline(int cHeadLineID) {
        ContentDBConnection contentDBConnection = new ContentDBConnection();

        ContentHeadline     newContentSubHeadline  = Form.form(ContentHeadline.class).bindFromRequest().get();
        List<ContentSubHeadline> contentSHList  = contentDBConnection.getContentSubHeadLinesToHeadline(cHeadLineID);
        contentSHList.stream().filter(contentSH -> contentSH.getSequence() >= newContentSubHeadline.getSequence()).forEach(contentSH -> contentDBConnection.changeContentSubHeadLineSequence(contentSH.getcSubHeadLineID(), (contentSH.getSequence() + 1)));
        contentDBConnection.createContentSubHeadLine(cHeadLineID, newContentSubHeadline.getTitle(), newContentSubHeadline.getSequence());

        //TODO
        int chapterID = contentDBConnection.getContentHeadLineByID(cHeadLineID).getChapterID();
        int docID = contentDBConnection.getChapterByID(chapterID).getDocID();
        return redirect(controllers.routes.DocumentControllerSama.documentPage(docID, true));
    }

    /**
     * adds rollback of deleted subHeadline to database
     * @param newContentSubHeadline subHeadline to be committed to database
     */
    public static void addContentSubHeadline(ContentSubHeadline newContentSubHeadline){
        ContentDBConnection         contentDBConnection         = new ContentDBConnection();

        List<ContentSubHeadline> contentSHList  = contentDBConnection.getContentSubHeadLinesToHeadline(newContentSubHeadline.getCheadlineID());
        contentSHList.stream().filter(contentSH -> contentSH.getSequence() >= newContentSubHeadline.getSequence()).forEach(contentSH -> contentDBConnection.changeContentSubHeadLineSequence(contentSH.getcSubHeadLineID(), (contentSH.getSequence() + 1)));
        contentDBConnection.createContentSubHeadLine(newContentSubHeadline.getcSubHeadLineID(), newContentSubHeadline.getCheadlineID(), newContentSubHeadline.getTitle(), newContentSubHeadline.getSequence());
    }

    /**
     * changes referred subHeadline
     * @param cSubHeadLineID ID of referred subHeadline
     * @param cHeadlineID ID of parent headline
     * @return redirect
     */
    @Security.Authenticated(SecurityModerator.class)
    public static Result changeContentSubHeadline(int cSubHeadLineID, int cHeadlineID) {
        ContentDBConnection contentDBConnection = new ContentDBConnection();

        ContentSubHeadline      oldContentSubHeadline   = contentDBConnection.getContentSubHeadLineByID(cSubHeadLineID);
        VersionController.subHeadlineChanged(oldContentSubHeadline);
        List<ContentSubHeadline>contentSubHeadlines     = contentDBConnection.getContentSubHeadLinesToHeadline(cHeadlineID);

        ContentSubHeadline      changedContentSubHeadline= Form.form(ContentSubHeadline.class).bindFromRequest().get();
        changedContentSubHeadline.setCheadlineID(cHeadlineID);
        changedContentSubHeadline.setcSubHeadLineID(cSubHeadLineID);

        changeContentSubHeadline(changedContentSubHeadline, oldContentSubHeadline, contentSubHeadlines);

        //TODO
        int chapterID = contentDBConnection.getContentHeadLineByID(cHeadlineID).getChapterID();
        int docID = contentDBConnection.getChapterByID(chapterID).getDocID();
        return redirect(controllers.routes.DocumentControllerSama.documentPage(docID, true));
    }

    /**
     * changes contentSubHeadline back to old version
     * sequence will remain the same
     * @param vCSubHeadlineID rollbackHeadline
     */
    public static void changeContentSubHeadline(int vCSubHeadlineID) {
        ContentDBConnection contentDBConnection = new ContentDBConnection();
        VersionControlDBConnection  versionControlDBConnection  = new VersionControlDBConnection();

        ContentSubHeadline      changedContentSubHeadline= versionControlDBConnection.getVersionSubHeadlineByID(vCSubHeadlineID);
        ContentSubHeadline      oldContentSubHeadline   = contentDBConnection.getContentSubHeadLineByID(changedContentSubHeadline.getcSubHeadLineID());
        changedContentSubHeadline.setSequence(oldContentSubHeadline.getSequence());
        VersionController.subHeadlineChanged(oldContentSubHeadline);
        List<ContentSubHeadline>contentSubHeadlines     = contentDBConnection.getContentSubHeadLinesToHeadline(changedContentSubHeadline.getCheadlineID());

        changeContentSubHeadline(changedContentSubHeadline, oldContentSubHeadline, contentSubHeadlines);
    }

    /**
     * changes contentSubHeadline back to old version
     * @param changedContentSubHeadline versionSubHeadline
     * @param oldContentSubHeadline subHeadline that was stored in db before
     * @param contentSubHeadlines list of all subHeadlines of chapter
     */
    private static void changeContentSubHeadline(ContentSubHeadline changedContentSubHeadline, ContentSubHeadline oldContentSubHeadline, List<ContentSubHeadline> contentSubHeadlines){
        ContentDBConnection contentDBConnection = new ContentDBConnection();
        //change headline
        if (!changedContentSubHeadline.getTitle().equals(oldContentSubHeadline.getTitle())) {
            contentDBConnection.changeContentSubHeadLineTitle(changedContentSubHeadline.getcSubHeadLineID(), changedContentSubHeadline.getTitle());
        }
        //change sequence
        if (changedContentSubHeadline.getSequence() !=  oldContentSubHeadline.getSequence()) {
            if (changedContentSubHeadline.getSequence() < oldContentSubHeadline.getSequence()) {
                for (int i = changedContentSubHeadline.getSequence(); i < oldContentSubHeadline.getSequence(); i++) {
                    contentDBConnection.changeContentSubHeadLineSequence(contentSubHeadlines.get(i - 1).getcSubHeadLineID(), contentSubHeadlines.get(i - 1).getSequence() + 1);
                }
                contentDBConnection.changeChapterSequence(oldContentSubHeadline.getCheadlineID(), changedContentSubHeadline.getSequence());
            } else if (changedContentSubHeadline.getSequence() > oldContentSubHeadline.getSequence()) {
                for (int i = oldContentSubHeadline.getSequence() + 1; i < changedContentSubHeadline.getSequence(); i++) {
                    contentDBConnection.changeContentSubHeadLineSequence(contentSubHeadlines.get(i - 1).getcSubHeadLineID(), contentSubHeadlines.get(i - 1).getSequence() - 1);
                }
                contentDBConnection.changeContentSubHeadLineSequence(oldContentSubHeadline.getcSubHeadLineID(), changedContentSubHeadline.getSequence() - 1);
            }
        }
    }

    /**
     * deletes contentSubHeadline
     * @param cSubHeadlineID ID of subHeadline to be deleted
     * @param cHeadlineID ID of parent
     * @return redirect
     */
    public static Result deleteContentSubHeadline(int cSubHeadlineID, int cHeadlineID) {
        ContentDBConnection contentDBConnection         = new ContentDBConnection();

        ContentSubHeadline  subHeadlineToBeDeleted      = contentDBConnection.getContentSubHeadLineByID(cSubHeadlineID);
        VersionController.subHeadlineDeleted(subHeadlineToBeDeleted);
        List<ContentSubHeadline>    contentSubHeadlines = contentDBConnection.getContentSubHeadLinesToHeadline(cHeadlineID);

        contentSubHeadlines.stream().filter(cSubHeadline -> cSubHeadline.getSequence() > subHeadlineToBeDeleted.getSequence()).forEach(cSubHeadline -> contentDBConnection.changeContentSubHeadLineSequence(cSubHeadline.getcSubHeadLineID(), cSubHeadline.getSequence() - 1));
        contentDBConnection.deleteContentSubHeadLine(cSubHeadlineID);
        //TODO
        int chapterID = contentDBConnection.getContentHeadLineByID(cHeadlineID).getChapterID();
        int docID = contentDBConnection.getChapterByID(chapterID).getDocID();
        return redirect(controllers.routes.DocumentControllerSama.documentPage(docID, true));
    }


    //Content related
    /**
     * creates content for parentChapter
     * @param chapterID ID of parent
     * @return redirect
     */
    @Security.Authenticated(SecurityModerator.class)
    public static Result addContentToChapter(int chapterID) {
        ContentDBConnection contentDBConnection = new ContentDBConnection();

        Content             newContent          = Form.form(Content.class).bindFromRequest().get();
        System.out.println(Form.form(Content.class).bindFromRequest());
        if(newContent.getType() == 1) { //If image Image
            addImageContent(newContent, chapterID, 0);
        } else {
            List<Content> contentList  = contentDBConnection.getContentToChapter(chapterID);
            contentList.stream().filter(content -> content.getSequence() >= newContent.getSequence()).forEach(content -> contentDBConnection.changeContentSequence(content.getContentID(), (content.getSequence() + 1)));
            contentDBConnection.createContent(chapterID, -1, -1, newContent.getContent(), newContent.getType(), newContent.getSequence());
        }

        //TODO
        int docID = contentDBConnection.getChapterByID(chapterID).getDocID();
        return redirect(controllers.routes.DocumentControllerSama.documentPage(docID, true));    }

    /**
     * creates content for parentHeadline
     * @param cHeadLineID ID of parent
     * @return redirect
     */
    @Security.Authenticated(SecurityModerator.class)
    public static Result addContentToContentHeadline(int cHeadLineID) {
        ContentDBConnection contentDBConnection = new ContentDBConnection();

        Content             newContent          = Form.form(Content.class).bindFromRequest().get();
        if(newContent.getType() == 1) { //If image Image
           addImageContent(newContent, cHeadLineID, 1);
        } else {
            List<Content> contentList  = contentDBConnection.getContentToContentHeadline(cHeadLineID);
            contentList.stream().filter(content -> content.getSequence() >= newContent.getSequence()).forEach(content -> contentDBConnection.changeContentSequence(content.getContentID(), (content.getSequence() + 1)));
            contentDBConnection.createContent(-1, cHeadLineID, -1, newContent.getContent(), newContent.getType(), newContent.getSequence());
        }
        //TODO
        int chapterID = contentDBConnection.getContentHeadLineByID(cHeadLineID).getChapterID();
        int docID = contentDBConnection.getChapterByID(chapterID).getDocID();
        return redirect(controllers.routes.DocumentControllerSama.documentPage(docID, true));
    }

    /**
     * creates content for parentSubHeadline
     * @param cSubHeadLineID ID of parent
     * @return redirect
     */
    @Security.Authenticated(SecurityModerator.class)
    public static Result addContentToContentSubHeadline(int cSubHeadLineID) {
        ContentDBConnection contentDBConnection = new ContentDBConnection();

        Content             newContent          = Form.form(Content.class).bindFromRequest().get();
        if(newContent.getType() == 1) { //If image Image
            addImageContent(newContent, cSubHeadLineID, 2);
        } else {
            List<Content> contentList  = contentDBConnection.getContentToContentSubHeadline(cSubHeadLineID);
            contentList.stream().filter(content -> content.getSequence() >= newContent.getSequence()).forEach(content -> contentDBConnection.changeContentSequence(content.getContentID(), (content.getSequence() + 1)));
            contentDBConnection.createContent(-1, -1, cSubHeadLineID, newContent.getContent(), newContent.getType(), newContent.getSequence());
        }
        //TODO
        int cHeadlineID = contentDBConnection.getContentSubHeadLineByID(cSubHeadLineID).getCheadlineID();
        int chapterID = contentDBConnection.getContentHeadLineByID(cHeadlineID).getChapterID();
        int docID = contentDBConnection.getChapterByID(chapterID).getDocID();
        return redirect(controllers.routes.DocumentControllerSama.documentPage(docID, true));
    }

    /**
     * adds versionContent that has been deleted to parent (resolves onto which parent)
     * @param newContent versionContent
     */
    public static void addContent(Content newContent){
        int chapterID   = newContent.getChapterID();
        int headlineID  = newContent.getcHeadLineID();
        if(chapterID != -1){
            addContentToChapter(newContent);
        }else if(headlineID != -1){
            addContentToHeadline(newContent);
        }else{
            addContentToSubHeadline(newContent);
        }
    }

    /**
     * adds versionContent that has been deleted to chapter)
     * @param newContent versionContent
     */
    private static void addContentToChapter(Content newContent){
        ContentDBConnection contentDBConnection = new ContentDBConnection();

        List<Content> contentList  = contentDBConnection.getContentToChapter(newContent.getChapterID());
        contentList.stream().filter(content -> content.getSequence() >= newContent.getSequence()).forEach(content -> contentDBConnection.changeContentSequence(content.getContentID(), (content.getSequence() + 1)));
        contentDBConnection.createContent(newContent.getContentID(), newContent.getChapterID(), -1, -1, newContent.getContent(), newContent.getType(), newContent.getSequence());
    }

    /**
     * adds versionContent that has been deleted to headline
     * @param newContent versionContent
     */
    private static void addContentToHeadline(Content newContent){
        ContentDBConnection contentDBConnection = new ContentDBConnection();

        List<Content> contentList  = contentDBConnection.getContentToChapter(newContent.getChapterID());
        contentList.stream().filter(content -> content.getSequence() >= newContent.getSequence()).forEach(content -> contentDBConnection.changeContentSequence(content.getContentID(), (content.getSequence() + 1)));
        contentDBConnection.createContent(newContent.getContentID(), -1, newContent.getcHeadLineID(), -1, newContent.getContent(), newContent.getType(), newContent.getSequence());
    }

    /**
     * adds versionContent that has been deleted to subHeadline
     * @param newContent versionContent
     */
    private static void addContentToSubHeadline(Content newContent){
        ContentDBConnection contentDBConnection = new ContentDBConnection();

        List<Content> contentList  = contentDBConnection.getContentToChapter(newContent.getChapterID());
        contentList.stream().filter(content -> content.getSequence() >= newContent.getSequence()).forEach(content -> contentDBConnection.changeContentSequence(content.getContentID(), (content.getSequence() + 1)));
        contentDBConnection.createContent(newContent.getContentID(), -1, -1, newContent.getcSubHeadLineID(), newContent.getContent(), newContent.getType(), newContent.getSequence());
    }

    /**
     *
     * @param contentID ID of content
     * @param aID ID of parent
     * @param type type of parent 0:= chapter, 1:= contentHeadline 2:= contentSubHeadline
     * @return redirect
     */
    @Security.Authenticated(SecurityModerator.class)
    public static Result changeContentPage(int contentID, int aID, int type) {
	    ContentDBConnection contentDBConnection = new ContentDBConnection();
        Content editContent = contentDBConnection.getContentByID(contentID);
        int docID = contentDBConnection.getDocIdToContent(contentID);
        String email = session().get("email");
        if(TokenController.checkToken(4, contentID, email)) {
            List<Content> contentList = new LinkedList<>();
            switch (type) {
                case 0: //Chapter
                    contentList = contentDBConnection.getContentToChapter(aID);
                    break;
                case 1: //Headline
                    contentList = contentDBConnection.getContentToContentHeadline(aID);
                    break;
                case 2: //SubHeadline
                    contentList = contentDBConnection.getContentToContentSubHeadline(aID);
                    break;
            }
            int role = Integer.parseInt(session().get("role"));
            return ok(changeContentPageNEU.render(editContent, contentList, type, aID, docID, email, role));
        }
        flash("danger", "Content is blocked. Someone else is editing the content. Please try again later.");
        return redirect(routes.DocumentControllerSama.documentPage(docID, true));
    }

    /**
     * changes content
     * @param contentID ID of content stc
     * @param aID ID of parent
     * @param type type of parent 0:= chapter, 1:= contentHeadline 2:= contentSubHeadline
     * @return redirect
     */
    @Security.Authenticated(SecurityModerator.class)
    public static Result changeContent(int contentID, int aID, int type) {
        ContentDBConnection contentDBConnection = new ContentDBConnection();

        Content      oldContent   = contentDBConnection.getContentByID(contentID);


        List<Content>contents     = new LinkedList<>();
        switch (type) {
            case 0:
                contents     = contentDBConnection.getContentToChapter(aID);
                break;
            case 1:
                contents     = contentDBConnection.getContentToContentHeadline(aID);
                break;
            case 2:
                contents     = contentDBConnection.getContentToContentSubHeadline(aID);
        }


        Content      changedContent= Form.form(Content.class).bindFromRequest().get();
        changedContent.setContentID(contentID);
        changedContent.setDocumentID(oldContent.getDocumentID());
        if(changedContent.getContent() != null) {
            if (!changedContent.getContent().equals(oldContent.getContent()))
                VersionController.contentChanged(oldContent);
        }else{//semi fix: does now save a version even if only sequence is changed when content is a Image
            VersionController.contentChanged(oldContent);
        }
        switch (type) {
            case 0:
                changedContent.setChapterID(aID);
                break;
            case 1:
                changedContent.setcHeadLineID(aID);
                break;
            case 2:
                changedContent.setcSubHeadLineID(aID);
        }

        if(changedContent.getType() == 1) {
            changeImageContent(changedContent, oldContent, aID, type);
        } else {
            changeContent(changedContent, oldContent, contents);
        }

        int docID = contentDBConnection.getDocIdToContent(contentID);
        return redirect(controllers.routes.DocumentControllerSama.documentPage(docID, true));
    }

    /**
     * changes content back through version
     * sequence will remain the same
     * @param vContentID VersionContentID
     */
    public static void changeContent(int vContentID){
        ContentDBConnection contentDBConnection = new ContentDBConnection();
        VersionControlDBConnection  versionControlDBConnection  = new VersionControlDBConnection();

        Content         changedContent  = versionControlDBConnection.getVersionContentByID(vContentID);
        Content         oldContent      = contentDBConnection.getContentByID(changedContent.getContentID());
        changedContent.setSequence(oldContent.getSequence());
        VersionController.contentChanged(oldContent);
        List<Content>   contents        = contentDBConnection.getContentToChapter(changedContent.getChapterID());

        changeContent(changedContent, oldContent, contents);
    }

    /**
     * changes content
     * @param changedContent versionContent
     * @param oldContent content that was stored in db before
     * @param contents list of all content of parent
     */
    private static void changeContent(Content changedContent, Content oldContent, List<Content> contents){
        ContentDBConnection contentDBConnection = new ContentDBConnection();
        if (changedContent.getSequence() !=  oldContent.getSequence()) {
            if (changedContent.getSequence() < oldContent.getSequence()) {
                for (int i = changedContent.getSequence(); i < oldContent.getSequence(); i++) {
                    contentDBConnection.changeContentSequence(contents.get(i - 1).getContentID(), contents.get(i - 1).getSequence() + 1);
                }
                contentDBConnection.changeContentSequence(oldContent.getContentID(), changedContent.getSequence());
            } else if (changedContent.getSequence() > oldContent.getSequence()) {
                for (int i = oldContent.getSequence() + 1; i < changedContent.getSequence(); i++) {
                    contentDBConnection.changeContentSequence(contents.get(i - 1).getContentID(), contents.get(i - 1).getSequence() - 1);
                }
                contentDBConnection.changeContentSequence(oldContent.getContentID(), changedContent.getSequence() - 1);
            }
        }
        contentDBConnection.changeContent(changedContent.getContentID(), changedContent.getContent(), changedContent.getType());
    }

    /**
     * creates new ImageContent or changes current Content to ImageContent
     * @param newContent content to be added
     * @param aID ID of parent
     * @param idType 0:= chapter, 1:= headline, 2:= subHeadline
     */
    private static void addImageContent(Content newContent, int aID, int idType) {
        ContentDBConnection contentDBConnection = new ContentDBConnection();
        //Get File
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart picture = body.getFile("picture");

        //Handle Picture
        if (picture != null) {
            File file = picture.getFile();
            String extension = picture.getFilename().substring(picture.getFilename().lastIndexOf(".") + 1);
            String newFileName = generateFileName(file)+"."+extension;
            System.out.println(newFileName);

            File newFile = new File(play.Play.application().path().toString() + "//public//uploads//"+ newFileName);
            InputStream inStream;
            OutputStream outStream;

            try{

                inStream = new FileInputStream(file);
                outStream = new FileOutputStream(newFile);

                byte[] buffer = new byte[1024];

                int length;
                //copy the file content in bytes
                while ((length = inStream.read(buffer)) > 0){

                    outStream.write(buffer, 0, length);

                }

                inStream.close();
                outStream.close();

                //delete the original file
                file.delete();

                System.out.println("File is copied successful!");

            }catch(IOException e){
                e.printStackTrace();
            }
            newContent.setContent("<img src=\"" + "/public/uploads/" + newFileName + "\" class=\"img-responsive insertAutomatically\" alt=\"Responsive image\">");
            switch (idType) {
                case 0:
                    List<Content> contentList  = contentDBConnection.getContentToChapter(aID);
                    contentList.stream().filter(content -> content.getSequence() >= newContent.getSequence()).forEach(content -> contentDBConnection.changeContentSequence(content.getContentID(), (content.getSequence() + 1)));
                    contentDBConnection.createContent(aID, -1, -1, newContent.getContent(), newContent.getType(), newContent.getSequence());
                    break;
                case 1:
                    List<Content> contentsList  = contentDBConnection.getContentToContentHeadline(aID);
                    contentsList.stream().filter(content -> content.getSequence() >= newContent.getSequence()).forEach(content -> contentDBConnection.changeContentSequence(content.getContentID(), (content.getSequence() + 1)));
                    contentDBConnection.createContent(-1, aID, -1, newContent.getContent(), newContent.getType(), newContent.getSequence());
                    break;
                case 2:
                    List<Content> contentesList  = contentDBConnection.getContentToContentSubHeadline(aID);
                    contentesList.stream().filter(content -> content.getSequence() >= newContent.getSequence()).forEach(content -> contentDBConnection.changeContentSequence(content.getContentID(), (content.getSequence() + 1)));
                    contentDBConnection.createContent(-1, -1, aID, newContent.getContent(), newContent.getType(), newContent.getSequence());
                    break;
            }
        }
    }

    /**
     * changes ImageContent
     * @param changedContent changedContent
     * @param oldContent oldContent before changes occured
     * @param aID ID of parent
     * @param idType type of parent 0:= chapter, 1:= headline, 2:= subHeadline
     */
    private static void changeImageContent(Content changedContent, Content oldContent, int aID, int idType) {
        ContentDBConnection contentDBConnection = new ContentDBConnection();
        List<Content>contents     = new LinkedList<>();
        //Get File
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart picture = body.getFile("picture");

        //Handle Picture
        if (picture != null) {
            File file = picture.getFile();
            String extension = picture.getFilename().substring(picture.getFilename().lastIndexOf(".") + 1);
            String newFileName = generateFileName(file) + "." + extension;
            System.out.println(newFileName);

            File newFile = new File(play.Play.application().path().toString() + "//public//uploads//" + newFileName);
            InputStream inStream;
            OutputStream outStream;

            try{

                inStream = new FileInputStream(file);
                outStream = new FileOutputStream(newFile);

                byte[] buffer = new byte[1024];

                int length;
                //copy the file content in bytes
                while ((length = inStream.read(buffer)) > 0){

                    outStream.write(buffer, 0, length);

                }

                inStream.close();
                outStream.close();

                //delete the original file
                file.delete();

                System.out.println("File is copied successful!");

            }catch(IOException e){
                e.printStackTrace();
            }

            changedContent.setContent("<img src=\"" + "/public/uploads/" + newFileName + "\" class=\"img-responsive insertAutomatically\" alt=\"Responsive image\">");
        }else{
            //keep the old image if no new image was uploaded
            changedContent.setContent(oldContent.getContent());
        }

        switch (idType) {
            case 0:
                contents    = contentDBConnection.getContentToChapter(aID);
                break;
            case 1:
                contents    = contentDBConnection.getContentToContentHeadline(aID);
                break;
            case 2:
                contents    = contentDBConnection.getContentToContentSubHeadline(aID);
                break;
        }

        if (changedContent.getSequence() !=  oldContent.getSequence()) {
            if (changedContent.getSequence() < oldContent.getSequence()) {
                for (int i = changedContent.getSequence(); i < oldContent.getSequence(); i++) {
                    contentDBConnection.changeContentSequence(contents.get(i - 1).getContentID(), contents.get(i - 1).getSequence() + 1);
                }
                contentDBConnection.changeContentSequence(oldContent.getContentID(), changedContent.getSequence());
            } else if (changedContent.getSequence() > oldContent.getSequence()) {
                for (int i = oldContent.getSequence() + 1; i < changedContent.getSequence(); i++) {
                    contentDBConnection.changeContentSequence(contents.get(i - 1).getContentID(), contents.get(i - 1).getSequence() - 1);
                }
                contentDBConnection.changeContentSequence(oldContent.getContentID(), changedContent.getSequence() - 1);
            }
        }
        contentDBConnection.changeContent(changedContent.getContentID(), changedContent.getContent(), changedContent.getType());

    }

    /**
     * Generates a unique filename for a file
     *
     * @param file The file to generate a filename.
     * @return filename
     */
    private static String generateFileName(File file) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5"); // SHA or MD5
            String hash = "";

            byte[] data = new byte[(int)file.length()];
            FileInputStream fis = new FileInputStream(file);
            //noinspection ResultOfMethodCallIgnored
            fis.read(data);
            fis.close();

            md.update(data);

            byte[] digest = md.digest();

            for (byte aDigest : digest) {
                String hex = Integer.toHexString(aDigest);
                if (hex.length() == 1)
                    hex = "0" + hex;
                hex = hex.substring(hex.length() - 2);
                hash += hex;
            }

            LocalDateTime time = LocalDateTime.now();
            String timeStamp = time.toString().replace(':', '_').replace('.', '_');

            return hash.substring(0,5)+timeStamp;

        } catch (NoSuchAlgorithmException | IOException e){
            e.printStackTrace();
        }

        return LocalDateTime.now().toString().replace(':', '_').replace('.', '_');
    }

    /**
     * deletes content
     * @param contentID ID of content to be deleted
     * @param aID ID of parent
     * @param type parent type 0:= chapter, 1:= headline, 2:= subHeadline
     * @return redirect
     */
    @Security.Authenticated(SecurityModerator.class)
    public static Result deleteContent(int contentID, int aID, int type) {
        ContentDBConnection contentDBConnection = new ContentDBConnection();
        List<Content>       contents            = new LinkedList<>();

        int docID = contentDBConnection.getDocIdToContent(contentID);
        String email = session().get("email");

        if(TokenController.checkToken(4, contentID, email)) {
            Content contentToDeleted = contentDBConnection.getContentByID(contentID);
            VersionController.contentDeleted(contentToDeleted);
            switch (type) {
                case 0:
                    contents = contentDBConnection.getContentToChapter(aID);
                    break;
                case 1:
                    contents = contentDBConnection.getContentToContentHeadline(aID);
                    break;
                case 2:
                    contents = contentDBConnection.getContentToContentSubHeadline(aID);
                    break;
            }

            //change sequences of old content
            contents.stream().filter(content -> content.getSequence() > contentToDeleted.getSequence()).forEach(content -> contentDBConnection.changeContentSequence(content.getContentID(), content.getSequence() - 1));

            contentDBConnection.deleteContent(contentID);
            return redirect(controllers.routes.DocumentControllerSama.documentPage(docID, true));

        } else {
            flash("danger", "Content is blocked. Someone else is editing the content. Please try again later.");
            return redirect(routes.DocumentControllerSama.documentPage(docID, true));
        }
    }

    /**
     * adds reference to content
     * @param contentID ID of content on which reference will be added
     * @param docID ID of doc
     * @return redirect
     */
    @Security.Authenticated(SecurityModerator.class)
    public static Result addReference(int contentID, int docID, int referencedContentID, int referencedDocID){
        ReferenceDBConnection referenceDBConnection = new ReferenceDBConnection();
        String  referenceName   = request().body().asFormUrlEncoded().get("referenceName")[0];
        referenceDBConnection.addReference(contentID, referencedContentID, referenceName, referencedDocID);

        return redirect(controllers.routes.DocumentControllerSama.documentPage(docID, true));
    }

    /**
     * deletes reference
     * @param referenceID ID of reference to be deleted
     * @param docID ID of document
     * @return redirect
     */
    @Security.Authenticated(SecurityModerator.class)
    public static Result deleteReference(int referenceID, int docID){
        ReferenceDBConnection referenceDBConnection = new ReferenceDBConnection();
        referenceDBConnection.deleteReference(referenceID);

        return redirect(controllers.routes.DocumentControllerSama.documentPage(docID, true));
    }

    /**
     * displays the page on which a reference to any content can be added
     * @param contentID ID of content to which reference is added
     * @param docID ID of document to which the redirect will go
     * @return page
     */
    @Security.Authenticated(SecurityModerator.class)
    public static Result addReferencePage(int contentID, int docID){
        ContentDBConnection     contentDBConnection = new ContentDBConnection();
        DocumentDBConnection    documentDBConnection= new DocumentDBConnection();
        List<Document>          documents           = documentDBConnection.getDocuments();
        HashMap<Integer, List<ChapterChan>>         chapters                = new HashMap<>();
        HashMap<Integer, List<ContentHeadline>>     headlines               = new HashMap<>();
        HashMap<Integer, List<ContentSubHeadline>>  subHeadlines            = new HashMap<>();
        HashMap<Integer, List<Content>>             contentToChapter        = new HashMap<>();
        HashMap<Integer, List<Content>>             contentToHeadline       = new HashMap<>();
        HashMap<Integer, List<Content>>             contentToSubHeadline    = new HashMap<>();
        //creating HashMap for chapter, headlines, subHeadlines of all documents and content of all of them
        for(Document document: documents) {
            int                 docsID       = document.getDocumentID();
            List<ChapterChan>   tmpChapters = contentDBConnection.getChaptersToDoc(docsID);
            chapters.put(docsID, tmpChapters);
            for (ChapterChan chan : tmpChapters) {
                List<Content> chapterContents = contentDBConnection.getContentToChapter(chan.getChapterID()); //getting all content for chapter
                contentToChapter.put(chan.getChapterID(), chapterContents); //adding contentList to hashmap with chapterID as key

                List<ContentHeadline> tmpHeadlines = contentDBConnection.getContentHeadLinesToChapter(chan.getChapterID()); //getting all headlines to chapter
                headlines.put(chan.getChapterID(), tmpHeadlines); //adding headlineList to hashmap with chapterID as key

                for (ContentHeadline ch : tmpHeadlines) {
                    List<Content> headlineContents = contentDBConnection.getContentToContentHeadline(ch.getcHeadLineID()); //getting all content to headline
                    contentToHeadline.put(ch.getcHeadLineID(), headlineContents); //adding contentList to hashmap with headlineID as key

                    List<ContentSubHeadline> tmpSubHeadlines = contentDBConnection.getContentSubHeadLinesToHeadline(ch.getcHeadLineID()); //getting all subHeadlines to headline
                    subHeadlines.put(ch.getcHeadLineID(), tmpSubHeadlines); // adding subHeadlineList to hashmap with headlineID as key

                    for (ContentSubHeadline chs : tmpSubHeadlines) {
                        List<Content> subHeadlineContents = contentDBConnection.getContentToContentSubHeadline(chs.getcSubHeadLineID()); //getting als content to subheadline
                        contentToSubHeadline.put(chs.getcSubHeadLineID(), subHeadlineContents); //adding contentList to hashmap with subHeadlineID as key
                    }
                }
            }
        }
        String email    = session().get("email");
        int role        = Integer.parseInt(session().get("role"));
        return ok(addReferencePage.render(docID, contentID, documents, chapters, headlines, subHeadlines, contentToChapter, contentToHeadline, contentToSubHeadline, email, role));
    }

}
