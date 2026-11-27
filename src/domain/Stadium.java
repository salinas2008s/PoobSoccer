package domain;

import java.io.Serializable;

/**
 * Stadium where the match is played. Each stadium has its own gravity.
 */
public class Stadium implements Serializable {

    private String name;
    private double width;
    private double height;
    private double gravity;

    /** Creates a stadium with its size and gravity. */
    public Stadium(String name, double width, double height, double gravity){
        this.name = name;
        this.width = width;
        this.height = height;
        this.gravity = gravity;
    }

    /** Creates one of the three stadiums of the game by its name. */
    public static Stadium create(String name){
        //Todos los estadios miden lo mismo, solo cambia la gravedad
        double width = 1920;
        double height = 1080;
        if ("Stadium1".equals(name)) {
            return new Stadium(name, width, height, 0.8);
        } else if ("Stadium2".equals(name)) {
            //Gravedad baja: se salta mas alto
            return new Stadium(name, width, height, 0.5);
        } else {
            //Gravedad alta: se salta mas bajo
            return new Stadium(name, width, height, 1.1);
        }
    }

    /** Returns the gravity of the stadium. */
    public double getGravity(){
        return gravity;
    }

    /** Returns the width of the field. */
    public double getWidth(){
        return width;
    }

    /** Returns the height of the field. */
    public double getHeight(){
        return height;
    }

    /** Returns the name of the stadium. */
    public String getName(){
        return name;
    }
}
