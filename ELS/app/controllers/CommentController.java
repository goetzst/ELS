package controllers;

import controllers.DBConnection.CommentDBConnection;
import controllers.Security.SecurityStudent;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import controllers.routes;
import models.*;

/**
 * Controls all the actions for comments and subcomments
 * Remove, add, change comments and subcomments
 *
 * @author Alex
 */

public class CommentController extends Controller {

    /**
     * creates a new comment to a content
     * @param contentID the ID of the content the comment is added to
     * @param documentID the ID of the document the content is part of
     * @param redirectTo 0 := redirect to DocumentPage, 1 := redirect to EditDocumentPage
     * @return redirect
     */
    @Security.Authenticated(SecurityStudent.class)
    public static Result newComment(int contentID, int documentID, int redirectTo) {
        CommentDBConnection cdbc = new CommentDBConnection();
        Comment comment = Form.form(Comment.class).bindFromRequest().get();
        cdbc.createComment(session().get("email"), contentID, comment.getContent());
        if(redirectTo == 0) {
            return redirect(routes.DocumentControllerSama.documentPage(documentID, false));
        } else {
            return redirect(routes.DocumentControllerSama.documentPage(documentID, true));
        }
    }

    /**
     * edits the content of a comment
     * @param commentID ID of suggested comment
     * @param contentID ID of content the comment is related to
     * @param documentID ID of document the content is related to
     * @param redirectTo 0 := redirect to DocumentPage, 1 := redirect to EditDocumentPage
     * @return redirect
     */
    @Security.Authenticated(SecurityStudent.class)
    public static Result editComment(int commentID, int contentID, int documentID, int redirectTo) {
        CommentDBConnection cdbc = new CommentDBConnection();
        Comment comment = Form.form(Comment.class).bindFromRequest().get();
        cdbc.changeComment(commentID, comment.getContent());
        if(redirectTo == 0) {
            return redirect(routes.DocumentControllerSama.documentPage(documentID, false));
        } else {
            return redirect(routes.DocumentControllerSama.documentPage(documentID, true));
        }
    }

    /**
     * deletes the comment
     * @param commentID ID of suggested comment
     * @param contentID ID of content comment is related to
     * @param documentID ID of document content is related to
     * @param redirectTo 0 := redirect to DocumentPage, 1 := redirect to EditDocumentPage
     * @return redirect
     */
    @Security.Authenticated(SecurityStudent.class)
    public static Result deleteComment(int commentID, int contentID, int documentID, int redirectTo) {
        CommentDBConnection cdbc = new CommentDBConnection();
        cdbc.deleteComment(commentID);
        if(redirectTo == 0) {
            return redirect(routes.DocumentControllerSama.documentPage(documentID, false));
        } else {
            return redirect(routes.DocumentControllerSama.documentPage(documentID, true));
        }
    }

    //subcomment related methods

    /**
     * creates a new SubComment
     * @param commentID ID of related comment
     * @param contentID ID of related content
     * @param documentID ID of related document
     * @param redirectTo 0 := redirect to DocumentPage, 1 := redirect to EditDocumentPage
     * @return redirect
     */
    @Security.Authenticated(SecurityStudent.class)
    public static Result newSubComment(int commentID, int contentID, int documentID, int redirectTo){
        CommentDBConnection cdbc = new CommentDBConnection();
        SubComment subComment = Form.form(SubComment.class).bindFromRequest().get();
        cdbc.createSubComment(session().get("email"), commentID, subComment.getContent());
        if(redirectTo == 0) {
            return redirect(routes.DocumentControllerSama.documentPage(documentID, false));
        } else {
            return redirect(routes.DocumentControllerSama.documentPage(documentID, true));
        }
    }

    /**
     * edits a SubComment
     * @param subCommentID ID of suggested SubComment
     * @param commentID ID of related comment
     * @param contentID ID of related content
     * @param documentID ID of related document
     * @param redirectTo 0 := redirect to DocumentPage, 1 := redirect to EditDocumentPage
     * @return redirect
     */
    @Security.Authenticated(SecurityStudent.class)
    public static Result editSubComment(int subCommentID, int commentID, int contentID, int documentID, int redirectTo){
        CommentDBConnection cdbc = new CommentDBConnection();
        SubComment subComment = Form.form(SubComment.class).bindFromRequest().get();
        cdbc.changeSubComment(subCommentID,subComment.getContent());
        if(redirectTo == 0) {
            return redirect(routes.DocumentControllerSama.documentPage(documentID, false));
        } else {
            return redirect(routes.DocumentControllerSama.documentPage(documentID, true));
        }
    }

    /**
     * deletes the SubComment
     * @param subCommentID ID of suggested SubComment
     * @param commentID ID of related comment
     * @param contentID ID of related content
     * @param documentID ID of related document
     * @param redirectTo 0 := redirect to DocumentPage, 1 := redirect to EditDocumentPage
     * @return redirect
     */
    @Security.Authenticated(SecurityStudent.class)
    public static Result deleteSubComment(int subCommentID, int commentID, int contentID, int documentID, int redirectTo){
        CommentDBConnection cdbc = new CommentDBConnection();
        cdbc.deleteSubComment(subCommentID);
        if(redirectTo == 0) {
            return redirect(routes.DocumentControllerSama.documentPage(documentID, false));
        } else {
            return redirect(routes.DocumentControllerSama.documentPage(documentID, true));
        }
    }
}
