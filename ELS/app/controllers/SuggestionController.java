package controllers;

import controllers.DBConnection.ContentDBConnection;
import controllers.DBConnection.DocumentDBConnection;
import controllers.DBConnection.FlashCardDBConnectionSenpai;
import controllers.DBConnection.SuggestionDBConnection;
import controllers.Security.SecurityDozent;
import controllers.Security.SecurityStudent;
import models.*;
import play.mvc.Controller;
import play.mvc.Result;
import controllers.routes;
import play.mvc.Security;
import views.html.documentSuggestionPage;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Stefan
 * class for suggestion of content for a Document
 */
public class SuggestionController extends Controller{

    /**
     * creates a suggestion with given flashCardContent to a KIND of parent
     * @param flashCardContentID ID of suggested content
     * @param parentOfSuggestionKind kind of parent 0:= chapter, 1:=headline, 2:=subHeadline
     * @param parentOfSuggestionID ID of parent to which the suggestion is made
     * @return redirect
     */
    @Security.Authenticated(SecurityStudent.class)
    public static Result suggestContent(int flashCardContentID, int parentOfSuggestionKind, int parentOfSuggestionID){
        FlashCardDBConnectionSenpai flashCardDBConnectionSenpai = new FlashCardDBConnectionSenpai();
        Content                     contentToSuggest            = flashCardDBConnectionSenpai.getContentByID(flashCardContentID);
        SuggestionDBConnection      suggestionDBConnection      = new SuggestionDBConnection();
        //int                         parentOfSuggestionKind      = -1;//TODO
        //int                         parentOfSuggestionID        = -1;//TODO
        suggestionDBConnection.createSuggestion(parentOfSuggestionID, contentToSuggest.getContent(), contentToSuggest.getType(), session().get("email"), parentOfSuggestionKind);

        int                         flashCardID                 = contentToSuggest.getDocumentID();//this works because if a content object is created from flashCardDBConnectionSenpai, the flashCardID is saved in docID
        return redirect(routes.FlashCardControllerSensei.flashCardPage(flashCardID, true));
    }

    /**
     * displays all suggestions a user made
     * @return page
     */
    @Security.Authenticated(SecurityStudent.class)
    public static Result userSuggestionPage(){
        SuggestionDBConnection  suggestionDBConnection  = new SuggestionDBConnection();
        List<Suggestion>        suggestionList          = suggestionDBConnection.getSuggestionsOfUser(session().get("email"));
        return ok();
        //TODO
        //return ok(suggestionPageUser.render(suggestionList, session().get("email"), session().get("role")))
    }

    /**
     * displays all suggestions to chapters, headlines and subHeadlines of document
     * @param docID ID of document
     * @return page
     */
    @Security.Authenticated(SecurityDozent.class)
    public static Result documentSuggestionPage(int docID){
        SuggestionDBConnection  suggestionDBConnection  = new SuggestionDBConnection();
        ContentDBConnection     contentDBConnection     = new ContentDBConnection();
        DocumentDBConnection    documentDBConnection    = new DocumentDBConnection();

        List<ChapterChan>                           chapters                = contentDBConnection.getChaptersToDoc(docID);
        HashMap<Integer, List<ContentHeadline> >    headlines               = new HashMap<>();
        HashMap<Integer, List<ContentSubHeadline>>  subHeadlines            = new HashMap<>();
        HashMap<Integer, List<Suggestion>>          suggestionsToChapter    = new HashMap<>();
        HashMap<Integer, List<Suggestion>>          suggestionsToHeadline   = new HashMap<>();
        HashMap<Integer, List<Suggestion>>          suggestionsToSubHeadline= new HashMap<>();

        //creating HashMap for headlines, subHeadlines of all chapters and suggestions to all of them
        for(ChapterChan chan: chapters) {
            int                             tmpChapterID            = chan.getChapterID();
            //getting all suggestions to chapter and putting them into hashMap with chapterID as key
            List<Suggestion>                tmpSuggestionsToChapter = suggestionDBConnection.getSuggestionsToChapter(tmpChapterID);
            suggestionsToChapter.put(tmpChapterID, tmpSuggestionsToChapter);

            List<ContentHeadline>           tmpHeadlines            = contentDBConnection.getContentHeadLinesToChapter(chan.getChapterID()); //getting all headlines to chapter
            headlines.put(tmpChapterID, tmpHeadlines); //adding headlineList to hashmap with chapterID as key
            for(ContentHeadline ch: tmpHeadlines) {
                int                         tmpHeadlineID           = ch.getcHeadLineID();
                //getting all suggestions to headline and putting them into hashMap with headlineID as key
                List<Suggestion>                tmpSuggestionsToHeadline = suggestionDBConnection.getSuggestionsToHeadline(tmpHeadlineID);
                suggestionsToHeadline.put(tmpHeadlineID, tmpSuggestionsToHeadline);

                List<ContentSubHeadline>    tmpSubHeadlines = contentDBConnection.getContentSubHeadLinesToHeadline(tmpHeadlineID); //getting all subHeadlines to headline
                subHeadlines.put(tmpHeadlineID, tmpSubHeadlines); // adding subHeadlineList to hashmap with headlineID as key
                for(ContentSubHeadline csh: tmpSubHeadlines){
                    int                     tmpSubHeadlineID    = csh.getcSubHeadLineID();
                    //getting all suggestions to subHeadline and putting them into hashMap with subHeadlineID as key
                    List<Suggestion>                tmpSuggestionsToSubHeadline = suggestionDBConnection.getSuggestionsToSubHeadline(tmpSubHeadlineID);
                    suggestionsToSubHeadline.put(tmpSubHeadlineID, tmpSuggestionsToSubHeadline);
                }
            }
        }


        Document doc = documentDBConnection.getDocumentByID(docID);
        return ok(documentSuggestionPage.render(
                chapters, headlines, subHeadlines,
                suggestionsToChapter, suggestionsToHeadline, suggestionsToSubHeadline,
                doc,
                session().get("email"), Integer.parseInt(session().get("role"))
        ));
    }

    /**
     * accepts suggestion adds it onto parent and deletes suggestion, it also notifies the user who suggested the content
     * @param suggestionID ID of suggestion which has been accepted
     * @param docID id of Document
     * @return redirect
     */
    @Security.Authenticated(SecurityDozent.class)
    public static Result acceptSuggestion(int suggestionID, int docID){
        SuggestionDBConnection  suggestionDBConnection  = new SuggestionDBConnection();
        ContentDBConnection     contentDBConnection     = new ContentDBConnection();

        Suggestion              acceptedSuggestion      = suggestionDBConnection.getSuggestionByID(suggestionID);
        if(parentExists(acceptedSuggestion.getParentKind(), acceptedSuggestion.getParentID())) {
            int suggestionSequence = getMaxParentSequence(acceptedSuggestion.getParentID(), acceptedSuggestion.getParentKind());

            switch (acceptedSuggestion.getParentKind()) {//adds suggested content to database with parentID depending on parent type, sequence is maxSequence +1 so it is the last one
                case 0:
                    contentDBConnection.createContent(acceptedSuggestion.getParentID(), -1, -1, acceptedSuggestion.getContent(), acceptedSuggestion.getType(), suggestionSequence + 1);
                    break;
                case 1:
                    contentDBConnection.createContent(-1, acceptedSuggestion.getParentID(), -1, acceptedSuggestion.getContent(), acceptedSuggestion.getType(), suggestionSequence + 1);
                    break;
                case 2:
                    contentDBConnection.createContent(-1, -1, acceptedSuggestion.getParentID(), acceptedSuggestion.getContent(), acceptedSuggestion.getType(), suggestionSequence + 1);
            }
            MailSender.sendMail(acceptedSuggestion.getEmail(), "Your suggestion with following content has been accepted:\n" + acceptedSuggestion.getContent(), "Suggestion accepted");
            suggestionDBConnection.deleteSuggestion(suggestionID);
        }else{
            flash("danger", "deleted suggestion because parent no longer exists");
            suggestionDBConnection.deleteSuggestion(suggestionID);
            MailSender.sendMail(acceptedSuggestion.getEmail(), "Your suggestion with following content has been declined because its parent does no longer exist:\n" + acceptedSuggestion.getContent(), "Suggestion declined");
        }
        return redirect(routes.SuggestionController.documentSuggestionPage(docID));
    }

    /**
     * deletes a suggestion and notifies and emails author of suggestion
     * @param suggestionID ID of suggestion to be deleted
     * @param docID ID of document to which suggestion belongs (for redirect purpose)
     * @return redirect
     */
    public static Result declineSuggestion(int suggestionID, int docID){
        declineSuggestion(suggestionID);
        return redirect(routes.SuggestionController.documentSuggestionPage(docID));
    }

    /**
     * deletes suggestion and emails author of suggestion
     * @param suggestionID ID of suggestion to be deleted
     */
    @Security.Authenticated(SecurityDozent.class)
    private static void declineSuggestion(int suggestionID){
        SuggestionDBConnection  suggestionDBConnection = new SuggestionDBConnection();
        Suggestion              declinedSuggestion      = suggestionDBConnection.getSuggestionByID(suggestionID);
        suggestionDBConnection.deleteSuggestion(suggestionID);
        MailSender.sendMail(declinedSuggestion.getEmail(), "Your suggestion with following content has been declined:\n" + declinedSuggestion.getContent(), "Suggestion declined");
    }

    /**
     * deletes ALL suggestions to one document
     * @param docID ID of document
     * @return redirect
     */
    @Security.Authenticated(SecurityDozent.class)
    public static Result declineALLSuggestions(int docID){
        SuggestionDBConnection  suggestionDBConnection  = new SuggestionDBConnection();
        ContentDBConnection     contentDBConnection     = new ContentDBConnection();
        List<ChapterChan>       chapterChanList         = contentDBConnection.getChaptersToDoc(docID);
        //collecting all headlines and subHeadlines to document
        List<ContentHeadline>   contentHeadlineList     = chapterChanList.      stream()
                .map(c -> contentDBConnection.getContentHeadLinesToChapter(c.getChapterID()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        List<ContentSubHeadline>contentSubHeadlineList  = contentHeadlineList.      stream()
                .map(ch -> contentDBConnection.getContentSubHeadLinesToHeadline(ch.getcHeadLineID()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        //creating 3 separate lists for suggestions to chapters, headlines and subHeadlines of document
        List<Suggestion>        chapterSuggestions      = chapterChanList.          stream()//creating stream
                .map(c -> suggestionDBConnection.getSuggestionsToChapter(c.getChapterID()))//gets list of suggestion to every chapter
                .flatMap(Collection::stream)//creates stream of all suggestions in mapped lists
                .collect(Collectors.toList());//collecting stream into a list

        List<Suggestion>        headlineSuggestions     = contentHeadlineList.      stream()
                .map(ch -> suggestionDBConnection.getSuggestionsToHeadline(ch.getcHeadLineID()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        List<Suggestion>        subHeadlineSuggestions  = contentSubHeadlineList.     stream()
                .map(csh -> suggestionDBConnection.getSuggestionsToSubHeadline(csh.getcSubHeadLineID()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        //unifying all suggestions
        List<Suggestion>        allSuggestions          = new LinkedList<>();
        allSuggestions.addAll(chapterSuggestions);
        allSuggestions.addAll(headlineSuggestions);
        allSuggestions.addAll(subHeadlineSuggestions);
        //declining every suggestion in unified list
        allSuggestions.forEach(c -> declineSuggestion(c.getId()));
        return redirect(routes.SuggestionController.documentSuggestionPage(docID));
    }

    /**
     * returns highest sequence number of all contentChildren of parent
     * @param parentID ID of parent
     * @param parentKind kind of parent 0:=chapter, 1:= headline, 2:= subHeadline
     * @return maxID or -1 if something went wrong
     */
    private static int getMaxParentSequence(int parentID, int parentKind){
        ContentDBConnection contentDBConnection = new ContentDBConnection();
        switch (parentKind){
            case 0:
                List<Content>   contentsOfParentChapter = contentDBConnection.getContentToChapter(parentID);
                return maxOfContentList(contentsOfParentChapter);
            case 1:
                List<Content>   contentsOfParentHeadline= contentDBConnection.getContentToContentHeadline(parentID);
                return maxOfContentList(contentsOfParentHeadline);
            default:
                List<Content>   contentsOfParentSHeadline= contentDBConnection.getContentToContentSubHeadline(parentID);
                return maxOfContentList(contentsOfParentSHeadline);
        }
    }

    /**
     *
     * @param contents List of Content
     * @return maxSequence of contents in list
     */
    private static int maxOfContentList(List<Content> contents){
        int max = 1;

        for(Content c: contents){
            if(c.getSequence() > max)
                max = c.getSequence();
        }
        return max;
    }

    /**
     * checks if parent of suggestions still exists in document
     * @param parentKind ID of parent
     * @param parentID kind of parent
     * @return true if it exists, otherwise false
     */
    private static boolean parentExists(int parentKind, int parentID){
        ContentDBConnection contentDBConnection = new ContentDBConnection();
        switch(parentKind){
            case 0:
                return(contentDBConnection.getChapterByID(parentID) != null);
            case 1:
                return(contentDBConnection.getContentHeadLineByID(parentID) != null);
            default:
                return(contentDBConnection.getContentSubHeadLineByID(parentID) != null);
        }
    }
}
