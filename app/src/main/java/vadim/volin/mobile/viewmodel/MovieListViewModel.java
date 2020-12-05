package vadim.volin.mobile.viewmodel;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import vadim.volin.mobile.error.ServiceStatus;
import vadim.volin.movie_api.entity.Movie;
import vadim.volin.movie_api.locator.ServiceLocator;
import vadim.volin.movie_api.service.cache.CachedMovieService;
import vadim.volin.movie_api.service.cache.CachedMovieServiceImpl;
import vadim.volin.movie_api.service.cache.model.CombinedMovie;
import vadim.volin.movie_api.service.db.ServiceDB;

public class MovieListViewModel extends ViewModel {

    private final MutableLiveData<List<Movie>> movieListLiveData = new MutableLiveData<>();
    private final MutableLiveData<ServiceStatus> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> progressBarLiveData = new MutableLiveData<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final CachedMovieService cachedMovieService;
    private final ServiceDB serviceDB;
    private Disposable dbSubscriberDisposable;
    private String keyWord;
    private final CountDownTimer timer;

    public static final String TAG = MovieListViewModel.class.getCanonicalName();


    /**
     * Creates a MovieListViewModel with no value assigned to it.
     * Initialize CachedMovieService from ServiceLocator
     *
     * @see CachedMovieService
     * @see CachedMovieServiceImpl
     * @see ServiceLocator#getService(Class)
     * Initialize ServiceDB from ServiceLocator for updating or getting data from DB
     * @see ServiceDB#getInstance(Context)
     * Start CountDownTimer for updating data cache in database
     * @see CountDownTimer#CountDownTimer(long, long)
     * @see MovieListViewModel#cacheDataByKeyWord(String)
     */
    public MovieListViewModel() {
        cachedMovieService = (CachedMovieService) ServiceLocator.getService(CachedMovieServiceImpl.class);
        serviceDB = (ServiceDB) ServiceLocator.getService(ServiceDB.class);
        timer = new CountDownTimer(15 * 60 * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            //            TODO start
            public void onFinish() {
                if (keyWord != null) {
                    cacheDataByKeyWord(keyWord);
                }
                start();
            }
        };
    }

    /**
     * First of all in method check Disposable object on null and on isDisposed,
     * if disposable object is not null and not disposed, dispose it.
     * Next get data from database by key word and update MutableLiveData
     * if data not found start load data from network, convert backend model to ui,
     * put to DB and update LiveData
     *
     * @param keyWord the string for search data
     * @see Disposable#isDisposed()
     * @see Disposable#dispose()
     * @see MutableLiveData#postValue(Object)
     */
    public void getRequestData(String keyWord) {
        this.keyWord = keyWord;
        if (dbSubscriberDisposable != null && !dbSubscriberDisposable.isDisposed()) {
            dbSubscriberDisposable.dispose();
        }
        dbSubscriberDisposable = serviceDB.getMovieListByKeyWord(keyWord)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(combinedMovies -> {
                    progressBarLiveData.postValue(true);
                    List<Movie> movieList = new LinkedList<>();
                    for (int i = 0; i < combinedMovies.size(); i++) {
                        movieList.add(combinedMovies.get(i).convertToMovie());
                    }
                    if (movieList.isEmpty()) {
                        if (timer != null) {
                            timer.cancel();
                            timer.onFinish();
                        }
                    } else {
                        timer.start();
                    }
                    progressBarLiveData.postValue(false);
                    movieListLiveData.postValue(movieList);
                });
    }

    /**
     * Load data from network using CachedMovieService
     * and put data to database
     *
     * @param keyWord the string for load data from network and update database
     * @see CachedMovieService
     * @see CachedMovieServiceImpl#cachedMovies(String)
     * @see MutableLiveData#postValue(Object)
     */
    public void cacheDataByKeyWord(String keyWord) {
        cachedMovieService.cachedMovies(keyWord)
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<List<CombinedMovie>>() {

                    Disposable disposable = null;

                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                        progressBarLiveData.postValue(true);
                        disposable = d;
                    }

                    @Override
                    public void onSuccess(@NotNull List<CombinedMovie> combinedMovies) {
                        Log.d(TAG, "onSuccess: " + combinedMovies.toString());
                        serviceDB.putMovies(combinedMovies);
                        progressBarLiveData.postValue(false);
                        disposable.dispose();
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        errorLiveData.postValue(ServiceStatus.SERVER_ERROR_500);
                        progressBarLiveData.postValue(false);
                        Log.e(TAG, "cachedMovies:  ", e);
                        disposable.dispose();
                    }
                });

//        cachedMovieService.cachedMovies(keyWord)
//                .subscribeOn(Schedulers.io())
//                .subscribe(new Observer<List<CombinedMovie>>() {
//                    //                    TODO
//                    List<CombinedMovie> movies = new LinkedList<>();
//
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        progressBarLiveData.postValue(true);
////                        Change
//                        compositeDisposable.add(d);
//                    }
//
//                    @Override
//                    public void onNext(List<CombinedMovie> combinedMovies) {
//                        movies.clear();
//                        movies.addAll(combinedMovies);
//                        progressBarLiveData.postValue(true);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        errorLiveData.postValue(ServiceStatus.SERVER_ERROR_500);
//                        progressBarLiveData.postValue(false);
//                        Log.e(TAG, "cachedMovies:  ", e);
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        serviceDB.putMovies(movies);
//                        progressBarLiveData.postValue(false);
//                    }
//                });
    }

    /**
     * @return MutableLivaData for update progressBar state
     * @see MutableLiveData
     */
    public MutableLiveData<Boolean> getProgressBarLiveData() {
        return progressBarLiveData;
    }

    /**
     * @return MutableLiveData for update list of Movies data
     * @see Movie
     * @see LiveData
     */
    public LiveData<List<Movie>> getMovieListLiveData() {
        return movieListLiveData;
    }

    /**
     * @return MutableLiveData for update and check error handling
     * @see MutableLiveData
     * @see ServiceStatus
     */
    public MutableLiveData<ServiceStatus> getErrorLiveData() {
        return errorLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
        serviceDB.closeDB();
    }
}
