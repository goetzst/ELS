package controllers;

import controllers.DBConnection.*;
import controllers.Security.SecurityDozent;
import controllers.Security.SecurityModerator;
import controllers.Security.SecurityStudent;
import models.*;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.documentPageNEW;
import views.html.newDocumentPage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Stefan, Verena
 * Class for controling document related stuff
 *
 */
public class DocumentControllerSama extends Controller{
    /**
     * display page for a document with the corresponding id
     * @param docID ID of the document that will be displayed
     * @param edit true if Document in editMode false otherwise
     * @return Result which contains the rendered html version of the document
     */
    @Security.Authenticated(SecurityModerator.class)
    public static Result documentPage(int docID, boolean edit) {
        ContentDBConnection     contentDBConnection = new ContentDBConnection();
        TagDBConnection         tagDBConnection     = new TagDBConnection();
        DocumentDBConnection    documentDBConnection= new DocumentDBConnection();
        CommentDBConnection     cdb                 = new CommentDBConnection();
        ReferenceDBConnection   refDBCon            = new ReferenceDBConnection();
        ModeratorDBConnection   moderatorDBConnection = new ModeratorDBConnection();
        Document                document            = documentDBConnection.getDocumentByID(docID);
        if(document != null) {
            if(document.isVisible() || edit) {
                List<Tag>					tags			= tagDBConnection.getTagsOfUser(session().get("email"));
                List<ChapterChan>				chapters		= contentDBConnection.getChaptersToDoc(docID);
                HashMap<Integer, List<ContentHeadline>>		headlines		= new HashMap<>();
                HashMap<Integer, List<ContentSubHeadline>>	subHeadlines		= new HashMap<>();
                HashMap<Integer, List<Content>> 		contentToChapters	= new HashMap<>();
                HashMap<Integer, List<Content>> 		contentToHeadlines 	= new HashMap<>();
                HashMap<Integer, List<Content>> 		contentToSubHeadlines 	= new HashMap<>();
                HashMap<Integer, List<Tag>> 			headlineTags 		= new HashMap<>();
                HashMap<Integer, List<Tag>> 			subHeadlineTags 	= new HashMap<>();
                HashMap<Integer, List<Tag>> 			chapterTags 		= new HashMap<>();
                HashMap<Integer, List<Tag>> 			commentTags 		= new HashMap<>();
                HashMap<Integer, List<Tag>> 			subCommentTags 		= new HashMap<>();
                HashMap<Integer, List<Comment>> 		comments 		= new HashMap<>();
                HashMap<Integer, List<SubComment>> 		subComments 		= new HashMap<>();
                HashMap<Integer, List<Reference>> 		references 		= new HashMap<>();

                //creating HashMap for headlines, subHeadlines of all chapters and content of all of them
                for (ChapterChan chan : chapters) {
                    List<Content> chapterContents = contentDBConnection.getContentToChapter(chan.getChapterID()); //getting all content for chapter
                    contentToChapters.put(chan.getChapterID(), chapterContents); //adding contentList to hashmap with chapterID as key
                    for (Content content : chapterContents) {
                        references.put(content.getContentID(), refDBCon.getReferencesToContent(content.getContentID()));
                    }

                    List<ContentHeadline> tmpHeadlines = contentDBConnection.getContentHeadLinesToChapter(chan.getChapterID()); //getting all headlines to chapter
                    headlines.put(chan.getChapterID(), tmpHeadlines); //adding headlineList to hashmap with chapterID as key

                    for (ContentHeadline ch : tmpHeadlines) {
                        List<Content> headlineContents = contentDBConnection.getContentToContentHeadline(ch.getcHeadLineID()); //getting all content to headline
                        contentToHeadlines.put(ch.getcHeadLineID(), headlineContents); //adding contentList to hashmap with headlineID as key
                        for (Content content : headlineContents) {
                            references.put(content.getContentID(), refDBCon.getReferencesToContent(content.getContentID()));
                        }

                        List<ContentSubHeadline> tmpSubHeadlines = contentDBConnection.getContentSubHeadLinesToHeadline(ch.getcHeadLineID()); //getting all subHeadlines to headline
                        subHeadlines.put(ch.getcHeadLineID(), tmpSubHeadlines); // adding subHeadlineList to hashmap with headlineID as key

                        for (ContentSubHeadline chs : tmpSubHeadlines) {
                            List<Content> subHeadlineContents = contentDBConnection.getContentToContentSubHeadline(chs.getcSubHeadLineID()); //getting als content to subheadline
                            contentToSubHeadlines.put(chs.getcSubHeadLineID(), subHeadlineContents); //adding contentList to hashmap with subHeadlineID as key
                            for (Content content : subHeadlineContents) {
                                references.put(content.getContentID(), refDBCon.getReferencesToContent(content.getContentID()));
                            }
                        }
                    }
                }
                //filling HashMap for all comments and subComments
                fillCommentAndSubCommentHM(cdb, comments, subComments, contentToChapters);
                fillCommentAndSubCommentHM(cdb, comments, subComments, contentToHeadlines);
                fillCommentAndSubCommentHM(cdb, comments, subComments, contentToSubHeadlines);


                //creating hashmaps for tagged chapters and comments
                for (ChapterChan chapter : chapters) {
                    chapterTags.put(chapter.getChapterID(), tagDBConnection.getTagsToChapterChan(chapter.getChapterID(), session().get("email")));

                    for (ContentHeadline headline : contentDBConnection.getContentHeadLinesToChapter(chapter.getChapterID())) {
                        headlineTags.put(headline.getcHeadLineID(), tagDBConnection.getTagsToContentHeadline(headline.getcHeadLineID(), session().get("email")));

                        for (ContentSubHeadline subHeadline : contentDBConnection.getContentSubHeadLinesToHeadline(headline.getcHeadLineID())) {
                            subHeadlineTags.put(subHeadline.getcSubHeadLineID(), tagDBConnection.getTagsToContentSubHeadline(subHeadline.getcSubHeadLineID(), session().get("email")));
                        }
                    }
                }

                //adding all tags of comments and subComments
                Set<Integer> commentKeys = comments.keySet();
                Set<Integer> subCommentKeys = subComments.keySet();

                for (Integer i : commentKeys) {
                    for (Comment c : comments.get(i)) {
                        commentTags.put(c.getCommentID(), tagDBConnection.getTagsToComment(c.getCommentID(), session().get("email")));
                    }
                }
                for (Integer i : subCommentKeys) {
                    for (SubComment sc : subComments.get(i)) {
                        subCommentTags.put(sc.getSubCommentID(), tagDBConnection.getTagsToSubComment(sc.getSubCommentID(), session().get("email")));
                    }
                }

                List<User> moderators = moderatorDBConnection.getModeratorsOfDocument(docID);

                String email = session().get("email");
                int role = Integer.parseInt(session().get("role"));


                return ok(documentPageNEW.render(document,
                        chapters, headlines, subHeadlines,
                        contentToChapters, contentToHeadlines, contentToSubHeadlines,
                        references,
                        comments, subComments,
                        moderators, //list of moderators
                        tags,  //List of tags from the user who is displaying the page
                        chapterTags, headlineTags, subHeadlineTags,
                        commentTags, subCommentTags,
                        email, role,
                        edit //true = edit Doument
                ));
            }else {
                return notFound("the Document is not yet available for public use \n Try to use the edit mode or contact an admin if this message should not be displayed");
            }


        } else {
            return  notFound("Document not found.");
        }
    }

    /**
     * fills hashMaps for comments and subComments with all comments and subComments related to content in given hashMap
     * @param cdb commentDBConnection
     * @param comments commentsHM to fill
     * @param subComments subCommentsHM to fill
     * @param contentHM related contentHM
     */
    private static void fillCommentAndSubCommentHM(CommentDBConnection cdb, HashMap<Integer, List<Comment>> comments, HashMap<Integer, List<SubComment>> subComments, HashMap<Integer, List<Content>> contentHM){
        Set<Integer>    keys    = contentHM.keySet();
        //for content of chapters
        for(Integer i: keys){
            for(Content c: contentHM.get(i)){
                List<Comment> tmp   = cdb.getCommentsOfContent(c.getContentID());
                comments.put(c.getContentID(), tmp);
                for(Comment co: tmp){
                    subComments.put(co.getCommentID(), cdb.getSubCommentsOfComment(co.getCommentID()));
                }
            }
        }
    }

    /**
     * display page for the creation of a new document
     * @return Result which contains the html to create a new page
     */
    @Security.Authenticated(SecurityDozent.class)
    public static Result newDocumentPage() {
        FacultyDBConnection fdb = new FacultyDBConnection();
        SubjectDBConnection sdb = new SubjectDBConnection();

        List<Faculty> facs = fdb.getFaculties();
        HashMap<String, List<Subject>> subs = new HashMap<>();
        for (Faculty fac : facs) {
            subs.put(fac.getFacultyName(), sdb.getSubjectsByFacultyName(fac.getFacultyName()));
        }

        String email    = session().get("email");
        int role        = Integer.parseInt(session().get("role"));

        return ok(newDocumentPage.render(subs, facs, email, role));
    }

    /**
     * Method for the creation of a document, specified in a previous newDocumentPage()
     * @return Redirect to the current users main page
     */
    @Security.Authenticated(SecurityDozent.class)
    public static Result createDocument() {
        Document newDoc = Form.form(Document.class).bindFromRequest().get();
        DocumentDBConnection ddb = new DocumentDBConnection();
        ddb.createDocument(newDoc.getSubjectID(), session().get("email"), newDoc.getDocumentName());

        return redirect(controllers.routes.MainPageController.mainPage());
    }

    /**
     * Method to change the Name of a document
     * @param documentId ID of the document of which the name will be changed
     * @return Redirect to the changeDocumentPage
     */
    @Security.Authenticated(SecurityDozent.class)
    public static Result changeDocumentName(int documentId){
        DocumentDBConnection    documentDBConnection    = new DocumentDBConnection();
        DynamicForm tempData    = Form.form().bindFromRequest();
        String newName          = tempData.get("name");

        VersionController.documentChanged(documentDBConnection.getDocumentByID(documentId));

        DocumentDBConnection docCon     = new DocumentDBConnection();
        docCon.changeDocumentName(documentId, newName);

        return redirect(controllers.routes.DocumentControllerSama.documentPage(documentId, true));
    }

    /**
     * Method to bookmark a document
     * @param id ID of the document which will be bookmarked
     * @return Redirect to the documentListPage
     */
    @Security.Authenticated(SecurityStudent.class)
    public static Result bookmarkDocument(int id) {
        DocumentDBConnection ddbc = new DocumentDBConnection();
        ddbc.createBookmark(session().get("email"), id);
        return redirect(controllers.routes.MainPageController.documentListPage());
    }

    /**
     * Method to delete a bookmark
     * @param documentId ID of the document of the bookmark to be deleted
     * @return Redirect to the documentListPage
     */
    @Security.Authenticated(SecurityStudent.class)
    public static Result deleteBookmark(int documentId) {
        DocumentDBConnection ddbc = new DocumentDBConnection();
        ddbc.deleteBookmark(session().get("email"), documentId);
        return redirect(controllers.routes.MainPageController.documentListPage());
    }

    /**
     * Method to delete a Document
     * @param id ID of the document which will be deleted
     * @return Redirect to the current users mainPage
     */
    @Security.Authenticated(SecurityDozent.class)
    public static Result deleteDocument(int id) {
        DocumentDBConnection con = new DocumentDBConnection();
        con.deleteDocument(id);
        return redirect(controllers.routes.MainPageController.mainPage());
    }

    /**
     * toggles visibility of document and redirects to editPage
     * @param id ID of document
     * @return redirect
     */
    @Security.Authenticated(SecurityDozent.class)
    public static Result changeVisibility(int id){
        DocumentDBConnection    con = new DocumentDBConnection();
        con.toggleVisibility(id);
        return redirect(routes.DocumentControllerSama.documentPage(id, true));
    }
}
