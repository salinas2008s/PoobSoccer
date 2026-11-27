package tests;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import domain.PoobSoccer;
import domain.Match;
import domain.Player;

/**
 * Tests of the stun when the life gets to 0.
 */
public class StunTest {

    private int[] keys1 = {65, 68, 87, 83, 81};
    private int[] keys2 = {37, 39, 38, 40, 17};  //Arrows, Ctrl
    private int left2 = 37, jump2 = 38, kick2 = 40;

    private Match match;
    private Player rigby;

    /** Creates a new match before each test. */
    @Before
    public void setUp(){
        match = TestGame.create().createMatch("Dipper", "Rigby", "Stadium1", keys1, keys2);
        rigby = match.getPlayer2();
    }

    /** Plays some frames of the match. */
    private void play(int frames){
        for (int i = 0; i < frames; i++) {
            match.update();
        }
    }

    /** Should not be stunned with life. */
    @Test
    public void shouldNotBeStunnedWithLife(){
        rigby.receiveDamage(40);
        assertFalse(rigby.isStunned());
        assertEquals(60, rigby.getLife(), 0.001);
    }

    /** Should be stunned when the life gets to zero. */
    @Test
    public void shouldBeStunnedWhenTheLifeGetsToZero(){
        rigby.receiveDamage(60);
        rigby.receiveDamage(60);
        assertEquals(0, rigby.getLife(), 0.001);
        assertTrue(rigby.isStunned());
    }

    /** A stunned player should not move. */
    @Test
    public void aStunnedPlayerShouldNotMove(){
        rigby.receiveDamage(100);
        double start = rigby.getPosX();
        match.pressKey(left2);
        play(30);
        assertEquals(start, rigby.getPosX(), 0.001);
    }

    /** A stunned player should not jump or kick. */
    @Test
    public void aStunnedPlayerShouldNotJumpOrKick(){
        rigby.receiveDamage(100);
        match.pressKey(jump2);
        match.pressKey(kick2);
        play(10);
        assertEquals(0, rigby.getPosY(), 0.001);
        assertFalse(rigby.isKicking());
    }

    /** The stun should last three seconds. */
    @Test
    public void theStunShouldLastThreeSeconds(){
        rigby.receiveDamage(100);
        play(179);
        assertTrue(rigby.isStunned());
        assertEquals(0, rigby.getLife(), 0.001);
        play(1);
        assertFalse(rigby.isStunned());
    }

    /** After the stun the life should be full again. */
    @Test
    public void afterTheStunTheLifeShouldBeFullAgain(){
        rigby.receiveDamage(100);
        play(180);
        assertEquals(rigby.getMaxLife(), rigby.getLife(), 0.001);
    }

    /** After the stun the player should move again. */
    @Test
    public void afterTheStunThePlayerShouldMoveAgain(){
        rigby.receiveDamage(100);
        play(180);
        double start = rigby.getPosX();
        match.pressKey(left2);
        play(10);
        assertTrue(rigby.getPosX() < start);
    }

    /** More damage while stunned should not make the stun longer. */
    @Test
    public void moreDamageWhileStunnedShouldNotMakeTheStunLonger(){
        rigby.receiveDamage(100);
        play(100);
        rigby.receiveDamage(40);
        play(80);
        assertFalse(rigby.isStunned());
    }
}
