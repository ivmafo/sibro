package org.koghi.properties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Configuration {

    private Properties properties;

    /** Nombre Del Archivo De Configuración */
    public static final String CONFIG_FILE_NAME = "Configuracion.properties";
    /** Nombre Del Servidor De La Base De Datos */
    public static final String DATABASE_SERVER = "dataBaseServer";
    /** Nombre De la Base de Datos */
    public static final String DATABASE_CATALOG = "dataBaseCatalog";
    /** Usuario De La Base De Datos */
    public static final String DATABASE_USER = "dataBaseUser";
    /** Password De La Base De Datos */
    public static final String DATABASE_PSWD = "dataBasePassword";
    /** Ruta Donde Seran Almacenados Los PDF */
    public static final String PDF_PATH = "pdfPath";
    /** Indica la ruta donde se encuentra el jboss */
    public static final String SERVER_PATH = "serverPath";
    /** Indica el tipo de Driver que se utilizara */
    public static final String DRIVER_CONNECTION = "driver";

    private Configuration() {
        this.properties = new Properties();
        try {
            log(getClass().getName(), Level.INFO, "Se realiza lectura de archivo properties.properties");
            properties.load(this.getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_NAME));
        } catch (FileNotFoundException e) {
            log(getClass().getName(), Level.SEVERE, "Archivo de Configuración no encontrado");

        } catch (IOException e) {
            log(getClass().getName(), Level.SEVERE, "Ocurrio un Error, causado por: ");
            e.printStackTrace();
        }
    }
    
    /**
     * Implementando Singleton
     *
     * @return
     */
    public static Configuration getInstance() {
        return INSTANCE;
    }
 
 
    private static final Configuration INSTANCE = new Configuration();
   
 
    
    /**
     * Retorna la propiedad de configuración solicitada
     *
     * @param key
     * @return
     */
    public String getProperty(String key) {
        return this.properties.getProperty(key);
    }
    

    /**
     * Metodo que Imprime los Logs de la Aplicación, recibe por parametro, el
     * nombre de la clase que lo esta invocando, el nivel o severidad, y el
     * mensage a imprimir.
     * 
     * @param className
     * @param level
     * @param message
     */
    public static void log(String className, Level level, String message) {
        Logger.getLogger(className).log(level, message);
    }

}
