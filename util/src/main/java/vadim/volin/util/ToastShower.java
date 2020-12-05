package vadim.volin.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Can make it easier and abstract to work with showing Toast on the screen.
 *
 * @author Vadym Volin
 */
public class ToastShower {

    /**
     * <p>
     * The method allows you to create a Toast and display it on the screen with the appropriate string passed in.
     * </p>
     *
     * @param context interface to global information about an application environment.  This is
     *                an abstract class whose implementation is provided by
     *                the Android system.  It
     *                allows access to application-specific resources and classes, as well as
     *                up-calls for application-level operations such as launching activities,
     *                broadcasting and receiving intents, etc.
     * @param message string value to display
     * @since 1.0
     */
    public static void showAlert(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
