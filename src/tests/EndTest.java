package tests;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import domain.PoobSoccer;
import domain.Match;

/**
 * Tests of the end of the match.
 */
public class EndTest {

    private int[] keys1 = {65, 68, 87, 83, 81};
    private int[] keys2 = {37, 39, 38, 40, 17};

    private Match match;

    /** Creates a new match before each test. */
    @Before
    public void setUp(){
        match = TestGame.create().createMatch("Dipper", "Rigby", "Stadium1", keys1, keys2);
    }

    /** Scores a goal for player 1. */
    private void goalPlayer1(){
        match.getBall().moveTo(1883, match.getBall().getRadius());
        match.update();
    }

    /** Scores a goal for player 2. */
    private void goalPlayer2(){
        match.getBall().moveTo(37, match.getBall().getRadius());
        match.update();
    }

    /** Plays some frames of the match. */
    private void play(int frames){
        for (int i = 0; i < frames; i++) {
            match.update();
        }
    }

    /** By goals should not end before the goals. */
    @Test
    public void byGoalsShouldNotEndBeforeTheGoals(){
        match.setGoalsToWin(5);
        for (int i = 0; i < 4; i++) {
            goalPlayer1();
        }
        assertFalse(match.isFinished());
    }

    /** By goals should end when someone gets the goals. */
    @Test
    public void byGoalsShouldEndWhenSomeoneGetsTheGoals(){
        match.setGoalsToWin(5);
        for (int i = 0; i < 5; i++) {
            goalPlayer2();
        }
        assertTrue(match.isFinished());
        assertSame(match.getPlayer2(), match.getWinner());
    }

    /** Nothing should change after the end. */
    @Test
    public void nothingShouldChangeAfterTheEnd(){
        match.setGoalsToWin(5);
        for (int i = 0; i < 5; i++) {
            goalPlayer1();
        }
        goalPlayer1();
        assertEquals(5, match.getScore().getGoalsPlayer1());
    }

    /** By time should not end before the time. */
    @Test
    public void byTimeShouldNotEndBeforeTheTime(){
        match.startTimer(1);
        goalPlayer1();
        play(60 * 30);
        assertFalse(match.isFinished());
    }

    /** By time should end when the time is over. */
    @Test
    public void byTimeShouldEndWhenTheTimeIsOver(){
        match.startTimer(1);
        goalPlayer1();
        play(60 * 60);
        assertTrue(match.isFinished());
        assertSame(match.getPlayer1(), match.getWinner());
    }

    /** A tie when the time is over should be sudden death. */
    @Test
    public void aTieWhenTheTimeIsOverShouldBeSuddenDeath(){
        match.startTimer(1);
        play(60 * 60);
        assertFalse(match.isFinished());
        assertTrue(match.isSuddenDeath());
        assertNull(match.getWinner());
    }

    /** In sudden death the next goal should win. */
    @Test
    public void inSuddenDeathTheNextGoalShouldWin(){
        match.startTimer(1);
        play(60 * 60);
        goalPlayer2();
        assertTrue(match.isFinished());
        assertFalse(match.isSuddenDeath());
        assertSame(match.getPlayer2(), match.getWinner());
    }

    /** There should be no sudden death in a match by goals. */
    @Test
    public void thereShouldBeNoSuddenDeathInAMatchByGoals(){
        match.setGoalsToWin(5);
        play(60 * 60 * 2);
        assertFalse(match.isSuddenDeath());
        assertFalse(match.isFinished());
    }
}
