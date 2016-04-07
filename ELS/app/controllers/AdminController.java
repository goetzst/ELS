package controllers;

import controllers.DBConnection.UserDataDBConnection;
import play.mvc.Security;
import controllers.Security.*;
import models.*;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;
import java.util.List;

/**
 * @author Stefan
 * class for administration
 */
public class AdminController extends Controller{

    /**
     * gets list of all Users and excludes self from editable rights
     * @return Page for changing rights
     */
    @Security.Authenticated(SecurityAdmin.class)
    public static Result rightsPage() {
        UserDataDBConnection    userDataDBConnection    = new UserDataDBConnection();
        List<User>              userList                = userDataDBConnection.getUsers();
        String                  self                    = session().get("email");
        int                     role                    = Integer.parseInt(session().get("role"));
        return ok(rigthsPage.render(userList, self, role));
    }

    /**
     *
     * @param email email of the user staged for right change
     * @param role new role user will get
     * @return redirect to rightsPage
     */
    @Security.Authenticated(SecurityAdmin.class)
    public static Result changeRight(String email, int role) {
        UserDataDBConnection    userDataDBConnection    = new UserDataDBConnection();
        userDataDBConnection.changeUserRole(email, role);
        flash("success", "Rights changed");
        return redirect(controllers.routes.AdminController.rightsPage());
    }
}
