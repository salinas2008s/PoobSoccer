package tests;

import static org.junit.Assert.*;
import org.junit.Test;
import domain.PoobSoccer;
import domain.Match;
import domain.Timer;

/**
 * Tests of the clock of the match.
 */
public class TimerTest {

    private int[] keys1 = {65, 68, 87, 83, 81};
    private int[] keys2 = {37, 39, 38, 40, 17};

    /** Advances the clock some frames. */
    private void tick(Timer timer, int frames){
        for (int i = 0; i < frames; i++) {
            timer.tick();
        }
    }

    /** Should start with the total time. */
    @Test
    public void shouldStartWithTheTotalTime(){
        Timer timer = new Timer(3);
        assertEquals(180, timer.getTotalTime());
        assertEquals(180, timer.getRemainingTime());
        assertTrue(timer.isInProgress());
        assertEquals("03:00", timer.getText());
    }

    /** Should take off one second every sixty frames. */
    @Test
    public void shouldTakeOffOneSecondEverySixtyFrames(){
        Timer timer = new Timer(1);
        tick(timer, 59);
        assertEquals(60, timer.getRemainingTime());
        tick(timer, 1);
        assertEquals(59, timer.getRemainingTime());
        assertEquals("00:59", timer.getText());
    }

    /** Should stop at zero. */
    @Test
    public void shouldStopAtZero(){
        Timer timer = new Timer(1);
        tick(timer, 60 * 60 + 500);
        assertEquals(0, timer.getRemainingTime());
        assertFalse(timer.isInProgress());
        assertEquals("00:00", timer.getText());
    }

    /** Should run the clock only in a match by time. */
    @Test
    public void shouldRunTheClockOnlyInAMatchByTime(){
        Match match = TestGame.create().createMatch("Dipper", "Rigby", "Stadium1", keys1, keys2);
        assertFalse(match.hasTimer());
        match.startTimer(2);
        assertTrue(match.hasTimer());
        for (int i = 0; i < 120; i++) {
            match.update();
        }
        assertEquals(118, match.getTimer().getRemainingTime());
    }
}
