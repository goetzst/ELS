package controllers;

import controllers.DBConnection.*;
import controllers.Security.SecurityDozent;
import models.*;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.moderatorPage;

import java.util.List;

/**
 * Controls all the actions for moderators.
 * Adds and deletes moderators from documents.
 *
 * @author Thomas
 */
public class ModeratorController extends Controller{

    /**
     * Deletes a moderator form a document.
     *
     * @param documentId ID of the the document you want to delete the moderator
     * @param email Email of the user you want to delete
     * @return Redirect to the edit document view
     */
    @Security.Authenticated(SecurityDozent.class)
    public static Result deleteModerator(int documentId, String email) {
        ModeratorDBConnection   mdb                 = new ModeratorDBConnection();

        mdb.deleteModeratorOfDocument(email, documentId);

        return redirect(controllers.routes.ModeratorController.moderatorPage(documentId));
    }

    /**
     * Adds a moderator to a document.
     *
     * @param documentId ID of the the document you want to add a moderator
     * @return Redirect to the edit document view
     */
    @Security.Authenticated(SecurityDozent.class)
    public static Result addModerator(int documentId) {

        DynamicForm dynamicData = Form.form().bindFromRequest();
        String email = dynamicData.get("moderatorEmail");
        if (email != null) {
            System.out.println(email);

            UserDataDBConnection userDb         = new UserDataDBConnection();
            ModeratorDBConnection moderatorDb   = new ModeratorDBConnection();

            User user = userDb.getUserByEmail(email);
            if (user != null) {
                moderatorDb.addModeratorToDocument(email, documentId);
                //TODO: email an Moderator senden
                return redirect(controllers.routes.ModeratorController.moderatorPage(documentId));
            } else {
                flash("danger", "User doesn't exists.");
                return redirect(controllers.routes.ModeratorController.moderatorPage(documentId));
            }
        } else {
            return badRequest("Error. No mail found.");
        }
    }

    /**
     * Shows the page where you can add, show and delete Moderators of a document
     *
     * @param documentID ID of the the document which moderators should be administered
     * @return Page to manage the moderators of a document
     */
    @Security.Authenticated(SecurityDozent.class)
    public static Result moderatorPage(int documentID) {
        ModeratorDBConnection   mdb                         = new ModeratorDBConnection();
        DocumentDBConnection    ddb                         = new DocumentDBConnection();

        List<User> moderators                           = mdb.getModeratorsOfDocument(documentID);
        Document doc = ddb.getDocumentByID(documentID);

        String email    = session().get("email");
        int role        = Integer.parseInt(session().get("role"));

        return ok(moderatorPage.render(moderators, doc, email, role));
    }
}
