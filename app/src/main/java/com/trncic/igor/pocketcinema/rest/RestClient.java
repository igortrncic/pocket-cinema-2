package com.trncic.igor.pocketcinema.rest;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trncic.igor.pocketcinema.BuildConfig;

import io.realm.RealmObject;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * This code is written by following this tutorial, Singleton pattern is used
 * See <a href="http://inaka.net/blog/2014/10/10/android-retrofit-rest-client/"/>
 * Created by igortrncic on 6/10/15.
 */
public class RestClient {
    private static MoviesService moviesService;
    private static String ROOT = "https://api.themoviedb.org/3";

    public static final String API_KEY = BuildConfig.API_KEY;

    static {
        setupRestClient();
    }

    private RestClient() {}

    public static MoviesService get() {
        return moviesService;
    }

    private static void setupRestClient() {

        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ROOT)
                .setConverter(new GsonConverter(gson))
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
