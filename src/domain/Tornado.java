package domain;

/**
 * Tornado power: a shot along the ground that throws the rival into the air.
 */
public class Tornado implements Power {

    private String name;
    private double damage;
    private int duration;
    private double shotSpeed;
    private double throwSpeed;

    /** Creates the Tornado power. */
    public Tornado(){
        name = "Tornado";
        damage = 40;
        duration = 180;  //3 segundos
        shotSpeed = 30;
        throwSpeed = 18;
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

    /** The ball goes along the ground to the rival's goal. */
    @Override
    public void shoot(Ball ball, int direction){
        //Se baja el balon al suelo antes de disparar
        ball.moveTo(ball.getPosX(), ball.getRadius());
        ball.shootStraight(direction * shotSpeed);
    }

    /** The rival is thrown up and cannot move, jump or kick. */
    @Override
    public void affect(Player rival, int direction){
        //En el primer cuadro del efecto lo lanza al aire, en los demas no lo deja saltar
        if (rival.getEffectFrames() == duration) {
            rival.throwUp(throwSpeed);
        } else {
            rival.cancelJump();
        }
        //Inconsciente: no se mueve ni patea
        rival.setSpeedX(0);
        rival.cancelKick();
    }
}
