package vadim.volin.mobile;

import android.app.Application;

import androidx.multidex.MultiDex;

import vadim.volin.movie_api.locator.ServiceLocator;

public class MovieApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ServiceLocator.getLookupService().setContext(getApplicationContext());
        MultiDex.install(this);
    }

}
