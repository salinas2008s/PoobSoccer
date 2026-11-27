package tests;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import domain.PoobSoccer;
import domain.Match;
import domain.Player;

/**
 * Tests of the power bar.
 */
public class PowerTest {

    private int[] keys1 = {65, 68, 87, 83, 81};
    private int[] keys2 = {37, 39, 38, 40, 17};

    private Match match;
    private Player player1;

    /** Creates a new match before each test. */
    @Before
    public void setUp(){
        match = TestGame.create().createMatch("Jack", "Rigby", "Stadium1", keys1, keys2);
        player1 = match.getPlayer1();
    }

    /** Plays some frames of the match. */
    private void play(int frames){
        for (int i = 0; i < frames; i++) {
            match.update();
        }
    }

    /** Should start empty. */
    @Test
    public void shouldStartEmpty(){
        assertEquals(0, player1.getEnergyPower());
        assertFalse(player1.hasFullPower());
    }

    /** Should go up one level every two seconds. */
    @Test
    public void shouldGoUpOneLevelEveryTwoSeconds(){
        play(player1.getFramesPerLevel() - 1);
        assertEquals(0, player1.getEnergyPower());
        play(1);
        assertEquals(1, player1.getEnergyPower());
        play(player1.getFramesPerLevel());
        assertEquals(2, player1.getEnergyPower());
    }

    /** Should be full after ten seconds. */
    @Test
    public void shouldBeFullAfterTenSeconds(){
        play(player1.getFramesPerLevel() * player1.getMaxPower());
        assertEquals(player1.getMaxPower(), player1.getEnergyPower());
        assertTrue(player1.hasFullPower());
    }

    /** Should not go over the maximum. */
    @Test
    public void shouldNotGoOverTheMaximum(){
        play(player1.getFramesPerLevel() * (player1.getMaxPower() + 3));
        assertEquals(player1.getMaxPower(), player1.getEnergyPower());
    }

    /** Both players should charge. */
    @Test
    public void bothPlayersShouldCharge(){
        play(player1.getFramesPerLevel() * 3);
        assertEquals(3, match.getPlayer1().getEnergyPower());
        assertEquals(3, match.getPlayer2().getEnergyPower());
    }

    /** Should keep the power after a goal. */
    @Test
    public void shouldKeepThePowerAfterAGoal(){
        play(player1.getFramesPerLevel() * 2);
        match.getBall().moveTo(1883, match.getBall().getRadius());
        match.update();
        assertEquals(1, match.getScore().getGoalsPlayer1());
        assertEquals(2, player1.getEnergyPower());
    }
}
