package com.mycompany.omdbproject;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 *
 * @author ivane
 */
public class FilmWrapper {
    @SerializedName("Search")
    List<Film> filmList;

    public List<Film> getFilmList() {
        return filmList;
    }

    public void setFilmList(List<Film> filmList) {
        this.filmList = filmList;
    }

    @Override
    public String toString() {
        return "Result: " +
                this.filmList;
    }
}
