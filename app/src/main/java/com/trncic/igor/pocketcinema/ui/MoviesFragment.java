package com.trncic.igor.pocketcinema.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.trncic.igor.pocketcinema.R;
import com.trncic.igor.pocketcinema.loaders.MoviesLoader;
import com.trncic.igor.pocketcinema.model.Movie;
import com.trncic.igor.pocketcinema.model.MoviesResponse;
import com.trncic.igor.pocketcinema.rest.RestClient;
import com.trncic.igor.pocketcinema.ui.adapters.MoviesAdapter;
import com.trncic.igor.pocketcinema.utils.Utils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends Fragment implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

    public static final String PREFS_SORT_ORDER = "PREFS_SORT_ORDER";
    private static final int LOADER_ID = 1;
    @Bind(R.id.gridview)
    GridView mGridView;
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
        ButterKnife.bind(this, view);

        checkConnection();

        mAdapter = new MoviesAdapter(getActivity(), new ArrayList<Movie>());
        mGridView.setAdapter(mAdapter);

        mGridView.setOnItemClickListener(this);

        discoverMovies();

        setHasOptionsMenu(true);

        return view;
    }

    private void checkConnection() {
        if (!Utils.isNetworkConnected(getActivity())) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(MoviesFragment.PREFS_SORT_ORDER, getString(R.string.sort_order_favorites));
            editor.apply();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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


    public void discoverMovies() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOptionsPref = sharedPref.getString(PREFS_SORT_ORDER, getActivity().getString(R.string.sort_order_popularity));

        if (sortOptionsPref.equals(getActivity().getString(R.string.sort_order_favorites))) {
            getActivity().getSupportLoaderManager().destroyLoader(LOADER_ID);
            getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, null, MoviesFragment.this);
        } else {
            getActivity().getSupportLoaderManager().destroyLoader(LOADER_ID);
            RestClient.get().discoverMovies(sortOptionsPref, new Callback<MoviesResponse>() {
                @Override
                public void success(MoviesResponse baseModel, Response response) {
                    mAdapter.setData(baseModel.results);
                    mActionListener.onMoviesLoaded(baseModel.results.get(0));
                }

                @Override
                public void failure(RetrofitError error) {
                }
            });
        }

    }

    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOptionsPref = sharedPref.getString(PREFS_SORT_ORDER, getActivity().getString(R.string.sort_order_popularity));
        return new MoviesLoader(getActivity(), sortOptionsPref);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
        mAdapter.setData(data);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_movies, menu);
        // check default or last selected option
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortParam = prefs.getString(PREFS_SORT_ORDER,
                getString(R.string.sort_order_popularity));
        int selectedId;
        if (getString(R.string.sort_order_rating).equals(sortParam)) {
            selectedId = R.id.rating_order;
        } else if (getString(R.string.sort_order_favorites).equals(sortParam)) {
            selectedId = R.id.favorites;
        } else {
            selectedId = R.id.popularity_order;
        }
        if (!Utils.isNetworkConnected(getActivity())) {
            menu.findItem(R.id.rating_order).setEnabled(false);
            menu.findItem(R.id.popularity_order).setEnabled(false);
        }
        menu.findItem(selectedId).setChecked(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String param = null;
        switch (item.getItemId()) {
            case R.id.popularity_order:
                param = getString(R.string.sort_order_popularity);
                break;
            case R.id.rating_order:
                param = getString(R.string.sort_order_rating);
                break;
            case R.id.favorites:
                param = getString(R.string.sort_order_favorites);
                break;
        }
        if (param != null) {
            item.setChecked(true);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PREFS_SORT_ORDER, param);
            editor.apply();
            discoverMovies();
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
        public void onItemClick(Movie movie);

        public void onMoviesLoaded(Movie movie);
    }
}
