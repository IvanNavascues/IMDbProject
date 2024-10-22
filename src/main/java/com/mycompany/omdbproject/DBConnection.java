package com.mycompany.omdbproject;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author ivan
 */
final class DBConnection {
    
    private static final String CLASS_NAME = DBConnection.class.getName();
    private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

    // Conexión a la BBDD.
    private Connection connection;
    DBConnection () throws SQLException, PrivilegedActionException {
        DBConnection();
    }

    Connection getConnection () {
        return connection;
    }

    void close () throws SQLException, PrivilegedActionException {
        try {
            AccessController.doPrivileged (
                new PrivilegedExceptionAction<Void>() {
                    public Void run () {
                        try {
                            _close();
                        } catch (SQLException ex) {
                            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        return null; 
                    }
                }
            );
        } catch (final AccessControlException ex) {
            LOGGER.warning(ex.getMessage());
            throw ex;
        }catch (final PrivilegedActionException ex) {
            LOGGER.warning(ex.getMessage());
            System.err.println("ERROR: Failed closing connecton to DB");
            throw ex;
        }
      }

      public void DBConnection() throws PrivilegedActionException {
         try {
            AccessController.doPrivileged (
                new PrivilegedExceptionAction<Void>() {
                    public Void run () {
                        try {
                            _DBConnection();
                        } catch (SQLException ex) {
                            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        return null; 
                    }
                }
            );
        } catch (final AccessControlException ex) {
            LOGGER.warning(ex.getMessage());
            throw ex;
        }catch (final PrivilegedActionException ex) {
            LOGGER.warning(ex.getMessage());
            System.err.println("ERROR: Failed connecton to DB");
            throw ex;
        }
    }

    private void _DBConnection() throws SQLException, FileNotFoundException {
        try {
            String fileName = System.getProperty("user.dir")
                    + File.separator + "data"
                    + File.separator + "login";

            File file = new File(fileName);
            String db_user = getUser(file);
            String db_password = getPassword(file);

            final String db_URL = "jdbc:h2:file:" + System.getProperty("user.dir")
                    + File.separator + "data"
                    + File.separator + "database";

            connection = DriverManager.getConnection(db_URL, db_user, db_password);

        } catch (final SQLTimeoutException ex) {
            LOGGER.log(Level.WARNING, "conection tiemout");
            LOGGER.log(Level.WARNING, ex.getMessage());
            throw new SQLTimeoutException();
        } catch (final SQLException ex) {
            LOGGER.log(Level.WARNING, "couldnt open connection");
            ex.printStackTrace();
            LOGGER.log(Level.WARNING, ex.getMessage());
            throw new SQLException();
        } catch (final FileNotFoundException ex) {
            LOGGER.log(Level.WARNING, "couldnt open login file");
            LOGGER.log(Level.WARNING, ex.getMessage());
            throw new FileNotFoundException();
        }
    }

    private String getUser(File file) throws FileNotFoundException {
        Scanner scan = new Scanner(file);
        return scan.nextLine().trim();
    }

    private String getPassword(File file) throws FileNotFoundException {
        Scanner scan = new Scanner(file);
        scan.nextLine();
        return scan.nextLine().trim();
    }

    private void _close() throws SQLException {
        try {
            connection.close();
        } catch (final SQLException ex) {
            LOGGER.log(Level.WARNING, "couldnt close connection");
            LOGGER.log(Level.WARNING, ex.getMessage());
            throw new SQLException();
        }
    }

}
