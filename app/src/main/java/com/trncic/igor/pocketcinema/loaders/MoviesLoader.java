package com.trncic.igor.pocketcinema.loaders;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

import com.trncic.igor.pocketcinema.R;
import com.trncic.igor.pocketcinema.model.Movie;
import com.trncic.igor.pocketcinema.providers.MoviesProvider;

import java.util.ArrayList;

public class MoviesLoader extends AsyncTaskLoader<ArrayList<Movie>> {

    private String movieQueryType;
    private Context context;

    public MoviesLoader(Context context, String movieQueryType) {
        super(context);
        this.context = context;
        this.movieQueryType = movieQueryType;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        if (movieQueryType == null) {
            deliverResult(new ArrayList<Movie>());
        } else {
            forceLoad();
        }
    }

    @Override
    public ArrayList<Movie> loadInBackground() {

        Cursor cursor = null;

        String orderByColumn = null;
        String selection = MoviesProvider.MovieContract.ORDER_TYPE + "='" + movieQueryType + "'";

        // Determine query arguments for favorites or different sort orders
        if (movieQueryType.equals(context.getString(R.string.sort_order_favorites))) {
        } else if (movieQueryType.equals(context.getString(R.string.sort_order_rating))) {
            orderByColumn = MoviesProvider.MovieContract.VOTE_AVERAGE;
        } else if (movieQueryType.equals(context.getString(R.string.sort_order_popularity))) {
            orderByColumn = MoviesProvider.MovieContract.POPULARITY;
        } else {
            return new ArrayList<>();
        }

        cursor = getContext().getContentResolver()
                .query(MoviesProvider.MovieContract.CONTENT_URI, null, selection, null,
                        orderByColumn + " DESC");

        if (null == cursor) {
            return null;
        } else if (cursor.getCount() < 1) {
            cursor.close();
            return new ArrayList<>();
        } else {
            int id = cursor.getColumnIndex(MoviesProvider.MovieContract._ID);
            int title = cursor.getColumnIndex(MoviesProvider.MovieContract.TITLE);
            int backdropPath = cursor.getColumnIndex(MoviesProvider.MovieContract.BACKDROP_PATH);
            int posterPath = cursor.getColumnIndex(MoviesProvider.MovieContract.POSTER_PATH);
            int overview = cursor.getColumnIndex(MoviesProvider.MovieContract.OVERVIEW);
            int voteAverage = cursor.getColumnIndex(MoviesProvider.MovieContract.VOTE_AVERAGE);
            int popularity = cursor.getColumnIndex(MoviesProvider.MovieContract.POPULARITY);
            int releaseDate = cursor.getColumnIndex(MoviesProvider.MovieContract.RELEASE_DATE);

            ArrayList<Movie> movies = new ArrayList<>();
            while (cursor.moveToNext()) {
                Movie movie = new Movie();
                movie.setId((int) cursor.getLong(id));
                movie.setTitle(cursor.getString(title));
                movie.setBackdropPath(cursor.getString(backdropPath));
                movie.setPosterPath(cursor.getString(posterPath));
                movie.setOverview(cursor.getString(overview));
                movie.setVoteAverage(cursor.getFloat(voteAverage));
                movie.setReleaseDate(cursor.getString(releaseDate));
                movie.setPopularity(cursor.getFloat(popularity));
                movies.add(movie);
            }
            cursor.close();
            return movies;
        }
    }

    @Override
    public void deliverResult(ArrayList<Movie> data) {
        super.deliverResult(data);
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }
}
