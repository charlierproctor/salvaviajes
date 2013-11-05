import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: charlierproctor
 * Date: 11/2/13
 * Time: 7:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class SMSJSON {
    public static String readUrl(String urlString) throws Exception {
        //reads text in from url; returns the text as a string
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }

    public static ArrayList<String> parseJSONCategories(String jsonString){
        //returns an ArrayList of the categories;
        //categories.get(0) = 1st category...etc

        String s = jsonString;
        boolean inString = false;
        int countQuotes = 0;
        StringBuffer word = new StringBuffer("");
        ArrayList<String> stringArray = new ArrayList<String>(); //stores the categories...

        boolean justEnteredString = false;

        //remove bad quotation marks
        StringBuffer sB = new StringBuffer(s);
        for(int i = 0; i<sB.length(); i++){
            if(i>0 && i<sB.length() && Character.isLetter(sB.charAt(i-1)) && sB.charAt(i) == '"' && Character.isLetter(sB.charAt(i+1))){
                sB.deleteCharAt(i);
            }
        }

        s = sB.toString();

        //this for-loop creates an ArrayList (stringArray) with the words (between "") in the string
        for(int i = 0; i<s.length(); i++){
            justEnteredString = false;
            if(s.charAt(i) == '"'){
                countQuotes++;
                if(countQuotes%2 == 1){
                    inString = true;
                    justEnteredString = true;
                } else{
                    inString = false;
                    stringArray.add(word.toString());
                    word = new StringBuffer();
                }
            }
            if(inString && !justEnteredString){
                word.append(s.charAt(i));
            }
        }


        //remove text "CategoryName" and "CategoryId" --> leaving only the names of the categories themselves
        for(int i =0; i<stringArray.size(); i++){
                stringArray.remove("CategoryName");
                stringArray.remove("CategoryId");
        }

        return stringArray;
    }

    public static ArrayList<SMSUser> getIssuesFromDB(){
        //to load all reported issues from the database

        //initializing the variables
        ArrayList<SMSUser> usersInDBArrayList = null;
        JSONObject jsonObjectFromDB;
        int responseCode = 0;
        HttpsURLConnection con;
        boolean wasGetSuccessful = true;
        do{
            try{

                String url = "http://smsdatafest.azurewebsites.net/api/Issue";

            URL obj = new URL(url);

            con = (HttpsURLConnection) obj.openConnection();   //opens the connection

            // using GET method
            con.setRequestMethod("GET");

            responseCode = con.getResponseCode();    //gets the response code

            String s = con.getResponseMessage();     //gets the message (the actual response)
                //we parse through the response and save it as a json array
                Object genObj = JSONValue.parse(s);
                JSONArray jsonArray = (JSONArray) genObj;

                for(int i = 0; i<jsonArray.size(); i++){
                    //parse through json array
                    JSONObject currentJSONObject = (JSONObject) jsonArray.get(i);
 //     public SMSUser(String phoneNumber1, String location1, Date timestamp1, int issueCategoryID1, String categoryName1, int likertScale1, String description1){

                    //create the currentUser from the json object
                    SMSUser currentUser = new SMSUser((String) currentJSONObject.get("PhoneNumber"), (String) currentJSONObject.get("LocationDescription"), (Date) currentJSONObject.get("Timestamp"), Integer.valueOf(currentJSONObject.get("CategoryId").toString()), (String) currentJSONObject.get("CategoryDescription"), Integer.valueOf(currentJSONObject.get("LikertScale").toString()), (String) currentJSONObject.get("IssueDescription"));
                    usersInDBArrayList.add(currentUser);      //add the current user to the db array list
                }

            }catch (Exception e){        //catch the exception
                System.out.println(e);
            }


        } while (responseCode != 200);      //if we weren't successful, repeat the above

        return usersInDBArrayList;

    }

}
