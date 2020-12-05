package vadim.volin.util;

import java.util.LinkedList;
import java.util.List;

import vadim.volin.entity.Category;
import vadim.volin.movie_api.entity.Movie;

/**
 * Can make it easier to pack list of Movie to Category with type and list of movies with that type.
 *
 * @author Vadym Volin
 */
public class CategoryPacker {

    /**
     * <p>
     * The method helps to pack list of Movie to Category using ordering by movie type
     * </p>
     *
     * @param movieList {@link List} list of {@link Movie} object
     * @return {@link List} list of {@link Category} object
     */
    public static List<Category> packingListToCategories(List<Movie> movieList) {
        Movie movie;
        List<Category> categories = new LinkedList<>();

        for (int i = 0; i < movieList.size(); i++) {
            movie = movieList.get(i);
            boolean flagAdding = false;
            if (movie != null) {
                if (!categories.isEmpty()) {
                    for (int j = 0; j < categories.size(); j++) {
                        if (!categories.get(j).getMovieList().isEmpty()
                                && categories.get(j).getType().equals(movie.getType())) {
                            categories.get(j).getMovieList().add(movie);
                            flagAdding = true;
                        }
                    }
                    if (!flagAdding) {
                        packMovieToCategories(movie, categories);
                    }
                } else {
                    packMovieToCategories(movie, categories);
                }
            }
        }
        return categories;
    }

    private static void packMovieToCategories(Movie movie, List<Category> categories) {
        List<Movie> movies = new LinkedList<>();
        movies.add(movie);
        Category category = new Category(movie.getType(), movies);
        categories.add(category);
    }

}
