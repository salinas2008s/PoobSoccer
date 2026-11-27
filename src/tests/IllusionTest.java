package tests;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import domain.Ball;
import domain.PoobSoccer;
import domain.Match;
import domain.Player;

/**
 * Tests of the Illusion power (Raven).
 */
public class IllusionTest {

    private int[] keys1 = {65, 68, 87, 83, 81};
    private int[] keys2 = {37, 39, 38, 40, 17};  //Arrows, Ctrl
    private int power1 = 81;

    private Match match;
    private Player raven;
    private Player rigby;
    private Ball ball;

    /** Creates a new match before each test. */
    @Before
    public void setUp(){
        match = TestGame.create().createMatch("Raven", "Rigby", "Stadium1", keys1, keys2);
        raven = match.getPlayer1();
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
        play(raven.getFramesPerLevel() * raven.getMaxPower());
        raven.restart(480);
        rigby.restart(1440);
        match.pressKey(power1);
        match.update();
        match.releaseKey(power1);
        ball.moveTo(raven.getPosX() + raven.getWidth() / 2 + ball.getRadius() - 5, ball.getRadius());
        ball.push(0, 0);
        int frames = 0;
        while (!rigby.isUnderEffect() && frames < 100) {
            match.update();
            frames++;
        }
    }

    /** Raven should have the illusion. */
    @Test
    public void ravenShouldHaveTheIllusion(){
        assertEquals("Illusion", raven.getCharacter().getPower().getName());
    }

    /** There should be no fake balls at the start. */
    @Test
    public void thereShouldBeNoFakeBallsAtTheStart(){
        assertEquals(0, match.getFakeBalls().size());
    }

    /** The illusion should take thirty of life. */
    @Test
    public void theIllusionShouldTakeThirtyOfLife(){
        hitRigby();
        assertEquals("Illusion", rigby.getEffect().getName());
        assertEquals(70, rigby.getLife(), 0.001);  //100 - 30
    }

    /** Seven fake balls should appear. */
    @Test
    public void sevenFakeBallsShouldAppear(){
        hitRigby();
        assertEquals(7, match.getFakeBalls().size());
    }

    /** The fake balls should move. */
    @Test
    public void theFakeBallsShouldMove(){
        hitRigby();
        Ball fake = match.getFakeBalls().get(0);
        double startY = fake.getPosY();
        play(3);
        assertTrue(fake.getPosY() != startY);
    }

    /** The fake balls should disappear after two and a half seconds. */
    @Test
    public void theFakeBallsShouldDisappearAfterTwoAndAHalfSeconds(){
        hitRigby();
        play(148);
        assertEquals(7, match.getFakeBalls().size());
        play(1);
        assertEquals(0, match.getFakeBalls().size());
    }

    /** The rival can still move. */
    @Test
    public void theRivalCanStillMove(){
        hitRigby();
        double start = rigby.getPosX();
        match.pressKey(37); //Left arrow
        play(10);
        assertTrue(rigby.getPosX() < start);
    }

    /** Other powers should not make fake balls. */
    @Test
    public void otherPowersShouldNotMakeFakeBalls(){
        Match other = TestGame.create().createMatch("Dipper", "Gumball", "Stadium1", keys1, keys2);
        assertEquals(0, other.getPlayer1().getCharacter().getPower().getFakeBalls());
        assertEquals(0, other.getPlayer2().getCharacter().getPower().getFakeBalls());
    }
}
