package vadim.volin.entity;

import java.util.LinkedList;
import java.util.List;

import vadim.volin.movie_api.entity.Movie;

/**
 * Category is base entity for catalog of movie with different types
 *
 * @author Vadym Volin
 */
public class Category {

    /**
     * type of {@link Movie} movie object
     */
    private String type;
    /**
     * {@link List} list of movie object with current type {@link Category#type}
     */
    private List<Movie> movieList = new LinkedList<>();

    /**
     * <p>
     * Base constructor for creating a category using type and {@link List} list of {@link Movie} Movie
     * </p>
     *
     * @param type      {@link String} type of {@link Movie} object
     * @param movieList {@link List} list of {@link Movie} object
     */
    public Category(String type, List<Movie> movieList) {
        this.type = type;
        this.movieList.addAll(movieList);
    }


    /**
     * <p>
     * The method return type of category
     * </p>
     *
     * @return {@link String} type of Category {@link Category#type}
     */
    public String getType() {
        return type;
    }


    /**
     * <p>
     * The method return a category list of {@link Movie} movie
     * </p>
     *
     * @return {@link List} list of {@link Category} object
     */
    public List<Movie> getMovieList() {
        return movieList;
    }
}
