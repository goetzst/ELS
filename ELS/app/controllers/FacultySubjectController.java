package controllers;

import controllers.DBConnection.FacultyDBConnection;
import controllers.DBConnection.SubjectDBConnection;
import controllers.Security.SecurityAdmin;
import models.Faculty;
import models.Subject;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.facultySubjectPage;

import java.util.LinkedList;
import java.util.List;

/**
 * Controls all the actions for faculties and subjects
 * Remove, add, change faculties and subjects
 *
 * @author Thomas
 */
public class FacultySubjectController extends Controller {

    /**
     * Show the page where you can edit the faculties and subjects.
     *
     * @return Page to edit the faculties and subjects
     */
    @Security.Authenticated(SecurityAdmin.class)
    public static Result editFacultySubjectPage() {

        FacultyDBConnection facultyDb   = new FacultyDBConnection();
        SubjectDBConnection subjectDb   = new SubjectDBConnection();

        List<Faculty> facultyList       = facultyDb.getFaculties();
        List<Subject> subjectList       = new LinkedList<>();

        for (Faculty faculty : facultyList) {
            List<Subject> temp = subjectDb.getSubjectsByFacultyName(faculty.getFacultyName());
            subjectList.addAll(temp);
        }

        String email    = session().get("email");
        int role        = Integer.parseInt(session().get("role"));

        return ok(facultySubjectPage.render(facultyList, subjectList, email, role));

    }

    /**
     * Delete a faculty including its subjects and documents.
     *
     * @param name Name of the faculty to delete
     * @return Redirect to the page where you can edit faculties and subjects
     */
    @Security.Authenticated(SecurityAdmin.class)
    public static Result removeFaculty(String name) {
        FacultyDBConnection facultyDb   = new FacultyDBConnection();
        facultyDb.removeFaculty(name);
        return redirect(controllers.routes.FacultySubjectController.editFacultySubjectPage());
    }

    /**
     * Add a new faculty
     *
     * @return Redirect to the page where you can edit faculties and subjects
     */
    @Security.Authenticated(SecurityAdmin.class)
    public static Result addFaculty() {
        FacultyDBConnection facultyDb   = new FacultyDBConnection();

        DynamicForm requestData         = Form.form().bindFromRequest();
        String name                     = requestData.get("name");
        facultyDb.addFaculty(name);

        return redirect(controllers.routes.FacultySubjectController.editFacultySubjectPage());
    }

    /**
     * Change the name of a existing faculty
     *
     * @param oldName Name of the faculty who should be changed
     * @return Redirect to the page where you can edit faculties and subjects
     */
    @Security.Authenticated(SecurityAdmin.class)
    public static Result changeFaculty(String oldName) {
        FacultyDBConnection facultyDb   = new FacultyDBConnection();

        DynamicForm requestData         = Form.form().bindFromRequest();
        String newName                  = requestData.get("newFacName"+oldName.replace(' ', '_'));

        facultyDb.editFaculty(oldName, newName);
        return redirect(controllers.routes.FacultySubjectController.editFacultySubjectPage());
    }

    /**
     * Delete a subject including its documents.
     *
     * @param subjectId Id of the subject to delete
     * @return Redirect to the page where you can edit faculties and subjects
     */
    @Security.Authenticated(SecurityAdmin.class)
    public static Result removeSubject(int subjectId) {
        SubjectDBConnection subjectDb   = new SubjectDBConnection();
        subjectDb.removeSubject(subjectId);
        return redirect(controllers.routes.FacultySubjectController.editFacultySubjectPage());
    }

    /**
     * Add a new subject
     *
     * @return Redirect to the page where you can edit faculties and subjects
     */
    @Security.Authenticated(SecurityAdmin.class)
    public static Result addSubject() {
        SubjectDBConnection subjectDb   = new SubjectDBConnection();

        DynamicForm requestData         = Form.form().bindFromRequest();
        String facName              = requestData.get("facName");
        String subName              = requestData.get("subName");
        subjectDb.addSubject(subName, facName);
        return redirect(controllers.routes.FacultySubjectController.editFacultySubjectPage());
    }

    /**
     * Change the name of a existing subject
     *
     * @param subjectId Id of the subject to change
     * @return Redirect to the page where you can edit faculties and subjects
     */
    @Security.Authenticated(SecurityAdmin.class)
    public static Result changeSubject(int subjectId) {
        SubjectDBConnection subjectDb   = new SubjectDBConnection();

        DynamicForm requestData         = Form.form().bindFromRequest();
        String newName                  = requestData.get("newName");

        subjectDb.editSubject(subjectId, newName);
        return redirect(controllers.routes.FacultySubjectController.editFacultySubjectPage());
    }
}
