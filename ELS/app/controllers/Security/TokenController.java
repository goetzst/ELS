package controllers.Security;

import play.mvc.Controller;
import play.mvc.Result;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Stefan on 30.03.2015.
 * class to tackle lost update
 */
public class TokenController extends Controller{

    private static List<String> docList     = new LinkedList<>();
    private static List<String> chapterList = new LinkedList<>();
    private static List<String> commentList = new LinkedList<>();
    private static List<String> subComList  = new LinkedList<>();
    private static List<String> contentList = new LinkedList<>();

    /**
     * resolves kind to List for tokenCheck
     * @param kind defines which kind of database entry is subject to change, 0 := doc, 1 := chapter, 2 := comment, 3 := subComment, 4 := content
     * @param id ID of entry
     * @param userEmail ID of user who wishes to change entry
     * @return true if access is allowed, false if not
     */
    public static boolean checkToken(int kind, int id, String userEmail) {
        if(kind == 0) {
            return tokenCheck(id, userEmail, docList);
        }else if(kind == 1) {
            return tokenCheck(id, userEmail, chapterList);
        }else if(kind == 2) {
            return tokenCheck(id, userEmail, commentList);
        }else if (kind == 3) {
            return tokenCheck(id, userEmail, subComList);
        } else
            return kind == 4 && tokenCheck(id, userEmail, contentList);
    }

    /**
     * checks token via ID, timestamp(valid for 2h), and userEmail
     * @param id of entry
     * @param userEmail ID of user who wishes to change entry
     * @param tokenList list of entryIDs of same kind
     * @return true if access is allowed, false if not
     */
    private static boolean tokenCheck(int id, String userEmail, List<String> tokenList) {
        LocalDateTime   timeNOW     = LocalDateTime.now();
        String[]        splitToken;
        for(String token: tokenList) {
            //splits token into id, timestamp, userMail
            splitToken  = token.split("/");
            if (splitToken[0].equals(Integer.toString(id))) { //idCheck
                if(timeNOW.minusHours(2).isBefore(LocalDateTime.parse(splitToken[1]))) { //check on currentness of timeStamp
                    //check if access is given to forwarded userMail or other user
                    return splitToken[2].equals(userEmail);
                }else {
                    break; // break because complexity crucial to make sure of uniqueness of token for entry
                }
            }
        }
        createToken(id, userEmail, tokenList, timeNOW);
        return true;
    }

    /**
     * creates tokenEntry: List-> ID/timestamp/userEmail
     * @param id ID of entry
     * @param userEmail ID of user which got access
     * @param tokenList List of kind
     * @param timeNOW current Time in LocalDateTime form
     */
    private static void createToken(int id, String userEmail, List<String> tokenList, LocalDateTime timeNOW) {
        //FORM: id/time/email
        tokenList.add(id + "/" + timeNOW.toString() + "/" + userEmail);
    }

    /**
     * deletes token of kind with id+userEmail match
     * @param kind defines which kind of database entry is subject to change, 0 := doc, 1 := chapter, 2 := comment, 3 := subComment, 4 := content
     * @param id ID of entry
     * @param userEmail ID of user who has had access
     */
    public static void deleteToken(int kind, int id, String userEmail) {
        if(kind == 0) {
            pdeleteToken(id, userEmail, docList);
        }else if(kind == 1) {
            pdeleteToken(id, userEmail, chapterList);
        }else if(kind == 2) {
           pdeleteToken(id, userEmail, commentList);
        }else if(kind == 3) {
            pdeleteToken(id, userEmail, subComList);
        }else if (kind == 4){
            pdeleteToken(id, userEmail, contentList);
            //error
        }
    }

    /**
     * deletes specific token from tokenList to allow new access on entry
     * @param id ID of entry
     * @param userEmail ID of user
     * @param tokenList List of kind
     */
    private static void pdeleteToken(int id, String userEmail, List<String> tokenList) {
        String[]        splitToken;
        for(String token: tokenList) {
            splitToken  = token.split("/");
            if(splitToken[0].equals(Integer.toString(id)) && userEmail.equals(splitToken[2])) { //id and email check
                tokenList.remove(token);
                break; // only one token for each entry so no additional search needed (time might be crucial here)
            }
        }
    }

    /**
     * PublicMethod to check for availability of resource
     * @param kind defines which kind of database entry is subject to change, 0 := doc, 1 := chapter, 2 := comment, 3 := subComment, 4 := content
     * @param id ID of entry
     * @param userEmail ID of user who wishes to change entry
     * @return http:ok if it is available, badRequest if it is not
     */
    public static Result publicCheckToken(int kind, int id, String userEmail){
        if(checkToken(kind, id, userEmail))
            return ok();
        return badRequest();
    }

    /**
     * deletes token for specific resource
     * @param kind defines which kind of database entry is subject to change, 0 := doc, 1 := chapter, 2 := comment, 3 := subComment, 4 := content
     * @param id ID of entry
     * @param userEmail ID of user who wishes to change entry
     * @return http:ok
     */
    public static Result publicDeleteToken(int kind, int id, String userEmail){
        deleteToken(kind, id, userEmail);
        return ok();
    }
}
