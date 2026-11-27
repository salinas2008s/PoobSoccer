package domain;

/**
 * Player controlled by the machine. It plays on the right side and each subclass is a way of playing.
 */
public abstract class AIPlayer extends Player {

    private Match match;
    private int direction;         //Hacia donde ataca: -1 es a la izquierda
    private double homeDistance;   //Que tan lejos de su arco se queda esperando cuando defiende

    /** Creates a machine player. */
    public AIPlayer(String name, Character character, double posX){
        super(name, character, posX);
        this.direction = -1;
        this.homeDistance = 250;
    }

    /** Gets the match, so the machine can look at the ball and the rival. */
    @Override
    public void watch(Match match){
        this.match = match;
    }

    /** Decides what to do in this frame. */
    @Override
    public void decide(){
        //Si todavia no esta en un partido se queda quieta
        if (match == null) {
            stop();
            return;
        }
        play();
    }

    /** Each way of playing decides here what the machine does in this frame. */
    protected abstract void play();

    /** Returns the match that the machine is playing. */
    protected Match getMatch(){
        return match;
    }

    /** Returns where the machine attacks: -1 to the left. */
    public int getDirection(){
        return direction;
    }

    /** Moves the machine to the position x and stops when it is close. */
    protected void goTo(double x){
        //Con un margen para que no se mueva de un lado a otro cuando ya llego
        if (getPosX() < x - 15) {
            moveRight();
        } else if (getPosX() > x + 15) {
            moveLeft();
        } else {
            stop();
        }
    }

    /** Goes after the ball and kicks it to the rival goal. */
    protected void attack(){
        Ball ball = match.getBall();
        //Distancia horizontal al balon, es positiva si el balon esta detras de la maquina
        double distance = ball.getPosX() - getPosX();
        if (distance > 0) {
            //El balon esta entre la maquina y su arco, entonces lo rodea saltando para quedar detras de el
            moveRight();
            if (distance < 200) {
                jump();
            }
        } else {
            moveLeft();
            //Si el balon viene por el aire salta para pegarle con la cabeza
            if (ball.getPosY() > getHeight() && distance > -200) {
                jump();
            }
        }
        //Patea cuando el balon esta al alcance del pie
        if (ball.isNearFoot(this, direction)) {
            kick();
        }
        //Si tiene la barra llena usa el poder
        usePower();
    }

    /** Waits near the goal, and when the ball is in its half it goes and clears it. */
    protected void defend(){
        Ball ball = match.getBall();
        double middle = match.getStadium().getWidth() / 2;
        if (ball.getPosX() > middle) {
            //El balon esta en su mitad de la cancha, lo despeja
            attack();
        } else {
            //Se queda delante de su arco esperando
            goTo(match.getRightGoal().getPosX() - homeDistance);
        }
    }
}
