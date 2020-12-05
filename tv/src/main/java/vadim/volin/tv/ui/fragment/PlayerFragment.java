package vadim.volin.tv.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;

import org.jetbrains.annotations.NotNull;

import vadim.volin.movie_api.locator.ServiceLocator;
import vadim.volin.movie_api.service.player.ExoPlayerService;
import vadim.volin.tv.R;
import vadim.volin.tv.databinding.FragmentPlayerBinding;
import vadim.volin.util.NetworkUtil;
import vadim.volin.util.ToastShower;

public class PlayerFragment extends Fragment {

    /**
     * field of {@link ExoPlayerService} ExoPlayerService
     */
    private ExoPlayerService exoPlayerService;
    /**
     * field of FragmentPlayerBinding
     */
    private FragmentPlayerBinding fragmentPlayerBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (NetworkUtil.isNetworkAvailable(requireContext())) {
            exoPlayerService = (ExoPlayerService) ServiceLocator.getService(ExoPlayerService.class);
        } else {
            ToastShower.showAlert(this.getContext(), getString(R.string.NET_CONNECTION));
        }
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(fragmentPlayerBinding.getRoot()).popBackStack();
            }
        });
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentPlayerBinding = FragmentPlayerBinding.inflate(inflater, container, false);
        return fragmentPlayerBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (exoPlayerService != null) {
            exoPlayerService.resetPosition();
        }
        fragmentPlayerBinding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        fragmentPlayerBinding.playerView.bringToFront();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (exoPlayerService != null) {
            if (exoPlayerService.isReleased()) {
                exoPlayerService.initPlayer(getContext());
            }
            fragmentPlayerBinding.playerView.setPlayer(exoPlayerService.getExoPlayer());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (exoPlayerService != null) {
            fragmentPlayerBinding.playerView.requestFocus();
            exoPlayerService.play();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (exoPlayerService != null) {
            exoPlayerService.releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (fragmentPlayerBinding.playerView.getPlayer() != null) {
            fragmentPlayerBinding.playerView.setPlayer(null);
        }
    }
}