package controllers;

import play.Play;
import play.mvc.Controller;
import org.apache.commons.mail.*;
/**
 * @author Stefan
 * For sending mails
 */
public class MailSender {

    /**
     * sends a e-mail to recipient
     * @param recipient recipient of the email
     * @param content content of mail
     * @param subject subject of mail
     */
    public static void sendMail(String recipient, String content, String subject) {
        // Load SMTP configuration
        String  smtpHost        = Play.application().configuration().getString("smtp.host");
        Integer smtpPort        = 587;
        String  smtpUser        = Play.application().configuration().getString( "smtp.user" );
        String  smtpPassword    = Play.application().configuration().getString( "smtp.password" );

        System.out.println(smtpHost);
        System.out.println(smtpPort);
        System.out.println(smtpUser);
        System.out.println(smtpPassword);

        //Render template
        //String body = email.render( created ).body();

        // Prepare email
        Email mail = new SimpleEmail();
        try {
            mail.setFrom("coffeescript@outlook.de");
            mail.setSubject(subject);
            mail.setMsg(content);
            mail.addTo(recipient);
        } catch (EmailException e) {
            e.printStackTrace();
        }


        // Application de la configuration SMTP
        mail.setHostName( smtpHost );
        if (smtpPort > 1 && smtpPort < 65536 ) {
            mail.setSmtpPort(smtpPort);
            mail.setTLS(true);
        }
        if (!smtpUser.isEmpty()) {
            mail.setAuthentication( smtpUser, smtpPassword );
        }

        // And finally
        try {
            mail.send();
        } catch (EmailException e) {
            e.printStackTrace();
        }
    }
}
