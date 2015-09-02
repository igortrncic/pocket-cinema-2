package com.trncic.igor.pocketcinema.ui;

import android.app.Activity;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.trncic.igor.pocketcinema.R;
import com.trncic.igor.pocketcinema.model.Movie;
import com.trncic.igor.pocketcinema.model.MovieUtils;
import com.trncic.igor.pocketcinema.picassoutils.PaletteTransformation;

import butterknife.Bind;
import butterknife.ButterKnife;

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

    private Movie mMovie;
    private boolean mTwoPane;

    @Bind(R.id.original_title) TextView mOriginalTitle;
    @Bind(R.id.background_image) ImageView mBackgroundImage;
    @Bind(R.id.poster_image) ImageView mPosterImage;
    @Bind(R.id.overview) TextView mOverview;
    @Bind(R.id.release_date) TextView mReleaseDate;
    @Bind(R.id.vote_average) TextView mVoteAverage;

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
            mMovie = (Movie) getArguments().getSerializable(MOVIE);
            mTwoPane = getArguments().getBoolean(IS_TWO_PANE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, view);

        if (mMovie != null) {
            if (!mTwoPane) {
                ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
                ab.setTitle(R.string.details);
                ab.setSubtitle(mMovie.getTitle());
            }

            mOriginalTitle.setText(mMovie.getTitle());
            mOverview.setText(mMovie.getOverview());
            mReleaseDate.setText(mMovie.getReleaseDate());
            mVoteAverage.setText(mMovie.getVoteAverage());

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
        }

        return view;
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
