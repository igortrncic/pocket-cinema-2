package com.trncic.igor.pocketcinema.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.trncic.igor.pocketcinema.R;
import com.trncic.igor.pocketcinema.async.MovieDeleteAsyncTask;
import com.trncic.igor.pocketcinema.async.MovieStoreAsyncTask;
import com.trncic.igor.pocketcinema.model.Movie;
import com.trncic.igor.pocketcinema.model.MovieUtils;
import com.trncic.igor.pocketcinema.model.Review;
import com.trncic.igor.pocketcinema.model.ReviewsResponse;
import com.trncic.igor.pocketcinema.model.Trailer;
import com.trncic.igor.pocketcinema.model.TrailersResponse;
import com.trncic.igor.pocketcinema.picassoutils.PaletteTransformation;
import com.trncic.igor.pocketcinema.providers.MoviesProvider;
import com.trncic.igor.pocketcinema.rest.RestClient;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailsFragment extends Fragment {
    public static final String MOVIE = "movie";
    public static final String IS_TWO_PANE = "is_two_pane";
    @Bind(R.id.original_title)
    TextView mOriginalTitle;
    @Bind(R.id.background_image)
    ImageView mBackgroundImage;
    @Bind(R.id.poster_image)
    ImageView mPosterImage;
    @Bind(R.id.overview)
    TextView mOverview;
    @Bind(R.id.release_date)
    TextView mReleaseDate;
    @Bind(R.id.vote_average)
    TextView mVoteAverage;
    @Bind(R.id.favorite)
    CheckBox mFavorite;
    @Bind(R.id.trailers_container)
    LinearLayout trailersContainer;
    @Bind(R.id.reviews_container)
    LinearLayout reviewsContainer;
    private Movie mMovie;

    private List<Trailer> mTrailers;
    private List<Review> mReviews;

    private boolean mTwoPane;
    private OnFragmentInteractionListener mListener;

    public DetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailsFragment newInstance(String param1, String param2) {
        DetailsFragment fragment = new DetailsFragment();
        return fragment;
    }

    public static DetailsFragment newInstance(Movie movie, boolean isTwoPane) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(MOVIE, movie);
        args.putBoolean(IS_TWO_PANE, isTwoPane);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMovie = (Movie) getArguments().getParcelable(MOVIE);
            mTwoPane = getArguments().getBoolean(IS_TWO_PANE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);

        if (mMovie != null) {
            if (!mTwoPane) {
                ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
                ab.setTitle(R.string.details);
                ab.setSubtitle(mMovie.getTitle());
            }

            mFavorite.setVisibility(View.VISIBLE);

            mOriginalTitle.setText(mMovie.getTitle());
            mOverview.setText(mMovie.getOverview());
            mReleaseDate.setText(mMovie.getReleaseDate());
            mVoteAverage.setText(String.valueOf(mMovie.getVoteAverage()));

            resolveFavoriteStatus();

            mFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        addMovieToFavorites();
                    } else {
                        removeMovieFromFavorites();
                    }
                }
            });

            mBackgroundImage.setColorFilter(getResources().getColor(R.color.black_45), PorterDuff.Mode.DARKEN);

            Picasso.with(getActivity())
                    .load(MovieUtils.getBackdropPath(mMovie, MovieUtils.ORIGINAL))
                    .placeholder(getResources().getDrawable(R.drawable.image_foreground))
                    .into(mBackgroundImage);

            //This code is taken from blog of Jake Wharton
            //http://jakewharton.com/coercing-picasso-to-play-with-palette/
            Picasso.with(getActivity())
                    .load(MovieUtils.getPosterPath(mMovie, MovieUtils.IMAGES_SIZE_342))
                    .transform(PaletteTransformation.instance())
                    .into(mPosterImage, new Callback.EmptyCallback() {
                        @Override
                        public void onSuccess() {
                            Bitmap bitmap = ((BitmapDrawable) mPosterImage.getDrawable()).getBitmap();
                            Palette palette = PaletteTransformation.getPalette(bitmap);
                            mVoteAverage.setTextColor(palette.getVibrantColor(R.color.average_vote));
                            AppCompatActivity activity = (AppCompatActivity) DetailsFragment.this.getActivity();
                            if (activity != null) {
                                ActionBar actionBar = ((AppCompatActivity) DetailsFragment.this.getActivity()).getSupportActionBar();
                                if (actionBar != null)
                                    actionBar.setBackgroundDrawable(new ColorDrawable(palette.getDarkVibrantColor(R.color.average_vote)));
                            }
                        }
                    });

            // Load trailers
            loadTrailers();

            // Load reviews
            loadReviews();
        }


        return view;
    }

    private void loadTrailers() {
        RestClient.get().getTrailers(mMovie.getId(), new retrofit.Callback<TrailersResponse>() {
            @Override
            public void success(TrailersResponse trailersResponse, Response response) {
                if (!isAdded()) {
                    return;
                }
                if (trailersResponse.results == null || trailersResponse.results.size() < 1) {
                    trailersContainer.setVisibility(View.GONE);
                    return;
                }
                mTrailers = trailersResponse.results;
                LayoutInflater inflater = LayoutInflater.from(getActivity());

                for (final Trailer trailer : trailersResponse.results) {
                    View view = inflater.inflate(R.layout.trailer_item, trailersContainer, false);
                    ((TextView) view.findViewById(R.id.name)).setText(trailer.getName());

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(trailer.generateYoutubeLink()));
                            startActivity(intent);
                        }
                    });
                    trailersContainer.addView(view);
                }
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    private void loadReviews() {
        RestClient.get().getReviews(mMovie.getId(), new retrofit.Callback<ReviewsResponse>() {
            @Override
            public void success(ReviewsResponse reviewsResponse, Response response) {

                if(!isAdded()){
                    return;
                }
                if (reviewsResponse.results == null || reviewsResponse.results.size() < 1) {
                    reviewsContainer.setVisibility(View.GONE);
                    return;
                }

                mReviews = reviewsResponse.results;

                LayoutInflater inflater = LayoutInflater.from(getActivity());

                for (Review review : reviewsResponse.results) {
                    View view = inflater.inflate(R.layout.review_item, reviewsContainer, false);
                    ((TextView) view.findViewById(R.id.title)).setText(review.getAuthor());
                    ((TextView) view.findViewById(R.id.content)).setText(review.getContent());

                    reviewsContainer.addView(view);
                }
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    private void resolveFavoriteStatus() {
        // Try to find this movie in DB
        Cursor cursor = getActivity().getContentResolver().query(
                Uri.withAppendedPath(MoviesProvider.MovieContract.CONTENT_URI, String.valueOf(mMovie.getId())),
                null, "", null, "");

        if (null == cursor) {
            mFavorite.setChecked(false);
        } else if (cursor.getCount() < 1) {
            cursor.close();
            mFavorite.setChecked(false);
        } else {
            mFavorite.setChecked(true);
        }
    }

    private void addMovieToFavorites() {
        new MovieStoreAsyncTask(getActivity()).execute(mMovie);
    }

    private void removeMovieFromFavorites() {
        new MovieDeleteAsyncTask(getActivity()).execute(mMovie);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onDetailsEvent(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_details, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share) {

            if (mTrailers != null && mTrailers.size() > 0) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");

                intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.watch_trailer) + mMovie.getTitle() + " " +
                        mTrailers.get(0).generateYoutubeLink());
                startActivity(Intent.createChooser(intent, getString(R.string.share_movie)));
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onDetailsEvent(Uri uri);
    }

}
