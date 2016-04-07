package controllers.Security;

import controllers.DBConnection.*;
import models.*;
import play.mvc.Http.*;
import play.mvc.Result;
import play.mvc.Security;
import controllers.routes;

import java.util.List;

/**
 * Created by Stefan on 04.02.2015.
 * class for authentication of logged in User
 */
public class SecurityStudent extends Security.Authenticator {

    /**
     *
     * @param ctx httpContext
     * @return any String if authentication was successful, null if authentication failed
     */
    @Override
    public String getUsername(Context ctx) {
        ctx = SessionController.checkSession(ctx);
        try {
            //most part same as SecurityModerator with some minor additions
            FlashCardDBConnectionSenpai   flashCardDBConnection= new FlashCardDBConnectionSenpai();
            CommentDBConnection     commentDBConnection = new CommentDBConnection();
            TagDBConnection         tagDBConnection     = new TagDBConnection();
            String                  ctxMail             = ctx.session().get("email");
            int                     role                = Integer.parseInt(ctx.session().get("role"));
            String                  url                 = ctx.request().uri();
            if (role == 0) {
                return "admin";
            } else if(url.contains("editComment") ||
                    url.contains("deleteComment") ||
                    url.contains("editSubComment") ||
                    url.contains("deleteSubComment")) {
                int                     firstSlash          = url.indexOf("/", 12);
                int                     secondSlash         = url.indexOf("/", firstSlash + 1);
                int                     thirdSlash          = url.indexOf("/", secondSlash + 1);
                int                     fourthSlash;
                int                     docID               = Integer.parseInt(url.substring(11, firstSlash));
                int                     commentID           = Integer.parseInt(url.substring(secondSlash + 1, thirdSlash));
                System.out.println("docID:" + docID);
                System.out.println("commentID:" + commentID);

                if(!url.contains("SubComment")) {
                    System.out.println("not SubComment");
                    Comment             comment             = commentDBConnection.getCommentByID(commentID);
                    if(ctxMail.equals(comment.getUserEmail()))//checks if user is owner of comment if not checks if user is dozent or moderator of document
                        return "owner of comment";
                    System.out.println("not owner of comment");
                    DocumentDBConnection    documentConnection = new DocumentDBConnection();
                    return SecurityModerator.rightCheck(role, documentConnection, docID, ctxMail);//end check

                } else if(url.contains("SubComment")) {
                    fourthSlash                             = url.indexOf("/", thirdSlash + 1);
                    int                 subCommentID        = Integer.parseInt(url.substring(thirdSlash + 1, fourthSlash));
                    System.out.println("subCommentID:" + subCommentID);
                    SubComment          subComment          = commentDBConnection.getSubCommentByID(subCommentID);
                    if(ctxMail.equals(subComment.getUserEmail()))//checks if user is owner of subComment if not checks if user is dozent or moderator of document
                        return "owner of subComment";
                    System.out.println("not owner of subComment");
                    DocumentDBConnection    documentConnection = new DocumentDBConnection();
                    return SecurityModerator.rightCheck(role, documentConnection, docID, ctxMail);//end check
                }
            } else if(url.contains("flashCard/")) {
                int                     firstSlash          = url.indexOf("/", 12);
                int                     flashCardID         = Integer.parseInt(url.substring(11, firstSlash));
                FlashCard               flashCard           = flashCardDBConnection.getFlashCardByID(flashCardID);
                if(ctxMail.equals(flashCard.getUserEmail())) {
                    return "owner of FlashCard";
                }
            } else if(url.contains("tag/") && !url.contains("add") && !url.contains("delete")) {
                int                     firstSlash          = url.indexOf("/", 6);
                int                     tagID               = Integer.parseInt(url.substring(5, firstSlash));
                List<Tag>               tagList             = tagDBConnection.getTagsOfUser(ctxMail);
                for(Tag tag: tagList) {
                    if(tag.getTagID() == tagID) {
                        return "owner of Tag";
                    }
                }
            } else if(url.contains("tag/delete")) {
                int                     firstSlash          = url.indexOf("/", 13);
                int                     tagID               = Integer.parseInt(url.substring(12, firstSlash));
                List<Tag>               tagList             = tagDBConnection.getTagsOfUser(ctxMail);
                for(Tag tag: tagList) {
                    if(tag.getTagID() == tagID) {
                        return "owner of Tag";
                    }
                }

            }else if(url.contains("suggestContent")){
                int                     firstSlash          = url.indexOf("/", 17);
                int                     flashCardContentID  = Integer.parseInt(url.substring(16, firstSlash));
                int                     flashCardID         = flashCardDBConnection.getContentByID(flashCardContentID).getDocumentID();//flashCardID is saved there
                FlashCard               flashCard           = flashCardDBConnection.getFlashCardByID(flashCardID);
                if(flashCard.getUserEmail().equals(ctxMail))
                        return "owner of flashCard";
            } else{
                if(role == 1 || role ==2) {
                    return "not less than student";
                }
            }
        } catch (NumberFormatException e) {
            //if role is not assigned or anything else went wrong
            return null;
        }
        return null;
    }

    /**
     * handles unauthorized requests
     * @param ctx httpContext
     * @return redirect to loginPage
     */
    @Override
    public Result onUnauthorized(Context ctx) {
        return redirect(routes.LoginController.loginPage());
    }
}
