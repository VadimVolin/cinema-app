package vadim.volin.movie_api.service.db;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import java.util.List;

import io.reactivex.Observable;
import vadim.volin.movie_api.service.cache.model.CombinedMovie;
import vadim.volin.movie_api.service.db.connection.MoviesDB;
import vadim.volin.movie_api.service.db.dao.MoviesDao;

public class ServiceDB {

    private static final String TAG = ServiceDB.class.getCanonicalName();
    private static ServiceDB serviceDB;
    private MoviesDao moviesDao;
    private MoviesDB moviesDB;

    private ServiceDB(Context context) {
        initDb(context);
    }

    /**
     * @param context
     * @return instance of database service
     */
    public static ServiceDB getInstance(Context context) {
        if (serviceDB == null) {
            serviceDB = new ServiceDB(context);
        }
        if (!serviceDB.isOpen()) {
            serviceDB.initDb(context);
        }
        return serviceDB;
    }

    public void initDb(Context context) {
        moviesDB = Room.databaseBuilder(context, MoviesDB.class, "movie-db").build();
        moviesDao = moviesDB.moviesDao();
        Log.d(TAG, "initDb: INIT MOVIESDB AND MOVIESDAO");
    }

    public boolean isOpen() {
        return serviceDB.moviesDB.isOpen();
    }

    /**
     * @param movies Put list of CombinedMovie to database
     */
    public void putMovies(List<CombinedMovie> movies) {
        moviesDao.putCombinedMovies(movies);
    }

    /**
     * @param keyWord
     * @return Observable of list CombinedMovie
     * <p>
     * Method return from database Observable of list CombinedMovie by keyWord
     */
    public Observable<List<CombinedMovie>> getMovieListByKeyWord(String keyWord) {
        return moviesDao.getMoviesByKeyWord(keyWord);
    }


    /**
     * @return Observable of list CombinedMovie
     * <p>
     * Method return from database Observable of all list CombinedMovie
     */
    public Observable<List<CombinedMovie>> getMovies() {
        return moviesDao.getAllMovies();
    }

    /**
     * @param imdbID
     * @return Observable object
     * <p>
     * search
     */
    public Observable<CombinedMovie> getMovieById(String imdbID) {
        return moviesDao.getMovieById(imdbID);
    }

    public void closeDB() {
        if (moviesDB != null) {
            moviesDB.close();
            moviesDao = null;
            moviesDB = null;
        }
    }
}
