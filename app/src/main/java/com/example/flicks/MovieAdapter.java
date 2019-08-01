package com.example.flicks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flicks.models.Movie;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    //list of movies
    ArrayList<Movie> movies;

    @NonNull

    //creates and inflates a new View
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //get the context and creates the inflater
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        //create the view using this item_movie layout
        View movieView = inflater.inflate(R.layout.item_movie,parent,false);
        //return a new ViewHolder
        return new ViewHolder (movieView);
    }

     //binds and inflates view to a new item
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
     // get the movie data at the specified position
        Movie movie=movies.get(position);
        //populate the view with the movie data
        holder.tvTitle.setText(movie.getTitle());
        holder.tvOverView.setText(movie.getOverview());

        // TODO- set image using Glide
    }
   //returns the total number items in the list
    @Override
    public int getItemCount() {
        return movies.size();
    }

    //initialize with list
    public MovieAdapter(ArrayList<Movie> movies) {
        this.movies = movies;

    }
    //create the viewholder as a static inner class
    public static class ViewHolder extends RecyclerView.ViewHolder{
      //track view objects
      ImageView ivPosterImage;
      TextView tvTitle;
      TextView tvOverView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //lookup view objects by id
            ivPosterImage = (ImageView) itemView.findViewById(R.id.ivPosterimage);
            tvOverView = (TextView) itemView.findViewById(R.id.tvOverview);
            tvTitle =  (TextView) itemView.findViewById(R.id.tvTitle);
        }
    }

}