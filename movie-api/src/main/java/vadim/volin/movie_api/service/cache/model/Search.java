package vadim.volin.movie_api.service.cache.model;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

public class Search {

    @SerializedName("Search")
    private LinkedList<RetrofitMovie> movies;

    public Search(LinkedList<RetrofitMovie> movies) {
        this.movies = movies;
    }

    public List<RetrofitMovie> getMovies() {
        return movies;
    }

}
