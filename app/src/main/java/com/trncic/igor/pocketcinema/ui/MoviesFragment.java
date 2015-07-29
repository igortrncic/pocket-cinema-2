package com.trncic.igor.pocketcinema.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.trncic.igor.pocketcinema.R;
import com.trncic.igor.pocketcinema.model.MoviesResponse;
import com.trncic.igor.pocketcinema.model.Movie;
import com.trncic.igor.pocketcinema.rest.RestClient;
import com.trncic.igor.pocketcinema.ui.adapters.MoviesAdapter;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends Fragment implements AdapterView.OnItemClickListener{

    private GridView mGridView;
    private MoviesAdapter mAdapter;
    private OnFragmentInteractionListener mActionListener;

    public MoviesFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movies, container, false);

        // Set the adapter
        mGridView = (GridView) view.findViewById(R.id.gridview);

        mAdapter = new MoviesAdapter(getActivity(), new ArrayList<Movie>());
        mGridView.setAdapter(mAdapter);

        mGridView.setOnItemClickListener(this);
        discoverMovies();

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Movie movie = (Movie) mAdapter.getItem(position);
        mActionListener.onItemClick(movie);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mActionListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            Log.e(this.getClass().getName(),
                    "Activity must implement " + OnFragmentInteractionListener.class.getName());
        }
    }


    public void discoverMovies(){

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOptionsPref = sharedPref.getString(SettingsActivity.KEY_PREF_SORT_OPTIONS, "");

        RestClient.get().discoverMovies(sortOptionsPref, new Callback<MoviesResponse>() {
            @Override
            public void success(MoviesResponse baseModel, Response response) {
                mAdapter.setData(baseModel.results);

                mActionListener.onMoviesLoaded(baseModel.results.get(0));
            }

            @Override
            public void failure(RetrofitError error) {
                String asas = "asdasd";
            }
        });
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
        public void onItemClick(Movie movie);
        public void onMoviesLoaded(Movie movie);
    }
}
