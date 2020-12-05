package vadim.volin.mobile.ui;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import org.jetbrains.annotations.NotNull;

import vadim.volin.mobile.databinding.FragmentMovieDescriptionBinding;

public class MovieDescriptionFragment extends Fragment {

    private String description;

    private FragmentMovieDescriptionBinding fragmentMovieDescriptionBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = this.getArguments();
        if (args != null) {
            this.description = args.getString(MovieInfoFragment.EXTRA_DESCRIPTION);
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentMovieDescriptionBinding = FragmentMovieDescriptionBinding.inflate(inflater, container, false);
        return fragmentMovieDescriptionBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentMovieDescriptionBinding.movieDescription.setText(this.description);

        fragmentMovieDescriptionBinding.movieDescription.setMovementMethod(new ScrollingMovementMethod());

        fragmentMovieDescriptionBinding.backBtn.setOnClickListener(view1 -> Navigation.findNavController(view1).popBackStack());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (fragmentMovieDescriptionBinding != null) {
            fragmentMovieDescriptionBinding.backBtn.setOnClickListener(null);
        }
    }
}