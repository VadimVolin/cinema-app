package vadim.volin.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * NetworkUtil contains method for checking network connection
 *
 * @author Vadym Volin
 */
public class NetworkUtil {

    /**
     * <p>
     * The method helps to check the internet connection using ConnectivityManager
     * </p>
     *
     * @param context interface to global information about an application environment.  This is
     *                an abstract class whose implementation is provided by
     *                the Android system.  It
     *                allows access to application-specific resources and classes, as well as
     *                up-calls for application-level operations such as launching activities,
     *                broadcasting and receiving intents, etc.
     * @return boolean value that network connect true/false
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
