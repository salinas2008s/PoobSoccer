package tests;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import domain.PoobSoccer;
import domain.Match;
import domain.Player;
import domain.Stadium;

/**
 * Tests of the base match: moving, jumping and the edges of the field.
 */
public class MatchTest {

    private int[] keys1 = {65, 68, 87, 83, 81};
    private int[] keys2 = {37, 39, 38, 40, 17};  //Arrows, Ctrl
    private int left1 = 65, right1 = 68, jump1 = 87, kick1 = 83;
    private int right2 = 39;

    private PoobSoccer game;
    private Match match;

    /** Creates a new match before each test. */
    @Before
    public void setUp(){
        game = TestGame.create();
        match = game.createMatch("Dipper", "Rigby", "Stadium1", keys1, keys2);
    }

    /** Plays some frames of the match. */
    private void play(int frames){
        for (int i = 0; i < frames; i++) {
            match.update();
        }
    }

    /** Should create the match with the chosen data. */
    @Test
    public void shouldCreateTheMatchWithTheChosenData(){
        assertSame(match, game.getActualMatch());
        assertEquals("PLAYING", game.getStatusMatch());
        assertEquals("Dipper", match.getPlayer1().getName());
        assertEquals("Rigby", match.getPlayer2().getName());
        assertEquals("Stadium1", match.getStadium().getName());
    }

    /** Should start each player on its side. */
    @Test
    public void shouldStartEachPlayerOnItsSide(){
        double middle = match.getStadium().getWidth() / 2;
        assertTrue(match.getPlayer1().getPosX() < middle);
        assertTrue(match.getPlayer2().getPosX() > middle);
        assertEquals(0, match.getPlayer1().getPosY(), 0.001);
    }

    /** Should give each stadium its gravity. */
    @Test
    public void shouldGiveEachStadiumItsGravity(){
        assertEquals(0.8, Stadium.create("Stadium1").getGravity(), 0.001);
        assertEquals(0.5, Stadium.create("Stadium2").getGravity(), 0.001);
        assertEquals(1.1, Stadium.create("Stadium3").getGravity(), 0.001);
    }

    /** Should move with its own keys. */
    @Test
    public void shouldMoveWithItsOwnKeys(){
        double start = match.getPlayer1().getPosX();
        match.pressKey(right1);
        play(10);
        assertTrue(match.getPlayer1().getPosX() > start);
    }

    /** Should ignore the keys of the other player. */
    @Test
    public void shouldIgnoreTheKeysOfTheOtherPlayer(){
        double start = match.getPlayer1().getPosX();
        match.pressKey(right2);
        play(10);
        assertEquals(start, match.getPlayer1().getPosX(), 0.001);
    }

    /** Should stop when the key is released. */
    @Test
    public void shouldStopWhenTheKeyIsReleased(){
        match.pressKey(right1);
        play(10);
        match.releaseKey(right1);
        play(1);
        double stopped = match.getPlayer1().getPosX();
        play(10);
        assertEquals(stopped, match.getPlayer1().getPosX(), 0.001);
    }

    /** Should not go past the left edge. */
    @Test
    public void shouldNotGoPastTheLeftEdge(){
        match.pressKey(left1);
        play(1000);
        assertEquals(match.getPlayer1().getWidth() / 2, match.getPlayer1().getPosX(), 0.001);
    }

    /** Should not go past the right edge. */
    @Test
    public void shouldNotGoPastTheRightEdge(){
        match.getBall().moveTo(350, match.getBall().getRadius());
        match.pressKey(right2);
        play(1000);
        assertEquals(match.getStadium().getWidth() - match.getPlayer2().getWidth() / 2, match.getPlayer2().getPosX(), 0.001);
    }

    /** The players should not be on the same place. */
    @Test
    public void thePlayersShouldNotOverlap(){
        match.getBall().moveTo(350, match.getBall().getRadius());
        match.pressKey(right1);
        play(1000);
        Player player1 = match.getPlayer1();
        Player player2 = match.getPlayer2();
        //Se quedan pegados uno al otro y no se atraviesan
        assertTrue(player2.getPosX() - player1.getPosX() >= player1.getWidth() - 0.001);
        assertTrue(player1.getPosX() < player2.getPosX());
    }

    /** A player that is higher than the other one should not be pushed. */
    @Test
    public void aPlayerHigherThanTheOtherShouldNotBePushed(){
        Player player1 = match.getPlayer1();
        Player player2 = match.getPlayer2();
        player1.restart(900);
        player2.restart(950);
        match.getBall().moveTo(100, match.getBall().getRadius());
        //El jugador 1 sube mas arriba que la altura del jugador 2
        player1.throwUp(40);
        for (int i = 0; i < 6; i++) {
            player1.update(0.8);
        }
        assertTrue(player1.getPosY() > player2.getHeight());
        match.update();
        assertEquals(900, player1.getPosX(), 0.001);
        assertEquals(950, player2.getPosX(), 0.001);
    }

    /** The first player should look to the right and the second to the left. */
    @Test
    public void shouldLookToTheRivalAtTheStart(){
        assertEquals(1, match.getPlayer1().getFacing());
        assertEquals(-1, match.getPlayer2().getFacing());
    }

    /** Should look to the side where it moves. */
    @Test
    public void shouldLookToTheSideWhereItMoves(){
        match.getBall().moveTo(350, match.getBall().getRadius());
        match.pressKey(left1);
        play(1);
        assertEquals(-1, match.getPlayer1().getFacing());
        match.releaseKey(left1);
        match.pressKey(right1);
        play(1);
        assertEquals(1, match.getPlayer1().getFacing());
        match.pressKey(right2);
        play(1);
        assertEquals(1, match.getPlayer2().getFacing());
    }

    /** A kick that reaches the rival should take life only once. */
    @Test
    public void aKickThatReachesTheRivalShouldTakeLifeOnce(){
        Player player1 = match.getPlayer1();
        Player player2 = match.getPlayer2();
        player1.restart(900);
        player2.restart(1030);
        match.getBall().moveTo(100, match.getBall().getRadius());
        double before = player2.getLife();
        match.pressKey(kick1);
        play(1);
        match.releaseKey(kick1);
        assertEquals(before - 10, player2.getLife(), 0.001);
        play(20);
        assertEquals(before - 10, player2.getLife(), 0.001);
    }

    /** A kick far from the rival should not take life. */
    @Test
    public void aKickFarFromTheRivalShouldNotTakeLife(){
        match.getBall().moveTo(100, match.getBall().getRadius());
        double before = match.getPlayer2().getLife();
        match.pressKey(kick1);
        play(5);
        assertEquals(before, match.getPlayer2().getLife(), 0.001);
    }

    /** Should go up when jumping and come back to the ground. */
    @Test
    public void shouldGoUpWhenJumpingAndComeBackToTheGround(){
        match.pressKey(jump1);
        play(1);
        match.releaseKey(jump1);
        assertTrue(match.getPlayer1().isInAir());
        play(5);
        assertTrue(match.getPlayer1().getPosY() > 0);
        play(500);
        assertFalse(match.getPlayer1().isInAir());
        assertEquals(0, match.getPlayer1().getPosY(), 0.001);
    }

    /** Should not jump again while in the air. */
    @Test
    public void shouldNotJumpAgainWhileInTheAir(){
        Player player = match.getPlayer1();
        player.jump();
        play(5);
        double speedBefore = player.getSpeedY();
        player.jump();
        assertEquals(speedBefore, player.getSpeedY(), 0.001);
    }

    /** Should jump higher with lower gravity. */
    @Test
    public void shouldJumpHigherWithLowerGravity(){
        double highLow = maxHeight("Stadium2");
        double highNormal = maxHeight("Stadium1");
        double highHigh = maxHeight("Stadium3");
        assertTrue(highLow > highNormal);
        assertTrue(highNormal > highHigh);
    }

    /** Returns the highest point of a jump in a stadium. */
    private double maxHeight(String stadium){
        Match m = TestGame.create().createMatch("Dipper", "Rigby", stadium, keys1, keys2);
        m.getPlayer1().jump();
        double max = 0;
        for (int i = 0; i < 200; i++) {
            m.update();
            if (m.getPlayer1().getPosY() > max) {
                max = m.getPlayer1().getPosY();
            }
        }
        return max;
    }

    /** Should show the kick image while kicking. */
    @Test
    public void shouldShowTheKickImageWhileKicking(){
        match.pressKey(kick1);
        play(1);
        match.releaseKey(kick1);
        assertTrue(match.getPlayer1().isKicking());
        assertEquals("Dipper2", match.getPlayer1().getImage());
        play(match.getPlayer1().getKickDuration());
        assertFalse(match.getPlayer1().isKicking());
        assertEquals("Dipper1", match.getPlayer1().getImage());
    }

    /** Should end the match. */
    @Test
    public void shouldEndTheMatch(){
        game.endMatch();
        assertNull(game.getActualMatch());
        assertEquals("NO_MATCH", game.getStatusMatch());
    }
}
