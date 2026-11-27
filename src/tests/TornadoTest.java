package tests;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import domain.Ball;
import domain.PoobSoccer;
import domain.Match;
import domain.Player;

/**
 * Tests of the Tornado power (Jack).
 */
public class TornadoTest {

    private int[] keys1 = {65, 68, 87, 83, 81};
    private int[] keys2 = {37, 39, 38, 40, 17};  //Arrows, Ctrl
    private int power1 = 81, right2 = 39, jump2 = 38, kick2 = 40;

    private Match match;
    private Player jack;
    private Player rigby;
    private Ball ball;

    /** Creates a new match before each test. */
    @Before
    public void setUp(){
        match = TestGame.create().createMatch("Jack", "Rigby", "Stadium1", keys1, keys2);
        jack = match.getPlayer1();
        rigby = match.getPlayer2();
        ball = match.getBall();
    }

    /** Plays some frames of the match. */
    private void play(int frames){
        for (int i = 0; i < frames; i++) {
            match.update();
        }
    }

    /** Uses the power and the shot hits Rigby. */
    private void hitRigby(){
        play(jack.getFramesPerLevel() * jack.getMaxPower());
        jack.restart(480);
        rigby.restart(1440);
        match.pressKey(power1);
        match.update();
        match.releaseKey(power1);
        ball.moveTo(jack.getPosX() + jack.getWidth() / 2 + ball.getRadius() - 5, 150);
        ball.push(0, 0);
        int frames = 0;
        while (!rigby.isUnderEffect() && frames < 100) {
            match.update();
            frames++;
        }
    }

    /** Jack should have the tornado. */
    @Test
    public void jackShouldHaveTheTornado(){
        assertEquals("Tornado", jack.getCharacter().getPower().getName());
    }

    /** The shot should go along the ground. */
    @Test
    public void theShotShouldGoAlongTheGround(){
        play(jack.getFramesPerLevel() * jack.getMaxPower());
        jack.restart(480);
        match.pressKey(power1);
        match.update();
        ball.moveTo(jack.getPosX() + jack.getWidth() / 2 + ball.getRadius() - 5, 150);
        ball.push(0, 0);
        match.update();
        assertEquals(ball.getRadius(), ball.getPosY(), 0.001);
        assertTrue(ball.getSpeedX() > 0);
    }

    /** The tornado should take forty of life. */
    @Test
    public void theTornadoShouldTakeFortyOfLife(){
        hitRigby();
        assertEquals("Tornado", rigby.getEffect().getName());
        assertEquals(60, rigby.getLife(), 0.001);  //100 - 40
    }

    /** The rival should be thrown into the air. */
    @Test
    public void theRivalShouldBeThrownIntoTheAir(){
        hitRigby();
        play(5);
        assertTrue(rigby.getPosY() > 0);
    }

    /** The rival should not move. */
    @Test
    public void theRivalShouldNotMove(){
        hitRigby();
        double start = rigby.getPosX();
        match.pressKey(right2);
        play(30);
        assertEquals(start, rigby.getPosX(), 0.001);
    }

    /** The rival should not jump or kick. */
    @Test
    public void theRivalShouldNotJumpOrKick(){
        hitRigby();
        play(60);
        match.pressKey(jump2);
        match.pressKey(kick2);
        play(5);
        assertEquals(0, rigby.getPosY(), 0.001);
        assertFalse(rigby.isKicking());
    }

    /** The effect should last three seconds. */
    @Test
    public void theEffectShouldLastThreeSeconds(){
        hitRigby();
        play(179);
        assertTrue(rigby.isUnderEffect());
        play(1);
        assertFalse(rigby.isUnderEffect());
    }
}
