package com.google.sps.classes;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
  
/*EmailSender creates and sends an email when users create and account to verify that they created the account*/
public class EmailSender{ 
    public static void sendEmail(String to) {
        String from = "christinachance315@gmail.com";
        String host = "localhost";
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        Session session = Session.getDefaultInstance(properties);

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("[Covidiena] Account Verification");
            message.setContent("<h1>An account on Covidiena has been created with this email. To verify this is you, please click <a href='covidiena.appspot.com/login.html'>here</a></h1>.", "text/html");
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
} 
