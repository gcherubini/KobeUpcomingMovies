package com.example.interview.model;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelMovie extends RealmObject {

    @Ignore
    private static int kMOVIES_PER_PAGE = 20;

    // Position of a loaded movie used to order correctly the list
    @PrimaryKey
    int position;

    int id;
    String title;
    String overview;
    String popularity;
    @JsonProperty("poster_path")
    String posterPath;
    @JsonProperty("release_date")
    String releaseDate;

    @JsonProperty("genre_ids")
    @Ignore
    ArrayList<Integer> genresList;

    String genres;

    /**
     * Getting from database the genres names, generating a String and updating movie data model
     */
    public static void updateMovieModelWithGenresString(Context ctx, ModelMovie m, String methodCalled, Realm realm) {
        try {
            String genresIds = "";
            for(Integer genreId : m.getGenresList()){
                ModelGenre genre = realm.where(ModelGenre.class).equalTo("id", genreId).findFirst();
                if(genre != null && genre.getName() != null && !genre.getName().isEmpty()) {
                    genresIds += genre.getName() + " / ";
                }
            }
            m.setGenres(genresIds);
        }
        catch (Exception e) {
            Log.e(methodCalled, "Could not generate the movies genres string with persisted genres");
            throw e;
        }
    }

    /**
     * Discover a range of positions of a specific page and return the first position
     * @param page specific page to load
     * @return first position of a discovered range
     */
    public static int getFirstPositionForSpecificPage(int page) {
        return (page * kMOVIES_PER_PAGE) - kMOVIES_PER_PAGE + 1;
    }

    /**
     * Discover a range of positions of a specific page and return the last position
     * @param page specific page to load
     * @return last position of a discovered range
     */
    public static int getLastPositionForSpecificPage(int page) {
        return (page * kMOVIES_PER_PAGE);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public ArrayList<Integer> getGenresList() {
        return genresList;
    }

    public void setGenresList(ArrayList<Integer> genresList) {
        this.genresList = genresList;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}


