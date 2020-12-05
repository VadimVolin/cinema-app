package vadim.volin.mobile.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import vadim.volin.mobile.R;
import vadim.volin.mobile.databinding.FragmentMovieInfoBinding;
import vadim.volin.mobile.error.ServiceStatus;
import vadim.volin.mobile.viewmodel.MovieDetailsViewModel;
import vadim.volin.movie_api.entity.MovieDetails;
import vadim.volin.movie_api.locator.ServiceLocator;
import vadim.volin.movie_api.service.player.ExoPlayerService;

public class MovieInfoFragment extends Fragment {

    public static final String TAG = MovieInfoFragment.class.getCanonicalName();

    private String description;
    private FragmentMovieInfoBinding fragmentMovieInfoBinding;

    private MovieDetailsViewModel movieDetailsViewModel;

    public static final String EXTRA_MOVIE_DATA = "MOVIE_INFO";
    public static final String EXTRA_DESCRIPTION = "DESCRIPTION";
    public static final String EXTRA_PLAYER_VISIBILITY = "PLAYER_VISIBILITY";

    private MovieDetails movieDetails;

    private ExoPlayerService exoPlayerService;
    private boolean playerVisibility = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();
        movieDetailsViewModel = new ViewModelProvider(this).get(MovieDetailsViewModel.class);
        if (isNetworkAvailable()) {
            if (args != null) {
                String imdbID = args.getString(EXTRA_MOVIE_DATA);
                if (movieDetailsViewModel.getMovieDetailsLiveData().getValue() == null
                        || !movieDetailsViewModel.getMovieDetailsLiveData().getValue().getImdbID().equals(imdbID)) {
                    movieDetailsViewModel.getRequestData(imdbID);
                }
            }

            exoPlayerService = (ExoPlayerService) ServiceLocator.getService(ExoPlayerService.class);

        } else {
            Toast.makeText(this.getContext(), getString(ServiceStatus.NET_CONNECTION.getResourceId()), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        subscribeOnLoadLiveData();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentMovieInfoBinding = FragmentMovieInfoBinding.inflate(inflater, container, false);
        return fragmentMovieInfoBinding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentMovieInfoBinding.backBtn.setOnClickListener(view1 -> {
            Navigation.findNavController(view1).popBackStack();
            if (exoPlayerService != null) {
                exoPlayerService.resetPosition();
            }
        });

        fragmentMovieInfoBinding.descBtn.setOnClickListener(view12 -> {
            Bundle bundle = new Bundle();
            bundle.putString(EXTRA_DESCRIPTION, description);
            Navigation.findNavController(view12).navigate(R.id.action_movieInfoFragment_to_movieDescriptionFragment, bundle);
        });

        fragmentMovieInfoBinding.playBtn.setOnClickListener(view13 -> {
//            Player visibility
            playerVisibility = true;
            swapToPlayerPoster();
        });

        fragmentMovieInfoBinding.playerView.setControllerAutoShow(false);
        fragmentMovieInfoBinding.playerView.setUseController(false);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            playerVisibility = savedInstanceState.getBoolean(EXTRA_PLAYER_VISIBILITY);
        }
        if (!playerVisibility && exoPlayerService != null) {
            exoPlayerService.resetPosition();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRA_PLAYER_VISIBILITY, playerVisibility);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT >= 24 && exoPlayerService != null) {
            loadPlayer();
        }

        if (playerVisibility) {
            swapToPlayerPoster();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT >= 24 && exoPlayerService != null) {
            exoPlayerService.pause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (fragmentMovieInfoBinding != null) {
            fragmentMovieInfoBinding.backBtn.setOnClickListener(null);
            fragmentMovieInfoBinding.playBtn.setOnClickListener(null);
            fragmentMovieInfoBinding.descBtn.setOnClickListener(null);
        }
    }

    /**
     * Swap to player view from main image view and start play video
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void swapToPlayerPoster() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            fragmentMovieInfoBinding.linearLayout.setVisibility(View.INVISIBLE);
            hideSystemUI();
        } else {
            fragmentMovieInfoBinding.playBtn.setVisibility(View.GONE);
            fragmentMovieInfoBinding.posterImage.setVisibility(View.INVISIBLE);
        }
        fragmentMovieInfoBinding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        fragmentMovieInfoBinding.playerView.setVisibility(View.VISIBLE);
        exoPlayerService.play();
    }

    /**
     * Method load player to fragment
     */
    @SuppressLint("ClickableViewAccessibility")
    private void loadPlayer() {
        if (fragmentMovieInfoBinding.playerView.getPlayer() == null) {
            fragmentMovieInfoBinding.playerView.setPlayer(exoPlayerService.getExoPlayer());

            fragmentMovieInfoBinding.playerView.setOnTouchListener((view, motionEvent) -> {
                if (fragmentMovieInfoBinding.descBtn.getVisibility() == View.VISIBLE
                        && fragmentMovieInfoBinding.backBtn.getVisibility() == View.VISIBLE) {
                    fragmentMovieInfoBinding.descBtn.setVisibility(View.INVISIBLE);
                    fragmentMovieInfoBinding.backBtn.setVisibility(View.INVISIBLE);
                } else {
                    fragmentMovieInfoBinding.descBtn.setVisibility(View.VISIBLE);
                    fragmentMovieInfoBinding.backBtn.setVisibility(View.VISIBLE);
                }
                return false;
            });

            int orientation = getResources().getConfiguration().orientation;
            exoPlayerService.getExoPlayer().addListener(new Player.EventListener() {
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if (playbackState == Player.STATE_ENDED) {
                        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            fragmentMovieInfoBinding.posterImage.setVisibility(View.VISIBLE);
                            fragmentMovieInfoBinding.playerView.setVisibility(View.INVISIBLE);
                            fragmentMovieInfoBinding.playBtn.setVisibility(View.VISIBLE);
                            fragmentMovieInfoBinding.linearLayout.setVisibility(View.VISIBLE);
                        } else {
                            fragmentMovieInfoBinding.posterImage.setVisibility(View.VISIBLE);
                            fragmentMovieInfoBinding.playerView.setVisibility(View.INVISIBLE);
                            fragmentMovieInfoBinding.playBtn.setVisibility(View.VISIBLE);
                        }
                        playerVisibility = false;

                    }
                }
            });

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

    /**
     * Initialize views by object data
     */
    private void initViews() {
        fragmentMovieInfoBinding.movieTitle.setText(movieDetails.getTitle());
        fragmentMovieInfoBinding.movieCountry.setText(movieDetails.getCountry());
        fragmentMovieInfoBinding.movieActors.setText(movieDetails.getActors());
        fragmentMovieInfoBinding.movieDirector.setText(movieDetails.getDirector());
        fragmentMovieInfoBinding.movieGenre.setText(movieDetails.getGenre());
        fragmentMovieInfoBinding.movieLanguages.setText(movieDetails.getLanguage());
        description = movieDetails.getPlot();
        fragmentMovieInfoBinding.releasedDate.setText(movieDetails.getYear());
        Glide.with(fragmentMovieInfoBinding.getRoot().getContext())
                .load(movieDetails.getPoster())
                .error(R.drawable.ic_not_found_film_24)
                .centerCrop()
                .into(fragmentMovieInfoBinding.posterImage);
    }

    /**
     * Subscribe on update in LiveData for Error handling, update MovieDetails info
     */
    @SuppressLint("WrongConstant")
    public void subscribeOnLoadLiveData() {
        movieDetailsViewModel.getMovieDetailsLiveData().observe(getViewLifecycleOwner(), movie -> {
            movieDetails = movie;
            initViews();
        });

        movieDetailsViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), status ->
                Snackbar.make(fragmentMovieInfoBinding.getRoot(), status.getResourceId(), Snackbar.LENGTH_SHORT).show()
        );
    }

    /**
     * Method hide system notification and tool bars
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = requireActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

}