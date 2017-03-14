package com.example.interview.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.interview.R;
import com.example.interview.network.NetworkApiTheMovieDb;
import com.squareup.picasso.Picasso;


public class ActivityMovieDetails extends AppCompatActivity {
    private static final String TAG = "ActivityMovieDetails";

    // Intent extra parameters
    private String extraMoviePopularity;
    private String extraMovieImage;
    private String extraMovieOverview;
    private String extraMovieTitle;
    private String extraMovieReleaseDate;

    // UI Elements
    private TextView movieTitleTextView;
    private ImageView movieImage;
    private TextView movieOverviewTextView;
    private TextView moviePopularityTextView;
    private TextView movieReleaseDateTextView;
    private TextView movieGenresTextView;
    private String extraMovieGenres;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details);

        // Load extra params
        Intent myIntent = getIntent();
        extraMovieGenres = myIntent.getStringExtra("movieGenres");
        extraMovieTitle = myIntent.getStringExtra("movieTitle");
        extraMovieOverview = myIntent.getStringExtra("movieOverview");
        extraMovieImage = myIntent.getStringExtra("movieImage");
        extraMoviePopularity = myIntent.getStringExtra("moviePopularity");
        extraMovieReleaseDate = myIntent.getStringExtra("movieReleaseDate");


        // UI elements
        movieTitleTextView =  (TextView) findViewById(R.id.movie_details_title);
        moviePopularityTextView =  (TextView) findViewById(R.id.movie_details_popularity);
        movieReleaseDateTextView =  (TextView) findViewById(R.id.movie_details_release_date);
        movieOverviewTextView =  (TextView) findViewById(R.id.movie_details_overview);
        movieGenresTextView =  (TextView) findViewById(R.id.movie_details_genres);
        movieImage =  (ImageView) findViewById(R.id.movie_details_image);
    }


    @Override
    protected void onResume() {
        super.onResume();
        populateExtraParams();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateExtraParams() {
        // Load data in UI elements
        setTitle(extraMovieTitle);
        movieTitleTextView.setText(extraMovieTitle);
        moviePopularityTextView.setText("Popularity: " + extraMoviePopularity);
        movieOverviewTextView.setText(extraMovieOverview);
        movieReleaseDateTextView.setText(extraMovieReleaseDate);
        movieGenresTextView.setText(extraMovieGenres);
        String imageUrl = String.format(NetworkApiTheMovieDb.API_IMG_URL, extraMovieImage);
        Picasso.with(this).load(imageUrl).into(movieImage);
    }
}
