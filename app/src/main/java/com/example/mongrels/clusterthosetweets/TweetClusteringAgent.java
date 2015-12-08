/**
 * Created by Mongrels (Bryan) on 12/3/15.
 */

package com.example.mongrels.clusterthosetweets;
import android.os.AsyncTask;
import java.io.*;
import java.net.*;
import java.util.*;
import org.json.*;

public class TweetClusteringAgent extends AsyncTask<Void,String,JSONObject> {
    public TweetData delegate = null;
    JSONObject jsonRequest;
    static JSONObject jsonResponse;
    TweetClusteringAgent(Map<Long,Tweet> _tweets) throws JSONException {
        jsonRequest = formatRXNLPRequest(_tweets); //could just format them to the json representation here
    }
    @Override
    protected JSONObject doInBackground(Void... parms) {
        publishProgress("Clustering Tweets...");
        cluster();
        publishProgress("Clustering Tweets... Finished!");
        return jsonResponse;
    }
    @Override
    protected void onProgressUpdate(String... progress) {
        System.out.println(progress[0]);
    }
    protected void onPostExecute(JSONObject results) {
        delegate.printLine("Returning from Clustering Agent.");
        delegate.setJSONData(results);
    }

    private JSONObject formatRXNLPRequest(Map<Long,Tweet> __tweets) throws JSONException{
        JSONObject request = new JSONObject();
        JSONArray tweets = new JSONArray();
        request.put("type", "pre-sentenced");
        for(Long id: __tweets.keySet()){
            tweets.put(__tweets.get(id).asJSON());
        }
        request.put("text", tweets);
        return request;

    }
    public void cluster() {
        try {
            //unirest implementation does not work for android development -- had to migrate to this.
            //This is the target URL
            URL targetUrl = new URL("https://rxnlp-core.p.mashape.com/generateClusters");

            //First set the headers
            HttpURLConnection httpConnection = (HttpURLConnection) targetUrl.openConnection();
            httpConnection.setDoOutput(true);
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Accept", "application/json");
            httpConnection.setRequestProperty("Content-Type", "application/json");
            httpConnection.setRequestProperty("X-Mashape-Key", "aImJDp6j55mshaFAr0whSvOxkQyzp1kD8Uujsn5sxJqxjqowWg");

            //Next, process output
            OutputStream outputStream = httpConnection.getOutputStream();
            outputStream.write(jsonRequest.toString().getBytes());
            outputStream.flush();


            //Throw exception on error
            if (httpConnection.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + httpConnection.getResponseCode() + httpConnection.getErrorStream());
            }

            BufferedReader responseBuffer = new BufferedReader(new InputStreamReader((httpConnection.getInputStream())));

            //Printing output from server
            String output;
//            System.out.println("Output from Server:\n");
            if ((output = responseBuffer.readLine()) != null) {
//                System.out.println(output);
                try {
                    jsonResponse = new JSONObject(output);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //disconnect from server
            httpConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
