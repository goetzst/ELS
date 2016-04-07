package controllers;

import controllers.DBConnection.DocumentDBConnection;
import controllers.DBConnection.FacultyDBConnection;
import controllers.DBConnection.SubjectDBConnection;
import controllers.Security.*;
import play.mvc.*;
import models.*;
import views.html.*;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Stefan
 * MainPage rendering and stuff
 */
public class MainPageController extends Controller{

    /**
     * finds all bookmarked Documents for user and displays them
     * @return mainPage
     */
    @Security.Authenticated(SecurityStudent.class)
    public static Result mainPage() {
        DocumentDBConnection    documentConnection  = new DocumentDBConnection();
        List<Document>          bookmarkedDocs;
        String                  userMail            = session().get("email");
        bookmarkedDocs                              = documentConnection.getBookmarkedDocuments(userMail);

        List<Document>          myDocs              = documentConnection.getDocumentsDozent(session().get("email"));
        myDocs.addAll(                                documentConnection.getDocumentsModerator(session().get("email")));

        return ok(mainPage.render(bookmarkedDocs, myDocs, session().get("email"), Integer.parseInt(session().get("role"))));
    }

    /**
     * gets list of all documents and calls view to display them
     * @return documentListPage
     */
    @Security.Authenticated(SecurityStudent.class)
    public static Result documentListPage() {
        DocumentDBConnection    documentConnection  = new DocumentDBConnection();
        FacultyDBConnection     facultyConnection   = new FacultyDBConnection();
        SubjectDBConnection     subjectConnection   = new SubjectDBConnection();

        List<Document>          docs;
        docs                                        = documentConnection.getDocuments();

        List<Faculty>           facs                = facultyConnection.getFaculties();
        List<Subject>           subs                = new LinkedList<>();
        List<Document>          bookmarkedDocs      = documentConnection.getBookmarkedDocuments(session().get("email"));

        List<Integer>            bookmarkedDocsIDs   = bookmarkedDocs.stream().map(Document::getDocumentID).collect(Collectors.toCollection(LinkedList::new));

        for (Faculty faculty : facs) {
            List<Subject> temp = subjectConnection.getSubjectsByFacultyName(faculty.getFacultyName());
            subs.addAll(temp);
        }

        String email    = session().get("email");
        int role        = Integer.parseInt(session().get("role"));

        return ok(documentListPage.render(docs, facs, subs, bookmarkedDocsIDs, email, role));
    }

}
