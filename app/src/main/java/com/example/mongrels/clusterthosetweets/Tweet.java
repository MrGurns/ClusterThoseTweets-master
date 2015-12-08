/**
 * Created by Mongrels (Juan) on 12/3/15
 */
package com.example.mongrels.clusterthosetweets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONException;
import org.json.JSONObject;

public class Tweet {
    private twitter4j.Status status;
    public Tweet(twitter4j.Status _status){
        status = _status;
    }
    public twitter4j.Status getStatus() {
        return status;
    }
    public String getCleanedName() { return removeUrl(status.getUser().getName().toLowerCase()); }
    public String getCleanedText(){
        return removeUrl(status.getText().toLowerCase());
    }
    public JSONObject asJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("sentence", getCleanedText());
        return json;
    }
    private String removeUrl(String commentStr){
        {
            String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
            Pattern p = Pattern.compile(urlPattern,Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(commentStr);
            int i = 0;
            while (m.find()) {
                commentStr = commentStr.replaceAll(m.group(i),"").trim();
                i++;
            }
            commentStr = commentStr.replaceAll( "[^\\p{L}\\p{Nd}]+"," " );
            return commentStr;

        }
    }
}
