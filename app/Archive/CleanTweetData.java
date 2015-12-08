package com.example.mongrels.clusterthosetweets;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by MrGurns on 12/5/15.
 */
public class CleanTweetData {
    Date    date;
    String  dirtyName;
    String  dirtyText;
    public CleanTweetData( Date _date, String _name, String _text ) {
        date        = _date;
        dirtyName   = _name;
        dirtyText   = _text;
    }
    public Date     getDate()       { return date; }
    public String   getName()       { return cleanData(dirtyName); }
    public String   getText()       { return cleanData(dirtyText); }
    public String   getDirtyName()  { return dirtyName; }
    public String   getDirtyText()  { return dirtyText; }

    public void setDate(Date _date)     { date = _date; }
    public void setName(String _name)   { dirtyName = _name; }
    public void setText(String _text)   { dirtyText = _text; }

    private String cleanData(String input) {
        String output = input;
        output = removeUrl(output);
        output = output.toLowerCase();
//        output = output.replaceAll("[-+.^:;,']", "");
        output = output.replaceAll("[^\\p{L}\\p{Nd}]+", " "); //could combine these two replaces
        output = output.replaceAll("rt", "");
//        output = output.replaceAll("\\P{Print}", ""); // this needs to change to not remove apostrophes, maybe?
        return output;
    }
    //this function is not behaving properly. it should clean the url's completely out of the tweet.
    private static String removeUrl(String cleanStr)
                     { //"/(?:(?:https?|ftp|file)://|www.|ftp.)(?:([-A-Z0-9+&@#/%=~_|$?!:;,.]*)|[-A-Z0-9+&@#/%=~_|$?!:;,.])*(?:([-A-Z0-9+&@#/%=~_|$?!:;,.]*)|[A-Z0-9+&@#/%=~_|$])/ix";
        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(cleanStr);
        int i = 0;
        while (m.find()) {
            cleanStr = cleanStr.replaceAll(m.group(i),"").trim();
            i++;
        }
        return cleanStr;
    }

    //a function to get the json object here would be nice, to reduce the formatting later    private static String formatTweets(List<CleanTweetData> myTweets) {
    public JSONObject getJSONText() throws JSONException {
        JSONObject myNode = new JSONObject();
        myNode.put("sentence", cleanData(dirtyText));
        return myNode;
    }
}
