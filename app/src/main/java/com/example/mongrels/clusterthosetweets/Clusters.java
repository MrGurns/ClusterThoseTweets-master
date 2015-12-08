package com.example.mongrels.clusterthosetweets;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

public class Clusters extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clusters);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton search = (FloatingActionButton) findViewById(R.id.search_button);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // this one will be the function that Initializes the search, then displays the data.
//                setContentView(R.layout.);
                EditText mEdit = (EditText) findViewById(R.id.search_bar);
                String searchValue = mEdit.getText().toString();
                Snackbar.make(view, "Searching...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                searchTwitter(searchValue);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_clusters, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search_bar) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void searchTwitter(String value) {
        //need to link up the button properly in all the views. already have most of it done, but also need to display tweet data
        TextView mText = (TextView) findViewById(R.id.search_content);
        mText.setText("Searching for " + value + "\n");
        TweetData tweets = new TweetData();
        tweets.delegate = this; //set delegate back
        tweets.setSearchTerm(value);
        tweets.searchTwitter();


        //would prefer to write it out in the thread, but not sure how.
//        mText.append("Printing out Clean Tweets.\n");
//        tweets.printToFile(); //doesn't work on emulator

        mText.append("----Cleaned Tweet Data----\n");
        mText.append(tweets.printMapForDisplay() + "\n");
        mText.append("\n----JSON Cluster Data----\n\n");
        mText.append(tweets.getJSONData().toString() + "\n");
        mText.append("\n----XML Cluster Data----\n\n");
        mText.append(tweets.printXML() + "\n");

        //TODO
        //1st write out and in from file the cleaned tweets (started)
        //2nd, display the data in columns (UI, Not started)
        //3rd, have it accessable by cluster, also be able to view the raw xml


        System.out.println("\nThat is all Folks!");
    }

    void printLine(String output) {
        System.out.println(output);
    }

}
