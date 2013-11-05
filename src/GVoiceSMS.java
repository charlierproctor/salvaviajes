import com.techventus.server.voice.Voice;
import com.techventus.server.voice.datatypes.records.SMS;
import com.techventus.server.voice.datatypes.records.SMSThread;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: charlierproctor
 * Date: 11/2/13
 * Time: 1:02 PM
 * To change this template use File | Settings | File Templates.
 */

public class GVoiceSMS {

        public static void sendSMS(String text, String destinationNumber){
            //this method is used to send text messages, with content text to phone number: destinationNumber
            String userName = "dreamteamdatafest@gmail.com";
            String password = "migrants123";
            try {
                Voice voice = new Voice(userName, password);
                voice.sendSMS(destinationNumber,text);
                System.out.println("SMS message sent to: " + destinationNumber + " -- " + text);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }


        }

        public static Collection<SMSThread> getUnreadSMS(){
            //this method returns a collection of unread SMSThreads

            String userName = "dreamteamdatafest@gmail.com";
            String password = "migrants123";
            String allSMSString;
            Collection<SMSThread> allSMS = null;
            Collection<SMSThread> unreadSMSCollection = null;
            try {
                Voice voice = new Voice(userName, password);
                allSMSString = voice.getSMS();   //all the SMS threads; in string format
                allSMS = voice.getSMSThreads(allSMSString);      //all the SMS threads currently in the inbox ... now as a collection
                System.out.println("All SMS messages (collection): " + allSMS.toString());

                Object[] allSMSArray = allSMS.toArray();     //converts allSMS to an array
                // this for-loop removes all the read SMSThreads from the array
                for(int i=0; i<allSMSArray.length; i++){   //iterates through allSMSArray
                    SMSThread currentSMSThread = (SMSThread) allSMSArray[i]; //the current thread is taken from allSMSArray
                    if(currentSMSThread.getRead()){      //if the current SMS thread has been read;
                    // it is removed from allSMS (the collection)
                        allSMS.remove(currentSMSThread);
                    };
                }

                unreadSMSCollection = allSMS;    // set unreadSMSCollection = allSMS (since all the read messages
                //have been removed from all SMS at this point

                System.out.println("Unread SMS messages (collection): " + unreadSMSCollection.toString());

                } catch (IOException e) {
                System.out.println(e);  //To change body of catch statement use File | Settings | File Templates.
            } catch (NullPointerException e){
                System.out.println(e);
            }
            return unreadSMSCollection;
        }

        public static void markSMSAsRead(Collection<SMSThread> smsThreads){
            String userName = "dreamteamdatafest@gmail.com";
            String password = "migrants123";
            try {
                Voice voice = new Voice(userName, password);
                //marks all messages in smsThreads collection as read
                for (SMSThread message : smsThreads)
                {
                    voice.markAsRead(message.getId());
                }
                System.out.println("Message threads marked as read");
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        public static void deleteSMS(SMSThread currentUser){
            String userName = "dreamteamdatafest@gmail.com";
            String password = "migrants123";
            try {
                Voice voice = new Voice(userName, password);
                voice.deleteMessage(currentUser.getId());     //deletes all the current user's messages
                System.out.println("Message thread deleted");
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }


    }
