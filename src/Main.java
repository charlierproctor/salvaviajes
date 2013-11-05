import com.techventus.server.voice.datatypes.records.SMSThread;

import java.util.ArrayList;
import java.util.Collection;

import static java.lang.Thread.sleep;

/**
 * Created with IntelliJ IDEA.
 * User: charlierproctor
 * Date: 11/2/13
 * Time: 1:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class Main {

    public static void main(String [] args) throws InterruptedException {
        ArrayList<SMSUser> userArrayList = new ArrayList<SMSUser>();     //stores SMSUsers in an array

        while(true){            //loops every 2 seconds

        try{
        Collection<SMSThread> unreadSMSCollection = GVoiceSMS.getUnreadSMS();          //loads unread SMSThreads into unreadSMSCollection

        System.out.println(unreadSMSCollection.toString());

        userArrayList = SMSAction.newSMS(unreadSMSCollection, userArrayList);     //deals with the new text messages
            GVoiceSMS.markSMSAsRead(unreadSMSCollection);       //marks the new ones as read

        }
        catch(NullPointerException e){
            System.out.println(e);
        }
        sleep(2 * 1000);
        }
    }
}
