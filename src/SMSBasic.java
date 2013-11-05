import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: charlierproctor
 * Date: 11/2/13
 * Time: 4:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class SMSBasic extends Date {
    //this is just a basic class for text messages; including only the content of the messages and the timestamp of the message

   //variable definitions
    private String content;
    private Date timestamp;

    //basic constructor
    public SMSBasic(String contentInput, Date timestampInput){
        content = contentInput;
        timestamp = timestampInput;
    }

    //set methods
    public void setTimestamp(Date timestampInput){
       timestamp = timestampInput;
    }

    public void setContent(String contentInput){
        content = contentInput;
    }

    //get methods
    public String getContent(){
        return content;
    }
    public Date getTimestamp(){
        return timestamp;
    }

}
