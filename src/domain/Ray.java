package domain;

/**
 * Ray power: a straight shot that electrocutes the rival.
 */
public class Ray implements Power {

    private String name;
    private double damage;
    private int duration;
    private double shotSpeed;

    /** Creates the Ray power. */
    public Ray(){
        name = "Ray";
        damage = 60;
        duration = 180;  //3 segundos
        shotSpeed = 35;
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

    /** The rival moves at half speed and cannot jump. */
    @Override
    public void affect(Player rival, int direction){
        //Electrocutado: la mitad de la velocidad y sin saltar
        rival.setSpeedX(rival.getSpeedX() / 2);
        rival.cancelJump();
    }
}
