package domain;

/**
 * Machine that stays near its goal and only goes out when the ball comes close.
 */
public class Defensive extends AIPlayer {

    /** Creates the defensive machine. */
    public Defensive(String name, Character character, double posX){
        super(name, character, posX);
    }

    /** Waits near the goal, and when the ball is in its half it goes and clears it. */
    @Override
    protected void play(){
        defend();
    }
}
