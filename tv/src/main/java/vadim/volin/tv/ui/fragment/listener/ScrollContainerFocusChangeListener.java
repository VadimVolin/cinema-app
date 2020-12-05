package vadim.volin.tv.ui.fragment.listener;

import android.view.View;
import android.widget.ScrollView;

import androidx.core.content.ContextCompat;

import vadim.volin.tv.R;

/**
 * ScrollContainerFocusChangeListener is a View.OnFocusChangeListener {@link View.OnFocusChangeListener}
 * that implement it for change focus in ScrollView {@link ScrollView}
 */
public class ScrollContainerFocusChangeListener implements View.OnFocusChangeListener {

    /**
     * field for layout in ScrollView
     */
    private View scrollContainer;

    /**
     * method for setting value to scrollContainer{@link ScrollContainerFocusChangeListener#scrollContainer}
     */
    public void setScrollContainer(View scrollContainer) {
        this.scrollContainer = scrollContainer;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (scrollContainer != null) {
            if (hasFocus) {
                scrollContainer.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.black_high_opacity));
            } else {
                scrollContainer.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.opacity));
            }
        }
    }
}
