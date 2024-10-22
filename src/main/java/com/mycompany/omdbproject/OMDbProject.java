package com.mycompany.omdbproject;

import com.google.gson.Gson;
import java.io.*;
import java.net.*;
import java.security.PrivilegedActionException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ivan
 */
public class OMDbProject {
    private static final String CLASS_NAME = DBConnection.class.getName();
    private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);
    
    private static String API_URL = "http://www.omdbapi.com/";
    private static String API_KEY_URL = "?apikey=";
    private static String API_FILM_URL = "&s=Harry+Potter&type=movie";
    private static String API_JSON_PAGE_URL = "&page=";
    

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your API KEY: ");
        String key = scanner.nextLine();
        
        FilmWrapper filmList = getFilmPageFromJSON(1, key);
        
        FilmWrapper completeFilmList = new FilmWrapper();
        completeFilmList.setFilmList(filmList.getFilmList());
        
        int i = 2;
        while (filmList.getFilmList() != null) {
            filmList = getFilmPageFromJSON(i, key);
            if (filmList.getFilmList() != null)
                completeFilmList.getFilmList().addAll(filmList.getFilmList());
            i++;
        }
        
        System.out.println("API read. Adding films to database...");
        
        try {
            DBConnection conn = new DBConnection();
            int filmCount = 0;
            for (Film film : completeFilmList.getFilmList()) {
                if (!insertFilm(conn,film)) 
                    System.err.println("ERROR: Film "+film.getTitle()+" not uploaded to DB");    
                else
                    filmCount++;
            }
            conn.close();
            System.out.println("SUCCESS! "+filmCount+ " films added to database.");
        } catch(PrivilegedActionException e) {
            System.err.println(e);
        } catch(SQLException e) {
            System.err.println(e);
        }
    }
    
    private static boolean insertFilm (DBConnection conn, Film film) {
        
        final String insertFilm = "INSERT INTO FILMS (imbdid, title, releaseyear, poster, rating) VALUES (?, ?, ?, ?, ?)";
        Connection connection = conn.getConnection();
        try (final PreparedStatement insertStatement = connection.prepareStatement(insertFilm)) {
            
            insertStatement.setString(1, film.getImdbID());
            insertStatement.setString(2, film.getTitle());
            insertStatement.setInt(3, Integer.parseInt(film.getYear()));
            if (!film.getPoster().equals("N/A"))   
                insertStatement.setBlob(4, new BufferedInputStream(new URL(film.getPoster()).openStream()));
            else
                insertStatement.setBlob(4, (InputStream)null);
            insertStatement.setInt(5, film.getRating());
            insertStatement.executeUpdate();
            return true;
        } catch (final SQLTimeoutException ex) {
            LOGGER.log(Level.WARNING, "timeout at register {0}", ex.getMessage());
        } catch (final SQLException ex) {
            LOGGER.log(Level.WARNING, "error at register {0}", ex.getMessage());
        } catch (final IOException ex) {
            LOGGER.log(Level.WARNING, "error at getting poster {0}", ex.getMessage());
        } catch (final NumberFormatException ex) {
            LOGGER.log(Level.WARNING, "error at parsing year {0}", ex.getMessage());
        } 

        return false;
    }
    
    private static FilmWrapper getFilmPageFromJSON(int pageNumber, String key) {
        String url = API_URL+API_KEY_URL+key+API_FILM_URL+API_JSON_PAGE_URL+pageNumber;
        String jsonRes = checkAPIKEY(url);
        if (jsonRes == null) 
            System.exit(0);
        
        Gson gson = new Gson();
        FilmWrapper filmList = gson.fromJson(jsonRes, FilmWrapper.class);
        if (filmList.getFilmList() != null) {
            for (Film film : filmList.getFilmList()) {
                film.setRating(new Random().nextInt(1,6));
            }
        }
        
        return filmList;
    }
    
    private static String checkAPIKEY(String url){
        URLConnection urlc = null;
        try {
            urlc = new URL(url).openConnection();
        }catch(IOException e) {
            System.err.println("ERROR: API KEY not valid");
            return null;
        } 
        
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
            
            String l = null;
            String res = "";
            while((l=br.readLine()) != null) 
                res += l;
           
            br.close();
            return res;
        } catch(IOException e) {
            System.err.println("ERROR: No valid response from API");
            return null;
        } 
    }
    
}
