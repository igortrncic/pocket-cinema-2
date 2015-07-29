package com.trncic.igor.pocketcinema.model;

/**
 * Created by igortrncic on 7/6/15.
 */
public class MovieUtils {

    public static final String IMAGES_BASE_URL = "http://image.tmdb.org/t/p/";

    public static final String IMAGES_SIZE_185 = "w185/";
    public static final String IMAGES_SIZE_342 = "w342/";
    public static final String IMAGES_SIZE_500 = "w500/";
    public static final String IMAGES_SIZE_780 = "w780/";
    public static final String ORIGINAL = "original/";

    public static String getBackdropPath(Movie movie, String size){
        return IMAGES_BASE_URL + size + movie.getBackdropPath();
    }

    public static String getPosterPath(Movie movie, String size){
        return IMAGES_BASE_URL + size + movie.getPosterPath();
    }
}
