package vadim.volin.movie_api.service.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import vadim.volin.movie_api.service.cache.model.CombinedMovie;

@Dao
public interface MoviesDao {

    @Query("SELECT * FROM combinedmovie")
    Observable<List<CombinedMovie>> getAllMovies();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void putCombinedMovie(CombinedMovie combinedMovie);

    @Query(value = "SELECT * FROM combinedmovie WHERE title LIKE ('%' || :keyWord || '%')")
    Observable<List<CombinedMovie>> getMoviesByKeyWord(String keyWord);

    @Query(value = "SELECT * FROM combinedmovie WHERE imdbID = :id")
    Observable<CombinedMovie> getMovieById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void putCombinedMovies(List<CombinedMovie> movies);
}
