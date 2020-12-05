package vadim.volin.mobile.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

import vadim.volin.mobile.R;
import vadim.volin.mobile.adapter.MovieAdapter;
import vadim.volin.mobile.databinding.FragmentMainBinding;
import vadim.volin.mobile.error.ServiceStatus;
import vadim.volin.mobile.viewmodel.MovieListViewModel;
import vadim.volin.movie_api.entity.Movie;

public class MovieListFragment extends Fragment {

    private static final String TAG = MovieListFragment.class.getCanonicalName();

    private static String keyWord = "apple";
    private FragmentMainBinding fragmentMainBinding;
    private MovieAdapter movieAdapter;

    private MovieListViewModel movieListViewModel;

    private List<Movie> movieList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        movieList = new LinkedList<>();
        movieListViewModel = new ViewModelProvider(this).get(MovieListViewModel.class);

        if (isNetworkAvailable()) {
            if (movieListViewModel != null && movieListViewModel.getMovieListLiveData().getValue() == null) {
                movieListViewModel.cacheDataByKeyWord(keyWord);
                movieListViewModel.getRequestData(keyWord);
            }
        } else {
            Toast.makeText(this.getContext(), getString(ServiceStatus.NET_CONNECTION.getResourceId()), Toast.LENGTH_SHORT).show();
        }

        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        });
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentMainBinding = FragmentMainBinding.inflate(inflater, container, false);
        fragmentMainBinding.searchTitle.setOnEditorActionListener((v, actionId, event) -> {
            switch (actionId) {
                case EditorInfo.IME_ACTION_DONE:
                case EditorInfo.IME_ACTION_NEXT:
                case EditorInfo.IME_ACTION_PREVIOUS: {
                    if (isNetworkAvailable()) {
                        if (!v.getText().toString().equals(keyWord)) {
                            keyWord = v.getText().toString();
                            if (keyWord.trim().equals("")) {
                                Snackbar.make(fragmentMainBinding.frameLayout, R.string.empty_string_warning_text, Snackbar.LENGTH_LONG).show();
                            } else {
                                movieListViewModel.getRequestData(keyWord);
                                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            }
                        }
                    } else {
                        Toast.makeText(getContext(), getString(ServiceStatus.NET_CONNECTION.getResourceId()), Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
            return false;
        });

        initRecyclerView();

        return fragmentMainBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        subscribeOnLiveData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (fragmentMainBinding != null) {
            fragmentMainBinding.searchTitle.setOnEditorActionListener(null);
        }
    }

    /**
     * Initialize recycler view with checking orientation screen
     */
    private void initRecyclerView() {
        fragmentMainBinding.movieRecyclerView.setHasFixedSize(true);
        if (movieList != null) {
            movieAdapter = new MovieAdapter(movieList);
        }
        int orientation = getResources().getConfiguration().orientation;
        RecyclerView.LayoutManager layoutManager;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = new GridLayoutManager(getContext(), 4, RecyclerView.VERTICAL, false);
            fragmentMainBinding.movieRecyclerView.setLayoutManager(layoutManager);
            fragmentMainBinding.movieRecyclerView.addItemDecoration(
                    new DividerItemDecoration(
                            requireActivity().getApplicationContext(),
                            DividerItemDecoration.HORIZONTAL
                    )
            );
        } else {
            layoutManager = new LinearLayoutManager(getContext());
            fragmentMainBinding.movieRecyclerView.setLayoutManager(layoutManager);
        }
        fragmentMainBinding.movieRecyclerView.addItemDecoration(
                new DividerItemDecoration(
                        requireActivity().getApplicationContext(),
                        DividerItemDecoration.VERTICAL
                )
        );
        fragmentMainBinding.movieRecyclerView.setAdapter(movieAdapter);
        fragmentMainBinding.movieRecyclerView.requestFocus();
    }

    /**
     * Subscribe on update in LiveData for Error handling, update MovieDetails info and update
     * progress bar state
     */
    private void subscribeOnLiveData() {
        if (movieListViewModel != null) {

            movieListViewModel
                    .getMovieListLiveData()
                    .observe(
                            getViewLifecycleOwner(),
                            movies -> {
                                if (movieList != null) {
                                    movieList.clear();
                                    movieList.addAll(movies);
                                }
                                movieAdapter.notifyDataSetChanged();
                            });

            movieListViewModel
                    .getProgressBarLiveData()
                    .observe(getViewLifecycleOwner(),
                            visibility -> {
                                if (fragmentMainBinding.progressBar != null) {
                                    fragmentMainBinding.progressBar.setVisibility(visibility ? View.VISIBLE : View.GONE);
                                }
                            });

            movieListViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), status ->
                    Snackbar.make(fragmentMainBinding.getRoot(), status.getResourceId(), Snackbar.LENGTH_LONG).show()
            );
        }
    }

    /**
     * @return boolean value that network connect true/false
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}