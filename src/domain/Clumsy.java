package domain;

import java.util.Random;

/**
 * Machine that chooses its movements at random.
 */
public class Clumsy extends AIPlayer {

    private Random random;
    private int action;        //0 izquierda, 1 derecha, 2 quieto, 3 saltar, 4 patear
    private int framesLeft;    //Cuadros que faltan para escoger otra accion

    /** Creates the clumsy machine. */
    public Clumsy(String name, Character character, double posX){
        super(name, character, posX);
        random = new Random();
        framesLeft = 0;
    }

    /** Chooses a random action and keeps it for some frames. */
    @Override
    protected void play(){
        if (framesLeft <= 0) {
            action = random.nextInt(5);
            framesLeft = 10 + random.nextInt(30);
        }
        framesLeft--;
        stop();
        if (action == 0) {
            moveLeft();
        }
        if (action == 1) {
            moveRight();
        }
        if (action == 3) {
            jump();
        }
        if (action == 4) {
            kick();
        }
    }
}
