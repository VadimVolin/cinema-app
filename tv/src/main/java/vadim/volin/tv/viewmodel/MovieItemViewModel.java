package vadim.volin.tv.viewmodel;

import android.util.Log;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import vadim.volin.movie_api.entity.MovieDetails;
import vadim.volin.movie_api.locator.ServiceLocator;
import vadim.volin.movie_api.service.cache.CachedMovieService;
import vadim.volin.movie_api.service.cache.CachedMovieServiceImpl;
import vadim.volin.movie_api.service.db.ServiceDB;
import vadim.volin.response.ServiceStatus;

public class MovieItemViewModel extends ViewModel {

    /**
     * field tag of class that contains class name
     */
    private static final String TAG = MovieItemViewModel.class.getCanonicalName();
    /**
     * field of {@link MutableLiveData} movieDetailsLiveData that save a data of {@link MovieDetails} object
     */
    private MutableLiveData<MovieDetails> movieDetailsLiveData = new MutableLiveData<>();
    /**
     * field of {@link MutableLiveData} serviceResponseLiveData that save a service response
     */
    private MutableLiveData<ServiceStatus> serviceResponseLiveData = new MediatorLiveData<>();
    /**
     * field of {@link ServiceDB} serviceDB, object for work with database service
     */
    private ServiceDB serviceDB;
    /**
     * field of {@link CachedMovieService} cachedMovieService, object for work with cache service
     */
    private CachedMovieService cachedMovieService;
    /**
     * field of {@link Disposable} disposableFromDb, disposable object for disposing in the end
     */
    private Disposable disposableFromDb;
    /**
     * field of {@link Disposable} disposableCache, disposable object for disposing in the end
     */
    private Disposable disposableCache;

    /**
     * <p>
     * Base constructor for creating a MovieItemViewModel {@link MovieItemViewModel}
     * </p>
     */
    public MovieItemViewModel() {
        serviceDB = (ServiceDB) ServiceLocator.getService(ServiceDB.class);
        cachedMovieService = (CachedMovieService) ServiceLocator.getService(CachedMovieServiceImpl.class);
    }

    /**
     * <p>
     * The method is search in database {@link MovieItemViewModel#serviceDB} movie by imdbId,
     * if data not found in database, start call cache {@link MovieItemViewModel#cacheMovieDataById(String)},
     * and update movieDetailsLiveData {@link MovieItemViewModel#movieDetailsLiveData}
     * if get a throwable object it update {@link MovieItemViewModel#serviceResponseLiveData}
     * </p>
     *
     * @param imDbId string value for get data that contains this id
     */
    public void getMovieDataById(String imDbId) {
        disposableFromDb = serviceDB.getMovieById(imDbId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        combinedMovie -> {
                            if (combinedMovie == null) {
                                cacheMovieDataById(imDbId);
                            } else {
                                movieDetailsLiveData.postValue(combinedMovie.convertToMovieDetails());
                            }
                        },
                        throwable -> {
                            serviceResponseLiveData.postValue(ServiceStatus.SERVICE_UNAVAILABLE_503);
                            Log.e(TAG, "onError: ", throwable);
                        }
                );
    }

    /**
     * <p>
     * The method cache data with imdbId using {@link MovieItemViewModel#cachedMovieService}
     * if get a throwable object it update {@link MovieItemViewModel#serviceResponseLiveData}
     * </p>
     */
    public void cacheMovieDataById(String imdbId) {
        disposableCache = cachedMovieService.getMovieInfo(imdbId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        combinedMovie -> {
                            if (combinedMovie == null) {
                                cacheMovieDataById(imdbId);
                            } else {
                                movieDetailsLiveData.postValue(combinedMovie.convertToMovieDetails());
                            }
                        },
                        throwable -> {
                            serviceResponseLiveData.postValue(ServiceStatus.SERVICE_UNAVAILABLE_503);
                            Log.e(TAG, "onError: ", throwable);
                        }
                );
    }

    /**
     * <p>
     * The method return movieDetailsLiveData
     * </p>
     *
     * @return movieDetailsLiveData {@link MovieItemViewModel#movieDetailsLiveData}
     */
    public MutableLiveData<MovieDetails> getMovieDetailsLiveData() {
        return movieDetailsLiveData;
    }

    /**
     * <p>
     * The method return serviceResponseLiveData
     * </p>
     *
     * @return serviceResponseLiveData {@link MovieItemViewModel#serviceResponseLiveData}
     */
    public MutableLiveData<ServiceStatus> getServiceResponseLiveData() {
        return serviceResponseLiveData;
    }

    /**
     * in this method disposableFromDb and disposableCache objects are dispose,
     */
    @Override
    protected void onCleared() {
        if (disposableFromDb != null) {
            disposableFromDb.dispose();
        }
        if (disposableCache != null) {
            disposableCache.dispose();
        }
        super.onCleared();
    }
}
