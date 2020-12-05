package vadim.volin.tv.adapters.catalog;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.leanback.widget.HorizontalGridView;
import androidx.leanback.widget.VerticalGridView;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import vadim.volin.entity.Category;
import vadim.volin.tv.adapters.catalog.category.CategoryItemAdapter;
import vadim.volin.tv.databinding.MoviesCategoryItemBinding;
import vadim.volin.tv.viewmodel.LastItemFocusViewModel;

/**
 * class for create instance MoviesCategoryAdapter extends VerticalGridView.Adapter
 * {@link VerticalGridView.Adapter}
 */
public class MoviesCategoryAdapter extends VerticalGridView.Adapter<MoviesCategoryAdapter.CategoryViewHolder> {

    /**
     * field for list of category use for init adapter
     */
    private List<Category> categories;
    /**
     * field for operate views in layout
     */
    private MoviesCategoryItemBinding moviesCategoryItemBinding;
    /**
     * field for FragmentActivity {@link FragmentActivity},
     * use for initialize ViewModel and like a parameter for
     * CategoryItemAdapter {@link CategoryItemAdapter}
     */
    private FragmentActivity fragmentActivity;
    /**
     * field for LastItemFocusViewModel {@link LastItemFocusViewModel},
     * use for operate last focus view in adapter
     */
    private LastItemFocusViewModel lastItemFocusViewModel;
    /**
     * field for save last focus category position
     */
    private int focusCategoryPosition = Integer.MIN_VALUE;
    /**
     * field for save last focus item position
     */
    private int focusItemPosition = Integer.MIN_VALUE;

    /**
     * base constructor for create MoviesCategoryAdapter {@link MoviesCategoryAdapter}
     * initialize list of categories, fragment activity and start observe
     * LastItemFocusViewModel {@link LastItemFocusViewModel}
     *
     * @param categories list of categories
     * @param activity   activity instance
     */
    public MoviesCategoryAdapter(List<Category> categories, FragmentActivity activity) {
        this.categories = categories;
        this.fragmentActivity = activity;
        this.lastItemFocusViewModel = new ViewModelProvider(fragmentActivity).get(LastItemFocusViewModel.class);
        observeLastItemFocus();
    }

    @NonNull
    @Override
    public MoviesCategoryAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        moviesCategoryItemBinding =
                MoviesCategoryItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryViewHolder(moviesCategoryItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesCategoryAdapter.CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.moviesCategoryItemBinding.categoryHeader.setText(category.getType().toUpperCase());
        CategoryItemAdapter categoryItemAdapter = new CategoryItemAdapter(category.getMovieList(), fragmentActivity);
        categoryItemAdapter.setCategoryMoviesBinding(moviesCategoryItemBinding);
        categoryItemAdapter.setCategoryPosition(position);
        if (focusCategoryPosition == position) {
            categoryItemAdapter.setSelectedPosition(focusItemPosition);
        }
        holder.moviesCategoryItemBinding.categoryRecycler.setAdapter(categoryItemAdapter);
    }

    /**
     * method for observe LastItemFocusViewModel and update focusCategoryPosition, focusItemPosition
     */
    private void observeLastItemFocus() {
        lastItemFocusViewModel.getLastItemPositionLiveData().observe(fragmentActivity, position -> {
            Integer categoryPosition = lastItemFocusViewModel.getCategoryItemPositionLiveData().getValue();
            if (position != null && position > HorizontalGridView.NO_POSITION) {
                if (categoryPosition != null && categoryPosition > VerticalGridView.NO_POSITION) {
                    this.focusCategoryPosition = categoryPosition;
                    this.focusItemPosition = position;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    /**
     * method for setting list of categories
     *
     * @param mapList list of categories
     */
    public void setCategories(List<Category> mapList) {
        this.categories = mapList;
    }

    public static class CategoryViewHolder extends VerticalGridView.ViewHolder {

        private MoviesCategoryItemBinding moviesCategoryItemBinding;

        public CategoryViewHolder(MoviesCategoryItemBinding moviesCategoryItemBinding) {
            super(moviesCategoryItemBinding.getRoot());
            this.moviesCategoryItemBinding = moviesCategoryItemBinding;
        }
    }
}
