package vadim.volin.movie_api.service.cache.model;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;

import vadim.volin.movie_api.entity.Movie;
import vadim.volin.movie_api.entity.MovieDetails;

@Entity
public class CombinedMovie implements Serializable {

    @PrimaryKey
    @NotNull
    @SerializedName("imdbID")
    private String imdbID;
    @SerializedName("Title")
    private String title;
    @SerializedName("Year")
    private String year;
    @SerializedName("Type")
    private String type;
    @SerializedName("Poster")
    private String poster;
    @SerializedName("Genre")
    private String genre;
    @SerializedName("Country")
    private String country;
    @SerializedName("Director")
    private String director;
    @SerializedName("Actors")
    private String actors;
    @SerializedName("Language")
    private String language;
    @SerializedName("Plot")
    private String plot;

    public CombinedMovie(String title, String year, @NotNull String imdbID, String type, String poster, String genre, String country, String director, String actors, String language, String plot) {
        this.title = title;
        this.year = year;
        this.imdbID = imdbID;
        this.type = type;
        this.poster = poster;
        this.genre = genre;
        this.country = country;
        this.director = director;
        this.actors = actors;
        this.language = language;
        this.plot = plot;
    }

    public MovieDetails convertToMovieDetails() {
        return new MovieDetails(
                getTitle(),
                getYear(),
                getImdbID(),
                getType(),
                getPoster(),
                getGenre(),
                getCountry(),
                getDirector(),
                getActors(),
                getLanguage(),
                getPlot()
        );
    }

    public Movie convertToMovie() {
        Movie movie = new Movie();
        movie.setTitle(getTitle());
        movie.setYear(getYear());
        movie.setImdbID(getImdbID());
        movie.setType(getType());
        movie.setPoster(getPoster());
        movie.setActors(getActors());
        return movie;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    @NotNull
    public String getImdbID() {
        return imdbID;
    }

    public String getPoster() {
        return poster;
    }

    public String getGenre() {
        return genre;
    }


    public String getCountry() {
        return country;
    }

    public String getDirector() {
        return director;
    }

    public String getActors() {
        return actors;
    }

    public String getPlot() {
        return plot;
    }

    public String getType() {
        return type;
    }

    @NotNull
    @Override
    public String toString() {
        return "CombinedMovie {" +
                "title='" + title + '\'' +
                ", year='" + year + '\'' +
                ", imdbID='" + imdbID + '\'' +
                ", type='" + type + '\'' +
                ", poster='" + poster + '\'' +
                ", genre='" + genre + '\'' +
                ", country='" + country + '\'' +
                ", director='" + director + '\'' +
                ", actors='" + actors + '\'' +
                ", plot='" + plot + '\'' +
                "}\n";
    }

    public String getLanguage() {
        return language;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(title, year, imdbID, type, poster, genre, country, director, actors, language, plot);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CombinedMovie that = (CombinedMovie) o;
        return Objects.equals(title, that.title) &&
                Objects.equals(year, that.year) &&
                Objects.equals(imdbID, that.imdbID) &&
                Objects.equals(type, that.type) &&
                Objects.equals(poster, that.poster) &&
                Objects.equals(genre, that.genre) &&
                Objects.equals(country, that.country) &&
                Objects.equals(director, that.director) &&
                Objects.equals(actors, that.actors) &&
                Objects.equals(language, that.language) &&
                Objects.equals(plot, that.plot);
    }
}
