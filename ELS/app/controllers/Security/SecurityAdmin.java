package controllers.Security;

import play.mvc.Http.*;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Created by Stefan on 04.02.2015.
 * class for authentication of logged in admin
 */
public class SecurityAdmin extends Security.Authenticator {

    /**
     *
     * @param ctx httpContext
     * @return any String if authentication was successful, null if authentication failed
     */
    @Override
    public String getUsername(Context ctx) {
        ctx = SessionController.checkSession(ctx);
        try {
            int role = Integer.parseInt(ctx.session().get("role"));
            if (role == 0 ) {
                return "admin";
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
