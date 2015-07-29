package com.trncic.igor.pocketcinema.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, SettingsActivity.CHANGE_ORDER);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SettingsActivity.CHANGE_ORDER) {
            moviesFragment.discoverMovies();
        }
    }

    @Override
    public void onItemClick(Movie movie) {
        if(mTwoPane){
            DetailsFragment fragment = DetailsFragment.newInstance(movie, mTwoPane);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        }else{
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra(DetailsFragment.MOVIE, movie);
            startActivity(intent);
        }
    }

    @Override
    public void onMoviesLoaded(Movie movie) {
        if(mTwoPane){
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
        if(!mTwoPane){
            ActionBar ab = getSupportActionBar();
            ab.setTitle(R.string.app_name);
        }
    }

    @Override
    public void onDetailsEvent(Uri uri) {

    }

}
