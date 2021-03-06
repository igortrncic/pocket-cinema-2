package com.trncic.igor.pocketcinema.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.trncic.igor.pocketcinema.R;
import com.trncic.igor.pocketcinema.model.Movie;

public class MoviesActivity extends AppCompatActivity implements MoviesFragment.OnFragmentInteractionListener, DetailsFragment.OnFragmentInteractionListener {

    private static final String DETAILFRAGMENT_TAG = DetailsFragment.class.getSimpleName();
    MoviesFragment moviesFragment;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        moviesFragment = (MoviesFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, DetailsFragment.newInstance(null, mTwoPane), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SettingsActivity.CHANGE_ORDER) {
            moviesFragment.discoverMovies();
        }
    }

    @Override
    public void onItemClick(Movie movie) {
        if (mTwoPane) {
            DetailsFragment fragment = DetailsFragment.newInstance(movie, mTwoPane);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra(DetailsFragment.MOVIE, (Parcelable) movie);
            startActivity(intent);
        }
    }

    @Override
    public void onMoviesLoaded(Movie movie) {
        if (mTwoPane) {
            DetailsFragment fragment = DetailsFragment.newInstance(movie, mTwoPane);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mTwoPane) {
            ActionBar ab = getSupportActionBar();
            ab.setTitle(R.string.app_name);
        }
    }

    @Override
    public void onDetailsEvent(Uri uri) {

    }

}
