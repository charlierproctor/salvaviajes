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
 * Time: 2:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class SMSAction {

    public static ArrayList<SMSUser> newSMS(Collection<SMSThread> unreadSMSCollection, ArrayList<SMSUser> userArrayList){
        if(!unreadSMSCollection.isEmpty()){      //checks to make sure the unread message collection is NOT empty

            for(SMSThread currentUser : unreadSMSCollection){
                //iterates through the unread message collection -- taking one currentUser at a time

                if(!currentUser.getAllSMS().isEmpty()){
                Collection<SMS> currentSMSThread= currentUser.getAllSMS();
                //retrieves all the text messages from this current user and stores them in currentSMSThread collection

                    String phoneNumber = currentUser.getContact().getNumber();     //gets the currentUser's phoneNumber

                    boolean isNewUser = true;
                    int currentUserIndex = 0;        // in the userArrayList
                    for(int i=0; i<userArrayList.size(); i++){
                        //if there is a user in the userArrayList that has the same phone number, this is not a new user
                        if(userArrayList.get(i).getPhoneNumber().equals(phoneNumber)){
                            isNewUser = false;
                            currentUserIndex = i;      //and the currentUser's index is i
                        }
                    }

                    if(isNewUser){        //if we do have a new user; let's add him / her to the userArrayList (at the end)
                        SMSUser newUser = new SMSUser(phoneNumber, currentUser.getDate());
                        //creates a new user with currentUser phone number and timestamp
                        userArrayList.add(newUser);
                        currentUserIndex = userArrayList.size()-1;     //and his index is size-1
                    }

                    //loads the user's old message array -- which has his prior text messages
                    ArrayList<SMS> oldMessageArray = userArrayList.get(currentUserIndex).getMessageArray();
                    ArrayList<SMSBasic> messageArray = new ArrayList<SMSBasic>();

                    //if the old message array is empty
                    if(oldMessageArray == null || oldMessageArray.isEmpty()){
                        //we create a new message array
                        ArrayList<SMS> newUserMessageArray = new ArrayList<SMS>();
                        //and populate it with all currentSMS in currentSMSThread
                        for(SMS currentSMS: currentSMSThread){
                            newUserMessageArray.add(currentSMS);
                        }

                        //we set this new message array as the user's message array
                        userArrayList.get(currentUserIndex).setMessageArray(newUserMessageArray);

                        // we add the new SMS messages to the messageArray (which just stores SMS basic)
                        for(int i = 0; i<newUserMessageArray.size(); i++){
                            SMSBasic smsBasic = new SMSBasic(newUserMessageArray.get(i).getContent(),newUserMessageArray.get(i).getDateTime());
                            messageArray.add(0,smsBasic);
                        }

                    } else{     //if the user does have old messages
                        Collection<SMS> newCurrentSMSThread = currentSMSThread;
                                   //newCurrentSMSThread starts full of all sms messages in thread
                        //loads currentSMSThread into an array
                        SMS[] currentSMSThreadArray = currentSMSThread.toArray(new SMS[currentSMSThread.size()]);

                        //this for-loop iterates through the current sms thread array
                        for(int k = 0; k<currentSMSThreadArray.length; k++){
                            SMS currentSMS = currentSMSThreadArray[k];
                            String currentSMSString = currentSMS.getContent();
                            int size = oldMessageArray.size();

                            //this for-loop iterates through the oldMessageArray
                            for (int i = 0; i<size; i++){

                                if(i>=oldMessageArray.size()){    //if we are outside the old message array, exit the loop
                                   break;
                                } else if(oldMessageArray.get(i).getContent().equals(currentSMSString)){
                                    //otherwise, if the old message array and the current sms string match,
                                    //then delete said string from both the oldMessageArray and the newCurrentSMSThread
                                    newCurrentSMSThread.remove(currentSMS);
                                    oldMessageArray.remove(currentSMS);
                                }
                            }
                        }

                    //creates a newUserMessageArray from the contents of oldMessageArray (the old messages have been removed)
                    ArrayList<SMS> newUserMessageArray = new ArrayList<SMS>();
                        newUserMessageArray.addAll(oldMessageArray);

                        for(int i = 0; i<oldMessageArray.size(); i++){
                            //throws all the old messages into SMSBasic form and into messageArray
                            SMSBasic smsBasic = new SMSBasic(oldMessageArray.get(i).getContent(),oldMessageArray.get(i).getDateTime());
                            messageArray.add(smsBasic);
                        }

                    for(SMS currentSMS: newCurrentSMSThread){
                        //throws all the new messages into SMSBasic form and into messageArray
                        SMSBasic smsBasic = new SMSBasic(currentSMS.getContent(),currentSMS.getDateTime());
                        messageArray.add(smsBasic);
                        newUserMessageArray.add(currentSMS);
                    }
                        //sets the message array for the current user in the userArrayList
                        userArrayList.get(currentUserIndex).setMessageArray(newUserMessageArray);

                    }


                System.out.println("Message Array: ");    //logging the message array to the console; should be early to late
                    for(int i =0; i<messageArray.size(); i++){
                        System.out.println(messageArray.get(i).getTimestamp().toString() + " ---- " + messageArray.get(i).getContent());
                    }


                      String jsonCategories = "";
                    try {
                        jsonCategories = SMSJSON.readUrl("http://salvaviajes.azurewebsites.net/api/Category");
                        //reads in the text from the url
                    } catch (Exception e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    System.out.println("JSON: " + jsonCategories);

                    ArrayList<String> categories = SMSJSON.parseJSONCategories(jsonCategories);
                    //parses it into a categories ArrayList


                SMSBasic latestSMS = messageArray.get(messageArray.size() - 1);
                    //the latest sms sent is the last element in the messageArray
                String smsString = latestSMS.getContent();
                    //smsString is the content of the latest message sent

                    //logging this to the console:
                    System.out.println("smsString: " + smsString);
                    System.out.println("messageArray: " + messageArray.toString());

                    //AND HERE COMES THE ACTUAL LOGIC!!!
                    if(messageArray.size()==1){     //if this is the first message from a user
                    //then we prompt whether he would like to report an issue or query...
                    GVoiceSMS.sendSMS("Reply REPORT to report an issue.", phoneNumber);
                    userArrayList.get(currentUserIndex).addToMessageArray(new SMS( null,"Reply REPORT to report an issue.", new Date()));
                }

                else if(messageArray.size()>=3 && smsString.toLowerCase().contains("report")){
                    //if he responded with the keyword "report"
                    //then we ask for his location:
                    GVoiceSMS.sendSMS("You would like to file a report. Where are you?", phoneNumber);
                    userArrayList.get(currentUserIndex).addToMessageArray(new SMS( null,"You would like to file a report. Where are you?", new Date()));

                }
//                    else if(messageArray.size()>=3 && smsString.toLowerCase().contains("query")){
//                        //if he responded "query", we prompt for his location
//                    GVoiceSMS.sendSMS("Please enter the location you would like to query.", phoneNumber);
//                        //add the message to his message array
//                    userArrayList.get(currentUserIndex).addToMessageArray(new SMS( null,"Please enter the location you would like to query.", new Date()));
//                }
//                    else if(messageArray.size()>=5 && (messageArray.get(messageArray.size()-2).equals("Please enter the location you would like to query.")
//                    || messageArray.get(messageArray.size()-3).equals("Please enter the location you would like to query."))){
//                        //if we just asked for his location we load the issues nearby from the database
//
//                    SMSUser issueToReturn = null;
//                    ArrayList<SMSUser> allReportedIssues = SMSJSON.getIssuesFromDB();    //loads all reported issues
//
//                    for(int i = 0; i<allReportedIssues.size(); i++){
//                        //searches through the array
//                        if(smsString.equalsIgnoreCase(allReportedIssues.get(i).getLocation())){
//                            //if there is a match
//                            issueToReturn = allReportedIssues.get(i);
//                            //we respond with the time and description of the incident
//                            String s1 = "The following incident was reported on " + issueToReturn.getTimestamp() + ": " + issueToReturn.getDescription();
//                            GVoiceSMS.sendSMS(s1,phoneNumber);
//                            //save it to his message array
//                            userArrayList.get(currentUserIndex).addToMessageArray(new SMS( null,s1, new Date()));
//                            GVoiceSMS.deleteSMS(currentUser);        //clears the user's thread (and hence allows for new conversations in the future
//                        }
//                    }
//                }

                else if(messageArray.get(messageArray.size()-2).getContent().contains("Where are you?")
                    || messageArray.get(messageArray.size()-3).getContent().contains("Where are you?")){
                    //if two or three messages ago contains "Where are you?"
                    // they just responded to our prompting them for their location

                    //now we save their location to their user info in the userArrayList
                    userArrayList.get(currentUserIndex).setLocation(smsString);

                    //this creates the string to prompt for a category
                    StringBuffer sB = new StringBuffer("Please respond: ");
                    for(int i = 0; i<categories.size(); i++){
                        String responseCode = getCharForNumber(i);
                        sB.append(responseCode + " for " + categories.get(i) + "; ");
                    }
                    sB.append(".");

                    GVoiceSMS.sendSMS(sB.toString(), phoneNumber);      //and we send the SMS
                        //and save it to his message array
                        userArrayList.get(currentUserIndex).addToMessageArray(new SMS( null,sB.toString(), new Date()));

                }

                else if(messageArray.get(messageArray.size()-2).getContent().contains("Please respond:")
                            || messageArray.get(messageArray.size()-3).getContent().contains("Please respond:")){
                    //if the message two before contained "Please respond: " we prompt for a short description of the incident
                    GVoiceSMS.sendSMS("Thank you! Please provide a short description of the incident.",phoneNumber);
                    userArrayList.get(currentUserIndex).addToMessageArray(new SMS( null,"Thank you! Please provide a short description of the incident.", new Date()));


                    int issueCategoryID = categories.size();  //the issue category ID
                    for(int i=1; i<=categories.size(); i++){
                        //this loop checks through the last text message to see which char it contains
                        if(messageArray.get(messageArray.size()-1).getContent().contains(getCharForNumber(i))){
                            issueCategoryID = i;
                            //and this number is set to the issueCategoryID
                        }
                    }
                    //and we store this issueCategoryID to their user info in the userArrayList:
                    userArrayList.get(currentUserIndex).setIssueCategoryID(issueCategoryID);
                }

                else if(messageArray.get(messageArray.size()-2).getContent().contains("Please provide a short description of the incident.")
                     || messageArray.get(messageArray.size()-3).getContent().contains("Please provide a short description of the incident.")){
                    // if the message two before contained "Please provide a short description...
                    GVoiceSMS.sendSMS("Please rate the issue on a scale of 1-5.", phoneNumber);
                    userArrayList.get(currentUserIndex).addToMessageArray(new SMS( null,"Please rate the issue on a scale of 1-5.", new Date()));

                    // we prompt for their likert scale assessment

                    userArrayList.get(currentUserIndex).setDescription(messageArray.get(messageArray.size()-1).getContent());
                    //and we store the previous text message as the description
                }

                else if(messageArray.get(messageArray.size()-2).getContent().contains("Please rate the issue on a scale of 1-5.")){

                    int likertScale = 1;  //the likert scale value
                    for(int i=1; i<=5; i++){
                        //this loop checks through the last text message to see which number it contains
                        if(messageArray.get(messageArray.size()-1).getContent().contains(Integer.toString(i))){
                            likertScale = i;
                            //and this number is set to the likertScale
                        }
                    }

                    userArrayList.get(currentUserIndex).setLikertScale(likertScale);
                    //sets the likertScale value for the currentUser's spot in the arraylist

                    //creates the confirmation text message
                    String response = "Thank you! Please confirm the following information:" + "Location ~ " + userArrayList.get(currentUserIndex).getLocation() + "; Category ~ " + userArrayList.get(currentUserIndex).getCategoryName() + "; Description ~ " + userArrayList.get(currentUserIndex).getDescription() + ". Reply YES to confirm.";
                    GVoiceSMS.sendSMS(response,phoneNumber);     //sends it out
                    userArrayList.get(currentUserIndex).addToMessageArray(new SMS(null, response, new Date()));


                }
                else if(messageArray.get(messageArray.size()-2).getContent().contains("Please confirm the following information")
                        || messageArray.get(messageArray.size()-3).getContent().contains("Please confirm the following information")    ){
                    if(smsString.toLowerCase().contains("yes")){     //if the message included "yes"
                        GVoiceSMS.sendSMS("Thank you! You are all set",phoneNumber);       //we're done!!
                        userArrayList.get(currentUserIndex).addToMessageArray(new SMS(null, "Thank you! You are all set", new Date()));

                        try {
                            SMSUser.HTTPPost(userArrayList.get(currentUserIndex));
                        } catch (IOException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        } catch (InterruptedException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }


                        GVoiceSMS.deleteSMS(currentUser);        //clears the user's thread (and hence allows for new conversations in the future
                    }

//                    else if(smsString.toLowerCase().contains("no")){           //if the message contained "no"
//                        GVoiceSMS.sendSMS("What would you like to change?", phoneNumber);        //we have some work to do
//                        userArrayList.get(currentUserIndex).addToMessageArray(new SMS( null,"What would you like to change?", new Date()));
//
//                        //code to handle this to come...
//                    }
                }

                }
            }
        }

        return userArrayList;

    }

    public static String getCharForNumber(int i) {
        int j = (i-1)%25;
        char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        return Character.toString(alphabet[i]);
    }

}
