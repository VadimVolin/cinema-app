package vadim.volin.tv.ui.view.menu.listener;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import androidx.leanback.widget.HorizontalGridView;

/**
 * MenuView class is custom view that contains {@link SearchView} SearchView
 * and {@link HorizontalGridView} HorizontalGridView
 *
 * @author Vadym Volin
 */
public class OnSearchItemClickListener implements View.OnClickListener {

    /**
     * field that contains view for search {@link SearchView}
     */
    private SearchView searchBar;
    /**
     * field that contains view for menu {@link HorizontalGridView}
     */
    private HorizontalGridView menuRecyclerView;

    /**
     * base constructor for init menu HorizontalGridView {@link HorizontalGridView}
     * and SearchView {@link SearchView}
     */
    public OnSearchItemClickListener(HorizontalGridView menuRecyclerView, SearchView searchBar) {
        this.menuRecyclerView = menuRecyclerView;
        this.searchBar = searchBar;
    }

    /**
     * this method implement onclick logic for SearchView
     */
    @Override
    public void onClick(View v) {
        searchBar.onActionViewExpanded();
        menuRecyclerView.setVisibility(View.GONE);
        searchBar.setOnQueryTextFocusChangeListener((v1, hasFocus) -> {
            if (!hasFocus) {
                searchBar.onActionViewCollapsed();
                searchBar.setVisibility(View.GONE);
                menuRecyclerView.setVisibility(View.VISIBLE);
                hideKeyBoard(v);
            }
        });
    }

    /**
     * this method hide screen keyboard using {@link InputMethodManager}
     */
    public void hideKeyBoard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
