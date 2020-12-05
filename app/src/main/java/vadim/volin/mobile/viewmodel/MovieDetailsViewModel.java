package vadim.volin.mobile.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import vadim.volin.movie_api.entity.MovieDetails;
import vadim.volin.mobile.error.ServiceStatus;
import vadim.volin.movie_api.locator.ServiceLocator;
import vadim.volin.movie_api.service.cache.CachedMovieService;
import vadim.volin.movie_api.service.cache.CachedMovieServiceImpl;
import vadim.volin.movie_api.service.cache.model.CombinedMovie;
import vadim.volin.movie_api.service.db.ServiceDB;

public class MovieDetailsViewModel extends ViewModel {

    private final ServiceDB serviceDB;
    private final MutableLiveData<MovieDetails> movieDetailsLiveData = new MutableLiveData<>();
    private final MutableLiveData<ServiceStatus> errorLiveData = new MutableLiveData<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final CachedMovieService cachedMovieService;
    private Disposable disposable;
    public static final String TAG = MovieDetailsViewModel.class.getCanonicalName();

    public MovieDetailsViewModel() {
        serviceDB = (ServiceDB) ServiceLocator.getService(ServiceDB.class);
        cachedMovieService = (CachedMovieService) ServiceLocator.getService(CachedMovieServiceImpl.class);
    }

    /**
     * Get object from database and put it to LiveData
     * if object not found, load data from network and save to DB
     * after this update LiveData
     *
     * @param imdbID string for search object in database or network
     */
    public void getRequestData(String imdbID) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }

        if (imdbID != null) {
            disposable = serviceDB.getMovieById(imdbID)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(combinedMovie -> {
                        if (combinedMovie == null) {
                            cacheMovieById(imdbID);
                        } else {
                            movieDetailsLiveData.postValue(combinedMovie.convertToMovieDetails());
                        }
                    });
        }
    }

    /**
     * Load film from network and update LiveData
     *
     * @param imdbId string for search film by id
     */
    public void cacheMovieById(String imdbId) {
        cachedMovieService.getMovieInfo(imdbId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CombinedMovie>() {

                    private MovieDetails movieDetails;

                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NotNull CombinedMovie combinedMovie) {
                        this.movieDetails = combinedMovie.convertToMovieDetails();
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.e(TAG, "onError: ", e);
                        errorLiveData.postValue(ServiceStatus.SERVER_ERROR_500);
                    }

                    @Override
                    public void onComplete() {
                        if (movieDetails == null) {
                            errorLiveData.postValue(ServiceStatus.NO_CONTENT_204);
                        } else {
                            movieDetailsLiveData.postValue(this.movieDetails);
                        }

                        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
                            compositeDisposable.dispose();
                        }
                    }
                });
    }

    /**
     * @return LiveData for update data MovieDetails object
     * @see LiveData
     * @see MovieDetails
     */
    public LiveData<MovieDetails> getMovieDetailsLiveData() {
        return movieDetailsLiveData;
    }

    /**
     * @return MutableLiveData for update and check error handling
     * @see MutableLiveData
     * @see ServiceStatus
     */
    public MutableLiveData<ServiceStatus> getErrorLiveData() {
        return errorLiveData;
    }

}
