package domain;

import java.io.Serializable;

/**
 * Goals of each player in a match.
 */
public class Score implements Serializable {

    private int goalsPlayer1;
    private int goalsPlayer2;

    /** Creates a score 0 - 0. */
    public Score(){
        goalsPlayer1 = 0;
        goalsPlayer2 = 0;
    }

    /** Adds one goal to player 1. */
    public void addGoalPlayer1(){
        goalsPlayer1++;
    }

    /** Adds one goal to player 2. */
    public void addGoalPlayer2(){
        goalsPlayer2++;
    }

    /** Returns the goals of player 1. */
    public int getGoalsPlayer1(){
        return goalsPlayer1;
    }

    /** Returns the goals of player 2. */
    public int getGoalsPlayer2(){
        return goalsPlayer2;
    }
}
