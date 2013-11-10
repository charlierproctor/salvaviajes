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

        while(true){

            for(int i=0; i<1800; i++){     //loops every 2 seconds for 3600 seconds (ie 1 hr)
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

            SendEmail.sendEmail("Salvaviajes Java App Running Successfully", "Hi Charlie,\nThis is just to let you know that the Salvaviajes Java App is running successfully.\n\nGo Dream Team!!\nHave a good day!\nCharlie", "charles.proctor@yale.edu");    //sends me an email every hr to confirm it's running successfully.

        }
    }
}
