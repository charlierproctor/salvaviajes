import com.techventus.server.voice.datatypes.records.SMS;

import sun.net.www.protocol.http.HttpURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: charlierproctor
 * Date: 11/2/13
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */

public class SMSUser implements java.io.Serializable{
    //this class stores information about users...such as their phoneNumber, location, timestamp, etc...

    private String phoneNumber;
    private String location;
    private Date timestamp;
    private int issueCategoryID;
    private String categoryName;
    private int likertScale;
    private String description;
    private int upVoteCounts;
    private ArrayList<SMS> messageArray;

    public SMSUser(String phoneNumberInput, Date timestampInput){   //constructor from phone num and timestamp
        phoneNumber = phoneNumberInput;
        timestamp = timestampInput;
    }
    //full constructor
    public SMSUser(String phoneNumber1, String location1, Date timestamp1, int issueCategoryID1, String categoryName1, int likertScale1, String description1){
        phoneNumber = phoneNumber1;
        location = location1;
        timestamp = timestamp1;
        issueCategoryID = issueCategoryID1;
        categoryName = categoryName1;
        likertScale = likertScale1;
        description = description1;
    }

    //set methods
    public void setLocation(String locationInput){
        location = locationInput;
    }

    public void setDescription(String descriptionInput){
        description = descriptionInput;
    }

    public void setIssueCategoryID(int categoryIDInput){
        issueCategoryID = categoryIDInput;

        //loads in the category array
        String jsonCategories = "";
        try {
            jsonCategories = SMSJSON.readUrl("http://smsdatafest.azurewebsites.net/api/Category");
            //reads in the text from the url
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        System.out.println("JSON: " + jsonCategories);

        ArrayList<String> categories = SMSJSON.parseJSONCategories(jsonCategories);
        //our category name array (id 1 = .get(0)...etc)

        categoryName = categories.get(issueCategoryID-1);
    }
    public void setLikertScale(int likertScaleInput){
        likertScale = likertScaleInput;
    }
    public void setMessageArray(ArrayList<SMS>messageArrayInput){
        messageArray = messageArrayInput;
    }
    public void addToMessageArray(SMS newMessage){
        messageArray.add(newMessage);
    }

    //get methods
    public String getLocation(){
        return location;
    }
    public String getDescription(){
        return description;
    }
    public String getCategoryName(){
        return categoryName;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }

    public ArrayList<SMS> getMessageArray(){
        return messageArray;
    }
    public int getIssueCategoryID(){
        return issueCategoryID;
    }
    public int getLikertScale(){
        return likertScale;
    }
    public Date getTimestamp(){
        return timestamp;
    }

    //HTTP POST method -- to save new users
    public static void HTTPPost(SMSUser currentUser) throws IOException, InterruptedException {

        Date now = new Date();       //now!

  //      String csvString = "----" + currentUser.getLocation() + "-" + now + "-" + currentUser.getIssueCategoryID() + "-" + currentUser.getCategoryName() + "-" + currentUser.getLikertScale() + "-" + currentUser.getDescription() + "-" + currentUser.getPhoneNumber();
                                          //csv string to send to db
        int responseCode;
        HttpURLConnection con;
        boolean wasGetSuccessful = true;
        do{

            // Construct data
            String data = "";

            data += URLEncoder.encode("LocationDescription", "UTF-8") + "=" + URLEncoder.encode(currentUser.getLocation(), "UTF-8");
            data += URLEncoder.encode("Timestamp", "UTF-8") + "=" + URLEncoder.encode(now.toString(),"UTF-8");
            data += URLEncoder.encode("CategoryId", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(currentUser.getIssueCategoryID()),"UTF-8");
            data += URLEncoder.encode("CategoryDescription", "UTF-8") + "=" + URLEncoder.encode(currentUser.getCategoryName(), "UTF-8");
            data += URLEncoder.encode("LikertScale", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(currentUser.getLikertScale()), "UTF-8");
            data += URLEncoder.encode("IssueDescription", "UTF-8") + "=" + URLEncoder.encode(currentUser.getDescription(), "UTF-8");
            data += URLEncoder.encode("PhoneNumber", "UTF-8") + "=" + URLEncoder.encode(currentUser.getPhoneNumber(), "UTF-8");


            String url = "http://smsdatafest.azurewebsites.net/api/Issue";
                                                 //url to post to
            URL obj = new URL(url);
                con = (HttpURLConnection) obj.openConnection();     //opens connection


            // Send data
            con.setDoOutput(true);
            con.setDoInput(true); //

            // No caching, we want the real thing.
            con.setUseCaches (false);
            // Specify the content type.
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
            wr.write(data);
            wr.flush();

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                // Process line...
                System.out.println(line);
            }
            wr.close();
            rd.close();


//            con.setRequestMethod("POST");       //POST method
//            con.setRequestProperty("Content-Length", "" +
//                    csvString.length());
            responseCode = con.getResponseCode();     //get the response code


        } while (responseCode != 200);   //repeat the above while we aren't successful


    }

}
