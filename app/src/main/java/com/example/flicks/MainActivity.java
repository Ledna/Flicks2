package com.example.flicks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.widget.Adapter;
import android.widget.Toast;

import com.example.flicks.models.Config;
import com.example.flicks.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    //constants
    //the base URL for the API
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    //the parameter for the API key
    public final static String API_KEY_PARAM = "api_key";
    //tag for logging from this activity
    public final static String TAG = "MainActivity";

//    String posterSize;
//    String imageBaseUrl;
    //instance field
    AsyncHttpClient Client;
    //the list of currently movie
    ArrayList<Movie> movies;
    //the recycler View
    RecyclerView rvMovie;
    //the adapter wired to the recycler View
    MovieAdapter adapter;
    //image config
    Config config;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //initialize the client
        Client = new AsyncHttpClient();
        //initialize the list of movie
        movies = new ArrayList<>();
        //initialize the adapter--movies Array cannot reinitialized after this point
        adapter = new MovieAdapter(movies);

        //resolve the recycler view and connect a layout manager and the adapter
        rvMovie = (RecyclerView) findViewById(R.id.rvMovies);
        rvMovie.setLayoutManager(new LinearLayoutManager(this));
        rvMovie.setAdapter(adapter);

        //get the configuration on app creation
        getConfiguration();

    }

    //get the list of currently playing from the API
    private void getNowPlaying() {
//create the url
        String url = API_BASE_URL + "/movie/now_playing";
        //set the request parameters
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));//API-key, always required
        // execute a GET request expecting a JSON object response
        Client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                super.onSuccess(statusCode, headers, response);
                //load the results into movies list
                try {
//                    System.out.println("totest");
                    JSONArray results = response.getJSONArray("results");
                    //iterate through result set and create Movies object
                    for(int i = 0; i < results.length(); i++){
                        Movie movie = new Movie(results.getJSONObject(i));
                        movies.add(movie);
                        //notify adapter that a row was added
                        adapter.notifyItemInserted(movies.size() -1);
                    }
                    Log.i(TAG, String.format("Loaded %s movies", results.length()));
                    //get the now playing movie list
//                    getNowPlaying();
                } catch (JSONException e) {
                    logError("Failed to parse now playing movies", e, true);
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
               logError("Failed to get data from now playing endpoint",throwable, true);
            }

//            @Override
//            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//
//            }
//
//            @Override
//             public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//
//            }
        });
    }
        //get the configuration from the API
        private void getConfiguration () {
            //create the url
            String url = API_BASE_URL + "/configuration";
            //set the request parameters
            RequestParams params = new RequestParams();
            params.put(API_KEY_PARAM, getString(R.string.api_key));//API-key, always required
            // execute a GET request expecting a JSON object response
            Client.get(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    try {
//                        JSONObject images = response.getJSONObject("images");
//                        imageBaseUrl = images.getString("secure_base_url");
//                        JSONArray posterSizeOptions = images.getJSONArray("poster_sizes");
//                        posterSize = posterSizeOptions.optString(3, "w342");
                        config  = new Config(response);
                        Log.i(TAG, String.format("Loaded configuration with imageBaseUrl %s and posterSize %s", config.getImageBaseUrl(), config.getPosterSize()));
                        //pass config to adapter
                        adapter.setConfig(config);
                        getNowPlaying();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        logError("Failed parsing Configuration", e, true);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    logError("Failed getting configuration", throwable, true);
                }
            });
        }

        //handle errors, log and alert user
        private void logError (String message, Throwable error,boolean alertUser){
            //always log the error
            Log.e(TAG, message, error);
            //alert the user to avoid silent errors
            if (alertUser) {
                //show a long toast with the error message
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        }
    }



