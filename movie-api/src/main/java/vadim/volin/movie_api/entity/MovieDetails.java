package vadim.volin.movie_api.entity;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class MovieDetails implements Serializable {

    private String title;
    private String year;
    private String imdbID;
    private String type;
    private String poster;
    private String genre;
    private String country;
    private String director;
    private String actors;
    private String language;
    private String plot;

    public MovieDetails(String title, String year, String imdbID, String type, String poster, String genre, String country, String director, String actors, String language, String plot) {
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

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

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

    @NotNull
    @Override
    public String toString() {
        return "MovieDetails{" +
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
                '}';
    }

    public String getLanguage() {
        return language;
    }

}
