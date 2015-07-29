package com.trncic.igor.pocketcinema.rest;

import com.trncic.igor.pocketcinema.model.MoviesResponse;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by igortrncic on 6/10/15.
 */
public interface MoviesService {

    @GET("/discover/movie")
    void discoverMovies(@Query("sort_by") String sort_by, Callback<MoviesResponse> callback);

    @GET("/movies/{id}/videos")
    void getTrailers(@Path("id") long id, Callback<MoviesResponse> callback);

    @GET("/movies/{id}/reviews")
    void getReviews(@Path("id") long id, Callback<MoviesResponse> callback);
}
