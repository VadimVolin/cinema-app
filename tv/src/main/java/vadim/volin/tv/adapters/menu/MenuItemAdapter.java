package vadim.volin.tv.adapters.menu;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.leanback.widget.HorizontalGridView;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import vadim.volin.tv.R;
import vadim.volin.tv.activity.MainActivity;
import vadim.volin.tv.databinding.MenuItemLayoutBinding;
import vadim.volin.tv.databinding.MenuViewLayoutBinding;
import vadim.volin.tv.ui.view.menu.MenuView;
import vadim.volin.tv.viewmodel.MenuFocusViewModel;
import vadim.volin.tv.viewmodel.MovieCatalogViewModel;

/**
 * class for initialize MenuItemAdapter, extends {@link HorizontalGridView.Adapter<MenuItemAdapter.MenuItemViewHolder>}
 */
public class MenuItemAdapter extends HorizontalGridView.Adapter<MenuItemAdapter.MenuItemViewHolder> {

    /**
     * field list of menu items
     */
    private List<String> menuItems;
    /**
     * field for operate views in menu item layout {@link MenuItemLayoutBinding}
     */
    private MenuItemLayoutBinding menuItemLayoutBinding;
    /**
     * field of activity object {@link MainActivity}
     */
    private MainActivity mainActivity;
    /**
     * field of {@link MenuFocusViewModel} for requesting focus in menu item
     */
    private MenuFocusViewModel menuFocusViewModel;
    /**
     * field of MovieCatalogViewModel object {@link MovieCatalogViewModel}
     */
    private MovieCatalogViewModel movieCatalogViewModel;
    /**
     * field of SearchView object {@link SearchView}
     */
    private SearchView searchView;

    /**
     * <p>
     *     Base constructor that init list of menu items {@link MenuItemAdapter#menuItems},
     *     activity {@link MenuItemAdapter#mainActivity}
     *     and searchView {@link MenuItemAdapter#searchView}
     * </p>
     *
     * @param menuItems list of menu items
     * @param mainActivity MainActivity instance
     * @param menuViewLayoutBinding layout of MenuView {@link MenuView}
     */
    public MenuItemAdapter(List<String> menuItems, MainActivity mainActivity, MenuViewLayoutBinding menuViewLayoutBinding) {
        this.menuItems = menuItems;
        this.mainActivity = mainActivity;
        searchView = menuViewLayoutBinding.searchBar;
        menuFocusViewModel = new ViewModelProvider(mainActivity).get(MenuFocusViewModel.class);
        movieCatalogViewModel = new ViewModelProvider(mainActivity).get(MovieCatalogViewModel.class);
    }

    @NonNull
    @Override
    public MenuItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        menuItemLayoutBinding = MenuItemLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MenuItemViewHolder(menuItemLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemViewHolder holder, int position) {
        holder.menuItemLayoutBinding.itemHeader.setText(menuItems.get(position));
        holder.itemView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                selectItem(holder);
            } else {
                deselectItem(holder);
            }
        });
        if (position == 0) {
            initSearchItem();
            holder.itemView.setOnClickListener(view -> onSearchItemClick());
            observeKeyEvent(holder);
        }
        if (position == 1) {
            holder.itemView.setOnClickListener(view -> onCatalogItemClick());
        }
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    /**
     * method for prepare drawable color in menu item
     * @param color int value of color
     */
    private void prepareDrawableColor(int color) {
        for (Drawable drawable : menuItemLayoutBinding.itemHeader.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(mainActivity, color), PorterDuff.Mode.SRC_IN));
            }
        }
    }

    /**
     * method for unset select style in menu item
     * @param holder holder for item in horizontal grid view
     */
    private void deselectItem(MenuItemViewHolder holder) {
        prepareDrawableColor(R.color.base_light_text);
        holder.menuItemLayoutBinding.itemHeader.setTextColor(ContextCompat.getColor(mainActivity, R.color.base_light_text));
    }

    /**
     * method for set select style in menu item
     * @param holder holder for item in horizontal grid view
     */
    private void selectItem(MenuItemViewHolder holder) {
        prepareDrawableColor(R.color.main_yellow_color);
        holder.menuItemLayoutBinding.itemHeader.setTextColor(ContextCompat.getColor(mainActivity, R.color.main_yellow_color));
    }

    /**
     * method for subscribe on KeyEventMutableLiveData {@link MenuFocusViewModel#getKeyEventMutableLiveData()}
     * for requesting focus on menu item
     * @param menuItemViewHolder holder for item in horizontal grid view
     */
    private void observeKeyEvent(MenuItemViewHolder menuItemViewHolder) {
        menuFocusViewModel.getKeyEventMutableLiveData().observe(mainActivity, keyCode -> {
            if (keyCode != null) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    Log.d("RESTORE_FOCUS", "observeKeyEvent: ");
                    menuItemViewHolder.itemView.requestFocus();
                    menuFocusViewModel.getKeyEventMutableLiveData().setValue(Integer.MIN_VALUE);
                }
            }
        });
    }

    /**
     * method for initialize search item in menu
     */
    private void initSearchItem() {
        menuItemLayoutBinding.itemHeader.setText("");
        menuItemLayoutBinding.itemHeader.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(mainActivity, R.drawable.ic_search_item),
                null, null, null
        );
        prepareDrawableColor(R.color.base_light_text);
        searchView.setVisibility(View.GONE);
    }

    /**
     * method for action on search menu item,
     * expand SearchView {@link SearchView}
     */
    private void onSearchItemClick() {
        searchView.setVisibility(View.VISIBLE);
        searchView.onActionViewExpanded();
    }

    /**
     * method for action on catalog menu item,
     * load data to catalog
     */
    private void onCatalogItemClick() {
        movieCatalogViewModel.setSearchState(false);
        movieCatalogViewModel.loadCatalog();
    }

    public static class MenuItemViewHolder extends HorizontalGridView.ViewHolder {

        private MenuItemLayoutBinding menuItemLayoutBinding;

        public MenuItemViewHolder(MenuItemLayoutBinding menuItemLayoutBinding) {
            super(menuItemLayoutBinding.getRoot());
            this.menuItemLayoutBinding = menuItemLayoutBinding;
        }
    }
}
