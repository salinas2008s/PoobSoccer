package domain;

import java.io.Serializable;

/**
 * Points and upgrades of one player: Speed, Jump, Kick and Survival, from 1 to 10.
 */
public class Stats implements Serializable {

    private int points;
    private int speed;
    private int jump;
    private int kick;
    private int survival;
    private int maxLevel;
    private int firstPrice;

    /** Creates the stats of a new player, everything in level 1 and without points. */
    public Stats(){
        points = 0;
        speed = 1;
        jump = 1;
        kick = 1;
        survival = 1;
        maxLevel = 10;
        firstPrice = 500;
    }

    /** Adds points to the player. */
    public void addPoints(int amount){
        points = points + amount;
    }

    /** Returns the level of a stat: "Speed", "Jump", "Kick" or "Survival". */
    public int getLevel(String stat){
        if ("Speed".equals(stat)) {
            return speed;
        }
        if ("Jump".equals(stat)) {
            return jump;
        }
        if ("Kick".equals(stat)) {
            return kick;
        }
        return survival;
    }

    /** Returns how much the next level of a stat costs. */
    public int getPrice(String stat){
        //El primer punto cuesta 500 y cada uno siguiente cuesta el doble
        int price = firstPrice;
        for (int i = 1; i < getLevel(stat); i++) {
            price = price * 2;
        }
        return price;
    }

    /** Buys one level of a stat. */
    public void upgrade(String stat) throws PoobSoccerException {
        if (getLevel(stat) >= maxLevel) {
            throw new PoobSoccerException(PoobSoccerException.MAX_LEVEL);
        }
        int price = getPrice(stat);
        if (points < price) {
            throw new PoobSoccerException(PoobSoccerException.NOT_ENOUGH_POINTS);
        }
        //Se cobran los puntos y se sube un nivel
        points = points - price;
        if ("Speed".equals(stat)) {
            speed++;
        }
        if ("Jump".equals(stat)) {
            jump++;
        }
        if ("Kick".equals(stat)) {
            kick++;
        }
        if ("Survival".equals(stat)) {
            survival++;
        }
    }

    /** Returns the points of the player. */
    public int getPoints(){
        return points;
    }

    /** Returns the max level of the stats. */
    public int getMaxLevel(){
        return maxLevel;
    }
}
