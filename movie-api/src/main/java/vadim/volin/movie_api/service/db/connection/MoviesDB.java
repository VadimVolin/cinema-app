package vadim.volin.movie_api.service.db.connection;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import vadim.volin.movie_api.service.cache.model.CombinedMovie;
import vadim.volin.movie_api.service.db.dao.MoviesDao;

@Database(entities = {CombinedMovie.class}, version = 1)
public abstract class MoviesDB extends RoomDatabase {
    public abstract MoviesDao moviesDao();
}
