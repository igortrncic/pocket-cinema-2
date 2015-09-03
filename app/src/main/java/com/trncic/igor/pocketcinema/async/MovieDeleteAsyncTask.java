package com.trncic.igor.pocketcinema.async;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.trncic.igor.pocketcinema.model.Movie;
import com.trncic.igor.pocketcinema.providers.MoviesProvider;

/**
 * AsyncTask to store movie list to db.
 * TODO: use {@link android.content.AsyncQueryHandler} if database grows.
 * Created by abhelly on 24.06.15.
 */
public class MovieDeleteAsyncTask extends AsyncTask<Movie, Void, Void> {

    private final Context mContext;

    public MovieDeleteAsyncTask(Context context) {
        mContext = context;
    }

    @SafeVarargs
    @Override
    final protected Void doInBackground(Movie... params) {
        if (mContext != null) {
            Movie movie = params[0];
            Uri contentUri = MoviesProvider.MovieContract.CONTENT_URI;
            ContentResolver cr = mContext.getContentResolver();

            cr.delete(MoviesProvider.MovieContract.CONTENT_URI,
                    MoviesProvider.MovieContract._ID + " = ? ",
                    new String[]{String.valueOf(movie.getId())});
        }
        return null;
    }
}
