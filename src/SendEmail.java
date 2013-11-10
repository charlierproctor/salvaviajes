import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmail {

    static void sendEmail(String subject, String messageBody, String emailAddress) {

        final String username = "dreamteamdatafest@gmail.com";
        final String password = "migrants123";

        if(emailAddress == null){
            emailAddress = "dreamteamdatafest@gmail.com";
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("dreamteamdatafest@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(emailAddress));
            message.setSubject(subject);
            message.setText(messageBody);

            Transport.send(message);

            System.out.println("Email Has Been Sent");
            System.out.println("-------------------------------------------------------------");
            System.out.println("TO: " + emailAddress);
            System.out.println("Subject: " + subject);
            System.out.println("Message Body:\n" + messageBody);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}