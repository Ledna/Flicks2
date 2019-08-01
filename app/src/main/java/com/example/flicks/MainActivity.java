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
    public final static String TAG = "MovieListActivity";

    //instance field
    AsyncHttpClient Client;
    //the base url for loading images
    String imageBaseUrl;
    //the poster size to use when fetching images, part of the url
    String posterSize;
    //the list of currently movie
    ArrayList<Movie> movies;
    //the recycler View
    RecyclerView rvMovies;
    //the adapter wired to the recycler View
    MovieAdapter adapter;




    @SuppressLint("WrongViewCast")
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
        rvMovies = (RecyclerView) findViewById(R.id.rvMovies);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        rvMovies.setAdapter(adapter);

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
                super.onSuccess(statusCode, headers, response);
                //load the results into movies list
                try {
                    JSONArray results = response.getJSONArray("results");
                    //iterate through result set and create Movies object
                    for(int i = 0; i<results.length();i++){
                        Movie movie = new Movie(results.getJSONObject(i));
                        movies.add(movie);
                        //notify adapter that a row was added
                        adapter.notifyItemInserted(movies.size() -1);
                    }
                    Log.i(TAG,String.format("loaded %s movies",results.length()));
                    //get the now playing movie list
                    getNowPlaying();
                } catch (JSONException e) {
                    logError("Failed to parse now playing movies",e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
               logError("Failed to get data from now playing endpoint",throwable, true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }
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
                        JSONObject images = response.getJSONObject("images");
                        //get the image base url
                        imageBaseUrl = images.getString("secure base url");
                        //get the poster size
                        JSONArray posterSizeOptions = images.getJSONArray("poster_size");
                        //user the option at index 3 or w342 as fallback
                        posterSize = posterSizeOptions.optString(3, "w342");
                        Log.i(TAG,String.format("Loaded configuration with imageBaseUrl %s posterSize %s",imageBaseUrl,posterSize));
                    } catch (JSONException e) {
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



