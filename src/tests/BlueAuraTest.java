package tests;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import domain.Ball;
import domain.BlueAura;
import domain.PoobSoccer;
import domain.Match;
import domain.Player;

/**
 * Tests of the Blue Aura power (Dipper).
 */
public class BlueAuraTest {

    private int[] keys1 = {65, 68, 87, 83, 81};
    private int[] keys2 = {37, 39, 38, 40, 17};  //Arrows, Ctrl
    private int power1 = 81, power2 = 17;

    private Match match;
    private Player dipper;
    private Player rigby;
    private Ball ball;

    /** Creates a new match before each test. */
    @Before
    public void setUp(){
        match = TestGame.create().createMatch("Dipper", "Rigby", "Stadium1", keys1, keys2);
        dipper = match.getPlayer1();
        rigby = match.getPlayer2();
        ball = match.getBall();
    }

    /** Plays some frames of the match. */
    private void play(int frames){
        for (int i = 0; i < frames; i++) {
            match.update();
        }
    }

    /** Waits until the power bars are full. */
    private void fillPowerBars(){
        play(dipper.getFramesPerLevel() * dipper.getMaxPower());
        ball.moveTo(960, 600);
        ball.push(0, 0);
        dipper.restart(480);
        rigby.restart(1440);
    }

    /** Dipper uses its power with the Q key. */
    private void dipperUsesPower(){
        match.pressKey(power1);
        match.update();
        match.releaseKey(power1);
    }

    /** Puts the ball just in front of Dipper. */
    private void ballInFrontOfDipper(){
        ball.moveTo(dipper.getPosX() + dipper.getWidth() / 2 + ball.getRadius() - 5, ball.getRadius());
        ball.push(0, 0);
    }

    /** The shot should hit the rival even if the rival is kicking. */
    @Test
    public void theShotShouldHitARivalThatIsKicking(){
        fillPowerBars();
        ballInFrontOfDipper();
        dipperUsesPower();
        //Rigby patea todo el tiempo mientras llega el tiro
        match.pressKey(40);
        play(40);
        assertTrue(rigby.getLife() < rigby.getMaxLife());
    }

    /** The shot should not be stopped by a kick of the player that shot it. */
    @Test
    public void theShotShouldNotBeStoppedByTheKickOfTheShooter(){
        fillPowerBars();
        ballInFrontOfDipper();
        dipperUsesPower();
        //Dipper sigue pateando mientras el tiro va volando
        match.pressKey(83);
        play(40);
        assertTrue(rigby.getLife() < rigby.getMaxLife());
    }

    /** Dipper should have the blue aura. */
    @Test
    public void dipperShouldHaveTheBlueAura(){
        assertEquals("Blue Aura", dipper.getCharacter().getPower().getName());
        assertNull(rigby.getCharacter().getPower());
    }

    /** Should get the aura and empty the bar. */
    @Test
    public void shouldGetTheAuraAndEmptyTheBar(){
        fillPowerBars();
        dipperUsesPower();
        assertTrue(dipper.hasAura());
        assertEquals(0, dipper.getEnergyPower());
    }

    /** Should not get the aura without a full bar. */
    @Test
    public void shouldNotGetTheAuraWithoutAFullBar(){
        match.pressKey(power1);
        match.update();
        assertFalse(dipper.hasAura());
    }

    /** A character without power should not get the aura. */
    @Test
    public void aCharacterWithoutPowerShouldNotGetTheAura(){
        fillPowerBars();
        match.pressKey(power2);
        match.update();
        assertFalse(rigby.hasAura());
        assertTrue(rigby.hasFullPower());
    }

    /** Should not use the power again while it has the aura. */
    @Test
    public void shouldNotUseThePowerAgainWhileItHasTheAura(){
        fillPowerBars();
        dipperUsesPower();
        play(dipper.getFramesPerLevel() * dipper.getMaxPower());
        dipperUsesPower();
        assertTrue(dipper.hasAura());
        assertTrue(dipper.hasFullPower());
    }

    /** Touching the ball should shoot straight and remove the aura. */
    @Test
    public void touchingTheBallShouldShootStraightAndRemoveTheAura(){
        fillPowerBars();
        dipperUsesPower();
        ballInFrontOfDipper();
        match.update();
        assertFalse(dipper.hasAura());
        assertTrue(ball.isStraight());
        assertTrue(ball.getSpeedX() > 30);
        double height = ball.getPosY();
        play(5);
        assertEquals(height, ball.getPosY(), 0.001); //It does not fall
    }

    /** The shot should hurt the rival and throw it to its goal. */
    @Test
    public void theShotShouldHurtTheRivalAndThrowItToItsGoal(){
        fillPowerBars();
        dipperUsesPower();
        ballInFrontOfDipper();
        double rivalStart = rigby.getPosX();
        play(40);
        assertEquals(60, rigby.getLife(), 0.001);    //100 - 40
        assertTrue(rigby.isUnderEffect());
        assertTrue(rigby.getPosX() > rivalStart);
    }

    /** The effect should last one and a half seconds. */
    @Test
    public void theEffectShouldLastOneAndAHalfSeconds(){
        fillPowerBars();
        dipperUsesPower();
        ballInFrontOfDipper();
        int frames = 0;
        while (!rigby.isUnderEffect() && frames < 100) {
            match.update();
            frames++;
        }
        assertTrue(rigby.isUnderEffect());
        play(89);
        assertTrue(rigby.isUnderEffect());
        play(1);
        assertFalse(rigby.isUnderEffect());
    }

    /** Shot into the goal without touching the rival is a goal without damage. */
    @Test
    public void shotIntoTheGoalWithoutTouchingTheRivalIsAGoalWithoutDamage(){
        fillPowerBars();
        dipperUsesPower();
        rigby.restart(1000);
        dipper.restart(1500);
        ball.moveTo(1500 + dipper.getWidth() / 2 + ball.getRadius() - 5, ball.getRadius());
        ball.push(0, 0);
        play(20);
        assertEquals(1, match.getScore().getGoalsPlayer1());
        assertEquals(100, rigby.getLife(), 0.001);
        assertFalse(rigby.isUnderEffect());
    }

    /** Life should never be less than zero. */
    @Test
    public void lifeShouldNeverBeLessThanZero(){
        rigby.receiveDamage(40);
        rigby.receiveDamage(40);
        rigby.receiveDamage(40);
        assertEquals(0, rigby.getLife(), 0.001);
    }

    /** A new effect should replace the old one. */
    @Test
    public void aNewEffectShouldReplaceTheOldOne(){
        BlueAura power = new BlueAura();
        rigby.receivePower(power, 1);
        play(50);
        rigby.receivePower(power, 1);
        play(power.getDuration() - 1);
        assertTrue(rigby.isUnderEffect());
    }
}
