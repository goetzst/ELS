package controllers;

import controllers.DBConnection.UserDataDBConnection;
import controllers.Security.*;
import controllers.routes;
import play.mvc.*;
import models.*;
import views.html.*;
import play.data.Form;

/**
 * @author Sabrina
 * logs the user in
 */
public class LoginController extends Controller {


    /**
     * renders the loginPage
     * @return to login Page
     */
    public static Result loginPage() {
        return ok(loginPage.render(Form.form(LoginData.class)));
    }

    /**
     * the user gets logged in, if possible
     * @return to the main Page, else to login Page if login not possible
     */
    public static Result authenticate() {
        UserDataDBConnection        UserData    = new UserDataDBConnection();
        Form<LoginData>             loginForm   = Form.form(LoginData.class).bindFromRequest();
        String                      userEmail   = loginForm.get().getMail();
        User                        user        = UserData.getUserByEmail(userEmail);
        if(user != null && PasswordChecker.check(user, loginForm.get().getPassword())){ //checks if the user exists and whether entered password is correct
            SessionController.createSession(loginForm.get().getMail(), user.getRole(), Http.Context.current());
            return redirect(routes.MainPageController.mainPage());
        } else {
            flash("danger", "Wrong Username or Password");
            return badRequest(loginPage.render(Form.form((LoginData.class))));
        }

    }
    /**
     * the user gets logged out, session will be cleared
     * @return redirects to the login Page
     */
    public static Result logout(){
        session().clear();
        return redirect(routes.LoginController.loginPage());
    }
}
