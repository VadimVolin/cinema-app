package vadim.volin.tv.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * LastItemFocusViewModel class that operate last focus view in catalog
 *
 * @author Vadym Volin
 */
public class LastItemFocusViewModel extends ViewModel {

    /**
     * field of {@link MutableLiveData} last card position in category
     */
    private MutableLiveData<Integer> lastItemPositionLiveData = new MutableLiveData<>();
    /**
     * field of {@link MutableLiveData} last category position in category
     */
    private MutableLiveData<Integer> categoryItemPositionLiveData = new MutableLiveData<>();

    /**
     * <p>
     * The method return lastItemPositionLiveData
     * </p>
     *
     * @return lastItemPositionLiveData {@link LastItemFocusViewModel#lastItemPositionLiveData}
     */
    public MutableLiveData<Integer> getLastItemPositionLiveData() {
        return lastItemPositionLiveData;
    }

    /**
     * <p>
     * The method return categoryItemPositionLiveData
     * </p>
     *
     * @return categoryItemPositionLiveData {@link LastItemFocusViewModel#categoryItemPositionLiveData}
     */
    public MutableLiveData<Integer> getCategoryItemPositionLiveData() {
        return categoryItemPositionLiveData;
    }

}
