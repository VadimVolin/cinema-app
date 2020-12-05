package vadim.volin.tv.adapters.catalog.category.listener;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import vadim.volin.tv.ui.fragment.MovieInfoFragment;

/**
 * class for implements View.OnClickListener
 */
public class MovieCardClickListener implements View.OnClickListener {

    /**
     * field for position in adapter
     */
    private int position;
    /**
     * field for movie id
     */
    private String imdbId;

    /**
     * base constructor for init fields
     * @param position parameter for init position field
     * @param imdbId parameter for init id field
     */
    public MovieCardClickListener(int position, String imdbId) {
        this.position = position;
        this.imdbId = imdbId;
    }

    @Override
    public void onClick(View v) {
        if (position == RecyclerView.NO_POSITION) {
            return;
        }
        MovieInfoFragment.navigateToFragment(v, imdbId);
    }
}
