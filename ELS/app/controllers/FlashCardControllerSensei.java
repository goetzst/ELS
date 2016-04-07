package controllers;

import controllers.DBConnection.*;
import controllers.Security.SecurityStudent;
import controllers.routes;
import models.*;
import play.data.DynamicForm;
import play.data.Form;
import play.db.DB;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import views.html.*;
import views.html.flashcardHelper.changeContentFlashcardPage;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Yves, Stefan
 */
public class FlashCardControllerSensei extends Controller {

    /**
     *
     * @return main FlashCardPage
     */
    @Security.Authenticated(SecurityStudent.class)
    public static Result flashCardsPage() {
        UserDataDBConnection userDataDBConnection    = new UserDataDBConnection();
        DocumentDBConnection documentDBConnection    = new DocumentDBConnection();
        FlashCardDBConnectionSenpai flashCardDBConnection   = new FlashCardDBConnectionSenpai();
        TagDBConnection tagDBConnection         = new TagDBConnection();
        User                    currentUser             = userDataDBConnection.getUserByEmail(session().get("email"));
        List<FlashCard>         flashCards              = flashCardDBConnection.getFlashCardsOfUser(currentUser.getEmail());
        List<Document>          bookmarkedDocs          = documentDBConnection.getBookmarkedDocuments(currentUser.getEmail());
        List<Tag>               tagList                 = tagDBConnection.getTagsOfUserGlobal(currentUser.getEmail());
        return ok(flashCardsPage.render(currentUser, flashCards, bookmarkedDocs, tagList));
    }

    /**
     *
     * @param flashCardID ID of flashCard that will be shown
     * @return page
     */
    @Security.Authenticated(SecurityStudent.class)
    public static Result flashCardPage(int flashCardID, boolean edit) {
        FlashCardDBConnectionSenpai flashCardDBConnectionSenpai = new FlashCardDBConnectionSenpai();
        FlashCard                   flashCard                   = flashCardDBConnectionSenpai.getFlashCardByID(flashCardID);

        if(flashCard != null){
            List<ChapterChan>                           chapters                = flashCardDBConnectionSenpai.getChaptersForFlashCard(flashCardID);
            HashMap<Integer, List<ContentHeadline> >    headlines               = new HashMap<>();
            HashMap<Integer, List<ContentSubHeadline>>  subHeadlines            = new HashMap<>();
            HashMap<Integer, List<Content>>             contentToChapters       = new HashMap<>();
            HashMap<Integer, List<Content>>             contentToHeadlines      = new HashMap<>();
            HashMap<Integer, List<Content>>             contentToSubHeadlines   = new HashMap<>();
            HashMap<Integer, List<Comment>>             comments                = new HashMap<>();
            HashMap<Integer, List<SubComment>>          subComments             = new HashMap<>();

            //creating HashMap for headlines, subHeadlines of all chapters and content of all of them
            for(ChapterChan chan: chapters) {
                List<Content>                   chapterContents = flashCardDBConnectionSenpai.getContentToChapterForFlashCard(chan.getChapterID()); //getting all content for chapter
                contentToChapters.put(chan.getChapterID(), chapterContents); //adding contentList to hashmap with chapterID as key

                List<ContentHeadline>           tmpHeadlines    = flashCardDBConnectionSenpai.getContentHeadlineToChapter(chan.getChapterID()); //getting all headlines to chapter
                headlines.put(chan.getChapterID(), tmpHeadlines); //adding headlineList to hashmap with chapterID as key

                for(ContentHeadline ch: tmpHeadlines) {
                    List<Content>               headlineContents= flashCardDBConnectionSenpai.getContentToHeadlineForFlashCard(ch.getcHeadLineID()); //getting all content to headline
                    contentToHeadlines.put(ch.getcHeadLineID(), headlineContents); //adding contentList to hashmap with headlineID as key

                    List<ContentSubHeadline>    tmpSubHeadlines = flashCardDBConnectionSenpai.getContentSubHeadlineToHeadline(ch.getcHeadLineID()); //getting all subHeadlines to headline
                    subHeadlines.put(ch.getcHeadLineID(), tmpSubHeadlines); // adding subHeadlineList to hashmap with headlineID as key

                    for(ContentSubHeadline  chs: tmpSubHeadlines) {
                        List<Content>           subHeadlineContents = flashCardDBConnectionSenpai.getContentToSubHeadlineForFlashCard(chs.getcSubHeadLineID()); //getting als content to subheadline
                        contentToSubHeadlines.put(chs.getcSubHeadLineID(), subHeadlineContents); //adding contentList to hashmap with subHeadlineID as key
                    }
                }
            }

            fillCommentAndSubCommentHM(flashCardDBConnectionSenpai, comments, subComments, contentToChapters);
            fillCommentAndSubCommentHM(flashCardDBConnectionSenpai, comments, subComments, contentToHeadlines);
            fillCommentAndSubCommentHM(flashCardDBConnectionSenpai, comments, subComments, contentToSubHeadlines);

            String email    = session().get("email");
            int role        = Integer.parseInt(session().get("role"));
            if(edit){
                //needed for suggestion
                DocumentDBConnection documentDBConnection                   = new DocumentDBConnection();
                ContentDBConnection contentDBConnection                     = new ContentDBConnection();
                Document                document                            = documentDBConnection.getDocumentByID(flashCard.getDocID());
                HashMap<Integer, List<ContentHeadline>> headlinesDoc        = new HashMap<>();
                HashMap<Integer, List<ContentSubHeadline>> subHeadlinesDoc  = new HashMap<>();
                List<ChapterChan> chaptersDoc                               = new LinkedList<>();
                if(document != null) {
                    chaptersDoc = contentDBConnection.getChaptersToDoc(document.getDocumentID());


                    //creating HashMap for headlines, subHeadlines of all chapters and content of all of them
                    for (ChapterChan chan : chaptersDoc) {
                        List<ContentHeadline> tmpHeadlines = contentDBConnection.getContentHeadLinesToChapter(chan.getChapterID()); //getting all headlines to chapter
                        headlinesDoc.put(chan.getChapterID(), tmpHeadlines); //adding headlineList to hashmap with chapterID as key

                        for (ContentHeadline ch : tmpHeadlines) {
                            List<ContentSubHeadline> tmpSubHeadlines = contentDBConnection.getContentSubHeadLinesToHeadline(ch.getcHeadLineID()); //getting all subHeadlines to headline
                            subHeadlinesDoc.put(ch.getcHeadLineID(), tmpSubHeadlines); // adding subHeadlineList to hashmap with headlineID as key

                        }
                    }
                }

                return ok(flashCardEditPage.render(
                        flashCard,
                        chapters, headlines, subHeadlines,
                        contentToChapters, contentToHeadlines, contentToSubHeadlines,
                        comments, subComments,
                        chaptersDoc, headlinesDoc, subHeadlinesDoc,
                        email, role, false));
            }
            return ok(flashCardPage.render(
                    flashCard,
                    chapters, headlines, subHeadlines,
                    contentToChapters, contentToHeadlines, contentToSubHeadlines,
                    comments, subComments,
                    email, role, false));
        }else{
            return notFound("FlashCard not found");
        }
    }

    public static Result updateFlashCard(int flashCardID){
        FlashCardDBConnectionSenpai flashCardDBConnectionSenpai = new FlashCardDBConnectionSenpai();
        FlashCard                   flashCard                   = flashCardDBConnectionSenpai.getFlashCardByID(flashCardID);
        if(flashCard != null){
            //getting all content of flashCard to prevent update form adding already existing stuff
            List<ChapterChan>                           chapters                = flashCardDBConnectionSenpai.getChaptersForFlashCard(flashCardID);
            HashMap<Integer, List<ContentHeadline> >    headlines               = new HashMap<>();
            HashMap<Integer, List<ContentSubHeadline>>  subHeadlines            = new HashMap<>();
            HashMap<Integer, List<Content>>             contentToChapters       = new HashMap<>();
            HashMap<Integer, List<Content>>             contentToHeadlines      = new HashMap<>();
            HashMap<Integer, List<Content>>             contentToSubHeadlines   = new HashMap<>();
            HashMap<Integer, List<Comment>>             comments                = new HashMap<>();
            HashMap<Integer, List<SubComment>>          subComments             = new HashMap<>();
            List<Integer>   chapterIDs      = new LinkedList<>();
            List<Integer>   headlineIDs     = new LinkedList<>();
            List<Integer>   subHeadlineIDs  = new LinkedList<>();
            List<Integer>   contentIDs      = new LinkedList<>();
            List<Integer>   commentIDs      = new LinkedList<>();
            List<Integer>   subCommentIDs   = new LinkedList<>();

            //creating HashMap for headlines, subHeadlines of all chapters and content of all of them
            for(ChapterChan chan: chapters) {
                chapterIDs.add(chan.getId());//adding ID of chapter to chapterID list
                List<Content>                   chapterContents = flashCardDBConnectionSenpai.getContentToChapterForFlashCard(chan.getChapterID()); //getting all content for chapter
                chapterContents.forEach(content -> contentIDs.add(content.getId()));//adding all IDs of contents of chapter to contentID list
                contentToChapters.put(chan.getChapterID(), chapterContents); //adding contentList to hashmap with chapterID as key

                List<ContentHeadline>           tmpHeadlines    = flashCardDBConnectionSenpai.getContentHeadlineToChapter(chan.getChapterID()); //getting all headlines to chapter
                headlines.put(chan.getChapterID(), tmpHeadlines); //adding headlineList to hashmap with chapterID as key

                for(ContentHeadline ch: tmpHeadlines) {
                    headlineIDs.add(ch.getId());//adding ID of headline to headlineID list
                    List<Content>               headlineContents= flashCardDBConnectionSenpai.getContentToHeadlineForFlashCard(ch.getcHeadLineID()); //getting all content to headline
                    headlineContents.forEach(content -> contentIDs.add(content.getId()));//adding all IDs of contents of headline to contentID list
                    contentToHeadlines.put(ch.getcHeadLineID(), headlineContents); //adding contentList to hashmap with headlineID as key

                    List<ContentSubHeadline>    tmpSubHeadlines = flashCardDBConnectionSenpai.getContentSubHeadlineToHeadline(ch.getcHeadLineID()); //getting all subHeadlines to headline
                    subHeadlines.put(ch.getcHeadLineID(), tmpSubHeadlines); // adding subHeadlineList to hashmap with headlineID as key

                    for(ContentSubHeadline  chs: tmpSubHeadlines) {
                        subHeadlineIDs.add(chs.getId());//adding ID of subHeadline to subHeadlineID list
                        List<Content>           subHeadlineContents = flashCardDBConnectionSenpai.getContentToSubHeadlineForFlashCard(chs.getcSubHeadLineID()); //getting als content to subheadline
                        subHeadlineContents.forEach(content -> contentIDs.add(content.getId()));//adding all IDs of contents of subHeadline to contentID list
                        contentToSubHeadlines.put(chs.getcSubHeadLineID(), subHeadlineContents); //adding contentList to hashmap with subHeadlineID as key
                    }
                }
            }

            fillCommentAndSubCommentHM(flashCardDBConnectionSenpai, comments, subComments, contentToChapters, commentIDs, subCommentIDs);
            fillCommentAndSubCommentHM(flashCardDBConnectionSenpai, comments, subComments, contentToHeadlines, commentIDs, subCommentIDs);
            fillCommentAndSubCommentHM(flashCardDBConnectionSenpai, comments, subComments, contentToSubHeadlines, commentIDs, subCommentIDs);

            for(int id: contentIDs){
                System.out.println("CONTENT ID: " + id);
            }

            for(int id: chapterIDs){
                System.out.println("CHAPTER ID: " + id);
            }

            for(int id: headlineIDs){
                System.out.println("HEADLINE ID: " + id);
            }

            for(int id: subHeadlineIDs){
                System.out.println("SUBHEADLINE ID " + id);
            }
            //now adding all new stuff to flashCard by doing the same as if it was a new flashCard and checking if content is already existing
            TagDBConnection             tdbc                    = new TagDBConnection();
            ContentDBConnection         cdbc                    = new ContentDBConnection();
            CommentDBConnection         codbc                   = new CommentDBConnection();
            FlashCardDBConnectionSenpai fcdbc                   = new FlashCardDBConnectionSenpai();
            int                         tagID                   = flashCard.getTagID();
            int                         docID                   = flashCard.getDocID();
            List<ChapterChan>           taggedChapters          = tdbc.getTaggedChapterChan(tagID);
            List<Comment>               taggedComments          = tdbc.getTaggedComments(tagID);
            List<SubComment>            taggedSubComments       = tdbc.getTaggedSubComments(tagID);
            List<ContentHeadline>       taggedHeadlines         = tdbc.getTaggedContentHeadlines(tagID);
            List<ContentSubHeadline>    taggedSubHeadline       = tdbc.getTaggedContentSubHeadline(tagID);
            List<ChapterChan>           allChaptersInDoc        = cdbc.getChaptersToDoc(docID);
            List<ContentHeadline>       allHeadlinesInDoc       = allChaptersInDoc      .stream()
                    .map(c -> cdbc.getContentHeadLinesToChapter(c.getChapterID()))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            System.out.println("allChaptersInDoc: " + allChaptersInDoc);
            List<Integer>               allHeadlinesInDocIDs    = allHeadlinesInDoc     .stream()
                    .map(ContentHeadline::getcHeadLineID)
                    .collect(Collectors.toList());
            System.out.println("allHeadlinesInDoc: " + allHeadlinesInDoc);

            List<ContentSubHeadline>    allSubHeadlinesInDoc    = allHeadlinesInDoc     .stream()
                    .map(h -> cdbc.getContentSubHeadLinesToHeadline(h.getcHeadLineID()))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            System.out.println("allSubHeadlinesInDoc: " + allSubHeadlinesInDoc);

            List<Integer>               allSubHeadlinesInDocIDs = allSubHeadlinesInDoc  .stream()
                    .map(ContentSubHeadline::getcSubHeadLineID)
                    .collect(Collectors.toList());
            System.out.println("allSubHeadlinesInDocIDs: " + allSubHeadlinesInDocIDs);
            List<Integer>               allContentOfChaInDocIDs = allChaptersInDoc      .stream()
                    .map(c -> cdbc.getContentToChapter(c.getChapterID()))
                    .flatMap(Collection::stream)
                    .map(Content::getContentID)
                    .collect(Collectors.toList());
            System.out.println("allContentOfChaInDocIDs: " + allContentOfChaInDocIDs);

            List<Integer>               allContentOfHeaInDocIDs = allHeadlinesInDoc     .stream()
                    .map(h -> cdbc.getContentToContentHeadline(h.getcHeadLineID()))
                    .flatMap(Collection::stream)
                    .map(Content::getContentID)
                    .collect(Collectors.toList());
            System.out.println("allContentOfHeaInDocIDs: " + allContentOfHeaInDocIDs);

            List<Integer>               allContentOfSubInDocIDs = allSubHeadlinesInDoc  .stream()
                    .map(sh -> cdbc.getContentToContentSubHeadline(sh.getcSubHeadLineID()))
                    .flatMap(Collection::stream)
                    .map(Content::getContentID)
                    .collect(Collectors.toList());
            System.out.println("allContentOfSubInDocIDs: " + allContentOfSubInDocIDs);

            List<ChapterChan>           taggedChaptersInDoc     = taggedChapters        .stream()
                    .filter(c -> c.getDocID() == docID)
                    .collect(Collectors.toList());
            System.out.println("taggedChaptersInDoc: " + taggedChaptersInDoc);

            List<ContentHeadline>       taggedHeadlinesInDoc    = taggedHeadlines       .stream()
                    .filter(h -> allHeadlinesInDocIDs.contains(h.getcHeadLineID()))
                    .collect(Collectors.toList());
            System.out.println("taggedHeadlinesInDoc: " + taggedHeadlinesInDoc);

            List<ContentSubHeadline>    taggedSubHeadlineInDoc  = taggedSubHeadline     .stream()
                    .filter(sh -> allSubHeadlinesInDocIDs.contains(sh.getcSubHeadLineID()))
                    .collect(Collectors.toList());
            System.out.println("taggedSubHeadlineInDoc: " + taggedSubHeadlineInDoc);

            List<Comment>               taggedCommentsInDoc     = taggedComments        .stream()
                    .filter(c -> allContentOfChaInDocIDs.contains(c.getContentID()) || allContentOfHeaInDocIDs.contains(c.getContentID()) || allContentOfSubInDocIDs.contains(c.getContentID()))
                    .collect(Collectors.toList());
            System.out.println("taggedCommentsInDoc: " + taggedCommentsInDoc);

            List<SubComment>            taggedSubCommentsInDoc  = taggedSubComments     .stream()
                    .filter(sc ->   {
                        int commentID = codbc.getCommentByID(sc.getCommentID()).getContentID();
                        return allContentOfChaInDocIDs.contains(commentID) || allContentOfHeaInDocIDs.contains(commentID) || allContentOfSubInDocIDs.contains(commentID);

                    })
                    .collect(Collectors.toList());
            System.out.println("taggedSubCommentsInDoc: " + taggedSubCommentsInDoc);

            List<Comment>               commentsToSubComments   = taggedSubComments     .stream()
                    .map(sc -> codbc.getCommentByID(sc.getCommentID()))
                    .collect(Collectors.toList());
            System.out.println("commentsToSubComments: " + commentsToSubComments);

            List<Content>               contentsOfChapters      = taggedChaptersInDoc   .stream()
                    .map(c -> cdbc.getContentToChapter(c.getChapterID()))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            System.out.println("contentsOfChapters: " + contentsOfChapters);

            List<Content>               contentsOfHeadlines     = taggedHeadlinesInDoc  .stream()
                    .map(h -> cdbc.getContentToContentHeadline(h.getcHeadLineID()))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            System.out.println("contentsOfHeadlines: " + contentsOfHeadlines);

            List<Content>               contentsOfSubHeadlines  = taggedSubHeadlineInDoc.stream()
                    .map(sh -> cdbc.getContentToContentSubHeadline(sh.getcSubHeadLineID()))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            System.out.println("contentsOfSubHeadlines: " + contentsOfSubHeadlines);

            List<Content>               contentsToComments      = taggedCommentsInDoc   .stream()
                    .map(c -> cdbc.getContentByID(c.getContentID()))
                    .collect(Collectors.toList());
            System.out.println("contentsToComments: " + contentsToComments);

            List<Content>               contentsToSubComments   = taggedSubCommentsInDoc.stream()
                    .map(sc -> cdbc.getContentByID(codbc.getCommentByID(sc.getCommentID()).getContentID()))
                    .collect(Collectors.toList());
            System.out.println("contentsToSubComments: " + contentsToSubComments);

            List<Content>               contentsToCommentsFCha  = contentsToComments    .stream()
                    .filter(c -> c.getChapterID() != -1)
                    .collect(Collectors.toList());
            System.out.println("contentsToCommentsFCha: " + contentsToCommentsFCha);

            List<Content>               contentsToCommentsFHead = contentsToComments    .stream()
                    .filter(c -> c.getcHeadLineID() != -1)
                    .collect(Collectors.toList());
            System.out.println("contentsToCommentsFHead: " + contentsToCommentsFHead);

            List<Content>               contentsToCommentsFSub  = contentsToComments    .stream()
                    .filter(c -> c.getcSubHeadLineID() != -1)
                    .collect(Collectors.toList());
            System.out.println("contentsToCommentsFSub: " + contentsToCommentsFSub);

            List<Content>               contentsToSubCommentsFCh = contentsToSubComments.stream()
                    .filter(c -> c.getChapterID() != -1)
                    .collect(Collectors.toList());
            System.out.println("contentsToSubCommentsFCh: " + contentsToSubCommentsFCh);

            List<Content>               contentsToSubCommentsFHd = contentsToSubComments.stream()
                    .filter(c -> c.getcHeadLineID() != -1)
                    .collect(Collectors.toList());
            System.out.println("contentsToSubCommentsFHd: " + contentsToSubCommentsFHd);

            List<Content>               contentsToSubCommentsFSh = contentsToSubComments.stream()
                    .filter(c -> c.getcSubHeadLineID() != -1)
                    .collect(Collectors.toList());
            System.out.println("contentsToSubCommentsFSh: " + contentsToSubCommentsFSh);

            HashSet<Content>            allContentsOfChapters   = new HashSet<>();
            allContentsOfChapters.addAll(contentsOfChapters);
            allContentsOfChapters.addAll(contentsToCommentsFCha);
            allContentsOfChapters.addAll(contentsToSubCommentsFCh);
            System.out.println("allContentsOfChapters: " + allContentsOfChapters);

            HashSet<Content>            allContentsOfHeadlines  = new HashSet<>();
            allContentsOfHeadlines.addAll(contentsOfHeadlines);
            allContentsOfHeadlines.addAll(contentsToCommentsFHead);
            allContentsOfHeadlines.addAll(contentsToSubCommentsFHd);
            System.out.println("allContentsOfHeadlines: " + allContentsOfHeadlines);

            HashSet<Content>            allContentsOfShline     = new HashSet<>();
            allContentsOfShline.addAll(contentsOfSubHeadlines);
            allContentsOfShline.addAll(contentsToCommentsFSub);
            allContentsOfShline.addAll(contentsToSubCommentsFSh);
            System.out.println("allContentsOfShline: " + allContentsOfShline);

            List<ContentSubHeadline>    allSubHeadlines2         = allContentsOfShline   .stream()
                    .map(c -> cdbc.getContentSubHeadLineByID(c.getcSubHeadLineID()))
                    .collect(Collectors.toList());
            System.out.println("allSubHeadlines: " + allSubHeadlines2);

            HashSet<ContentSubHeadline> allSubHeadlines        = new HashSet<>();
            allSubHeadlines.addAll(allSubHeadlines2);

            List<ContentHeadline>       almostallHeadlines      = allContentsOfHeadlines.stream()
                    .map(c -> cdbc.getContentHeadLineByID(c.getcHeadLineID()))
                    .collect(Collectors.toList());
            System.out.println("almostallHeadlines: " + almostallHeadlines);

            List<ContentHeadline>       almostallHeadlines2     = allSubHeadlines       .stream()
                    .map(sh -> cdbc.getContentHeadLineByID(sh.getCheadlineID()))
                    .collect(Collectors.toList());
            System.out.println("almostallHeadlines2: " + almostallHeadlines2);

            HashSet<ContentHeadline>    allHeadlines            = new HashSet<>();
            allHeadlines.addAll(almostallHeadlines);
            allHeadlines.addAll(almostallHeadlines2);
            allHeadlines.addAll(taggedHeadlinesInDoc);
            System.out.println("allHeadlines: " + allHeadlines);

            List<ChapterChan>           almostAllChapters       = allContentsOfChapters .stream()
                    .map(c -> cdbc.getChapterByID(c.getChapterID()))
                    .collect(Collectors.toList());
            System.out.println("almostAllChapters: " + almostAllChapters);

            List<ChapterChan>           almostAllChapters2      = allHeadlines          .stream()
                    .map(h -> cdbc.getChapterByID(h.getChapterID()))
                    .collect(Collectors.toList());
            System.out.println("allmostAllChapters2: " + almostAllChapters2);

            HashSet<ChapterChan>        allChapters             = new HashSet<>();
            allChapters.addAll(almostAllChapters);
            allChapters.addAll(almostAllChapters2);
            allChapters.addAll(taggedChaptersInDoc);
            System.out.println("allChapters: " + allChapters);

            HashSet<Comment>            allComments             = new HashSet<>();
            allComments.addAll(taggedCommentsInDoc);
            allComments.addAll(commentsToSubComments);
            System.out.println("allComments: " + allComments);
            //adding everything to flashCard
            Connection connection   = DB.getConnection();
            try{
                connection.setAutoCommit(false);
            }catch(Exception e){
                e.printStackTrace();
            }
            int fcid = flashCardID;
            HashMap<Integer, Integer>   chaptersToFChapters     = new HashMap<>();
            for(ChapterChan c: allChapters){
                if(!alreadyExists(c.getChapterID(), chapterIDs)) {
                    fcdbc.addFlashCardChapter(fcid, c.getChapterID(), connection);
                }
                chaptersToFChapters.put(c.getChapterID(), fcdbc.getFChapterToChapter(fcid, c.getChapterID()));
            }
            System.out.println("chaptersToFChapters: " + chaptersToFChapters);

            HashMap<Integer, Integer>   headlinesToFHeadlines   = new HashMap<>();
            for(ContentHeadline h: allHeadlines){
                System.out.println("ID OF HEADLINE: " + h.getcHeadLineID());
                if(!alreadyExists(h.getcHeadLineID(), headlineIDs)) {
                    fcdbc.addFlashCardContentHeadline(fcid, h.getcHeadLineID(), chaptersToFChapters.get(h.getChapterID()), connection);
                }
                headlinesToFHeadlines.put(h.getcHeadLineID(), fcdbc.getFHeadlineToHeadline(flashCardID, h.getcHeadLineID()));
            }
            System.out.println("headlinesToFHeadlines: " + headlinesToFHeadlines);

            HashMap<Integer, Integer>   subhlinesToFSubheadlines = new HashMap<>();
            for(ContentSubHeadline sh: allSubHeadlines){
                if(!alreadyExists(sh.getcSubHeadLineID(), subHeadlineIDs)) {
                    fcdbc.addFlashCardContentSubHeadline(fcid, sh.getcSubHeadLineID(), headlinesToFHeadlines.get(sh.getCheadlineID()), connection);
                }
                subhlinesToFSubheadlines.put(sh.getcSubHeadLineID(), fcdbc.getFSubHeadlineToSubHeadline(flashCardID, sh.getcSubHeadLineID()));
            }
            System.out.println("subhlinesToFSubheadlines: " + subhlinesToFSubheadlines);

            HashMap<Integer, Integer>   contentToFContent        = new HashMap<>();
            for(Content c: allContentsOfChapters){
                System.out.println("ID OF CONTENT: " + c.getContentID());
                if(!alreadyExists(c.getContentID(), contentIDs)) {
                    System.out.println("adding content of chapters");
                    fcdbc.addFlashCardContent(fcid, c.getContentID(), chaptersToFChapters.get(c.getChapterID()), connection);
                }
                contentToFContent.put(c.getContentID(), fcdbc.getFContentToContent(flashCardID, c.getContentID()));
            }
            System.out.println("contentToFContent: " + contentToFContent);

            for(Content c: allContentsOfHeadlines){
                System.out.println("ID OF CONTENT: " + c.getContentID());
                if(!alreadyExists(c.getContentID(), contentIDs)) {
                    System.out.println("adding content of headlines");
                    fcdbc.addFlashCardContent(fcid, c.getContentID(), headlinesToFHeadlines.get(c.getcHeadLineID()), connection);
                }
                contentToFContent.put(c.getContentID(), fcdbc.getFContentToContent(flashCardID, c.getContentID()));
            }

            for(Content c: allContentsOfShline){
                if(!alreadyExists(c.getContentID(), contentIDs)) {
                    System.out.println("adding content of subheadlines");
                    fcdbc.addFlashCardContent(fcid, c.getContentID(), subhlinesToFSubheadlines.get(c.getcSubHeadLineID()), connection);
                }
                contentToFContent.put(c.getContentID(), fcdbc.getFContentToContent(flashCardID, c.getContentID()));
            }

            HashMap<Integer, Integer>   commentsToFComments         = new HashMap<>();
            for(Comment c: allComments){
                if(!alreadyExists(c.getCommentID(), commentIDs)) {
                    System.out.println(contentToFContent.get(c.getContentID()));
                    fcdbc.addFlashCardComment(fcid, c.getCommentID(), contentToFContent.get(c.getContentID()), connection);
                }
                commentsToFComments.put(c.getCommentID(), fcdbc.getFCommentToComment(flashCardID, c.getCommentID()));
            }
            System.out.println("commentsToFComments: " + commentsToFComments);

            for(SubComment sc: taggedSubCommentsInDoc){
                if(!alreadyExists(sc.getSubCommentID(), subCommentIDs)){
                    fcdbc.addFlashCardSubComment(fcid, sc.getSubCommentID(), commentsToFComments.get(sc.getCommentID()), connection);
                }
            }
            try {
                connection.commit();
            }catch(Exception e){
                e.printStackTrace();
            }

            return(redirect(routes.FlashCardControllerSensei.flashCardPage(flashCardID, true)));
        }
        return notFound("FlashCard not found");
    }

    private static boolean alreadyExists(int id, List<Integer> idList){
        for(int i: idList){
            if(i == id)
                return true;
        }
        return false;
    }

    /**
     *
     * fills hashMaps for comments and subComments with all comments and subComments related to content in given hashMap and adds IDs to referring lists
     * @param fdb flashCardDBConnectionSenpai for database access
     * @param comments commentsHM to fill
     * @param subComments subCommentsHM to fill
     * @param contentHM related contentHM
     * @param commentIDs list of IDs of comments in flashCard
     * @param subCommentIDs list of IDs of subComments in flashCard
     */
    private static void fillCommentAndSubCommentHM(FlashCardDBConnectionSenpai fdb, HashMap<Integer, List<Comment>> comments, HashMap<Integer, List<SubComment>> subComments, HashMap<Integer, List<Content>> contentHM, List<Integer> commentIDs, List<Integer> subCommentIDs){
        Set<Integer>    keys    = contentHM.keySet();
        //for content of chapters
        for(Integer i: keys){
            for(Content c: contentHM.get(i)){
                List<Comment> tmp   = fdb.getCommentForContent(c.getContentID());
                tmp.forEach(comment -> commentIDs.add(comment.getId()));//adding all commentIDs to commentID list
                comments.put(c.getContentID(), tmp);
                for(Comment co: tmp){
                    List<SubComment> tmpSubComments = fdb.getSubCommentForComment(co.getCommentID());
                    tmpSubComments.forEach(subComment -> subCommentIDs.add(subComment.getId()));
                    subComments.put(co.getCommentID(), tmpSubComments);
                }
            }
        }
    }

    /**
     * fills hashMaps for comments and subComments with all comments and subComments related to content in given hashMap
     * @param fdb flashCardDBConnectionSenpai for database access
     * @param comments commentsHM to fill
     * @param subComments subCommentsHM to fill
     * @param contentHM related contentHM
     */
    private static void fillCommentAndSubCommentHM(FlashCardDBConnectionSenpai fdb, HashMap<Integer, List<Comment>> comments, HashMap<Integer, List<SubComment>> subComments, HashMap<Integer, List<Content>> contentHM){
        Set<Integer>    keys    = contentHM.keySet();
        //for content of chapters
        for(Integer i: keys){
            for(Content c: contentHM.get(i)){
                List<Comment> tmp   = fdb.getCommentForContent(c.getContentID());
                comments.put(c.getContentID(), tmp);
                for(Comment co: tmp){
                    subComments.put(co.getCommentID(), fdb.getSubCommentForComment(co.getCommentID()));
                }
            }
        }
    }

    /**
     *
     * @param docID ID of the document on which the FlashCard will be based
     * @param tagID ID of the tag on which the FlashCard will be based
     * @return flashCardsPage
     */
    @Security.Authenticated(SecurityStudent.class)
    public static Result createFlashCard(int docID, int tagID) {

        String fcname = request().body().asFormUrlEncoded().get("flashcardname")[0];
        if(fcname != null && fcname.length() > 0){
            TagDBConnection             tdbc                    = new TagDBConnection();
            ContentDBConnection         cdbc                    = new ContentDBConnection();
            CommentDBConnection         codbc                   = new CommentDBConnection();
            FlashCardDBConnectionSenpai fcdbc                   = new FlashCardDBConnectionSenpai();
            List<ChapterChan>           taggedChapters          = tdbc.getTaggedChapterChan(tagID);
            List<Comment>               taggedComments          = tdbc.getTaggedComments(tagID);
            List<SubComment>            taggedSubComments       = tdbc.getTaggedSubComments(tagID);
            List<ContentHeadline>       taggedHeadlines         = tdbc.getTaggedContentHeadlines(tagID);
            List<ContentSubHeadline>    taggedSubHeadline       = tdbc.getTaggedContentSubHeadline(tagID);
            List<ChapterChan>           allChaptersInDoc        = cdbc.getChaptersToDoc(docID);
            List<ContentHeadline>       allHeadlinesInDoc       = allChaptersInDoc      .stream()
                    .map(c -> cdbc.getContentHeadLinesToChapter(c.getChapterID()))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            System.out.println("allChaptersInDoc: " + allChaptersInDoc);
            List<Integer>               allHeadlinesInDocIDs    = allHeadlinesInDoc     .stream()
                    .map(ContentHeadline::getcHeadLineID)
                    .collect(Collectors.toList());
            System.out.println("allHeadlinesInDoc: " + allHeadlinesInDoc);

            List<ContentSubHeadline>    allSubHeadlinesInDoc    = allHeadlinesInDoc     .stream()
                    .map(h -> cdbc.getContentSubHeadLinesToHeadline(h.getcHeadLineID()))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            System.out.println("allSubHeadlinesInDoc: " + allSubHeadlinesInDoc);

            List<Integer>               allSubHeadlinesInDocIDs = allSubHeadlinesInDoc  .stream()
                    .map(ContentSubHeadline::getcSubHeadLineID)
                    .collect(Collectors.toList());
            System.out.println("allSubHeadlinesInDocIDs: " + allSubHeadlinesInDocIDs);
            List<Integer>               allContentOfChaInDocIDs = allChaptersInDoc      .stream()
                    .map(c -> cdbc.getContentToChapter(c.getChapterID()))
                    .flatMap(Collection::stream)
                    .map(Content::getContentID)
                    .collect(Collectors.toList());
            System.out.println("allContentOfChaInDocIDs: " + allContentOfChaInDocIDs);

            List<Integer>               allContentOfHeaInDocIDs = allHeadlinesInDoc     .stream()
                    .map(h -> cdbc.getContentToContentHeadline(h.getcHeadLineID()))
                    .flatMap(Collection::stream)
                    .map(Content::getContentID)
                    .collect(Collectors.toList());
            System.out.println("allContentOfHeaInDocIDs: " + allContentOfHeaInDocIDs);

            List<Integer>               allContentOfSubInDocIDs = allSubHeadlinesInDoc  .stream()
                    .map(sh -> cdbc.getContentToContentSubHeadline(sh.getcSubHeadLineID()))
                    .flatMap(Collection::stream)
                    .map(Content::getContentID)
                    .collect(Collectors.toList());
            System.out.println("allContentOfSubInDocIDs: " + allContentOfSubInDocIDs);

            List<ChapterChan>           taggedChaptersInDoc     = taggedChapters        .stream()
                    .filter(c -> c.getDocID() == docID)
                    .collect(Collectors.toList());
            System.out.println("taggedChaptersInDoc: " + taggedChaptersInDoc);

            List<ContentHeadline>       taggedHeadlinesInDoc    = taggedHeadlines       .stream()
                    .filter(h -> allHeadlinesInDocIDs.contains(h.getcHeadLineID()))
                    .collect(Collectors.toList());
            System.out.println("taggedHeadlinesInDoc: " + taggedHeadlinesInDoc);

            List<ContentSubHeadline>    taggedSubHeadlineInDoc  = taggedSubHeadline     .stream()
                    .filter(sh -> allSubHeadlinesInDocIDs.contains(sh.getcSubHeadLineID()))
                    .collect(Collectors.toList());
            System.out.println("taggedSubHeadlineInDoc: " + taggedSubHeadlineInDoc);

            List<Comment>               taggedCommentsInDoc     = taggedComments        .stream()
                    .filter(c -> allContentOfChaInDocIDs.contains(c.getContentID()) || allContentOfHeaInDocIDs.contains(c.getContentID()) || allContentOfSubInDocIDs.contains(c.getContentID()))
                    .collect(Collectors.toList());
            System.out.println("taggedCommentsInDoc: " + taggedCommentsInDoc);

            List<SubComment>            taggedSubCommentsInDoc  = taggedSubComments     .stream()
                    .filter(sc ->   {
                        int commentID = codbc.getCommentByID(sc.getCommentID()).getContentID();
                        return allContentOfChaInDocIDs.contains(commentID) || allContentOfHeaInDocIDs.contains(commentID) || allContentOfSubInDocIDs.contains(commentID);

                    })
                    .collect(Collectors.toList());
            System.out.println("taggedSubCommentsInDoc: " + taggedSubCommentsInDoc);

            List<Comment>               commentsToSubComments   = taggedSubComments     .stream()
                    .map(sc -> codbc.getCommentByID(sc.getCommentID()))
                    .collect(Collectors.toList());
            System.out.println("commentsToSubComments: " + commentsToSubComments);

            List<Content>               contentsOfChapters      = taggedChaptersInDoc   .stream()
                    .map(c -> cdbc.getContentToChapter(c.getChapterID()))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            System.out.println("contentsOfChapters: " + contentsOfChapters);

            List<Content>               contentsOfHeadlines     = taggedHeadlinesInDoc  .stream()
                    .map(h -> cdbc.getContentToContentHeadline(h.getcHeadLineID()))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            System.out.println("contentsOfHeadlines: " + contentsOfHeadlines);

            List<Content>               contentsOfSubHeadlines  = taggedSubHeadlineInDoc.stream()
                    .map(sh -> cdbc.getContentToContentSubHeadline(sh.getcSubHeadLineID()))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            System.out.println("contentsOfSubHeadlines: " + contentsOfSubHeadlines);

            List<Content>               contentsToComments      = taggedCommentsInDoc   .stream()
                    .map(c -> cdbc.getContentByID(c.getContentID()))
                    .collect(Collectors.toList());
            System.out.println("contentsToComments: " + contentsToComments);

            List<Content>               contentsToSubComments   = taggedSubCommentsInDoc.stream()
                    .map(sc -> cdbc.getContentByID(codbc.getCommentByID(sc.getCommentID()).getContentID()))
                    .collect(Collectors.toList());
            System.out.println("contentsToSubComments: " + contentsToSubComments);

            List<Content>               contentsToCommentsFCha  = contentsToComments    .stream()
                    .filter(c -> c.getChapterID() != -1)
                    .collect(Collectors.toList());
            System.out.println("contentsToCommentsFCha: " + contentsToCommentsFCha);

            List<Content>               contentsToCommentsFHead = contentsToComments    .stream()
                    .filter(c -> c.getcHeadLineID() != -1)
                    .collect(Collectors.toList());
            System.out.println("contentsToCommentsFHead: " + contentsToCommentsFHead);

            List<Content>               contentsToCommentsFSub  = contentsToComments    .stream()
                    .filter(c -> c.getcSubHeadLineID() != -1)
                    .collect(Collectors.toList());
            System.out.println("contentsToCommentsFSub: " + contentsToCommentsFSub);

            List<Content>               contentsToSubCommentsFCh = contentsToSubComments.stream()
                    .filter(c -> c.getChapterID() != -1)
                    .collect(Collectors.toList());
            System.out.println("contentsToSubCommentsFCh: " + contentsToSubCommentsFCh);

            List<Content>               contentsToSubCommentsFHd = contentsToSubComments.stream()
                    .filter(c -> c.getcHeadLineID() != -1)
                    .collect(Collectors.toList());
            System.out.println("contentsToSubCommentsFHd: " + contentsToSubCommentsFHd);

            List<Content>               contentsToSubCommentsFSh = contentsToSubComments.stream()
                    .filter(c -> c.getcSubHeadLineID() != -1)
                    .collect(Collectors.toList());
            System.out.println("contentsToSubCommentsFSh: " + contentsToSubCommentsFSh);

            HashSet<Content>            allContentsOfChapters   = new HashSet<>();
            allContentsOfChapters.addAll(contentsOfChapters);
            allContentsOfChapters.addAll(contentsToCommentsFCha);
            allContentsOfChapters.addAll(contentsToSubCommentsFCh);
            System.out.println("allContentsOfChapters: " + allContentsOfChapters);

            HashSet<Content>            allContentsOfHeadlines  = new HashSet<>();
            allContentsOfHeadlines.addAll(contentsOfHeadlines);
            allContentsOfHeadlines.addAll(contentsToCommentsFHead);
            allContentsOfHeadlines.addAll(contentsToSubCommentsFHd);
            System.out.println("allContentsOfHeadlines: " + allContentsOfHeadlines);

            HashSet<Content>            allContentsOfShline     = new HashSet<>();
            allContentsOfShline.addAll(contentsOfSubHeadlines);
            allContentsOfShline.addAll(contentsToCommentsFSub);
            allContentsOfShline.addAll(contentsToSubCommentsFSh);
            System.out.println("allContentsOfShline: " + allContentsOfShline);

            List<ContentSubHeadline>    allSubHeadlines2         = allContentsOfShline   .stream()
                    .map(c -> cdbc.getContentSubHeadLineByID(c.getcSubHeadLineID()))
                    .collect(Collectors.toList());
            System.out.println("allSubHeadlines: " + allSubHeadlines2);

            HashSet<ContentSubHeadline> allSubHeadlines        = new HashSet<>();
            allSubHeadlines.addAll(allSubHeadlines2);

            List<ContentHeadline>       almostallHeadlines      = allContentsOfHeadlines.stream()
                    .map(c -> cdbc.getContentHeadLineByID(c.getcHeadLineID()))
                    .collect(Collectors.toList());
            System.out.println("almostallHeadlines: " + almostallHeadlines);

            List<ContentHeadline>       almostallHeadlines2     = allSubHeadlines       .stream()
                    .map(sh -> cdbc.getContentHeadLineByID(sh.getCheadlineID()))
                    .collect(Collectors.toList());
            System.out.println("almostallHeadlines2: " + almostallHeadlines2);

            HashSet<ContentHeadline>    allHeadlines            = new HashSet<>();
            allHeadlines.addAll(almostallHeadlines);
            allHeadlines.addAll(almostallHeadlines2);
            allHeadlines.addAll(taggedHeadlinesInDoc);
            System.out.println("allHeadlines: " + allHeadlines);

            List<ChapterChan>           almostAllChapters       = allContentsOfChapters .stream()
                    .map(c -> cdbc.getChapterByID(c.getChapterID()))
                    .collect(Collectors.toList());
            System.out.println("almostAllChapters: " + almostAllChapters);

            List<ChapterChan>           almostAllChapters2      = allHeadlines          .stream()
                    .map(h -> cdbc.getChapterByID(h.getChapterID()))
                    .collect(Collectors.toList());
            System.out.println("allmostAllChapters2: " + almostAllChapters2);

            HashSet<ChapterChan>        allChapters             = new HashSet<>();
            allChapters.addAll(almostAllChapters);
            allChapters.addAll(almostAllChapters2);
            allChapters.addAll(taggedChaptersInDoc);
            System.out.println("allChapters: " + allChapters);

            HashSet<Comment>            allComments             = new HashSet<>();
            allComments.addAll(taggedCommentsInDoc);
            allComments.addAll(commentsToSubComments);
            System.out.println("allComments: " + allComments);
            //adding everything to flashCard
            Connection connection   = DB.getConnection();
            try{
                connection.setAutoCommit(false);
            }catch(Exception e){
                e.printStackTrace();
            }
            int fcid = fcdbc.createFlashCard(tagID, session().get("email"), fcname, docID, connection);
            HashMap<Integer, Integer>   chaptersToFChapters     = new HashMap<>();
            for(ChapterChan c: allChapters){
                int i = fcdbc.addFlashCardChapter(fcid, c.getChapterID(), connection);
                chaptersToFChapters.put(c.getChapterID(), i);
            }
            System.out.println("chaptersToFChapters: " + chaptersToFChapters);

            HashMap<Integer, Integer>   headlinesToFHeadlines   = new HashMap<>();
            for(ContentHeadline h: allHeadlines){
                int i = fcdbc.addFlashCardContentHeadline(fcid, h.getcHeadLineID(), chaptersToFChapters.get(h.getChapterID()), connection);
                headlinesToFHeadlines.put(h.getcHeadLineID(), i);
            }
            System.out.println("headlinesToFHeadlines: " + headlinesToFHeadlines);

            HashMap<Integer, Integer>   subhlinesToFSubheadlines = new HashMap<>();
            for(ContentSubHeadline sh: allSubHeadlines){
                int i = fcdbc.addFlashCardContentSubHeadline(fcid, sh.getcSubHeadLineID(), headlinesToFHeadlines.get(sh.getCheadlineID()), connection);
                subhlinesToFSubheadlines.put(sh.getcSubHeadLineID(), i);
            }
            System.out.println("subhlinesToFSubheadlines: " + subhlinesToFSubheadlines);

            HashMap<Integer, Integer>   contentToFContent        = new HashMap<>();
            for(Content c: allContentsOfChapters){
                System.out.println("adding content of chapters");
                int i = fcdbc.addFlashCardContent(fcid, c.getContentID(), chaptersToFChapters.get(c.getChapterID()), connection);
                contentToFContent.put(c.getContentID(), i);
            }
            System.out.println("contentToFContent: " + contentToFContent);

            for(Content c: allContentsOfHeadlines){
                System.out.println("adding content of headlines");
                int i = fcdbc.addFlashCardContent(fcid, c.getContentID(), headlinesToFHeadlines.get(c.getcHeadLineID()), connection);
                contentToFContent.put(c.getContentID(), i);
            }

            for(Content c: allContentsOfShline){
                System.out.println("adding content of subheadlines");
                int i = fcdbc.addFlashCardContent(fcid, c.getContentID(), subhlinesToFSubheadlines.get(c.getcSubHeadLineID()), connection);
                contentToFContent.put(c.getContentID(), i);
            }

            HashMap<Integer, Integer>   commentsToFComments         = new HashMap<>();
            for(Comment c: allComments){
                System.out.println(contentToFContent.get(c.getContentID()));
                int i = fcdbc.addFlashCardComment(fcid,c.getCommentID(), contentToFContent.get(c.getContentID()), connection);
                commentsToFComments.put(c.getCommentID(), i);
            }
            System.out.println("commentsToFComments: " + commentsToFComments);

            for(SubComment sc: taggedSubCommentsInDoc){
                fcdbc.addFlashCardSubComment(fcid, sc.getSubCommentID(), commentsToFComments.get(sc.getCommentID()), connection);
            }
            try {
                connection.commit();
            }catch(Exception e){
                e.printStackTrace();
            }

        }
        return(redirect(routes.FlashCardControllerSensei.flashCardsPage()));
    }

    /**
     * deletes FlashCard and all related entries
     * @param flashCardID id of flashcard to delete
     * @return flashCards page
     */
    @Security.Authenticated(SecurityStudent.class)
    public static Result deleteFlashCard(int flashCardID) {
        FlashCardDBConnectionSenpai   flashCardDBConnection   = new FlashCardDBConnectionSenpai();
        flashCardDBConnection.deleteFlashCard(flashCardID);
        //flash("danger", "flashCard deleted");
        return redirect(routes.FlashCardControllerSensei.flashCardsPage());
    }

    /**
     * deletes given flashCardMember from flashCard
     * if kind could not be resolved do nothing
     * @param kind of element accepted kinds: 0 := chapter, 1 := comment, 2 := subComment, 3 := headline, 4 := subHeadline 5:= content
     * @param flashCardID ID of flashCard for redirect
     * @param id ID of element to delete
     * @return FlashCard edit Page
     */
    @Security.Authenticated(SecurityStudent.class)
    public static Result deleteFromFlashCard(int kind, int flashCardID, int id) {
        FlashCardDBConnectionSenpai flashCardDBConnectionSenpai = new FlashCardDBConnectionSenpai();
        if(kind == 0) {//checks list of other Objects of same type and decreases sequence if it was higher than the one that will be deleted
            ChapterChan         chapterToBeDeleted      = flashCardDBConnectionSenpai.getChapterByID(id);
            flashCardDBConnectionSenpai.getChaptersForFlashCard(flashCardID).stream().filter(chapter -> chapter.getSequence() > chapterToBeDeleted.getSequence()).forEach(chapter -> flashCardDBConnectionSenpai.changeFlashCardChapterSequence(chapter.getChapterID(), chapter.getSequence() - 1));
            flashCardDBConnectionSenpai.deleteFlashCardChapter(id);
        }else if(kind == 1) {
            flashCardDBConnectionSenpai.deleteFlashCardComment(id);
        }else if(kind == 2) {
            flashCardDBConnectionSenpai.deleteFlashCardSubComment(id);
        }else if(kind == 3) {
            ContentHeadline     headlineToBeDeleted     = flashCardDBConnectionSenpai.getHeadlineByID(id);
            flashCardDBConnectionSenpai.getContentHeadlineForFlashCard(flashCardID).stream().filter(headline -> headline.getSequence() > headlineToBeDeleted.getSequence()).forEach(headline -> flashCardDBConnectionSenpai.changeFlashCardContentHeadlineSequence(headline.getcHeadLineID(), (headline.getSequence() -1)));
            flashCardDBConnectionSenpai.deleteFlashCardContentHeadline(id);
        }else if(kind == 4) {
            ContentSubHeadline  subHeadlineToBeDeleted  = flashCardDBConnectionSenpai.getSubHeadlineByID(id);
            flashCardDBConnectionSenpai.getContentSubHeadlineForFlashCard(flashCardID).stream().filter(subHeadline -> subHeadline.getSequence() > subHeadlineToBeDeleted.getSequence()).forEach(subHeadline -> flashCardDBConnectionSenpai.changeFlashCardContentSubHeadlineSequence(subHeadline.getcSubHeadLineID(), (subHeadline.getSequence() -1)));
            flashCardDBConnectionSenpai.deleteFlashCardContentSubHeadline(id);
        }else if(kind == 5) {
            Content             contentToBeDeleted      = flashCardDBConnectionSenpai.getContentByID(id);
            flashCardDBConnectionSenpai.getContentToFlashCard(flashCardID).stream().filter(content -> content.getSequence() > contentToBeDeleted.getSequence()).forEach(content -> flashCardDBConnectionSenpai.changeFlashCardContentSequence(content.getContentID(), (content.getSequence() -1)));
            flashCardDBConnectionSenpai.deleteFlashCardContent(id);
        }

        return redirect(routes.FlashCardControllerSensei.flashCardPage(flashCardID, true));
    }

    //TODO: Look if something references addStuffToFlashcard and if not delete it.
    @Security.Authenticated(SecurityStudent.class)
    public static Result addStuffToFlashcard(int flashcardID) {
        return ok();
    }

    /**
     *
     * @param flashCardID ID of the flashCard to which the new chapter will be added
     * @return editFlashCardPage of the flashCard with flashCardID
     */
    @Security.Authenticated(SecurityStudent.class)
    public static Result addNewChapter(int flashCardID) {
        FlashCardDBConnectionSenpai fcdbc       = new FlashCardDBConnectionSenpai();

        ChapterChan                 cc          = Form.form(ChapterChan.class).bindFromRequest().get();

        List<ChapterChan>           chapters    = fcdbc.getChaptersForFlashCard(flashCardID);

        chapters.stream().filter(c -> c.getSequence() >= cc.getSequence()).forEach(c -> fcdbc.changeFlashCardChapterSequence(c.getChapterID(), (c.getSequence() + 1)));

        fcdbc.addNewChapter(flashCardID, cc.getTitle(), cc.getSequence());

        return redirect(routes.FlashCardControllerSensei.flashCardPage(flashCardID, true));
    }

    /**
     *
     * @param flashCardID ID of the flashCard to which the new headline will be added
     * @param fChapterID ID of the chapter to which the new healine will be added
     * @return editFlashCardPage of flashCard with flashCardID
     */
    @Security.Authenticated(SecurityStudent.class)
    public static Result addNewHeadline(int flashCardID, int fChapterID) {
        FlashCardDBConnectionSenpai     fcdbc       = new FlashCardDBConnectionSenpai();

        ContentHeadline                 ch          = Form.form(ContentHeadline.class).bindFromRequest().get();

        List<ContentHeadline>           headlines   = fcdbc.getContentHeadlineToChapter(fChapterID);

        headlines.stream().filter(h -> h.getSequence() >= ch.getSequence()).forEach(h -> fcdbc.changeFlashCardContentHeadlineSequence(h.getcHeadLineID(), (h.getSequence() + 1)));

        fcdbc.addNewContentHeadline(flashCardID, fChapterID, ch.getTitle(), ch.getSequence());
        return redirect(routes.FlashCardControllerSensei.flashCardPage(flashCardID, true));
    }

    /**
     *
     * @param flashCardID ID of the flashCard to which the new subheadline will be added
     * @param fContentHeadlineID ID of the headline to which the new subheadline will be added
     * @return editFlashCardPage of flashCard with flashCardID
     */
    @Security.Authenticated(SecurityStudent.class)
    public static Result addNewSubHeadline(int flashCardID, int fContentHeadlineID) {
        FlashCardDBConnectionSenpai fcdbc   = new FlashCardDBConnectionSenpai();

        ContentSubHeadline          sh      = Form.form(ContentSubHeadline.class).bindFromRequest().get();

        List<ContentSubHeadline>    sHLines = fcdbc.getContentSubHeadlineToHeadline(fContentHeadlineID);

        sHLines.stream().filter(csh -> csh.getSequence() >= sh.getSequence()).forEach(csh -> fcdbc.changeFlashCardContentSubHeadlineSequence(csh.getcSubHeadLineID(), (csh.getSequence() + 1)));

        fcdbc.addNewContentSubHeadline(flashCardID, fContentHeadlineID, sh.getTitle(), sh.getSequence());
        return redirect(routes.FlashCardControllerSensei.flashCardPage(flashCardID, true));
    }

    /**
     *
     * @param flashCardID ID of the flashCard to which the new Content will be added
     * @param parentID ID of the chapter to which the content should be added
     * @return editFlashCardPage of flashCard with flashCardID
     */
    @Security.Authenticated(SecurityStudent.class)
    public static Result addNewContentToChapter(int flashCardID, int parentID) {
        FlashCardDBConnectionSenpai     fcdbc       = new FlashCardDBConnectionSenpai();
        Content                         content     = Form.form(Content.class).bindFromRequest().get();
        if(content.getType() == 1) {
            addImageContent(content, parentID, 0, flashCardID, parentID);
        } else {
            List<Content> contentList = fcdbc.getContentToChapterForFlashCard(parentID);
            contentList.stream().filter(c -> c.getSequence() >= content.getSequence()).forEach(c -> fcdbc.changeFlashCardContentSequence(c.getContentID(), (c.getSequence() + 1)));
            fcdbc.addNewContent(flashCardID,parentID, content.getSequence(), content.getContent(), content.getType(), 0);
        }
        return redirect(routes.FlashCardControllerSensei.flashCardPage(flashCardID, true));
    }

    /**
     *
     * @param flashCardID ID of the flashCard to which the new Content will be added
     * @param parentID ID of the headline to which the content should be added
     * @return editFlashCardPage of flashCard with flashCardID
     */
    @Security.Authenticated(SecurityStudent.class)
    public static Result addNewContentToContentHeadline(int flashCardID, int parentID) {
        FlashCardDBConnectionSenpai     fcdbc       = new FlashCardDBConnectionSenpai();
        Content                         content     = Form.form(Content.class).bindFromRequest().get();
        if(content.getType() == 1) {
            addImageContent(content, parentID, 1, flashCardID, parentID);
        } else {
            List<Content> contentList = fcdbc.getContentToHeadlineForFlashCard(parentID);
            contentList.stream().filter(c -> c.getSequence() >= content.getSequence()).forEach(c -> fcdbc.changeFlashCardContentSequence(c.getContentID(), (c.getSequence() + 1)));
            fcdbc.addNewContent(flashCardID,parentID, content.getSequence(), content.getContent(), content.getType(), 1);
        }
        return redirect(routes.FlashCardControllerSensei.flashCardPage(flashCardID, true));
    }

    /**
     *
     * @param flashCardID ID of the flashCard to which the new Content will be added
     * @param parentID ID of the subheadline to which the content will be added
     * @return editFlashCardPage of flashCard with flashCardID
     */
    @Security.Authenticated(SecurityStudent.class)
    public static Result addNewContentToContentSubHeadline(int flashCardID, int parentID) {
        FlashCardDBConnectionSenpai     fcdbc       = new FlashCardDBConnectionSenpai();
        Content                         content     = Form.form(Content.class).bindFromRequest().get();
        if(content.getType() == 1) {
            addImageContent(content, parentID, 2, flashCardID, parentID);
        } else {
            List<Content> contentList = fcdbc.getContentToSubHeadlineForFlashCard(parentID);
            contentList.stream().filter(c -> c.getSequence() >= content.getSequence()).forEach(c -> fcdbc.changeFlashCardContentSequence(c.getContentID(), (c.getSequence() + 1)));
            fcdbc.addNewContent(flashCardID,parentID, content.getSequence(), content.getContent(), content.getType(), 2);
        }
        return redirect(routes.FlashCardControllerSensei.flashCardPage(flashCardID, true));
    }

    /**
     *
     * @param newContent Content that will be added
     * @param aID ID Of the Parent
     * @param idType ID of the type
     * @param flashCardID ID of the flashCard to which the Image-Content will be added
     * @param parentID ID of the parent
     */
    private static void addImageContent(Content newContent, int aID, int idType, int flashCardID, int parentID) {
        FlashCardDBConnectionSenpai fcdbc = new FlashCardDBConnectionSenpai();
        //Get File
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart picture = body.getFile("picture");

        //Handle Picture
        if (picture != null) {
            File file = picture.getFile();
            String extension = picture.getFilename().substring(picture.getFilename().lastIndexOf(".") + 1);
            String newFileName = generateFileName(file) + "." + extension;
            System.out.println(newFileName);

            File newFile = new File(play.Play.application().path().toString() + "//public//uploads//" + newFileName);
            InputStream inStream;
            OutputStream outStream;

            try{

                inStream = new FileInputStream(file);
                outStream = new FileOutputStream(newFile);

                byte[] buffer = new byte[1024];

                int length;
                //copy the file content in bytes
                while ((length = inStream.read(buffer)) > 0){

                    outStream.write(buffer, 0, length);

                }

                inStream.close();
                outStream.close();

                //delete the original file
                file.delete();

                System.out.println("File is copied successful!");

            }catch(IOException e){
                e.printStackTrace();
            }

            newContent.setContent("<img src=\"" + "/public/uploads/" + newFileName + "\" class=\"img-responsive insertAutomatically\" alt=\"Responsive image\">");
        }
            switch (idType) {
                case 0:
                    List<Content> contentList  = fcdbc.getContentToChapterForFlashCard(aID);
                    contentList.stream().filter(content -> content.getSequence() >= newContent.getSequence()).forEach(content -> fcdbc.changeFlashCardContentSequence(content.getContentID(), (content.getSequence() + 1)));
                    fcdbc.addNewContent(flashCardID, parentID, newContent.getSequence(), newContent.getContent(), 1, idType);
                    break;
                case 1:
                    List<Content> contentsList  = fcdbc.getContentToHeadlineForFlashCard(aID);
                    contentsList.stream().filter(content -> content.getSequence() >= newContent.getSequence()).forEach(content -> fcdbc.changeFlashCardContentSequence(content.getContentID(), (content.getSequence() + 1)));
                    fcdbc.addNewContent(flashCardID, parentID, newContent.getSequence(), newContent.getContent(), 1, idType);
                    break;
                case 2:
                    List<Content> contentesList  = fcdbc.getContentToSubHeadlineForFlashCard(aID);
                    contentesList.stream().filter(content -> content.getSequence() >= newContent.getSequence()).forEach(content -> fcdbc.changeFlashCardContentSequence(content.getContentID(), (content.getSequence() + 1)));
                    fcdbc.addNewContent(flashCardID, parentID, newContent.getSequence(), newContent.getContent(), 1, idType);
                    break;
            }
    }

    /**
     *
     * @param file file to which a name will be generated
     * @return String which contains the generated filename
     */
    private static String generateFileName(File file) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5"); // SHA or MD5
            String hash = "";

            byte[] data = new byte[(int)file.length()];
            FileInputStream fis = new FileInputStream(file);
            //noinspection ResultOfMethodCallIgnored
            fis.read(data);
            fis.close();

            md.update(data);

            byte[] digest = md.digest();

            for (byte aDigest : digest) {
                String hex = Integer.toHexString(aDigest);
                if (hex.length() == 1)
                    hex = "0" + hex;
                hex = hex.substring(hex.length() - 2);
                hash += hex;
            }

            LocalDateTime time = LocalDateTime.now();
            String timeStamp = time.toString().replace(':', '_').replace('.', '_');

            return hash.substring(0,5)+timeStamp;

        } catch (NoSuchAlgorithmException | IOException e){
            e.printStackTrace();
        }

        return LocalDateTime.now().toString().replace(':', '_').replace('.', '_');
    }

    /**
     * Change the name of a FlashCard
     * @param flashCardId id of the flashcard to change
     * @return redirect to editFlashCardPage
     */
    public static Result changeFlashCardName(int flashCardId) {
        DynamicForm tempData    = Form.form().bindFromRequest();
        String newName          = tempData.get("newFlashCardName");

        FlashCardDBConnectionSenpai flashCon = new FlashCardDBConnectionSenpai();
        flashCon.changeFlashCardName(flashCardId, newName);

        return redirect(routes.FlashCardControllerSensei.flashCardPage(flashCardId, true));
    }

    /**
     *
     * @param flashCardID ID of the flashCard in which the Chapter will be changed
     * @param fChapterID ID of the Chapter that will be changed
     * @return editFlashCardPage of the flashcard with flashCardID
     */
    public static Result changeChapter(int flashCardID, int fChapterID) {
        FlashCardDBConnectionSenpai fcdbc       = new FlashCardDBConnectionSenpai();
        ChapterChan                 chapter     = Form.form(ChapterChan.class).bindFromRequest().get();
        ChapterChan                 oldcha      = fcdbc.getChapterByID(fChapterID);
        List<ChapterChan>           chapters    = fcdbc.getChaptersForFlashCard(flashCardID);
        if(oldcha.getSequence() != chapter.getSequence()) {
            for(ChapterChan cc : chapters) {
                if(chapter.getSequence() < oldcha.getSequence()) {
                    if (cc.getSequence() >= chapter.getSequence() && cc.getSequence() < oldcha.getSequence()) {
                        fcdbc.changeFlashCardChapterSequence(cc.getChapterID(), (cc.getSequence() + 1));
                    }
                }
                if(chapter.getSequence() > oldcha.getSequence()) {
                    if(cc.getSequence() > oldcha.getSequence() && cc.getSequence() <= chapter.getSequence() ) {
                        fcdbc.changeFlashCardChapterSequence(cc.getChapterID(), (cc.getSequence() - 1));
                    }
                }
            }
        }
        fcdbc.changeFlashCardChapter(flashCardID, fChapterID, chapter.getTitle(), chapter.getSequence());
        return redirect(routes.FlashCardControllerSensei.flashCardPage(flashCardID, true));
    }

    /**
     *
     * @param flashCardID ID of the flashcard in which the headline will be changed
     * @param fChapterID ID of the Chapter in which the headline will be changed
     * @param fHeadlineID ID of the headline that will be changed
     * @return editFlashCardPage of the flashcard with flashCardID
     */
    public static Result changeHeadline(int flashCardID, int fChapterID, int fHeadlineID) {
        FlashCardDBConnectionSenpai fcdbc       = new FlashCardDBConnectionSenpai();
        ContentHeadline             headline    = Form.form(ContentHeadline.class).bindFromRequest().get();
        ContentHeadline             oldhead     = fcdbc.getHeadlineByID(fHeadlineID);
        List<ContentHeadline>       headlines   = fcdbc.getContentHeadlineToChapter(fChapterID);
        if(oldhead.getSequence() != headline.getSequence()) {
            for(ContentHeadline ch : headlines) {
                if(headline.getSequence() < oldhead.getSequence()) {
                    if (ch.getSequence() >= headline.getSequence() && ch.getSequence() < oldhead.getSequence()) {
                        fcdbc.changeFlashCardContentHeadlineSequence(ch.getcHeadLineID(), (ch.getSequence() + 1));
                    }
                }
                if(headline.getSequence() > oldhead.getSequence()) {
                    if(ch.getSequence() > oldhead.getSequence() && ch.getSequence() <= headline.getSequence() ) {
                        fcdbc.changeFlashCardContentHeadlineSequence(ch.getcHeadLineID(), (ch.getSequence() - 1));
                    }
                }
            }
        }
        fcdbc.changeFlashCardHeadline(flashCardID, fHeadlineID, headline.getTitle(), headline.getSequence());
        return redirect(routes.FlashCardControllerSensei.flashCardPage(flashCardID, true));
    }

    /**
     *
     * @param flashCardID ID of the flashcard in which the subheadline will be changed
     * @param fHeadlineID ID of the headline in which the subheadline will be changed
     * @param fSubHeadlineID ID of the subheadline that will be changed
     * @return editFlashCardPage of the flashcard with flashCardID
     */
    public static Result changeSubHeadline(int flashCardID, int fHeadlineID, int fSubHeadlineID) {
        FlashCardDBConnectionSenpai fcdbc               = new FlashCardDBConnectionSenpai();
        ContentSubHeadline             subheadline     = Form.form(ContentSubHeadline.class).bindFromRequest().get();
        ContentSubHeadline             oldsub          = fcdbc.getSubHeadlineByID(fSubHeadlineID);
        List<ContentSubHeadline>       subheadlines    = fcdbc.getContentSubHeadlineToHeadline(fHeadlineID);
        if(oldsub.getSequence() != subheadline.getSequence()) {
            for(ContentSubHeadline ch : subheadlines) {
                if(subheadline.getSequence() < oldsub.getSequence()) {
                    if (ch.getSequence() >= subheadline.getSequence() && ch.getSequence() < oldsub.getSequence()) {
                        fcdbc.changeFlashCardContentSubHeadlineSequence(ch.getcSubHeadLineID(), (ch.getSequence() + 1));
                    }
                }
                if(subheadline.getSequence() > oldsub.getSequence()) {
                    if(ch.getSequence() > oldsub.getSequence() && ch.getSequence() <= subheadline.getSequence() ) {
                        fcdbc.changeFlashCardContentSubHeadlineSequence(ch.getcSubHeadLineID(), (ch.getSequence() - 1));
                    }
                }
            }
        }
        fcdbc.changeFlashCardSubHeadline(flashCardID, fSubHeadlineID, subheadline.getTitle(), subheadline.getSequence());
        return redirect(routes.FlashCardControllerSensei.flashCardPage(flashCardID, true));
    }


    /**
     * Displays the page where you can chance a content
     * @param contentID ID of content
     * @param aID ID of parent
     * @param type type of parent 0:= chapter, 1:= contentHeadline 2:= contentSubHeadline
     * @param flashcardID ID of flashCard
     * @return redirect
     */
    public static Result changeContentFlascardPage(int flashcardID, int contentID, int aID, int type) {
        //ContentDBConnection contentDBConnection = new ContentDBConnection();

        FlashCardDBConnectionSenpai fcdbc   = new FlashCardDBConnectionSenpai();

        Content editContent = fcdbc.getContentByID(contentID);

        List<Content> contentList = new LinkedList<>();
        switch (type) {
            case 0: //Chapter
                contentList = fcdbc.getContentToChapterForFlashCard(aID);
                break;
            case 1: //Headline
                contentList = fcdbc.getContentToHeadlineForFlashCard(aID);
                break;
            case 2: //SubHeadline
                contentList = fcdbc.getContentToSubHeadlineForFlashCard(aID);
                break;
        }

        FlashCard flashCard = fcdbc.getFlashCardByID(flashcardID);

        String email = session().get("email");
        int role = Integer.parseInt(session().get("role"));

        return ok(changeContentFlashcardPage.render(editContent, contentList, type, aID, flashCard, email, role));
    }

    /**
     * Method to change a content, no matter what parent type the content has.
     * @param flashCardID ID of FlashCard
     * @param contentID ID of content
     * @param aID ID of parent
     * @param type of parent 0:= chapter, 1:= contentHeadline 2:= contentSubHeadline
     * @return redirect
     */
    public static Result changeContent(int flashCardID, int contentID, int aID, int type){
        Result result = ok("not found");
        if(type==0){
            result = changeContentOfChapter(flashCardID, aID, contentID );
        }
        if(type==1){
            result = changeContentOfHeadline(flashCardID, aID, contentID);
        }
        if(type==2){
            result = changeContentOfSubHeadline(flashCardID, aID, contentID);
        }
        return result;
    }

    /**
     *
     * @param flashCardID ID of the flashcard in which the content will be changed
     * @param fChapterID ID of the chapter in which the content will be changed
     * @param fContentID ID of the content that will be changed
     * @return editFlashCardPage of the flashcard with flashCardID
     */
    public static Result changeContentOfChapter(int flashCardID, int fChapterID, int fContentID) {
        FlashCardDBConnectionSenpai fcdbc   = new FlashCardDBConnectionSenpai();
        Content                     content = Form.form(Content.class).bindFromRequest().get();
        Content                     oldcon  = fcdbc.getContentByID(fContentID);
        List<Content>               cons    = fcdbc.getContentToChapterForFlashCard(fChapterID);
        if(content.getSequence() != oldcon.getSequence()) {
            for(Content c : cons) {
                if(content.getSequence() < oldcon.getSequence()) {
                    if(c.getSequence() >= content.getSequence() && c.getSequence() < oldcon.getSequence()) {
                        fcdbc.changeFlashCardContentSequence(c.getContentID(), (c.getSequence() + 1));
                    }
                }
                if(content.getSequence() > oldcon.getSequence()) {
                    if(c.getSequence() > oldcon.getSequence() && c.getSequence() <= content.getSequence()) {
                        fcdbc.changeFlashCardChapterSequence(c.getContentID(), (c.getSequence() - 1));
                    }
                }
            }
        }
        if(content.getType() == 1) {
            addImageContent(content, fChapterID, 0, flashCardID, fChapterID);
        } else {
            fcdbc.changeFlashCardContent(flashCardID, fContentID, content.getContent(), content.getSequence(), content.getType());
        }
        return redirect(routes.FlashCardControllerSensei.flashCardPage(flashCardID, true));
    }

    /**
     *
     * @param flashCardID ID of the flashcard in which the content will be changed
     * @param fHeadlineID ID of the headline in which the content will be changed
     * @param fContentID ID of the content that will be changed
     * @return editFlashCardPage of the flashcard with flashCardID
     */
    public static Result changeContentOfHeadline(int flashCardID, int fHeadlineID, int fContentID) {
        FlashCardDBConnectionSenpai fcdbc   = new FlashCardDBConnectionSenpai();
        Content                     content = Form.form(Content.class).bindFromRequest().get();
        Content                     oldcon  = fcdbc.getContentByID(fContentID);
        List<Content>               cons    = fcdbc.getContentToHeadlineForFlashCard(fHeadlineID);
        if(content.getSequence() != oldcon.getSequence()) {
            for(Content c : cons) {
                if(content.getSequence() < oldcon.getSequence()) {
                    if(c.getSequence() >= content.getSequence() && c.getSequence() < oldcon.getSequence()) {
                        fcdbc.changeFlashCardContentSequence(c.getContentID(), (c.getSequence() + 1));
                    }
                }
                if(content.getSequence() > oldcon.getSequence()) {
                    if(c.getSequence() > oldcon.getSequence() && c.getSequence() <= content.getSequence()) {
                        fcdbc.changeFlashCardChapterSequence(c.getContentID(), (c.getSequence() - 1));
                    }
                }
            }
        }
        if(content.getType() == 1) {
            addImageContent(content, fHeadlineID, 0, flashCardID, fHeadlineID);
        } else {
            fcdbc.changeFlashCardContent(flashCardID, fContentID, content.getContent(), content.getSequence(), content.getType());
        }
        return redirect(routes.FlashCardControllerSensei.flashCardPage(flashCardID, true));
    }

    /**
     *
     * @param flashCardID ID of the flashcard in which the content will be changed
     * @param fsubHeadlineID ID of the subheadline in which the content will be changed
     * @param fContentID ID of the content that will be changed
     * @return editFlashCardPage of the flashcard with flashCardID
     */
    public static Result changeContentOfSubHeadline(int flashCardID, int fsubHeadlineID, int fContentID) {
        FlashCardDBConnectionSenpai fcdbc   = new FlashCardDBConnectionSenpai();
        Content                     content = Form.form(Content.class).bindFromRequest().get();
        Content                     oldcon  = fcdbc.getContentByID(fContentID);
        List<Content>               cons    = fcdbc.getContentToSubHeadlineForFlashCard(fsubHeadlineID);
        if(content.getSequence() != oldcon.getSequence()) {
            for(Content c : cons) {
                if(content.getSequence() < oldcon.getSequence()) {
                    if(c.getSequence() >= content.getSequence() && c.getSequence() < oldcon.getSequence()) {
                        fcdbc.changeFlashCardContentSequence(c.getContentID(), (c.getSequence() + 1));
                    }
                }
                if(content.getSequence() > oldcon.getSequence()) {
                    if(c.getSequence() > oldcon.getSequence() && c.getSequence() <= content.getSequence()) {
                        fcdbc.changeFlashCardChapterSequence(c.getContentID(), (c.getSequence() - 1));
                    }
                }
            }
        }
        if(content.getType() == 1) {
            addImageContent(content, fsubHeadlineID, 0, flashCardID, fsubHeadlineID);
        } else {
            fcdbc.changeFlashCardContent(flashCardID, fContentID, content.getContent(), content.getSequence(), content.getType());
        }
        return redirect(routes.FlashCardControllerSensei.flashCardPage(flashCardID, true));
    }

}
