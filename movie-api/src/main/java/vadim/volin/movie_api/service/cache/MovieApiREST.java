package vadim.volin.movie_api.service.cache;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import vadim.volin.movie_api.service.cache.model.CombinedMovie;
import vadim.volin.movie_api.service.cache.model.Search;

public interface MovieApiREST {

    @GET("?apikey=2f91c9c2&type=movie")
    Observable<Search> getMoviesList(@Query("s") String keyWord);

    @GET("?apikey=2f91c9c2&type=series")
    Observable<Search> getSeriesList(@Query("s") String keyWord);

    @GET("?apikey=2f91c9c2&plot=full")
    Observable<CombinedMovie> getMovieDetails(@Query("i") String keyId);

}
