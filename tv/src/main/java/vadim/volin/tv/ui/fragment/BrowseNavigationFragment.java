package vadim.volin.tv.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.leanback.widget.VerticalGridView;
import androidx.lifecycle.ViewModelProvider;

import java.util.LinkedList;
import java.util.List;

import vadim.volin.entity.Category;
import vadim.volin.response.ServiceStatus;
import vadim.volin.tv.R;
import vadim.volin.tv.adapters.catalog.MoviesCategoryAdapter;
import vadim.volin.tv.databinding.BrowseNavFragmentBinding;
import vadim.volin.tv.viewmodel.MovieCatalogViewModel;
import vadim.volin.util.CategoryPacker;
import vadim.volin.util.NetworkUtil;
import vadim.volin.util.ToastShower;

/**
 * Main fragment in application, use lifecycle from Fragment {@link Fragment}
 */
public class BrowseNavigationFragment extends Fragment {

    /**
     * field category vertical grid view {@link VerticalGridView}
     */
    private VerticalGridView mainCategoryRecycler;
    /**
     * field for catalog adapter {@link MoviesCategoryAdapter}
     */
    private MoviesCategoryAdapter moviesCategoryAdapter;
    /**
     * field for MovieCatalogViewModel {@link MovieCatalogViewModel}
     */
    private MovieCatalogViewModel movieCatalogViewModel;
    /**
     * field for list {@link List} of categories {@link Category}
     */
    private List<Category> categoryList = new LinkedList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        movieCatalogViewModel = new ViewModelProvider(requireActivity()).get(MovieCatalogViewModel.class);
        updateCatalogData();
        BrowseNavFragmentBinding browseNavFragmentBinding = BrowseNavFragmentBinding.inflate(inflater, container, false);
        mainCategoryRecycler = browseNavFragmentBinding.moviesCategoryRecycler;
        return browseNavFragmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        moviesCategoryAdapter = new MoviesCategoryAdapter(categoryList, requireActivity());
        mainCategoryRecycler.setAdapter(moviesCategoryAdapter);
        subscribeOnLiveData();
    }

    /**
     * <p>
     * This method is load subscribing on live data's logic using
     * {@link MovieCatalogViewModel#getMovieListLiveData()},
     * {@link MovieCatalogViewModel#getSearchLiveData()},
     * {@link MovieCatalogViewModel#getServiceResponseLiveData()}
     * </p>
     */
    public void subscribeOnLiveData() {
        movieCatalogViewModel.getMovieListLiveData().observe(getViewLifecycleOwner(), movies -> {
            if (!movieCatalogViewModel.isSearchState()) {
                categoryList.clear();
                List<Category> categories = CategoryPacker.packingListToCategories(movies);
                categoryList.addAll(categories);
                moviesCategoryAdapter.setCategories(categoryList);
                moviesCategoryAdapter.notifyDataSetChanged();
            }
        });
        movieCatalogViewModel.getSearchLiveData().observe(getViewLifecycleOwner(), movies -> {
            if (movieCatalogViewModel.isSearchState()) {
                categoryList.clear();
                List<Category> categories = CategoryPacker.packingListToCategories(movies);
                categoryList.addAll(categories);
                moviesCategoryAdapter.setCategories(categoryList);
                moviesCategoryAdapter.notifyDataSetChanged();
                if (movieCatalogViewModel.getDisposableSearch() != null) {
                    movieCatalogViewModel.getDisposableSearch().dispose();
                }
            }
        });
        movieCatalogViewModel.getServiceResponseLiveData().observe(getViewLifecycleOwner(), serviceStatus -> {
            if (serviceStatus != null) {
                if (serviceStatus == ServiceStatus.SERVICE_UNAVAILABLE_503) {
                    ToastShower.showAlert(getContext(), getString(R.string.SERVICE_UNAVAILABLE_503));
                } else if (serviceStatus == ServiceStatus.BAD_REQUEST_400) {
                    ToastShower.showAlert(getContext(), getString(R.string.BAD_REQUEST_400));
                } else if (serviceStatus == ServiceStatus.SERVER_ERROR_500) {
                    ToastShower.showAlert(getContext(), getString(R.string.SERVER_ERROR_500));
                }
                movieCatalogViewModel.getServiceResponseLiveData().postValue(null);
            }
        });
    }

    /**
     * <p>
     * The method is update catalog data if {@link MovieCatalogViewModel#getMovieListLiveData()}
     * has list reload catalog, else update data
     * </p>
     */
    public void updateCatalogData() {
        if (NetworkUtil.isNetworkAvailable(requireContext())) {
            if (movieCatalogViewModel.getMovieListLiveData().getValue() == null) {
                movieCatalogViewModel.updateCatalog();
            } else {
                movieCatalogViewModel.loadCatalog();
            }
        } else {
            ToastShower.showAlert(getContext(), getString(R.string.NET_CONNECTION));
        }
    }

}
