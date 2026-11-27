package domain;

import java.io.Serializable;

/**
 * Clock of a match by time. It counts down to zero.
 */
public class Timer implements Serializable {

    private int totalTime;        //Segundos
    private int remainingTime;    //Segundos
    private boolean inProgress;
    private int framesPerSecond;
    private int frames;

    /** Creates a clock with the given minutes. */
    public Timer(int minutes){
        totalTime = minutes * 60;
        remainingTime = totalTime;
        inProgress = true;
        framesPerSecond = 60;
        frames = 0;
    }

    /** Advances one frame of the clock. */
    public void tick(){
        //Si ya llego a cero no sigue contando
        if (!inProgress) {
            return;
        }
        //El juego va a 60 cuadros por segundo, entonces cada 60 cuadros se quita un segundo
        frames++;
        if (frames == framesPerSecond) {
            frames = 0;
            remainingTime--;
            if (remainingTime <= 0) {
                remainingTime = 0;
                inProgress = false;
            }
        }
    }

    /** Returns the remaining time as mm:ss. */
    public String getText(){
        int minutes = remainingTime / 60;
        int seconds = remainingTime % 60;
        //Se le pone un 0 adelante a los numeros de un digito, por ejemplo 02:07
        String text = "";
        if (minutes < 10) {
            text += "0";
        }
        text += minutes + ":";
        if (seconds < 10) {
            text += "0";
        }
        text += seconds;
        return text;
    }

    /** Returns the total time in seconds. */
    public int getTotalTime(){
        return totalTime;
    }

    /** Returns the remaining time in seconds. */
    public int getRemainingTime(){
        return remainingTime;
    }

    /** Tells if the clock is still running. */
    public boolean isInProgress(){
        return inProgress;
    }
}
