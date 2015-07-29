package com.trncic.igor.pocketcinema.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.trncic.igor.pocketcinema.R;
import com.trncic.igor.pocketcinema.model.Movie;

public class DetailsActivity extends AppCompatActivity implements DetailsFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Movie movie = (Movie) getIntent().getSerializableExtra(DetailsFragment.MOVIE);
        boolean isTwoPane = getIntent().getBooleanExtra(DetailsFragment.IS_TWO_PANE, false);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, DetailsFragment.newInstance(movie, isTwoPane))
                    .commit();
        }
    }

    @Override
    public void onDetailsEvent(Uri uri) {

    }
}
