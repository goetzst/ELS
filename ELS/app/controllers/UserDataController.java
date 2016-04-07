package controllers;

import controllers.DBConnection.UserDataDBConnection;
import play.mvc.Security;
import controllers.Security.*;
import models.*;
import controllers.routes;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;

/**
 * @author Sabrina
 * is responsible for the user data
 */
public class UserDataController extends Controller {

    /**
     * prepares data for display of the userDataPage
     * @return userDataPage
     */
    @Security.Authenticated(SecurityStudent.class)
    public static Result userDataPage() {
        String                      mail            = session().get("email");
        int                         role            = Integer.parseInt(session().get("role"));
        UserDataDBConnection        userDBcon       = new UserDataDBConnection();

        User user = userDBcon.getUserByEmail(mail);
        return ok(userDataPage.render(Form.form(UserData.class), user, mail, role));
    }

    /**
     * changes the password of user
     * @param email ID of user
     * @return redirect
     */
    @Security.Authenticated(SecurityStudent.class)
    public static Result changeUserData(String email) {
        UserDataDBConnection        userDB          = new UserDataDBConnection();
        Form<UserData>              userData        = Form.form(UserData.class).bindFromRequest();
        User                        user            = userDB.getUserByEmail(email);
        UserData                    changedUser     = userData.get();
        if(PasswordChecker.check(user, changedUser.getOldPassword())) {
            if(!changedUser.getFirstName().equals("")) {
                userDB.changeUserFirstName(email, changedUser.getFirstName());
            }
            if(!changedUser.getSurName().equals("")) {
                userDB.changeUserSurName(email, changedUser.getSurName());
            }
            if(!changedUser.getPassword().equals("")) {
                userDB.changeUserPassword(email, PasswordChecker.hash(changedUser.getPassword()));
            }
            flash("success", "User data changed");
            return redirect(routes.MainPageController.mainPage());
        } else {
            flash("danger", "Password incorrect. Please try again.");
            return redirect(routes.UserDataController.userDataPage());
        }
    }

    /**
     * shows page on which user can type his email address to be able to set a new password
     * @return the page
     */
    public static Result passwordForgottenPage() {
        return ok(passwordForgottenPage.render(Form.form(LoginData.class)));
    }

    /**
     * checks if email is used and sends link to page on which password can be changed
     * @return redirect to mainPage
     */
    public static Result passwordForgotten() {
        UserDataDBConnection        userDB          = new UserDataDBConnection();
        Form<LoginData>             restoreForm     = Form.form(LoginData.class).bindFromRequest();
        System.out.println(restoreForm);
        String                      mail            = restoreForm.get().getMail();
        User                        userToRestore   = userDB.getUserByEmail(mail);
        if(userToRestore != null) {
            String                  hash            = PasswordChecker.restoreHash(userToRestore.getPassword());
            String                  link            = "localhost:9000/restorePassword/" + userToRestore.getEmail() + "/" + hash;
            System.out.println(link);
            MailSender.sendMail(userToRestore.getEmail(), "You requested a password reset follow the steps of the following link (please ignore if you did not request a reset):\n"+ link, "Password forgotten");
            flash("success", "A mail has been sent");
        } else {
            flash("danger", "The email you provided does not seem to be used");
        }
        return redirect(routes.LoginController.loginPage());
    }

    /**
     * checks if mail and hash match and displays page to change password
     * @param email to check
     * @param hash to check
     * @param spaceLength length of noise between realHash
     * @return redirect to loginPage with flash or restorePasswordPage
     */
    public static Result restorePasswordPage(String email,int spaceLength, String hash) {
        UserDataDBConnection        userDB          = new UserDataDBConnection();
        String                      oldHash         = userDB.getUserByEmail(email).getPassword();

        if(PasswordChecker.checkRestoreHash(oldHash, spaceLength, hash)) {
            return ok(restorePasswordPage.render(Form.form(LoginData.class), email));
        } else {
            return redirect(routes.LoginController.loginPage());
        }
    }

    /**
     * changes password to new one
     * @param email forwarded by page
     * @return redirect to loginPage with flash
     */
    public static Result restorePassword(String email) {
        UserDataDBConnection            userDB      = new UserDataDBConnection();
        Form<LoginData>                 restoreForm = Form.form(LoginData.class).bindFromRequest();
        String                          pass        = PasswordChecker.hash(restoreForm.get().getPassword());

        userDB.changeUserPassword(email, pass);
        flash("success", "Password changed");
        return redirect(routes.LoginController.loginPage());
    }
}
