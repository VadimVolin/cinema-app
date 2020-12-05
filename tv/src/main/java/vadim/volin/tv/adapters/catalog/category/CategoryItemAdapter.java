package vadim.volin.tv.adapters.catalog.category;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.leanback.widget.HorizontalGridView;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import java.util.List;

import vadim.volin.movie_api.entity.Movie;
import vadim.volin.tv.R;
import vadim.volin.tv.adapters.catalog.category.listener.CategoryGlobalLayoutListener;
import vadim.volin.tv.adapters.catalog.category.listener.MovieCardClickListener;
import vadim.volin.tv.databinding.MovieCardLayoutBinding;
import vadim.volin.tv.databinding.MoviesCategoryItemBinding;
import vadim.volin.tv.viewmodel.LastItemFocusViewModel;
import vadim.volin.tv.viewmodel.MenuFocusViewModel;

/**
 * class for category adapter that extends {@link HorizontalGridView.Adapter}
 */
public class CategoryItemAdapter extends HorizontalGridView.Adapter<CategoryItemAdapter.CategoryItemViewHolder> {

    /**
     * field for list of movie use for init adapter
     */
    private List<Movie> movieList;
    /**
     * field for MenuFocusViewModel {@link MenuFocusViewModel}
     * use for operate focus in menu
     */
    private MenuFocusViewModel menuFocusViewModel;
    /**
     * field for LastItemFocusViewModel {@link LastItemFocusViewModel},
     * use for operate last focus view in adapter
     */
    private LastItemFocusViewModel lastItemFocusViewModel;
    /**
     * field for FragmentActivity {@link FragmentActivity},
     * use for initialize ViewModel and operate colors
     */
    private FragmentActivity fragmentActivity;
    /**
     * field for operate views in layout
     */
    private MoviesCategoryItemBinding moviesCategoryItemBinding;
    /**
     * field for save category position
     */
    private int categoryPosition = HorizontalGridView.NO_POSITION;
    /**
     * field for save selected item position
     */
    private int selectedPosition = HorizontalGridView.NO_POSITION;

    /**
     * base constructor for init CategoryItemAdapter object {@link CategoryItemAdapter},
     * and initialize ViewModel's object
     *
     * @param movieList        parameter for initialize list of Movie {@link Movie}
     * @param fragmentActivity parameter for initialize instance of FragmentActivity {@link FragmentActivity}
     */
    public CategoryItemAdapter(List<Movie> movieList, FragmentActivity fragmentActivity) {
        this.movieList = movieList;
        this.fragmentActivity = fragmentActivity;
        this.menuFocusViewModel = new ViewModelProvider(fragmentActivity).get(MenuFocusViewModel.class);
        this.lastItemFocusViewModel = new ViewModelProvider(fragmentActivity).get(LastItemFocusViewModel.class);
    }

    @NonNull
    @Override
    public CategoryItemAdapter.CategoryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MovieCardLayoutBinding movieCardLayoutBinding = MovieCardLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryItemViewHolder(movieCardLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryItemAdapter.CategoryItemViewHolder holder, int position) {
        initItemView(holder);
        String imDbId = movieList.get(position).getImdbID();
        MovieCardClickListener movieCardClickListener = new MovieCardClickListener(position, imDbId);
        holder.itemView.setOnClickListener(movieCardClickListener);
        holder.itemView.setOnFocusChangeListener((v, hasFocus) -> operateCardFocus(position, hasFocus));
        if (categoryPosition == 0) {
            MovieCardKeyListener movieCardKeyListener = new MovieCardKeyListener(position);
            holder.itemView.setOnKeyListener(movieCardKeyListener);
        }
        restorePreviousFocus(holder, position);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    /**
     * method for restore last focus item in adapter if position equals to selectedPosition field
     * {@link CategoryItemAdapter#selectedPosition}
     *
     * @param holder   parameter for setting last focus view
     * @param position parameter for check position of view
     */
    private void restorePreviousFocus(CategoryItemViewHolder holder, int position) {
        if (selectedPosition != HorizontalGridView.NO_POSITION && selectedPosition < getItemCount()) {
            moviesCategoryItemBinding.categoryRecycler.smoothScrollToPosition(selectedPosition);
            if (selectedPosition == position) {
                selectedPosition = HorizontalGridView.NO_POSITION;
                View lastFocusItem = holder.itemView;
                ViewTreeObserver viewTreeObserver = moviesCategoryItemBinding.categoryRecycler.getViewTreeObserver();
                CategoryGlobalLayoutListener categoryGlobalLayoutListener = new CategoryGlobalLayoutListener(lastFocusItem, viewTreeObserver);
                viewTreeObserver.addOnGlobalLayoutListener(categoryGlobalLayoutListener);
            }
        }
    }

    /**
     * method for initialize views in layout
     *
     * @param holder parameter for initialize view
     */
    private void initItemView(CategoryItemViewHolder holder) {
        Glide.with(holder.movieCardLayoutBinding.getRoot().getContext())
                .load(movieList.get(holder.getLayoutPosition()).getPoster())
                .error(R.drawable.ic_play_btn)
                .centerCrop()
                .into(holder.movieCardLayoutBinding.moviePoster);
        holder.itemView.clearFocus();
    }

    /**
     * method for operate focus on card item in adapter
     *
     * @param position parameter for updating last focus item position
     * @param hasFocus parameter for check that item has focus or no
     */
    private void operateCardFocus(int position, boolean hasFocus) {
        if (hasFocus) {
            moviesCategoryItemBinding
                    .categoryHeader
                    .setTextColor(ContextCompat.getColor(fragmentActivity, R.color.main_pink_color));
            updateLastFocusData(categoryPosition, position);
        } else {
            moviesCategoryItemBinding
                    .categoryHeader
                    .setTextColor(ContextCompat.getColor(fragmentActivity, R.color.base_light_text));
        }
    }

    /**
     * method for update last category position and item position in lastItemFocusViewModel
     * {@link LastItemFocusViewModel}
     *
     * @param categoryPosition parameter for save category position
     * @param itemPosition     parameter for save item position
     */
    public void updateLastFocusData(int categoryPosition, int itemPosition) {
        lastItemFocusViewModel
                .getCategoryItemPositionLiveData()
                .postValue(categoryPosition);
        lastItemFocusViewModel
                .getLastItemPositionLiveData()
                .postValue(itemPosition);
    }

    /**
     * method for setting category position
     *
     * @param position parameter of category position
     */
    public void setCategoryPosition(int position) {
        this.categoryPosition = position;
    }

    /**
     * method for setting last selected position
     *
     * @param selectedPosition parameter of last selected position
     */
    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    /**
     * method for setting MoviesCategoryItemBinding to adapter
     *
     * @param moviesCategoryItemBinding parameter of type MoviesCategoryItemBinding
     *                                  {@link MoviesCategoryItemBinding} for setting
     *                                  in field
     */
    public void setCategoryMoviesBinding(MoviesCategoryItemBinding moviesCategoryItemBinding) {
        this.moviesCategoryItemBinding = moviesCategoryItemBinding;
    }

    public static class CategoryItemViewHolder extends HorizontalGridView.ViewHolder {

        private MovieCardLayoutBinding movieCardLayoutBinding;

        public CategoryItemViewHolder(MovieCardLayoutBinding movieCardLayoutBinding) {
            super(movieCardLayoutBinding.getRoot());
            this.movieCardLayoutBinding = movieCardLayoutBinding;
        }
    }

    /**
     * class that implements View.OnKeyListener {@link View.OnKeyListener}
     * using it for operate KeyEvent and update MenuFocusLiveData {@link MenuFocusViewModel}
     */
    public class MovieCardKeyListener implements View.OnKeyListener {

        private int cardPosition;

        public MovieCardKeyListener(int cardPosition) {
            this.cardPosition = cardPosition;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                menuFocusViewModel.getKeyEventMutableLiveData().setValue(keyCode);
                updateLastFocusData(categoryPosition, cardPosition);
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_BUTTON_SELECT) {
                v.callOnClick();
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                fragmentActivity.onBackPressed();
            }
            return false;
        }
    }

}
