package utils.Jira;

import java.io.IOException;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;


public class checkEmail {

  public static void check(
    String host,
    String user,
    String password
  ) {
    try {
      Properties properties = new Properties();
      properties.put("mail.imap.host", host);
      properties.put("mail.imap.port", "993");
      properties.put("mail.imap.starttls.enable", "true");
      Session emailSession = Session.getDefaultInstance(properties);
      Store store = emailSession.getStore("imaps");

      store.connect(host, user, password);
      Folder emailFolder = store.getFolder("INBOX");
      emailFolder.open(Folder.READ_WRITE);
      Message messages[] = emailFolder.search(
        new FlagTerm(new Flags(Flags.Flag.SEEN), false)
      );
      if (messages.length == 0){
        System.out.println("No New Email Found in MailBox");
      }
      for (int i = 0; i < messages.length; i++) {
        if (
          messages[i].getFrom()[0].toString()
            .equals("") &&
          !messages[i].getSubject().contains("Re:") && !messages[i].getSubject().contains("Fwd:")
        ) {
          Jira.createIssue(
            messages[i].getSubject(),
            getTextFromMessage(messages[i])
          );

        }
        else {
          System.out.println("No Email Found from bugs@wingify.com");
        }
        messages[i].setFlag(Flags.Flag.SEEN, true);
      }
      emailFolder.close(false);
      store.close();
    } catch (NoSuchProviderException e) {
      e.printStackTrace();
    } catch (MessagingException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static String getTextFromMessage(Message message)
    throws IOException, MessagingException {
    String result = "";
    if (message.isMimeType("text/plain")) {
      result = message.getContent().toString();
    } else if (message.isMimeType("multipart/*")) {
      MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
      result = getTextFromMimeMultipart(mimeMultipart);
    }
    return result;
  }

  private static String getTextFromMimeMultipart(MimeMultipart mimeMultipart)
    throws IOException, MessagingException {
    int count = mimeMultipart.getCount();
    if (count == 0) throw new MessagingException(
      "Multipart with no body parts not supported."
    );
    boolean multipartAlt = new ContentType(mimeMultipart.getContentType())
      .match("multipart/alternative");
    if (multipartAlt) return getTextFromBodyPart(
      mimeMultipart.getBodyPart(count - 2)
    );
    String result = "";
    for (int i = 0; i < count; i++) {
      BodyPart bodyPart = mimeMultipart.getBodyPart(i);
      result += getTextFromBodyPart(bodyPart);
    }
    return result;
  }

  private static String getTextFromBodyPart(BodyPart bodyPart)
    throws IOException, MessagingException {
    String result = "";
    if (bodyPart.isMimeType("text/plain")) {
      result = (String) bodyPart.getContent();
    } else if (bodyPart.getContent() instanceof MimeMultipart) {
      result = getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
    }
    return result;
  }
}
