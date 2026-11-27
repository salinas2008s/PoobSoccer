package domain;

import java.io.Serializable;

/**
 * Character that a player can choose. It has its base stats and its power.
 */
public class Character implements Serializable {

    private String name;
    private double baseSpeed;
    private double baseJump;
    private double baseForce;
    private Power power;

    /** Creates a character with its stats and its power (it can be null). */
    public Character(String name, double baseSpeed, double baseJump, double baseForce, Power power){
        this.name = name;
        this.baseSpeed = baseSpeed;
        this.baseJump = baseJump;
        this.baseForce = baseForce;
        this.power = power;
    }

    /** Returns the name of the image: 1 standing, 2 kicking. */
    public String getImage(boolean kicking){
        //La imagen 2 es la del personaje pateando
        if (kicking) {
            return name + "2";
        }
        return name + "1";
    }

    /** Returns the name of the character. */
    public String getName(){
        return name;
    }

    /** Returns the base speed. */
    public double getBaseSpeed(){
        return baseSpeed;
    }

    /** Returns the base jump. */
    public double getBaseJump(){
        return baseJump;
    }

    /** Returns the base force. */
    public double getBaseForce(){
        return baseForce;
    }

    /** Returns the power of the character, or null if it has none. */
    public Power getPower(){
        return power;
    }
}
