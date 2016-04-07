package controllers.Security;

import controllers.DBConnection.ContentDBConnection;
import controllers.DBConnection.DocumentDBConnection;
import controllers.DBConnection.ModeratorDBConnection;
import play.mvc.Http.Context;
import play.mvc.Security;
import play.mvc.Result;
import models.*;
import java.util.List;

/**
 * Created by Stefan on 11.03.2015.
 * class for authentication of document moderator
 */
public class SecurityModerator extends Security.Authenticator{
    /**
     *
     * @param ctx httpContext
     * @return any String if authentication was successful, null if authentication failed
     */
    @Override
    public String getUsername(Context ctx) {
        ctx = SessionController.checkSession(ctx);
        try {
            DocumentDBConnection    documentConnection  = new DocumentDBConnection();
            String                  ctxMail             = ctx.session().get("email");
            int                     role                = Integer.parseInt(ctx.session().get("role"));
            String                  url                 = ctx.request().uri();
            if (role == 0) {
                return "admin";
            } else if(url.contains("addNewChapter") ||
                    url.contains("editChapter") ||
                    url.contains("deleteChapter") ||
                    url.contains("editDocument") || url.contains("reference")) {
                int                     docID               = Integer.parseInt(url.substring(11, url.indexOf("/", 12)));

                return rightCheck(role, documentConnection, docID, ctxMail);

            }else if(url.contains("addContentHeadlineToChapter") || url.contains("addContentToChapter") || url.contains("deleteHeadline") || url.contains("editContentHeadline")){
                ContentDBConnection contentDBConnection = new ContentDBConnection();
                int                 chapterID           = Integer.parseInt(url.substring(11, url.indexOf("/", 12)));
                int                 docID               = contentDBConnection.getChapterByID(chapterID).getDocID();

                return rightCheck(role, documentConnection, docID, ctxMail);

            }else if(url.contains("addContentSubHeadlineToHeadline") || url.contains("addContentToHeadline")|| url.contains("deleteSubHeadline") || url.contains("editContentSubHeadline")){
                ContentDBConnection contentDBConnection = new ContentDBConnection();
                int                 headlineID          = Integer.parseInt(url.substring(11, url.indexOf("/", 12)));
                int                 docID               = contentDBConnection.getChapterByID(contentDBConnection.getContentHeadLineByID(headlineID).getChapterID()).getDocID();

                return rightCheck(role, documentConnection, docID, ctxMail);

            }else if(url.contains("addContentToSubHeadline")){
                ContentDBConnection contentDBConnection = new ContentDBConnection();
                int                 subHeadlineID       = Integer.parseInt(url.substring(11, url.indexOf("/", 12)));
                int                 docID               = contentDBConnection.getChapterByID(contentDBConnection.getContentHeadLineByID(contentDBConnection.getContentSubHeadLineByID(subHeadlineID).getCheadlineID()).getChapterID()).getDocID();

                return rightCheck(role, documentConnection, docID, ctxMail);

            }else if(url.contains("deleteContent") || url.contains("editContent")){
                ContentDBConnection contentDBConnection = new ContentDBConnection();
                int                 firstSlash          = url.indexOf("/", 12);
                int                 secondSlash         = url.indexOf("/", firstSlash + 1);
                int                 contentID           = Integer.parseInt(url.substring(firstSlash +1 , secondSlash));
                int                 docID               = contentDBConnection.getDocIdToContent(contentID);

                return rightCheck(role, documentConnection, docID, ctxMail);
            }else if(url.contains("documents")){
                return "documentPage";
            }
        } catch (NumberFormatException e) {
            //if role is not assigned or anything else went wrong
            return null;
        }
        return null;
    }

    /**
     * checks if user is a moderator of given document
     * @param docID ID of document
     * @param ctxMail mail of current user
     * @return true if he is a moderator, false if not
     */
    static boolean checkForModerator(int docID, String ctxMail){
        ModeratorDBConnection   moderatorDBConnection   = new ModeratorDBConnection();
        List<User>              modList                 = moderatorDBConnection.getModeratorsOfDocument(docID);
        for(User u: modList){
            if(ctxMail.equals(u.getEmail()))
                return true;
        }
        return false;
    }

    /**
     *
     * @param role role of user
     * @param documentConnection connection so we don't have to open another one
     * @param docID ID of document
     * @param ctxMail mail of current user
     * @return String if access is grated null otherwise
     */
    static String rightCheck(int role, DocumentDBConnection documentConnection, int docID, String ctxMail){
        if(role == 1){
            String          dozentEmail         = documentConnection.getDocumentByID(docID).getDozentEmail();
            if(ctxMail.equals(dozentEmail))
                return "dozent";
        }else{
            if(checkForModerator(docID, ctxMail))
                return "moderator";
        }

        return null;
    }

    /**
     * handles unauthorized requests
     * @param ctx httpContext
     * @return redirect with http:forbidden
     */
    @Override
    public Result onUnauthorized(Context ctx) {
        return notFound("The page you requested does not exist");
    }
}
