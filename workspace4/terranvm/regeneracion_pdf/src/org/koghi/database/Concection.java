package org.koghi.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

import org.koghi.properties.Configuration;

public class Concection {

    private static Concection INSTANCE = new Concection();
    static private Connection conn;
    private static String className = Concection.class.getName();
    
    private Concection(){
        String server = Configuration.getInstance().getProperty(Configuration.DATABASE_SERVER);
        String dataBase = Configuration.getInstance().getProperty(Configuration.DATABASE_CATALOG);
        String user = Configuration.getInstance().getProperty(Configuration.DATABASE_USER);
        String password = Configuration.getInstance().getProperty(Configuration.DATABASE_PSWD);
        String driver = Configuration.getInstance().getProperty(Configuration.DRIVER_CONNECTION);
        String url = "jdbc:postgresql://" + server + "/" +dataBase;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, password); 
            } catch (Exception e) {
            Configuration.log(className, Level.SEVERE, "Error al crear la concexión, causado por: ");
            e.printStackTrace();
        }
    }
    
    public static Concection getInstance(){
        
        return INSTANCE;
        
    }
    
    public static Connection getConction(){
        return conn;
    }

   
    public static void cerrarConcection(){
        try {
            conn.close();
            
        } catch (SQLException e) {
            Configuration.log(className, Level.SEVERE, "Error al cerrar la concexión");
            e.printStackTrace();
        }
    }
}
