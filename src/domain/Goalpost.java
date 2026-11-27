package domain;

import java.io.Serializable;

/**
 * A goal of the field. side is 'L' for the left goal and 'R' for the right goal.
 */
public class Goalpost implements Serializable {

    private double posX;
    private double posY;
    private double width;
    private double height;
    private char side;

    /** Creates a goal on the left or on the right of the field, with its size. */
    public Goalpost(char side, double fieldWidth, double width, double height){
        this.side = side;
        this.width = width;
        this.height = height;
        this.posY = 0;
        //El arco derecho va pegado al final de la cancha
        if (side == 'L') {
            this.posX = 0;
        } else {
            this.posX = fieldWidth - width;
        }
    }

    /** Tells if the whole ball is inside the goal. */
    public boolean hasBall(Ball ball){
        //Solo es gol si el balon entra completo, no a medias
        double r = ball.getRadius();
        boolean insideX = ball.getPosX() - r >= posX && ball.getPosX() + r <= posX + width;
        boolean insideY = ball.getPosY() + r <= posY + height;
        return insideX && insideY;
    }

    /** Makes the ball bounce on the top bar of the goal. */
    public void bounceOnTop(Ball ball){
        bounceOnCorner(ball);
        double r = ball.getRadius();
        double top = posY + height;
        //Revisa si alguna parte del balon esta encima del arco, no solo el centro
        boolean overTheGoal = ball.getPosX() + r > posX && ball.getPosX() - r < posX + width;
        if (!overTheGoal) {
            return;
        }
        //Si cae encima del travesano rebota hacia arriba, si lo toca desde adentro rebota hacia abajo
        boolean fromAbove = ball.getPosY() > top && ball.getPosY() - r < top && ball.getSpeedY() < 0;
        boolean fromBelow = ball.getPosY() < top && ball.getPosY() + r > top && ball.getSpeedY() > 0;
        if (fromAbove) {
            ball.moveTo(ball.getPosX(), top + r);
            ball.push(ball.getSpeedX(), -ball.getSpeedY() * ball.getBounce());
        }
        if (fromBelow) {
            ball.moveTo(ball.getPosX(), top - r);
            ball.push(ball.getSpeedX(), -ball.getSpeedY() * ball.getBounce());
        }
    }

    /** Makes the ball bounce on the front corner of the top bar. */
    private void bounceOnCorner(Ball ball){
        //La esquina que mira a la cancha: en el arco izquierdo es la de la derecha y al reves
        double cornerX = posX;
        if (side == 'L') {
            cornerX = posX + width;
        }
        double cornerY = posY + height;

        //Distancia del centro del balon a la esquina (Pitagoras)
        double dx = ball.getPosX() - cornerX;
        double dy = ball.getPosY() - cornerY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        //Si la distancia es mayor que el radio, el balon no esta tocando la esquina
        if (distance >= ball.getRadius() || distance == 0) {
            return;
        }

        //Direccion de la esquina hacia el balon, con tamano 1
        double directionX = dx / distance;
        double directionY = dy / distance;

        //Saca el balon de la esquina y lo manda en esa direccion un poco mas lento
        ball.moveTo(cornerX + directionX * ball.getRadius(), cornerY + directionY * ball.getRadius());
        double speed = Math.sqrt(ball.getSpeedX() * ball.getSpeedX() + ball.getSpeedY() * ball.getSpeedY());
        speed = speed * ball.getBounce();
        ball.push(directionX * speed, directionY * speed);
    }

    /** Returns the x of the left side of the goal. */
    public double getPosX(){
        return posX;
    }

    /** Returns the y of the bottom of the goal. */
    public double getPosY(){
        return posY;
    }

    /** Returns the width of the goal. */
    public double getWidth(){
        return width;
    }

    /** Returns the height of the goal. */
    public double getHeight(){
        return height;
    }

    /** Returns 'L' or 'R'. */
    public char getSide(){
        return side;
    }
}
