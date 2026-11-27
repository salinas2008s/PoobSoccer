package tests;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import domain.Ball;
import domain.PoobSoccer;
import domain.Match;
import domain.Player;

/**
 * Tests of the Ray power (Gumball).
 */
public class RayTest {

    private int[] keys1 = {65, 68, 87, 83, 81};
    private int[] keys2 = {37, 39, 38, 40, 17};  //Arrows, Ctrl
    private int power1 = 81, right2 = 39, jump2 = 38;

    private Match match;
    private Player gumball;
    private Player rigby;
    private Ball ball;

    /** Creates a new match before each test. */
    @Before
    public void setUp(){
        match = TestGame.create().createMatch("Gumball", "Rigby", "Stadium1", keys1, keys2);
        gumball = match.getPlayer1();
        rigby = match.getPlayer2();
        ball = match.getBall();
    }

    /** Plays some frames of the match. */
    private void play(int frames){
        for (int i = 0; i < frames; i++) {
            match.update();
        }
    }

    /** Gumball uses its power and the shot hits Rigby. */
    private void electrocuteRigby(){
        play(gumball.getFramesPerLevel() * gumball.getMaxPower());
        gumball.restart(480);
        rigby.restart(1440);
        match.pressKey(power1);
        match.update();
        match.releaseKey(power1);
        ball.moveTo(gumball.getPosX() + gumball.getWidth() / 2 + ball.getRadius() - 5, ball.getRadius());
        ball.push(0, 0);
        int frames = 0;
        while (!rigby.isUnderEffect() && frames < 100) {
            match.update();
            frames++;
        }
    }

    /** Gumball should have the ray. */
    @Test
    public void gumballShouldHaveTheRay(){
        assertEquals("Ray", gumball.getCharacter().getPower().getName());
    }

    /** The ray should take sixty of life. */
    @Test
    public void theRayShouldTakeSixtyOfLife(){
        electrocuteRigby();
        assertTrue(rigby.isUnderEffect());
        assertEquals("Ray", rigby.getEffect().getName());
        assertEquals(40, rigby.getLife(), 0.001);  //100 - 60
    }

    /** An electrocuted player should move at half speed. */
    @Test
    public void anElectrocutedPlayerShouldMoveAtHalfSpeed(){
        electrocuteRigby();
        rigby.restart(1000);
        rigby.receivePower(gumball.getCharacter().getPower(), 1);
        double start = rigby.getPosX();
        match.pressKey(right2);
        play(10);
        assertEquals(start + 40, rigby.getPosX(), 0.001);
    }

    /** An electrocuted player should not jump. */
    @Test
    public void anElectrocutedPlayerShouldNotJump(){
        electrocuteRigby();
        match.pressKey(jump2);
        play(10);
        assertEquals(0, rigby.getPosY(), 0.001);
        assertFalse(rigby.isInAir());
    }

    /** The effect should last three seconds. */
    @Test
    public void theEffectShouldLastThreeSeconds(){
        electrocuteRigby();
        play(179);
        assertTrue(rigby.isUnderEffect());
        play(1);
        assertFalse(rigby.isUnderEffect());
    }

    /** After the effect the player should jump again. */
    @Test
    public void afterTheEffectThePlayerShouldJumpAgain(){
        electrocuteRigby();
        play(180);
        match.pressKey(jump2);
        play(5);
        assertTrue(rigby.getPosY() > 0);
    }
}
