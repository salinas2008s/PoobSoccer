package domain;

/**
 * Machine that always goes after the ball to score.
 */
public class Offensive extends AIPlayer {

    /** Creates the offensive machine. */
    public Offensive(String name, Character character, double posX){
        super(name, character, posX);
    }

    /** Goes after the ball and kicks it to the rival goal. */
    @Override
    protected void play(){
        attack();
    }
}
