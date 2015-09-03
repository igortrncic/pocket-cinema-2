package com.trncic.igor.pocketcinema.async;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.trncic.igor.pocketcinema.model.Movie;
import com.trncic.igor.pocketcinema.providers.MoviesProvider;

import java.util.ArrayList;


/**
 * This code is implemented with help of https://github.com/ab-helly
 */
public class MovieStoreAsyncTask extends AsyncTask<Movie, Void, Void> {

    private final Context mContext;

    public MovieStoreAsyncTask(Context context) {
        mContext = context;
    }

    @SafeVarargs
    @Override
    final protected Void doInBackground(Movie... params) {
        if (mContext != null) {
            Movie movie = params[0];
            Uri contentUri = MoviesProvider.MovieContract.CONTENT_URI;
            ContentResolver cr = mContext.getContentResolver();
            ArrayList<ContentValues> updateValues = new ArrayList<>();

            ContentValues value = new ContentValues();
            value.put(MoviesProvider.MovieContract._ID, movie.getId());
            value.put(MoviesProvider.MovieContract.TITLE, movie.getTitle());
            value.put(MoviesProvider.MovieContract.BACKDROP_PATH, movie.getBackdropPath());
            value.put(MoviesProvider.MovieContract.POSTER_PATH, movie.getPosterPath());
            value.put(MoviesProvider.MovieContract.OVERVIEW, movie.getOverview());
            value.put(MoviesProvider.MovieContract.VOTE_AVERAGE, movie.getVoteAverage());
            value.put(MoviesProvider.MovieContract.POPULARITY, movie.getPopularity());
            value.put(MoviesProvider.MovieContract.RELEASE_DATE, movie.getReleaseDate());
            Uri uri = cr.insert(contentUri, value);
            if (uri.compareTo(Uri.withAppendedPath(contentUri, Long.toString(movie.getId()))) < 0) {
                updateValues.add(value);
            }

            for (ContentValues updateValue : updateValues) {
                cr.update(contentUri, value, MoviesProvider.MovieContract._ID + "=?",
                        new String[]{value.getAsString(MoviesProvider.MovieContract._ID)});
            }
        }
        return null;
    }
}
