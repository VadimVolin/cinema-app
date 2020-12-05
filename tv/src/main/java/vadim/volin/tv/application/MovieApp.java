package vadim.volin.tv.application;

import android.app.Application;

import vadim.volin.movie_api.locator.LookupService;
import vadim.volin.movie_api.locator.ServiceLocator;

/**
 * class that initialize Application {@link Application} in app,
 * and set ApplicationContext {@link android.content.Context}
 * to LookupService {@link LookupService} in ServiceLocator {@link ServiceLocator}
 */
public class MovieApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ServiceLocator.getLookupService().setContext(getApplicationContext());
    }

}
