package vadim.volin.tv.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import vadim.volin.movie_api.entity.MovieDetails;
import vadim.volin.response.ServiceStatus;
import vadim.volin.tv.R;
import vadim.volin.tv.databinding.FragmentMovieInfoBinding;
import vadim.volin.tv.ui.fragment.listener.InfoFragmentLayoutChangeListener;
import vadim.volin.tv.viewmodel.MovieItemViewModel;
import vadim.volin.util.NetworkUtil;
import vadim.volin.util.ToastShower;

/**
 * MovieInfoFragment is a fragment for present details movie information, use lifecycle from Fragment {@link Fragment}
 */
public class MovieInfoFragment extends Fragment {

    /**
     * field for sending extra data to MovieInfoFragment{@link MovieInfoFragment}
     */
    public static final String MEDIA_EXTRA_DATA = "MEDIA_EXTRA_DATA";
    /**
     * field for operate views in layout {@link FragmentMovieInfoBinding}
     */
    private FragmentMovieInfoBinding fragmentMovieInfoBinding;
    /**
     * field of MovieItemViewModel {@link MovieItemViewModel}
     */
    private MovieItemViewModel movieItemViewModel;

    /**
     * method for navigate to {@link MovieInfoFragment} MovieInfoFragment
     * using Navigation{@link Navigation}
     */
    public static void navigateToFragment(View v, String imdbId) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(MovieInfoFragment.MEDIA_EXTRA_DATA, imdbId);
        Navigation.findNavController(v).navigate(R.id.action_browseNavigationFragment_to_movieInfoFragment, bundle);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        movieItemViewModel = new ViewModelProvider(requireActivity()).get(MovieItemViewModel.class);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(fragmentMovieInfoBinding.getRoot()).popBackStack();
            }
        });
        fragmentMovieInfoBinding = FragmentMovieInfoBinding.inflate(inflater, container, false);
        Bundle args = getArguments();
        if (args != null) {
            if (NetworkUtil.isNetworkAvailable(requireContext())) {
                String imDbId = args.getString(MEDIA_EXTRA_DATA);
                movieItemViewModel.getMovieDataById(imDbId);
            } else {
                ToastShower.showAlert(getContext(), getString(R.string.NET_CONNECTION));
            }
        } else {
            ToastShower.showAlert(getContext(), getString(R.string.NO_CONTENT_204));
            Navigation.findNavController(fragmentMovieInfoBinding.getRoot()).popBackStack();
        }
        return fragmentMovieInfoBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        subscribeOnLiveData(view);
        fragmentMovieInfoBinding.playBtn.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_movieInfoFragment_to_playerFragment));
        InfoFragmentLayoutChangeListener infoFragmentLayoutChangeListener = new InfoFragmentLayoutChangeListener();
        fragmentMovieInfoBinding.scrollContainer.addOnLayoutChangeListener(infoFragmentLayoutChangeListener);
        fragmentMovieInfoBinding.playBtn.requestFocus();
    }

    /**
     * method for subscribing on update live data's using
     * {@link MovieItemViewModel#getMovieDetailsLiveData()} MovieDetailsLiveData,
     * {@link MovieItemViewModel#getServiceResponseLiveData()} ServiceResponseLiveData
     */
    private void subscribeOnLiveData(View view) {
        movieItemViewModel.getServiceResponseLiveData().observe(getViewLifecycleOwner(), serviceStatus -> {
            if (serviceStatus.equals(ServiceStatus.NOT_FOUND_404)) {
                ToastShower.showAlert(getContext(), getString(R.string.NOT_FOUND_404));
            } else if (serviceStatus.equals(ServiceStatus.SERVICE_UNAVAILABLE_503)) {
                ToastShower.showAlert(getContext(), getString(R.string.SERVICE_UNAVAILABLE_503));
            }
            Navigation.findNavController(view).popBackStack();
        });
        movieItemViewModel.getMovieDetailsLiveData().observe(getViewLifecycleOwner(), this::updateDataInView);
    }

    /**
     * method for initialize data in views using {@link MovieDetails} MovieDetails object
     */
    private void updateDataInView(MovieDetails movieDetails) {
        Glide.with(fragmentMovieInfoBinding.getRoot().getContext())
                .load(movieDetails.getPoster())
                .error(R.drawable.lb_ic_play)
                .centerCrop()
                .into(fragmentMovieInfoBinding.mediaPoster);
        fragmentMovieInfoBinding.mediaHeader.setText(movieDetails.getTitle());
        fragmentMovieInfoBinding.mediaGenre.setText(movieDetails.getGenre());
        fragmentMovieInfoBinding.mediaLanguage.setText(movieDetails.getLanguage());
        fragmentMovieInfoBinding.mediaActors.setText(movieDetails.getActors());
        fragmentMovieInfoBinding.mediaCountry.setText(movieDetails.getCountry());
        fragmentMovieInfoBinding.mediaDirector.setText(movieDetails.getDirector());
        fragmentMovieInfoBinding.mediaReleaseDate.setText(movieDetails.getYear());
        fragmentMovieInfoBinding.mediaPlot.setText(movieDetails.getPlot());
    }

}