package com.mycompany.omdbproject;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author ivane
 */
public class Film {
    
    private String imdbID;
    @SerializedName("Title")
    private String title;
    @SerializedName("Year")
    private String year;
    @SerializedName("Poster")
    private String poster;
    private Integer rating;

    public Film(String imdbID, String title, String year, String poster) {
        this.imdbID = imdbID;
        this.title = title;
        this.year = year;
        this.poster = poster;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getImdbID() {
        return imdbID;
    }

    public String getPoster() {
        return poster;
    }

    public Integer getRating() {
        return rating;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }
    
    
    
    @Override
    public String toString() {
        return "{Title="+this.title+
                  ", Year="+this.year+
                  ", imdbID="+this.imdbID+
                  ", Poster=" +this.poster+
                  ", Rating=" +this.rating+
                "}\n";
    }
}
