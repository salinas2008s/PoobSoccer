package domain;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Error log for the programmers. The errors are saved in the file POOBSoccer.log.
 */
public class Log {

    public static String name = "POOBSoccer";   //Nombre del archivo, queda POOBSoccer.log

    /** Saves the error in the log file. */
    public static void record(Exception e){
        try {
            Logger logger = Logger.getLogger(name);
            //Para que el error no salga tambien en la consola
            logger.setUseParentHandlers(false);
            //true: agrega el error al final del archivo sin borrar los anteriores
            FileHandler file = new FileHandler(name + ".log", true);
            file.setFormatter(new SimpleFormatter());
            logger.addHandler(file);
            //Guarda la fecha, el mensaje y en que clase y linea paso el error
            logger.log(Level.SEVERE, e.toString(), e);
            file.close();
            logger.removeHandler(file);
        } catch (Exception oe) {
            //Si ni siquiera se puede escribir el log, se muestra en consola y se cierra
            oe.printStackTrace();
            System.exit(0);
        }
    }
}
