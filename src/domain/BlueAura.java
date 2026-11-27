package domain;

/**
 * Blue Aura power: a straight and fast shot that throws the rival to its own goal.
 */
public class BlueAura implements Power {

    private String name;
    private double damage;
    private int duration;
    private double shotSpeed;
    private double pushSpeed;

    /** Creates the Blue Aura power. */
    public BlueAura(){
        name = "Blue Aura";
        damage = 40;
        duration = 90;     //1.5 segundos
        shotSpeed = 35;
        pushSpeed = 14;
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

    /** Returns how many frames the effect lasts. */
    @Override
    public int getDuration(){
        return duration;
    }

    /** This power does not make fake balls. */
    @Override
    public int getFakeBalls(){
        return 0;
    }

    /** The ball goes straight to the rival's goal. */
    @Override
    public void shoot(Ball ball, int direction){
        ball.shootStraight(direction * shotSpeed);
    }

    /** The rival is pushed to its own goal. */
    @Override
    public void affect(Player rival, int direction){
        //El tiro va hacia el arco del rival, entonces empujarlo en la misma direccion lo manda a su arco
        rival.setSpeedX(direction * pushSpeed);
    }
}
