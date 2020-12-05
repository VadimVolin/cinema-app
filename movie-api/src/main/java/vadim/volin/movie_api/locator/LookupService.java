package vadim.volin.movie_api.locator;

import android.content.Context;

import vadim.volin.movie_api.service.cache.CachedMovieServiceImpl;
import vadim.volin.movie_api.service.db.ServiceDB;
import vadim.volin.movie_api.service.player.ExoPlayerService;

public class LookupService {

    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public Object lookupService(Class className) {
        if (CachedMovieServiceImpl.class == className) {
            return CachedMovieServiceImpl.getInstance();
        } else if (ExoPlayerService.class == className) {
            return ExoPlayerService.getInstance(context);
        } else if (ServiceDB.class == className) {
            return ServiceDB.getInstance(context);
        } else {
            return null;
        }
    }

}
