package com.example.mongrels.clusterthosetweets;

import android.os.AsyncTask;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.IOException;
import java.util.*;

/**
 * Created by MrGurns on 12/5/15.
 */
public class clusterCleanTweets extends AsyncTask<Void,String,JSONObject> {
    List<CleanTweetData> cleanTweets = new ArrayList<>();

    clusterCleanTweets(List<CleanTweetData> myTweets) {
        cleanTweets = myTweets;
    }

    @Override
    protected JSONObject doInBackground(Void... parms) {
        String request = formatTweets(cleanTweets);                    //Clean Tweets
        //now that the tweets are formatted in json, we can display it or continue processing

        //RxNLP API -- turn this into a request like above
        HttpResponse<JsonNode> postResponse = null;
        try {
            postResponse = Unirest.post("https://rxnlp-core.p.mashape.com/generateClusters")
                    .header("X-Mashape-Key", "aImJDp6j55mshaFAr0whSvOxkQyzp1kD8Uujsn5sxJqxjqowWg")
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .body(request)
                    .asJson(); //issue here. may need to convert it to string, then to json
        } catch (UnirestException e) {
            e.printStackTrace();
        }

        try {
            Unirest.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //convert to xml. could prompt, or could continue, but this data should be cleaned up to be used properly
        JSONObject json = null;    //POST response to json
        if (postResponse != null) {
            try {
                json = new JSONObject(postResponse.getBody().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Tweets have been Clustered!\n");
        return json;
    }

    private static String formatTweets(List<CleanTweetData> myTweets) {
        //body header
        String body = "{" + "\"type\":\"pre-sentenced\"," + "\"text\":[";

        //format each item
        for (CleanTweetData current : myTweets) {
            body += "{" + "\"sentence\":";
            body += "\"" + current.getText() + "\"},";
        }
        //remove last comma
        body = body.substring(0, body.length() - 1);
        //add final stuff
        body += "]}";
        body.trim();
        return body;
    }

    //function does not work yet.
    private static JSONObject formatTweetsInJSON(List<CleanTweetData> myTweets) throws JSONException {

        JSONArray myArray = new JSONArray();
        for( CleanTweetData current : myTweets) {
            myArray.put(current.getJSONText());
        }

        JSONObject mainObject = new JSONObject();
        mainObject.put("text",myArray);

        JSONObject returnObject = new JSONObject();
        returnObject.put("type","pre-sentenced");
//        returnObject.put(mainObject);
        return returnObject;
    }
}
