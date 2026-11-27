package domain;

/**
 * Player controlled by a person with the keyboard.
 */
public class HumanPlayer extends Player {

    private int numberPlayer;
    private int rightKey;
    private int leftKey;
    private int jumpKey;
    private int kickKey;
    private int powerKey;

    //Teclas que estan presionadas en este momento
    private transient boolean leftPressed;
    private transient boolean rightPressed;
    private transient boolean jumpPressed;
    private transient boolean kickPressed;
    private transient boolean powerPressed;

    /** Creates a human player with its keys. */
    public HumanPlayer(String name, Character character, double posX, int numberPlayer,
                       int leftKey, int rightKey, int jumpKey, int kickKey, int powerKey){
        super(name, character, posX);
        this.numberPlayer = numberPlayer;
        this.leftKey = leftKey;
        this.rightKey = rightKey;
        this.jumpKey = jumpKey;
        this.kickKey = kickKey;
        this.powerKey = powerKey;
    }

    /** A key was pressed. */
    @Override
    public void pressKey(int key){
        setKey(key, true);
    }

    /** A key was released. */
    @Override
    public void releaseKey(int key){
        setKey(key, false);
    }

    /** Saves if one of the keys of this player is pressed. */
    private void setKey(int key, boolean pressed){
        //Si la tecla no es de este jugador no entra a ningun if y se ignora
        if (key == leftKey) {
            leftPressed = pressed;
        }
        if (key == rightKey) {
            rightPressed = pressed;
        }
        if (key == jumpKey) {
            jumpPressed = pressed;
        }
        if (key == kickKey) {
            kickPressed = pressed;
        }
        if (key == powerKey) {
            powerPressed = pressed;
        }
    }

    /** Decides what to do with the keys that are pressed. */
    @Override
    public void decide(){
        //Si estan las dos flechas presionadas se queda quieto
        if (leftPressed && !rightPressed) {
            moveLeft();
        } else if (rightPressed && !leftPressed) {
            moveRight();
        } else {
            stop();
        }
        if (jumpPressed) {
            jump();
        }
        if (kickPressed) {
            kick();
        }
        if (powerPressed) {
            usePower();
        }
    }

    /** Returns the number of the player (1 or 2). */
    public int getNumberPlayer(){
        return numberPlayer;
    }

    /** Returns the key to move right. */
    public int getRightKey(){
        return rightKey;
    }

    /** Returns the key to move left. */
    public int getLeftKey(){
        return leftKey;
    }

    /** Returns the key to jump. */
    public int getJumpKey(){
        return jumpKey;
    }

    /** Returns the key to kick. */
    public int getKickKey(){
        return kickKey;
    }

    /** Returns the key to use the power. */
    public int getPowerKey(){
        return powerKey;
    }

    /** Tells if the power key is pressed. */
    public boolean isPowerPressed(){
        return powerPressed;
    }
}
