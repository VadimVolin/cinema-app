package vadim.volin.tv.viewmodel;

import android.os.CountDownTimer;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import vadim.volin.movie_api.entity.Movie;
import vadim.volin.movie_api.locator.ServiceLocator;
import vadim.volin.movie_api.service.cache.CachedMovieService;
import vadim.volin.movie_api.service.cache.CachedMovieServiceImpl;
import vadim.volin.movie_api.service.cache.model.CombinedMovie;
import vadim.volin.movie_api.service.db.ServiceDB;
import vadim.volin.response.ServiceStatus;

/**
 * MovieCatalogViewModel class that operate search and catalog lists of movie
 *
 * @author Vadym Volin
 */
public class MovieCatalogViewModel extends ViewModel {

    /**
     * int field that contains tick value for CountDownTimer {@link CountDownTimer}
     */
    public static final int COUNT_DOWN_INTERVAL_MILLIS = 1000;
    /**
     * int field that contains time to finish value for CountDownTimer {@link CountDownTimer}
     */
    public static final int TIMER_WORK_MILLIS = 15 * 60 * 1000;
    /**
     * field tag of class that contains class name
     */
    private static final String TAG = MovieCatalogViewModel.class.getCanonicalName();
    /**
     * field of {@link MutableLiveData} movieListLiveData that save a list of {@link Movie} object
     */
    private MutableLiveData<List<Movie>> movieListLiveData = new MutableLiveData<>();
    /**
     * field of {@link MutableLiveData} searchLiveData that save a search list of {@link Movie} object
     */
    private MutableLiveData<List<Movie>> searchLiveData = new MutableLiveData<>();
    /**
     * field of {@link MutableLiveData} serviceResponseLiveData that save a service response
     */
    private MutableLiveData<ServiceStatus> serviceResponseLiveData = new MutableLiveData<>();
    /**
     * field of {@link CompositeDisposable} compositeDisposable that save disposable objects for disposing it in the end
     */
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    /**
     * field of {@link ServiceDB} serviceDB, object for work with database service
     */
    private ServiceDB serviceDB;
    /**
     * field of {@link CachedMovieService} cachedMovieService, object for work with cache service
     */
    private CachedMovieService cachedMovieService;
    /**
     * field of {@link Disposable} disposableSearch, disposable object for disposing after search
     */
    private Disposable disposableSearch;
    /**
     * field of {@link CountDownTimer} countDownTimer, timer for restarting cache after {@link MovieCatalogViewModel#TIMER_WORK_MILLIS}
     */
    private CountDownTimer countDownTimer;
    /**
     * boolean field isSearchState, checking state for updating catalog after caching
     */
    private boolean isSearchState = false;
    /**
     * field of {@link String} keyWord, for cache data
     */
    private String keyWord = "apple";

    /**
     * <p>
     * Base constructor for creating a MovieCatalogViewModel {@link MovieCatalogViewModel}
     * </p>
     */
    public MovieCatalogViewModel() {
        serviceDB = (ServiceDB) ServiceLocator.getService(ServiceDB.class);
        cachedMovieService = (CachedMovieService) ServiceLocator.getService(CachedMovieServiceImpl.class);

        countDownTimer = new CountDownTimer(TIMER_WORK_MILLIS, COUNT_DOWN_INTERVAL_MILLIS) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                if (keyWord != null) {
                    cacheSearchDataToDb();
                }
                start();
            }
        };
        countDownTimer.start();
    }

    /**
     * <p>
     * method that load catalog data
     * </p>
     */
    public void loadCatalog() {
        movieListLiveData.postValue(movieListLiveData.getValue());
    }

    /**
     * <p>
     * The method is first point for cache, that combine and wait two observable's
     * (requests for get data by two types serise/movie) in one and return it
     * </p>
     *
     * @param keyWord string value for cache by ite
     * @return {@link Observable} object that contains results from two observables
     */
    public Observable<List<CombinedMovie>> cacheSearchData(String keyWord) {
        return Observable.zip(
                cachedMovieService.cachedMovies(keyWord)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .toObservable(),
                cachedMovieService.cachedSeries(keyWord)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .toObservable(),
                ((combinedMovies, combinedMovies2) -> {
                    combinedMovies.addAll(combinedMovies2);
                    return combinedMovies;
                })
        );
    }

    /**
     * <p>
     * The method call cacheSearchData {@link MovieCatalogViewModel#cacheSearchData(String)}
     * with keyWord {@link MovieCatalogViewModel#keyWord} and save data to database
     * using {@link MovieCatalogViewModel#serviceDB}
     * if get a throwable object it update {@link MovieCatalogViewModel#serviceResponseLiveData}
     * </p>
     */
    public void cacheSearchDataToDb() {
        Disposable disposableFromCache = cacheSearchData(keyWord)
                .subscribe(
                        (combinedMovies) -> {
                            if (!combinedMovies.isEmpty()) {
                                serviceDB.putMovies(combinedMovies);
                            }
                        },
                        (throwable) -> Log.e(TAG, "onError:", throwable),
                        () -> {
                        }
                );
        compositeDisposable.add(disposableFromCache);
    }

    /**
     * <p>
     * The method is search in database {@link MovieCatalogViewModel#serviceDB}
     * list of movies by keyword {@link MovieCatalogViewModel#keyWord},
     * if data not found in database, start call cache {@link MovieCatalogViewModel#cacheSearchData(String)},
     * and update searchLiveData {@link MovieCatalogViewModel#searchLiveData} and save data to database
     * if get a throwable object it update {@link MovieCatalogViewModel#serviceResponseLiveData}
     * </p>
     *
     * @param keyWord string value for get data that contains this keyWord in movie name
     */
    public void searchByKeyWord(String keyWord) {
        disposableSearch = serviceDB.getMovieListByKeyWord(keyWord)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(combinedMovies -> {
                    if (combinedMovies.isEmpty()) {
                        return cacheSearchData(keyWord);
                    } else {
                        return Observable.just(combinedMovies);
                    }
                })
                .subscribe(
                        (combinedMovies) -> {
                            if (!combinedMovies.isEmpty()) {
                                this.keyWord = keyWord;
                            }
                            List<Movie> movieList = packToMovieList(combinedMovies);
                            searchLiveData.postValue(movieList);
                            serviceDB.putMovies(combinedMovies);
                        },
                        (throwable) -> {
                            Log.e(TAG, "onError:", throwable);
                            if (throwable instanceof NullPointerException) {
                                serviceResponseLiveData.postValue(ServiceStatus.BAD_REQUEST_400);
                            } else {
                                serviceResponseLiveData.postValue(ServiceStatus.SERVER_ERROR_500);
                            }
                        }
                );
        compositeDisposable.add(disposableSearch);
    }

    /**
     * <p>
     * The method is get list of all movies in database {@link MovieCatalogViewModel#serviceDB}
     * and update movieListLiveData {@link MovieCatalogViewModel#movieListLiveData}
     * and save data to database, if get a throwable object it update
     * {@link MovieCatalogViewModel#serviceResponseLiveData}
     * </p>
     */
    public void updateCatalog() {
        Disposable disposableFromCatalog = serviceDB.getMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        combinedMovies -> {
                            List<Movie> movieList = packToMovieList(combinedMovies);
                            movieListLiveData.postValue(movieList);
                        },
                        throwable -> {
                            Log.e(TAG, "onError:", throwable);
                            serviceResponseLiveData.postValue(ServiceStatus.SERVER_ERROR_500);
                        }
                );
        compositeDisposable.add(disposableFromCatalog);
    }

    /**
     * <p>
     * The method return serviceResponseLiveData
     * </p>
     *
     * @return serviceResponseLiveData {@link MovieCatalogViewModel#serviceResponseLiveData}
     */
    public MutableLiveData<ServiceStatus> getServiceResponseLiveData() {
        return serviceResponseLiveData;
    }

    /**
     * <p>
     * The method return movieListLiveData
     * </p>
     *
     * @return movieListLiveData {@link MovieCatalogViewModel#movieListLiveData}
     */
    public MutableLiveData<List<Movie>> getMovieListLiveData() {
        return movieListLiveData;
    }

    /**
     * <p>
     * The method return searchLiveData
     * </p>
     *
     * @return searchLiveData {@link MovieCatalogViewModel#searchLiveData}
     */
    public MutableLiveData<List<Movie>> getSearchLiveData() {
        return searchLiveData;
    }

    /**
     * <p>
     * The method return disposableSearch
     * </p>
     *
     * @return disposableSearch {@link MovieCatalogViewModel#disposableSearch}
     */
    public Disposable getDisposableSearch() {
        return disposableSearch;
    }

    /**
     * in this method compositeDisposable object is dispose,
     * and countDownTimer is cancel
     */
    @Override
    protected void onCleared() {
        if (compositeDisposable.size() > 0) {
            compositeDisposable.dispose();
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.onCleared();
    }

    /**
     * <p>
     * The method return boolean value, that what state now(search/not search)
     * </p>
     *
     * @return isSearchState {@link MovieCatalogViewModel#isSearchState}
     */
    public boolean isSearchState() {
        return isSearchState;
    }

    /**
     * <p>
     * The method set new boolean value to isSearchState
     * </p>
     */
    public void setSearchState(boolean searchState) {
        this.isSearchState = searchState;
    }

    /**
     * <p>
     * The method pack list of {@link CombinedMovie} to list of {@link Movie}
     * </p>
     *
     * @return movieList {@link List} {@link Movie}
     */
    public List<Movie> packToMovieList(List<CombinedMovie> combinedMovies) {
        List<Movie> movieList = new LinkedList<>();
        for (int i = 0; i < combinedMovies.size(); i++) {
            movieList.add(combinedMovies.get(i).convertToMovie());
        }
        return movieList;
    }
}