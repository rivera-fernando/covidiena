package com.google.sps.classes;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

  
/*EmailSender creates and sends an email when users create and account to verify that they created the account*/
public class EmailSender{ 
    public static void sendEmail(String to, String newPassword) {

      // Put sender’s address
      String from = "covidiena@gmail.com";
      final String username = "covidiena";
      final String password = "Covidiena2020";

      String host = "smtp.gmail.com";

      Properties props = new Properties();
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.smtp.host", host);
      props.put("mail.smtp.port", "587");

      // Get the Session object.
      Session session = Session.getInstance(props,
         new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(username, password);
    }
         });

      try {
        // Create a default MimeMessage object.
        Message message = new MimeMessage(session);
    
        // Set From: header field 
        message.setFrom(new InternetAddress(from));
    
        // Set To: header field
        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(to));
        StringBuilder htmlBuilder = new StringBuilder();
        if (password.equals("")){
            message.setSubject("[Covidiena] Account Verification");
            htmlBuilder.append("<p>");
            htmlBuilder.append("An account on Covidiena has been created with this email. To verify this is you, please click ");
            htmlBuilder.append(" <a href='covidiena.appspot.com/login.html'>here</a>.");
            htmlBuilder.append("</p>");
        } else {
            message.setSubject("[Covidiena] Reset Account Password");
            htmlBuilder.append("<p>");
            htmlBuilder.append("The account linked to this email requested a password change. The new password is: " + newPassword + ".");
            htmlBuilder.append(" Log in <a href='covidiena.appspot.com/login.html'>here</a>.");
            htmlBuilder.append("</p>");
        }
        String html = htmlBuilder.toString();
        message.setContent(html, "text/html");
        Transport.send(message);

        System.out.println("Sent message successfully....");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
      }
    }
} 
