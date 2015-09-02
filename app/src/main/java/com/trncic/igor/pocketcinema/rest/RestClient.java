package com.trncic.igor.pocketcinema.rest;

import com.trncic.igor.pocketcinema.BuildConfig;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

/**
 * This code is written by following this tutorial, Singleton pattern is used
 * See <a href="http://inaka.net/blog/2014/10/10/android-retrofit-rest-client/"/>
 * Created by igortrncic on 6/10/15.
 */
public class RestClient {
    public static final String API_KEY = BuildConfig.API_KEY;
    private static MoviesService moviesService;
    private static String ROOT = "https://api.themoviedb.org/3";

    static {
        setupRestClient();
    }

    private RestClient() {
    }

    public static MoviesService get() {
        return moviesService;
    }

    private static void setupRestClient() {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ROOT)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addQueryParam("api_key", BuildConfig.API_KEY);
                    }
                })
                .build();

        moviesService = restAdapter.create(MoviesService.class);
    }
}
