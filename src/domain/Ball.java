package domain;

import java.io.Serializable;

/**
 * The ball of the match. posX and posY are the center of the ball.
 */
public class Ball implements Serializable {

    private double posX;
    private double posY;         //0 es el suelo y hacia arriba es positivo
    private double speedX;
    private double speedY;
    private double radius;
    private double bounce;       //Parte de la velocidad que conserva tras un rebote
    private double minSpeed;     //Con menos velocidad que esta deja de rebotar
    private boolean straight;    //Es true durante un tiro de poder recto, ahi el balon no cae
    private double kickSpeedX;   //Velocidad que sale el balon con una patada
    private double kickSpeedY;
    private double kickReach;    //Hasta donde llega el pie delante del jugador
    private double headBounce;   //Rebote minimo en la cabeza
    private double bodyPush;     //Empujon minimo con el cuerpo

    /** Creates a still ball at the given position. */
    public Ball(double posX, double posY){
        this.posX = posX;
        this.posY = posY;
        this.speedX = 0;
        this.speedY = 0;
        this.radius = 35;
        this.bounce = 0.75;
        this.minSpeed = 1.5;
        this.straight = false;
        this.kickSpeedX = 22;
        this.kickSpeedY = 14;
        this.kickReach = 60;
        this.headBounce = 8;
        this.bodyPush = 3;
    }

    /** Tells if the ball is touching the player. */
    public boolean touches(Player player){
        //El jugador es un rectangulo del tamano de su imagen, se mira si el balon se cruza con el
        double half = player.getWidth() / 2;
        boolean touchX = posX + radius > player.getPosX() - half && posX - radius < player.getPosX() + half;
        boolean touchY = posY + radius > player.getPosY() && posY - radius < player.getPosY() + player.getHeight();
        return touchX && touchY;
    }

    /** Tells if the ball is near the foot of the player. */
    public boolean isNearFoot(Player player, int direction){
        //distance es positiva si el balon esta adelante del jugador
        double distance = (posX - player.getPosX()) * direction;
        boolean inFront = distance > 0 && distance - radius < player.getWidth() / 2 + kickReach;
        //Tiene que estar en la mitad de abajo del jugador
        boolean low = posY - radius < player.getPosY() + player.getHeight() / 2;
        return inFront && low;
    }

    /** Normal kick to the rival's goal, force is the kick multiplier of the player. */
    public void kick(int direction, double force){
        //Con mas fuerza el balon sale mas rapido hacia adelante
        push(direction * kickSpeedX * force, kickSpeedY);
    }

    /** Makes the ball bounce on the head or on the body of the player. */
    public void bounceOnPlayer(Player player){
        double top = player.getPosY() + player.getHeight();
        if (posY > top - radius / 2) {
            //Cabeza: rebota hacia arriba, y si el jugador va saltando sale mas fuerte
            posY = top + radius;
            double up = Math.max(Math.abs(speedY) * bounce, headBounce) + Math.max(0, player.getSpeedY());
            push(speedX + (posX - player.getPosX()) * 0.1, up);
        } else {
            //Cuerpo: se saca el balon hacia el lado donde esta y se empuja con la velocidad del jugador
            int side = 1;
            if (posX < player.getPosX()) {
                side = -1;
            }
            posX = player.getPosX() + side * (player.getWidth() / 2 + radius);
            double speed = Math.max(Math.abs(speedX) * bounce, Math.abs(player.getSpeedX()) + bodyPush);
            push(side * speed, speedY);
        }
    }

    /** Moves the ball one frame. */
    public void move(double gravity, double fieldWidth, double fieldHeight){
        //En el tiro recto no se aplica la gravedad
        if (!straight) {
            speedY = speedY - gravity;
        }
        posX = posX + speedX;
        posY = posY + speedY;

        bounceOnGround();
        bounceOnCeiling(fieldHeight);
        bounceOnSides(fieldWidth);
    }

    /** Bounces on the ground. */
    private void bounceOnGround(){
        if (posY - radius <= 0) {
            posY = radius;
            //Si venia cayendo de verdad, se acaba el tiro recto
            if (speedY < 0) {
                straight = false;
            }
            speedY = -speedY * bounce;
            //Si ya rebota muy poquito se queda en el suelo
            if (speedY < minSpeed) {
                speedY = 0;
            }
        }
    }

    /** Bounces on the top of the field. */
    private void bounceOnCeiling(double fieldHeight){
        if (posY + radius > fieldHeight) {
            posY = fieldHeight - radius;
            speedY = -speedY * bounce;
            straight = false;
        }
    }

    /** Bounces on the left and right sides of the field. */
    private void bounceOnSides(double fieldWidth){
        if (posX - radius < 0) {
            posX = radius;
            speedX = -speedX * bounce;
            straight = false;
        }
        if (posX + radius > fieldWidth) {
            posX = fieldWidth - radius;
            speedX = -speedX * bounce;
            straight = false;
        }
    }

    /** Tells if the ball is still on the ground. */
    public boolean isStopped(){
        return speedX == 0 && speedY == 0 && posY == radius;
    }

    /** Puts the ball in another place. */
    public void moveTo(double posX, double posY){
        this.posX = posX;
        this.posY = posY;
    }

    /** Changes the speed of the ball. */
    public void push(double speedX, double speedY){
        this.speedX = speedX;
        this.speedY = speedY;
        //Despues de un golpe normal el balon vuelve a caer
        this.straight = false;
    }

    /** Power shot: the ball goes straight without falling. */
    public void shootStraight(double speedX){
        this.speedX = speedX;
        this.speedY = 0;
        this.straight = true;
    }

    /** Tells if the ball is in a straight power shot. */
    public boolean isStraight(){
        return straight;
    }

    /** Returns the x of the center. */
    public double getPosX(){
        return posX;
    }

    /** Returns the y of the center. */
    public double getPosY(){
        return posY;
    }

    /** Returns the horizontal speed. */
    public double getSpeedX(){
        return speedX;
    }

    /** Returns the vertical speed. */
    public double getSpeedY(){
        return speedY;
    }

    /** Returns the radius. */
    public double getRadius(){
        return radius;
    }

    /** Returns the part of the speed kept after a bounce. */
    public double getBounce(){
        return bounce;
    }
}
