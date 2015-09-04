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
import android.widget.TextView;

import com.trncic.igor.pocketcinema.R;
import com.trncic.igor.pocketcinema.loaders.MoviesLoader;
import com.trncic.igor.pocketcinema.model.Movie;
import com.trncic.igor.pocketcinema.model.MoviesResponse;
import com.trncic.igor.pocketcinema.rest.RestClient;
import com.trncic.igor.pocketcinema.ui.adapters.MoviesAdapter;
import com.trncic.igor.pocketcinema.utils.Utils;

import java.util.ArrayList;
import java.util.List;

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
    public static final String MOVIE_LIST_KEY = "MOVIE_LIST_KEY";

    private static final int LOADER_ID = 1;
    @Bind(R.id.cant_connect)
    TextView mCantConnect;
    @Bind(R.id.no_movies)
    TextView mNoMovies;
    @Bind(R.id.gridview)
    GridView mGridView;
    private MoviesAdapter mAdapter;
    private OnFragmentInteractionListener mActionListener;
    private String mSortParam;

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

        mAdapter = new MoviesAdapter(getActivity(), new ArrayList<Movie>());
        mGridView.setAdapter(mAdapter);

        mGridView.setOnItemClickListener(this);

        if (savedInstanceState == null) {
            discoverMovies();
        } else {
            mSortParam = savedInstanceState.getString(PREFS_SORT_ORDER);
            if (mSortParam != null && !mSortParam.equalsIgnoreCase(getSortParam())) {
                discoverMovies();
            }

            List<Movie> movies = savedInstanceState.getParcelableArrayList(MOVIE_LIST_KEY);
            mAdapter.setData(movies);
        }

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIE_LIST_KEY, mAdapter.getValues());
        outState.putString(PREFS_SORT_ORDER, mSortParam);
        super.onSaveInstanceState(outState);
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
        if (!isAdded()) return;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOptionsPref = sharedPref.getString(PREFS_SORT_ORDER, getActivity().getString(R.string.sort_order_popularity));

        if (sortOptionsPref.equals(getActivity().getString(R.string.sort_order_favorites))) {
            mCantConnect.setVisibility(View.GONE);
            getActivity().getSupportLoaderManager().destroyLoader(LOADER_ID);
            getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, null, MoviesFragment.this);
        } else {
            if (Utils.isNetworkConnected(getActivity())) {
                getActivity().getSupportLoaderManager().destroyLoader(LOADER_ID);
                RestClient.get().discoverMovies(sortOptionsPref, new Callback<MoviesResponse>() {
                    @Override
                    public void success(MoviesResponse baseModel, Response response) {
                        mAdapter.setData(baseModel.results);
                        mActionListener.onMoviesLoaded(baseModel.results.get(0));
                        mCantConnect.setVisibility(View.GONE);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                    }
                });
            } else {
                mCantConnect.setVisibility(View.VISIBLE);
                mCantConnect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        discoverMovies();
                    }
                });
            }
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
        if (data != null && data.size() > 0) {
            mNoMovies.setVisibility(View.GONE);
            mAdapter.setData(data);
        } else {
            mNoMovies.setVisibility(View.VISIBLE);
        }
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
            mAdapter.setData(new ArrayList<Movie>());
            discoverMovies();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public String getSortParam() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortParam = prefs.getString(PREFS_SORT_ORDER,
                getString(R.string.sort_order_popularity));
        return sortParam;
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
