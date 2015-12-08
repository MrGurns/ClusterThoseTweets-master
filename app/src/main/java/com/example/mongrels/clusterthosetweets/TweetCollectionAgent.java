/**
 * Created by Mongrels on 12/3/15
 */

package com.example.mongrels.clusterthosetweets;
import android.os.AsyncTask;
import java.util.*;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

public class TweetCollectionAgent extends AsyncTask<Void,String,Map<Long, Tweet>> {
    public TweetData delegate = null;
    private Query query;
    private long marker;
    private Twitter twitter;
    static Map<Long, Tweet> sink = new HashMap<Long, Tweet>();

    public TweetCollectionAgent(String _queryString, Integer _count){
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("RN8utiONEPbzD4t3fZrD8iSXB")
                .setOAuthConsumerSecret("7140hVLtfjLj8idnwbTygG8gTntMBo9FHRS18r5CcsZ9Sffesq")
                .setOAuthAccessToken("4138308738-ihEjmUCvOCSnB7oXLRLec4L8lHxzlhg2YkdTJZk")
                .setOAuthAccessTokenSecret("u39fFe5ki5tbJF78SY90aiWm4PayqgOUI2uMOOjWJOeBK");
        twitter = new TwitterFactory(cb.build()).getInstance();

        marker = Long.MAX_VALUE;
        query = new Query(_queryString);
        query.setLang("en");
        query.setCount(_count);
    }
    @Override
    protected Map<Long, Tweet> doInBackground(Void... parms) {
        publishProgress("Gathering Tweets...");
        try {
            collect();
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        publishProgress("Gathering Tweets... Finished!");
        return sink;
    }
    @Override
    protected void onProgressUpdate(String... progress) {
        System.out.println(progress[0]);
    }
    protected void onPostExecute(Map<Long, Tweet> results) {
        delegate.printLine("Returning from Collection Agent.");
        delegate.setTweetData(results);

    }
    public void collect() throws TwitterException{
        int count = query.getCount();
        while(sink.size() < count) {
            int requestSize = (count - sink.size()) > 100 ? 100 : count - sink.size();
            query.setCount(requestSize);
            query.setMaxId(marker);
            QueryResult result = twitter.search(query);

            if(result.getCount() == 0){
                System.out.println("no tweets returned for the request query");
                break;
            }
            for(twitter4j.Status tweet : result.getTweets()){
                if(!sink.containsKey(tweet.getId())){
                    sink.put(tweet.getId(), new Tweet(tweet));
                    //System.out.println(tweet);
                    //System.out.println("");
                    if(tweet.getId() < marker){
                        marker = tweet.getId() - 1;
                    }
                }
            }
        }
    }
}
