package vadim.volin.tv.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * MenuFocusViewModel class that operate focus menu item
 *
 * @author Vadym Volin
 */
public class MenuFocusViewModel extends ViewModel {

    /**
     * field of {@link MutableLiveData} keyEventLiveData that operate keypad events
     */
    private MutableLiveData<Integer> keyEventMutableLiveData = new MutableLiveData<>();

    /**
     * <p>
     * The method return keyEventMutableLiveData
     * </p>
     *
     * @return keyEventMutableLiveData {@link MenuFocusViewModel#keyEventMutableLiveData}
     */
    public MutableLiveData<Integer> getKeyEventMutableLiveData() {
        return keyEventMutableLiveData;
    }
}
