package vadim.volin.tv.ui.fragment.listener;

import android.view.View;
import android.widget.ScrollView;

/**
 * InfoFragmentLayoutChangeListener is a OnLayoutChangeListener {@link View.OnLayoutChangeListener}
 * that implement it for understand need scroll for ScrollView {@link ScrollView} or no
 */
public class InfoFragmentLayoutChangeListener implements View.OnLayoutChangeListener {

    /**
     * field for focus action in scroll view {@link ScrollView}
     */
    private ScrollContainerFocusChangeListener scrollContainerFocusChangeListener;

    /**
     * base constructor for initialize scrollContainerFocusChangeListener {@link ScrollContainerFocusChangeListener}
     */
    public InfoFragmentLayoutChangeListener() {
        scrollContainerFocusChangeListener = new ScrollContainerFocusChangeListener();
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        int scrollViewHeight = v.getHeight() - v.getPaddingTop() - v.getPaddingBottom();
        int scrollLayoutHeight = 0;
        View scrollLayout = ((ScrollView) v).getChildAt(0);
        if (scrollLayout != null) {
            scrollLayoutHeight = scrollLayout.getHeight();
            scrollContainerFocusChangeListener.setScrollContainer(scrollLayout);
        }
        v.setOnFocusChangeListener(scrollContainerFocusChangeListener);
        v.setFocusable(scrollViewHeight < scrollLayoutHeight);
    }
}
