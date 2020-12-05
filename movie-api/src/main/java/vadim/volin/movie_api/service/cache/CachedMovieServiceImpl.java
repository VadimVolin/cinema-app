package vadim.volin.movie_api.service.cache;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import vadim.volin.movie_api.service.cache.model.CombinedMovie;
import vadim.volin.movie_api.service.cache.model.Search;

public class CachedMovieServiceImpl implements CachedMovieService {

    public static final String TAG = CachedMovieServiceImpl.class.getCanonicalName();
    public static final String BASE_URL = "https://www.omdbapi.com/";
    private static CachedMovieService cachedMovieServiceInstance;
    private MovieApiREST movieApiREST;

    private CachedMovieServiceImpl() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        movieApiREST = retrofit.create(MovieApiREST.class);
    }

    public static CachedMovieService getInstance() {
        if (cachedMovieServiceInstance == null) {
            cachedMovieServiceInstance = new CachedMovieServiceImpl();
        }
        return cachedMovieServiceInstance;
    }

    public Single<List<CombinedMovie>> cachedMovies(String keyWord) {
        return movieApiREST
                .getMoviesList(keyWord)
                .subscribeOn(Schedulers.io())
                .map(Search::getMovies)
                .flatMap(Observable::fromIterable)
                .flatMap(retrofitMovie -> getMovieInfo(retrofitMovie.getImdbID())
                        .subscribeOn(Schedulers.io())
                )
                .toList();
    }

    public Single<List<CombinedMovie>> cachedSeries(String keyWord) {
        return movieApiREST
                .getSeriesList(keyWord)
                .subscribeOn(Schedulers.io())
                .map(Search::getMovies)
                .flatMap(Observable::fromIterable)
                .flatMap(retrofitMovie -> getMovieInfo(retrofitMovie.getImdbID())
                        .subscribeOn(Schedulers.io())
                )
                .toList();
    }

    public Observable<CombinedMovie> getMovieInfo(String movieId) {
        return movieApiREST
                .getMovieDetails(movieId);
    }

}