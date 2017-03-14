package com.example.interview.network;

import com.example.interview.model.ModelGenre;
import com.example.interview.model.ModelMovie;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Class responsible to access The ModelMovie DB API via Retrofit/HTTP
 */
public class NetworkApiTheMovieDb {

    static final String API_URL = "https://api.themoviedb.org/3/";
    public static final String API_IMG_URL = "https://image.tmdb.org/t/p/w185%s";
    public static final String API_KEY = "c5850ed73901b8d268d0898a8a9d8bff";
    static final String MOVIES_UPCOMING_ENDPOINT = "movie/upcoming";
    static final String MOVIES_GENRES_LIST = "genre/movie/list";
    static final String MOVIES_SEARCH = "search/movie";

    private static TheMovieDbNetworkApiInterface client;

    public interface TheMovieDbNetworkApiInterface {
        /**
         * Get last upcoming movies
         * @param apiKey theMovieDB api key
         * @param page pagination page to be loaded
         * */
        @GET(MOVIES_UPCOMING_ENDPOINT)
        Call<UpcomingMoviesResult> getUpcomingMovies(@Query("api_key") String apiKey,
                                                     @Query("page") int page);

        /**
         * Search for movies with query string
         * @param apiKey theMovieDB api key
         * @param query filter movies passing a movie title
         *  @param page pagination page to be loaded
         * */
        @GET(MOVIES_SEARCH)
        Call<UpcomingMoviesResult> searchMovies(@Query("api_key") String apiKey,
                                                @Query("query") String query,
                                                @Query("page") int page);

        /**
         * Get TheMovieDB genres list
         * @param apiKey theMovieDB api key
         * */
        @GET(MOVIES_GENRES_LIST)
        Call<GenresListResult> getGenresList(@Query("api_key") String apiKey);
    }

    /**
     * Retrofit configuration and instantiation
     * */
    public static TheMovieDbNetworkApiInterface getClient() {
        if(client == null) {
            final Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(JacksonConverterFactory.create())
                    .baseUrl(API_URL)
                    .build();

            client = retrofit.create(TheMovieDbNetworkApiInterface.class);
        }

        return client;
    }


    // Response bodies
  	@JsonIgnoreProperties(ignoreUnknown = true)
  	public static class UpcomingMoviesResult {
        String page;
        String total_pages;
        String total_results;
        List<ModelMovie> results;

        public UpcomingMoviesResult() {}

        public List<ModelMovie> getResults() {
            return results;
        }

        public String getPage() {
            return page;
        }

        public String getTotalPages() {
            return total_pages;
        }

        public String getTotalResults() {
            return total_results;
        }
    }

    // Response bodies
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GenresListResult {
        List<ModelGenre> genres;

        public List<ModelGenre> getGenres() {
            return genres;
        }

        public void setGenres(List<ModelGenre> genres) {
            this.genres = genres;
        }
    }
}
