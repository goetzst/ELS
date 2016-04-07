package controllers;

import controllers.DBConnection.CommentDBConnection;
import controllers.DBConnection.ContentDBConnection;
import controllers.DBConnection.TagDBConnection;
import controllers.Security.SecurityStudent;
import controllers.routes;
import models.*;
import play.mvc.*;
import play.data.Form;
import views.html.*;
import java.util.LinkedList;
import java.util.List;
/**
 * @author Stefan
 * Class for Tagging and Tag related access
 */
public class TagController extends Controller{

    /**
     * display page of all Tags of user
     * @return page
     */
    @Security.Authenticated(SecurityStudent.class)
    public static Result tagsPage() {
        TagDBConnection tagDBConnection = new TagDBConnection();
        List<Tag>       tagList         = tagDBConnection.getTagsOfUser(session().get("email"));
        return ok(tagsPage.render(session().get("email"), Integer.parseInt(session().get("role")), tagList));
    }

    /**
     * changes name of specific tag
     * @param tagID id of tag which name will be changed
     * @return redirect to tagPage(tagID)
     */
    @Security.Authenticated(SecurityStudent.class)
    public static Result changeTagName(int tagID) {
        TagDBConnection tagDBConnection = new TagDBConnection();
        String          newName         = Form.form(Tag.class).bindFromRequest().get().getTagName();
        tagDBConnection.changeTagName(tagID, newName);
        return redirect(routes.TagController.tagsPage());
    }

    /**
     *
     * @param id of element to Tag
     * @param kind of element 0 := chapter, 1 := comment, 2 := subComment
     * @param docID ID of document on which tag is added for redirecting purpose
     * @return addTagPage
     */
    //TODO Check -> stefan
    @Security.Authenticated(SecurityStudent.class)
    public static Result addTagPage(int id, int kind, int docID) {
        TagDBConnection tagDBConnection = new TagDBConnection();
        List<Tag>       tagList         = tagDBConnection.getTagsOfUser(session().get("email"));
        List<Tag>       usedTagList     = new LinkedList<>();
        if(kind == 0) {
            usedTagList                 = tagDBConnection.getTagsToChapterChan(id, session().get("email"));
        }else if(kind == 1) {
            usedTagList                 = tagDBConnection.getTagsToComment(id, session().get("email"));
        }else if(kind == 2) {
            usedTagList                 = tagDBConnection.getTagsToSubComment(id, session().get("email"));
        }
        return ok(addTagPage.render(tagList, id, kind, docID, usedTagList));
    }

    /**
     * adds tag to element
     * @param kind of element 0 := chapter, 1 := comment, 2 := subComment 3:= headline 4:= subHeadline
     * @param ID of element
     * @param tagID id of tag which is added
     * @param docID ID for redirect
     * @return redirect to document Page
     */
    @Security.Authenticated(SecurityStudent.class)
    public static Result addTag(int kind, int ID, int tagID, int docID) {
        if(kind == 0){
            addChapterChanTag(tagID, ID);
        }else if(kind == 1) {
            addCommentTag(tagID, ID);
        }else if(kind ==2) {
            addSubCommentTag(tagID, ID);
        }else if(kind == 3) {
            addContentHeadlineTag(tagID, ID);
        }else if(kind ==4) {
            addContentSubHeadlineTag(tagID, ID);
        }
        return redirect(routes.DocumentControllerSama.documentPage(docID, false));
    }

    /**
     * creates a tag and adds it to something.
     * @param kind of class on which tag will be added 0 := chapter, 1 := comment, 2 := subComment 3:= headline 4:= subHeadline
     * @param ID ID of element
     * @param docID ID of document
     * @return call addTag
     */
    @Security.Authenticated(SecurityStudent.class)
    public static Result createAndAddTag(int kind, int ID, int docID) {
        TagDBConnection tagDBConnection = new TagDBConnection();
        String          newTagName      = Form.form(Tag.class).bindFromRequest().get().getTagName();
        boolean         global          = Form.form(Tag.class).bindFromRequest().get().isGlobal();
        int             newTagID        = tagDBConnection.createTag(newTagName, session().get("email"), global, docID);
        return addTag(kind, ID, newTagID, docID);
    }

    /**
     * adds tag to chapterChan
     * @param tagID tag to be added
     * @param id chapterID
     */
    private static void addChapterChanTag(int tagID, int id) {
        TagDBConnection         tagDBConnection = new TagDBConnection();
        tagDBConnection.addChapterChanTag(tagID, id);
        System.out.println("added chapterChanTag");
    }

    /**
     * adds tag to comment
     * @param tagID tag to be added
     * @param id commentID
     */
    private static void addCommentTag(int tagID, int id) {
        TagDBConnection         tagDBConnection     = new TagDBConnection();
        tagDBConnection.addCommentTag(tagID, id);
        System.out.println("added commentTag");
    }

    /**
     * adds tag to subComment
     * @param tagID tag to be added
     * @param id subCommentID
     */
    private static void addSubCommentTag(int tagID, int id) {
        TagDBConnection         tagDBConnection     = new TagDBConnection();
        tagDBConnection.addSubCommentTag(tagID, id);
        System.out.println("added subCommentTag");
    }

    /**
     * adds tag to headline
     * @param tagID ID of tag
     * @param id ID of headline
     */
    private static void addContentHeadlineTag(int tagID, int id) {
        TagDBConnection         tagDBConnection = new TagDBConnection();
        tagDBConnection.addContentHeadlineTag(tagID, id);
        System.out.println("added headlineTag");
    }

    /**
     * adds tag to subHeadline
     * @param tagID ID of tag
     * @param id ID of subHeadline
     */
    private static void addContentSubHeadlineTag(int tagID, int id) {
        TagDBConnection         tagDBConnection = new TagDBConnection();
        tagDBConnection.addContentSubHeadlineTag(tagID, id);
        System.out.println("added subHeadlineTag");
    }

    /**
     * deletes tag from chapterChan
     * @param tagID tagID of tag to be deleted from chapter
     * @param chapterID ID of chapter
     * @param redirectID redirectID to 0 := tagPage, 1 := documentPage()
     * @return redirect to redirectID
     */
    public static Result deleteChapterChanTag(int tagID, int chapterID, int redirectID) {
        TagDBConnection tagDBConnection = new TagDBConnection();
        tagDBConnection.deleteChapterChanTag(tagID, chapterID);

        ContentDBConnection contentDBConnection = new ContentDBConnection();
        int docId = contentDBConnection.getChapterByID(chapterID).getDocumentID();
        System.out.println(docId);

        if (redirectID == 0){
            return redirect(routes.TagController.tagsPage());
        } else if (redirectID == 1) {
            return redirect(routes.DocumentControllerSama.documentPage(docId, false));
        } else {
            return notFound("Something went wrong. Illegal Request");
        }
    }

    /**
     * deletes tag from comment
     * @param tagID ID of tag to be deleted
     * @param commentID ID of comment
     * @param redirectID redirectID to 0 := tagPage, 1 := documentPage()
     * @return redirect to tagPage
     */
    @Security.Authenticated(SecurityStudent.class)
    public static Result deleteCommentTag(int tagID, int commentID, int redirectID) {
        TagDBConnection tagDBConnection = new TagDBConnection();
        tagDBConnection.deleteCommentTag(tagID, commentID);

        CommentDBConnection commentDBConnection = new CommentDBConnection();
        ContentDBConnection contentDBConnection = new ContentDBConnection();
        Comment tempComment = commentDBConnection.getCommentByID(commentID);
        int docId = contentDBConnection.getDocIdToContent(tempComment.getContentID());

        if (redirectID == 0){
            return redirect(routes.TagController.tagsPage());
        } else if (redirectID == 1) {
            return redirect(routes.DocumentControllerSama.documentPage(docId, false));
        } else {
            return notFound("Something went wrong. Illegal Request");
        }
    }

    /**
     * deletes tag from subComment
     * @param tagID ID of tag to be deleted
     * @param subCommentID ID of subComment
     * @param redirectID redirectID to 0 := tagPage, 1 := documentPage()
     * @return redirect to tagPage
     */
    @Security.Authenticated(SecurityStudent.class)
    public static Result deleteSubCommentTag(int tagID, int subCommentID, int redirectID) {
        TagDBConnection tagDBConnection = new TagDBConnection();
        tagDBConnection.deleteSubCommentTag(tagID, subCommentID);

        CommentDBConnection commentDBConnection = new CommentDBConnection();
        ContentDBConnection contentDBConnection = new ContentDBConnection();
        SubComment tempSubComment = commentDBConnection.getSubCommentByID(subCommentID);
        Comment tempComment = commentDBConnection.getCommentByID(tempSubComment.getCommentID());
        int docId = contentDBConnection.getDocIdToContent(tempComment.getContentID());

        if (redirectID == 0){
            return redirect(routes.TagController.tagsPage());
        } else if (redirectID == 1) {
            return redirect(routes.DocumentControllerSama.documentPage(docId, false));
        } else {
            return notFound("Something went wrong. Illegal Request");
        }
    }

    /**
     * deletes tag of contentHeadline
     * @param tagID ID of tag
     * @param cHeadlineID ID of headline
     * @param redirectID redirectID to 0 := tagPage, 1 := documentPage()
     * @return redirect
     */
    public static Result deleteContentHeadlineTag(int tagID, int cHeadlineID, int redirectID) {
        TagDBConnection         tagDBConnection         = new TagDBConnection();
        tagDBConnection.deleteContentHeadlineTag(tagID, cHeadlineID);

        ContentDBConnection     contentDBConnection     = new ContentDBConnection();
        ContentHeadline         tmpContentHeadline = contentDBConnection.getContentHeadLineByID(cHeadlineID);
        int docID                                       = contentDBConnection.getChapterByID(tmpContentHeadline.getChapterID()).getDocID();

        if (redirectID == 0){
            return redirect(routes.TagController.tagsPage());
        } else if (redirectID == 1) {
            return redirect(routes.DocumentControllerSama.documentPage(docID, false));
        } else {
            return notFound("Something went wrong. Illegal Request");
        }
    }

    /**
     * deletes tag of contentSubHeadline
     * @param tagID ID of tag
     * @param cSubHeadlineID ID of subHeadline
     * @param redirectID redirectID to 0 := tagPage, 1 := documentPage()
     * @return redirect
     */
    public static Result deleteContentSubHeadlineTag(int tagID, int cSubHeadlineID, int redirectID) {
        TagDBConnection         tagDBConnection         = new TagDBConnection();
        tagDBConnection.deleteContentSubHeadlineTag(tagID, cSubHeadlineID);

        ContentDBConnection contentDBConnection     = new ContentDBConnection();
        ContentHeadline         tmpContentHeadline      = contentDBConnection.getContentHeadLineByID(contentDBConnection.getContentSubHeadLineByID(cSubHeadlineID).getCheadlineID());
        int docID                                       = contentDBConnection.getChapterByID(tmpContentHeadline.getChapterID()).getDocID();

        if (redirectID == 0){
            return redirect(routes.TagController.tagsPage());
        } else if (redirectID == 1) {
            return redirect(routes.DocumentControllerSama.documentPage(docID, false));
        } else {
            return notFound("Something went wrong. Illegal Request");
        }
    }

    /**
     * deletes specific tag
     * @param tagID id of tag to be deleted
     * @return redirect to tagsPage
     */
    @Security.Authenticated(SecurityStudent.class)
    public static Result deleteTag(int tagID) {
        TagDBConnection tagDBConnection = new TagDBConnection();
        tagDBConnection.deleteTag(tagID);

        return redirect(routes.TagController.tagsPage());
    }
}
