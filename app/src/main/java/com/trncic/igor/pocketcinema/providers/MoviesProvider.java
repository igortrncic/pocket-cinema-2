package com.trncic.igor.pocketcinema.providers;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.tjeannin.provigen.ProviGenBaseContract;
import com.tjeannin.provigen.ProviGenOpenHelper;
import com.tjeannin.provigen.ProviGenProvider;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.ContentUri;

/**
 * Olga Vshivtseva inspired me to use ProviGen library, similar as she did
 * https://github.com/ab-helly/popular-movies-2
 * Created by igortrncic on 7/30/15.
 */
public class MoviesProvider extends ProviGenProvider {
    public static final String COL_MOVIE_ID = "id";

    private static final String AUTHORITY = "content://com.trncic.igor.pocketcinema.movies/";

    private static Class[] contracts = new Class[]{
            MovieContract.class};

    @Override
    public SQLiteOpenHelper openHelper(Context context) {
        return new ProviGenOpenHelper(getContext(), "MovieDatabase", null, 1, contracts);
    }

    @Override
    public Class[] contractClasses() {
        return contracts;
    }

    /**
     * Movie data model contract.
     */
    public interface MovieContract extends ProviGenBaseContract {

        @Column(Column.Type.TEXT)
        String TITLE = "title";

        @Column(Column.Type.TEXT)
        String ORIGINAL_TITLE = "original_title";

        @Column(Column.Type.TEXT)
        String BACKDROP_PATH = "backdrop_path";

        @Column(Column.Type.TEXT)
        String POSTER_PATH = "poster_path";

        @Column(Column.Type.TEXT)
        String OVERVIEW = "overview";

        @Column(Column.Type.REAL)
        String VOTE_AVERAGE = "vote_average";

        @Column(Column.Type.REAL)
        String POPULARITY = "popularity";

        @Column(Column.Type.TEXT)
        String RELEASE_DATE = "release_date";

        @ContentUri
        Uri CONTENT_URI = Uri.parse(AUTHORITY + "movie");
    }
}
