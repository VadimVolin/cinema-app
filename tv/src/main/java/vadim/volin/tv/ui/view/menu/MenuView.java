package vadim.volin.tv.ui.view.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.leanback.widget.HorizontalGridView;
import androidx.lifecycle.ViewModelProvider;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vadim.volin.tv.R;
import vadim.volin.tv.activity.MainActivity;
import vadim.volin.tv.adapters.menu.MenuItemAdapter;
import vadim.volin.tv.databinding.MenuViewLayoutBinding;
import vadim.volin.tv.ui.view.menu.listener.OnSearchItemClickListener;
import vadim.volin.tv.viewmodel.MovieCatalogViewModel;
import vadim.volin.util.NetworkUtil;
import vadim.volin.util.ToastShower;

/**
 * MenuView class is custom view that contains {@link SearchView} SearchView
 * and {@link HorizontalGridView} HorizontalGridView
 *
 * @author Vadym Volin
 */
public class MenuView extends LinearLayout {

    /**
     * field of {@link MovieCatalogViewModel} movieCatalogViewModel for operate catalog data
     */
    private MovieCatalogViewModel movieCatalogViewModel;
    /**
     * field that contains view for search {@link SearchView}
     */
    private SearchView searchBar;
    /**
     * field that contains view for menu {@link HorizontalGridView}
     */
    private HorizontalGridView menuRecyclerView;

    public MenuView(Context context) {
        super(context, null);
    }

    public MenuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
        vadim.volin.tv.databinding.MenuViewLayoutBinding menuViewLayoutBinding = MenuViewLayoutBinding.inflate(LayoutInflater.from(context), this);
        movieCatalogViewModel = new ViewModelProvider((MainActivity) context).get(MovieCatalogViewModel.class);
        searchBar = menuViewLayoutBinding.searchBar;
        menuRecyclerView = menuViewLayoutBinding.menuRecyclerView;
        OnSearchItemClickListener onSearchItemClickListener = new OnSearchItemClickListener(menuRecyclerView, searchBar);
        searchBar.setOnSearchClickListener(onSearchItemClickListener);
        OnSearchQueryTextListener onSearchQueryTextListener = new OnSearchQueryTextListener();
        searchBar.setOnQueryTextListener(onSearchQueryTextListener);
        List<String> menuItems = new LinkedList<>();
        menuItems.add(context.getString(R.string.menu_search_item));
        menuItems.add(context.getString(R.string.menu_catalog_item));
        MenuItemAdapter menuItemAdapter = new MenuItemAdapter(menuItems, (MainActivity) context, menuViewLayoutBinding);
        menuRecyclerView.setAdapter(menuItemAdapter);
    }

    public MenuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * <p>
     * The method is update data after search action, if network is unavailable
     * show notification on screen
     * </p>
     *
     * @param keyWord string value for search
     */
    public void updateCatalogData(String keyWord) {
        if (NetworkUtil.isNetworkAvailable(getContext())) {
            movieCatalogViewModel.setSearchState(true);
            movieCatalogViewModel.searchByKeyWord(keyWord);
        } else {
            ToastShower.showAlert(getContext(), getContext().getString(R.string.NET_CONNECTION));
        }
    }

    /**
     * <p>
     * The method is check input query using regexp, if has error
     * show notification on screen
     * </p>
     *
     * @param query string value for check
     */
    private void checkInputSearchQuery(String query) {
        String regex = getContext().getString(R.string.search_input_regexp);
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(query);
        if (!matcher.matches()) {
            ToastShower.showAlert(getContext(), getContext().getString(R.string.search_input_error_alert));
        }
    }

    /**
     * <p>
     * The method is check input query using regexp, if has error
     * show notification on screen. After checking query submit and
     * update search list
     * </p>
     *
     * @param query string value for check
     */
    private void checkAndSubmitSearch(String query) {
        String regex = getContext().getString(R.string.search_input_regexp);
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(query);
        if (NetworkUtil.isNetworkAvailable(getContext())) {
            boolean equals = query.trim().replaceAll("\\s+", "").equals("");
            if (equals) {
                searchBar.onActionViewCollapsed();
                menuRecyclerView.setVisibility(View.VISIBLE);
            } else {
                if (matcher.matches()) {
                    searchBar.onActionViewCollapsed();
                    menuRecyclerView.setVisibility(View.VISIBLE);
                    updateCatalogData(query);
                } else {
                    ToastShower.showAlert(getContext(), getContext().getString(R.string.search_input_error_alert));
                }
            }
        } else {
            ToastShower.showAlert(getContext(), getContext().getString(R.string.NET_CONNECTION));
        }
    }

    /**
     * <p>
     * class that implement {@link SearchView.OnQueryTextListener} and init logic for using search
     * </p>
     */
    public class OnSearchQueryTextListener implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String query) {
            checkAndSubmitSearch(query);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            checkInputSearchQuery(newText);
            return false;
        }

    }

}
