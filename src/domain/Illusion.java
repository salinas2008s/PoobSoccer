package domain;

/**
 * Illusion power: when it hits the rival, 7 fake balls appear.
 */
public class Illusion implements Power {

    private String name;
    private double damage;
    private int duration;
    private double shotSpeed;
    private int fakeBalls;

    /** Creates the Illusion power. */
    public Illusion(){
        name = "Illusion";
        damage = 30;
        duration = 150;   //2.5 segundos
        shotSpeed = 35;
        fakeBalls = 7;
    }

    /** Returns the name of the power. */
    @Override
    public String getName(){
        return name;
    }

    /** Returns the damage of the shot. */
    @Override
    public double getDamage(){
        return damage;
    }

    /** Returns how many frames the fake balls last. */
    @Override
    public int getDuration(){
        return duration;
    }

    /** Returns how many fake balls appear. */
    @Override
    public int getFakeBalls(){
        return fakeBalls;
    }

    /** The ball goes straight to the rival's goal. */
    @Override
    public void shoot(Ball ball, int direction){
        ball.shootStraight(direction * shotSpeed);
    }

    /** The rival is only confused, its body does not change. */
    @Override
    public void affect(Player rival, int direction){
        //No hace nada, la confusion son los balones falsos que crea el partido
    }
}
