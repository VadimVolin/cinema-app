package vadim.volin.tv.adapters.catalog.category.listener;

import android.view.View;
import android.view.ViewTreeObserver;

/**
 * class for do action on layout change, for restore last focus
 */
public class CategoryGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {

    /**
     * field for last focus view
     */
    private View lastFocusItem;
    /**
     * field observer for initialize listener or remove it
     */
    private ViewTreeObserver viewTreeObserver;

    /**
     * base constructor for CategoryGlobalLayoutListener
     * @param lastFocusItem parameter for init last focus view
     * @param viewTreeObserver parameter for init viewTreeObserver
     */
    public CategoryGlobalLayoutListener(View lastFocusItem, ViewTreeObserver viewTreeObserver) {
        this.lastFocusItem = lastFocusItem;
        this.viewTreeObserver = viewTreeObserver;
    }

    @Override
    public void onGlobalLayout() {
        if (lastFocusItem != null) {
            lastFocusItem.requestFocus();
            lastFocusItem = null;
        }
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.removeOnGlobalLayoutListener(this);
        }
    }
}
