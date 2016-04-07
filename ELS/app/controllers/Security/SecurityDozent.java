package controllers.Security;

import controllers.DBConnection.DocumentDBConnection;
import play.mvc.Http.Context;
import play.mvc.Security;
import play.mvc.Result;

/**
 * Created by Stefan on 04.02.2015.
 * class for authentication of logged in dozent
 */
public class SecurityDozent extends Security.Authenticator {

    /**
     *
     * @param ctx httpContext
     * @return any String if authentication was successful, null if authentication failed
     */
    @Override
    public String getUsername(Context ctx) {
        ctx = SessionController.checkSession(ctx);
        try {
            DocumentDBConnection documentConnection  = new DocumentDBConnection();
            String                  ctxMail             = ctx.session().get("email");
            int                     role                = Integer.parseInt(ctx.session().get("role"));
            String                  url                 = ctx.request().uri();
            if (role == 0) {
                return "admin";
            } else if((url.contains("documents") &&
                            (url.contains("delete") ||
                            url.contains("changeDocumentName") ||
                            url.contains("moderators") ||
                            url.contains("removeModerator") ||
                            url.contains("addModerator"))) ||
                            url.contains("version") ||
                            url.contains("suggestion") ||
                            url.contains("visibility")) {
                int                     docID               = Integer.parseInt(url.substring(11, url.indexOf("/", 12)));
                String                  dozentMail          = documentConnection.getDocumentByID(docID).getDozentEmail();
                if(dozentMail.equals(ctxMail)) {
                    return "owner of document";
                }
            } else if (role == 1) {
                return "dozent";
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
     * @return redirect with http:forbidden
     */
    @Override
    public Result onUnauthorized(Context ctx) {
        return notFound("The page you requested does not exist");
    }

}
