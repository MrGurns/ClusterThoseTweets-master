package com.example.mongrels.clusterthosetweets;

import org.json.*;
import java.util.*;
import java.io.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by MrGurns on 12/6/15.
 */
class TweetData {
    public Clusters delegate = null;
    private String searchTerm;
    private JSONObject clusterResults;
    private Map<Long, Tweet> rawTweets = new HashMap<Long, Tweet>();;

    TweetData() { }   //default, set memory
    TweetData(String _searchTerm) {     //set just search terms, and memory
        searchTerm = _searchTerm;
    }
    //set all values and memory
    TweetData(String _searchTerm, JSONObject _clusterResults, Map<Long, Tweet> _rawTweets) {
        searchTerm = _searchTerm;
        clusterResults = _clusterResults;
        rawTweets.putAll(_rawTweets);
    }
    //setters
    public void setSearchTerm(String _searchTerm)         { searchTerm = _searchTerm; }
    public void setJSONData(JSONObject _clusterResults)   { clusterResults = _clusterResults; }
    public void setTweetData(Map<Long, Tweet> _rawTweets) { rawTweets.putAll(_rawTweets); }

    //getters
    public String           getSearchTerm()               { return searchTerm; }
    public JSONObject       getJSONData()                 { return clusterResults; }
    public Map<Long, Tweet> getTweetData()                { return rawTweets; }

    //need function to print relevent data, in a line format, as well as to file
    public void printToFile() {
        //have to out put the cluster and the associated data
        //searchTerm_date_Cluster.txt
        //searchTerm_date_Tweets.txt
        try {
            PrintWriter writer = new PrintWriter(searchTerm + "_tweets.txt");
            writer.println(printMapForFile());
            writer.close();
        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
            System.out.println("Error with output file '" + searchTerm + "_tweets.txt" + "'");
        }

        try {
            PrintWriter writer = new PrintWriter(searchTerm + "_cluster.txt");
            writer.println(clusterResults.toString());
            writer.close();
        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
            System.out.println("Error with output file '" + searchTerm + "_cluster.txt" + "'");
        }
    }

    public void readFromFile() {
        //if searchTerm does not have files, display no files found, run search.
        File file = new File( searchTerm + "_tweets.txt" );
        BufferedReader reader = null;
        if (file.exists()) {
            try {
                reader = new BufferedReader(new FileReader(file));
                //read in tweet file data.
                String line = reader.readLine(); //need to parse it appropriately
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        //read in cluster data
        file = new File( searchTerm + "_cluster.txt" );
        if (file.exists()) {
            try {
                reader = new BufferedReader(new FileReader(file));
                //read in cluster file data.
                clusterResults = new JSONObject(reader.readLine());
                reader.close();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public String printMapForDisplay() {
        String output = "";
        Iterator<Map.Entry<Long, Tweet>> it = rawTweets.entrySet().iterator();
        Map.Entry<Long, Tweet> pair;
        while (it.hasNext()) {
            pair = it.next();
            long tweetKey = pair.getKey();
            Tweet tweet = pair.getValue();
            output += /*"(" + tweetKey + ") " +*/ tweet.getCleanedName() + " : " + tweet.getCleanedText() + "\n\n"; //prints out the tweet data
//            it.remove(); // avoids a ConcurrentModificationException
        }
        return output;
    }

    private String printMapForFile() {
        String output = "";
        Iterator<Map.Entry<Long, Tweet>> it = rawTweets.entrySet().iterator();
        Map.Entry<Long, Tweet> pair;
        while (it.hasNext()) {
            pair = it.next();
            long tweetKey = pair.getKey();
            Tweet tweet = pair.getValue();
            output += "(" + tweetKey + ") " + tweet.getStatus().getUser().getName() + " : " + tweet.getCleanedText() + ":"; //prints out the tweet data and other relevent needed data...
//            it.remove(); // avoids a ConcurrentModificationException
        }
        return output;
    }
    public String printXML() {
        String xml = null;
        if (clusterResults != null) {
            try {
                xml = XML.toString(clusterResults);                                //convert json to xml
            } catch (JSONException e) { //**it's lying. it does throw a jsonException**
                e.printStackTrace();
            }
        }
        return xml;
    }

    public void searchTwitter() {
        //Gathers The Tweets by calling the TweetCollectionAgent AsyncTask Thread
        try {
            TweetCollectionAgent tweetGatherer = new TweetCollectionAgent(searchTerm, 50); //change this variable to increase the amount queried for
            tweetGatherer.delegate = this;
            tweetGatherer.execute();
            this.setTweetData(tweetGatherer.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        //Clusters The Tweets by calling the TweetClusteringAgent AsyncTask Thread
        try {
            TweetClusteringAgent tweetClusterer = new TweetClusteringAgent(this.getTweetData());
            tweetClusterer.delegate = this;
            tweetClusterer.execute();
            this.setJSONData(tweetClusterer.get());//remove the setJSONData if issues
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }
    }
    void printLine(String output) {
        System.out.println(output);
    }
}
