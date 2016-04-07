package controllers.Security;

import play.mvc.Http.*;

import java.time.LocalDateTime;

/**
 * Created by Stefan on 04.02.2015.
 * handles all Session related actions
 */
public class SessionController {

    /**
     * creates session in http context with
     * email, role and a timestamp for timeouts
     * @param email ID of user
     * @param role role of user
     * @param ctx httpContext
     */
    public static void createSession(String email, int role, Context ctx) {
        //preparing timeStamp with LocalDateTime and toString
        LocalDateTime   time        = LocalDateTime.now();
        String          timeStamp   = time.toString();

        //creating session
        ctx.session().clear();
        ctx.session().put("email", email);
        ctx.session().put("role", Integer.toString(role));
        ctx.session().put("timestamp", timeStamp);
    }

    /**
     * checks if session timestamp is younger than 2hours
     * @param ctx httpContext
     * @return context with updated timeStamp if true, context with cleared session if false
     */
    public static Context checkSession(Context ctx) {
        //creating currentTimeStamp and parsing the StringTimeStamp into a LocalDateTime Object
        try {
            LocalDateTime   timeNOW     = LocalDateTime.now();
            String          sessionTime = ctx.session().get("timestamp");
            LocalDateTime   timeSession = LocalDateTime.parse(sessionTime);

            //check if currentTime-2H is happening after timeSession meaning timeSession is older than 2H
            if(timeNOW.minusHours(2).isAfter(timeSession)) {
                ctx.session().clear();
            } else {
                String      email       = ctx.session().get("email");
                String      role        = ctx.session().get("role");
                String      timeStamp   = timeNOW.toString();
                //clearing and renewing session cookie because session.replace() does not work
                ctx.session().clear();
                ctx.session().put("email", email);
                ctx.session().put("role", role);
                ctx.session().put("timestamp", timeStamp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ctx;
    }

}
