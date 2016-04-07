package controllers;

import controllers.DBConnection.UserDataDBConnection;
import controllers.Security.PasswordChecker;
import models.*;
import controllers.routes;
import play.mvc.*;
import play.data.Form;
import views.html.registerPage;


/**
 * @author Sabrina
 * is responsible for registerin a user
 */
public class RegisterController extends Controller {

    /**
     * renders the new page
     * @return to register Page
     */
    public static Result registerPage() {
        return ok(registerPage.render(Form.form(User.class)));
    }


    /**
     * registers a new user
     * @return redirects if registration is successful to login page, else to register page
     */
    public static Result register() {
        UserDataDBConnection        userData    = new UserDataDBConnection();
        Form<User>                  formUser    = Form.form(User.class).bindFromRequest();
        if(userData.getUserByEmail(formUser.get().getEmail()) != null) {
            flash("danger", "mail already used");
            return badRequest(registerPage.render(Form.form(User.class)));
        } else {
            UserDataDBConnection    dbCon       = new UserDataDBConnection();
            User                    newUser     = formUser.get();
            String                  pass        = PasswordChecker.hash(newUser.getPassword());
            dbCon.createUser(newUser.getEmail() , newUser.getSurName(), newUser.getFirstName(), 2, pass);
            flash("success", "Registration successful");
            return redirect(routes.LoginController.loginPage());
        }
    }
}
