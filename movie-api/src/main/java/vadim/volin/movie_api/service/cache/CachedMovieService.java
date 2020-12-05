package vadim.volin.movie_api.service.cache;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import vadim.volin.movie_api.service.cache.model.CombinedMovie;

public interface CachedMovieService {

    Single<List<CombinedMovie>> cachedMovies(String keyWord);

    Single<List<CombinedMovie>> cachedSeries(String keyWord);

    Observable<CombinedMovie> getMovieInfo(String movieId);
}
