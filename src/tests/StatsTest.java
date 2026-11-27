package tests;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import domain.PoobSoccer;
import domain.Match;
import domain.PoobSoccerException;
import domain.Stats;

/**
 * Tests of the points and the upgrades of the shop.
 */
public class StatsTest {

    private int[] keys1 = {65, 68, 87, 83, 81};
    private int[] keys2 = {37, 39, 38, 40, 17};

    private Stats stats;

    /** Creates new stats before each test. */
    @Before
    public void setUp(){
        stats = new Stats();
    }

    /** Should start in level 1 and without points. */
    @Test
    public void shouldStartInLevelOneWithoutPoints(){
        assertEquals(0, stats.getPoints());
        assertEquals(1, stats.getLevel("Speed"));
        assertEquals(1, stats.getLevel("Jump"));
        assertEquals(1, stats.getLevel("Kick"));
        assertEquals(1, stats.getLevel("Survival"));
    }

    /** The prices should be 500, 1000 and 2000. */
    @Test
    public void thePricesShouldDouble() throws PoobSoccerException {
        stats.addPoints(5000);
        assertEquals(500, stats.getPrice("Kick"));
        stats.upgrade("Kick");
        assertEquals(1000, stats.getPrice("Kick"));
        stats.upgrade("Kick");
        assertEquals(2000, stats.getPrice("Kick"));
    }

    /** Buying should raise the level and take the points. */
    @Test
    public void buyingShouldRaiseTheLevelAndTakeThePoints() throws PoobSoccerException {
        stats.addPoints(600);
        stats.upgrade("Speed");
        assertEquals(2, stats.getLevel("Speed"));
        assertEquals(100, stats.getPoints());
        assertEquals(1, stats.getLevel("Jump"));
    }

    /** Buying without points should throw an exception. */
    @Test
    public void buyingWithoutPointsShouldFail(){
        stats.addPoints(400);
        try {
            stats.upgrade("Jump");
            fail("It should not let buy without points");
        } catch (PoobSoccerException e) {
            assertEquals(PoobSoccerException.NOT_ENOUGH_POINTS, e.getMessage());
            assertEquals(1, stats.getLevel("Jump"));
            assertEquals(400, stats.getPoints());
        }
    }

    /** Should not go over level 10. */
    @Test
    public void shouldNotGoOverTheMaxLevel() throws PoobSoccerException {
        stats.addPoints(1000000);
        for (int i = 1; i < 10; i++) {
            stats.upgrade("Survival");
        }
        assertEquals(10, stats.getLevel("Survival"));
        try {
            stats.upgrade("Survival");
            fail("It should not pass level 10");
        } catch (PoobSoccerException e) {
            assertEquals(PoobSoccerException.MAX_LEVEL, e.getMessage());
        }
    }

    /** The winner gets 100 points and the loser 25. */
    @Test
    public void theWinnerShouldGet100AndTheLoser25(){
        PoobSoccer game = TestGame.create();
        Match match = game.createMatch("Dipper", "Rigby", "Stadium1", keys1, keys2);
        match.setGoalsToWin(1);
        match.getBall().moveTo(37, match.getBall().getRadius());
        match.getBall().push(0, 0);
        match.update();
        game.givePoints();
        assertEquals(25, game.getStats1().getPoints());
        assertEquals(100, game.getStats2().getPoints());
    }

    /** Speed, jump and survival upgrades should change the player. */
    @Test
    public void upgradesShouldChangeThePlayer() throws PoobSoccerException {
        PoobSoccer game = TestGame.create();
        game.getStats1().addPoints(2000000);
        for (int i = 1; i < 10; i++) {
            game.getStats1().upgrade("Speed");
            game.getStats1().upgrade("Jump");
            game.getStats1().upgrade("Survival");
            game.getStats1().upgrade("Kick");
        }
        Match match = game.createMatch("Dipper", "Rigby", "Stadium1", keys1, keys2);
        match.pressKey(keys1[1]);
        match.pressKey(keys2[1]);
        match.update();
        double fast = match.getPlayer1().getSpeedX();
        double normal = match.getPlayer2().getSpeedX();
        assertTrue(fast > normal * 2);
        assertTrue(match.getPlayer1().getMaxLife() > match.getPlayer2().getMaxLife() * 2);
        assertTrue(match.getPlayer1().getJumpForce() > match.getPlayer2().getJumpForce() * 1.4);
        assertTrue(match.getPlayer1().getKickMultiplier() > 2);
    }

    /** A tie should not give points. */
    @Test
    public void aTieShouldNotGivePoints(){
        PoobSoccer game = TestGame.create();
        game.createMatch("Dipper", "Rigby", "Stadium1", keys1, keys2);
        game.givePoints();
        assertEquals(0, game.getStats1().getPoints());
        assertEquals(0, game.getStats2().getPoints());
    }
}
