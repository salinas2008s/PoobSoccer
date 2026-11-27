package tests;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import domain.Ball;
import domain.PoobSoccer;
import domain.Match;
import domain.Player;

/**
 * Tests of touching and kicking the ball.
 */
public class KickTest {

    private int[] keys1 = {65, 68, 87, 83, 81};
    private int[] keys2 = {37, 39, 38, 40, 17};  //Arrows, Ctrl
    private int right1 = 68, kick1 = 83, kick2 = 40;

    private Match match;
    private Player player1;
    private Player player2;
    private Ball ball;

    /** Creates a new match before each test. */
    @Before
    public void setUp(){
        match = TestGame.create().createMatch("Dipper", "Rigby", "Stadium1", keys1, keys2);
        player1 = match.getPlayer1();
        player2 = match.getPlayer2();
        ball = match.getBall();
    }

    /** Puts the ball still on the ground. */
    private void putBallOnGround(double x){
        ball.moveTo(x, ball.getRadius());
        ball.push(0, 0);
    }

    /** Player1 should kick the ball to the right. */
    @Test
    public void player1ShouldKickTheBallToTheRight(){
        putBallOnGround(player1.getPosX() + 100);
        match.pressKey(kick1);
        match.update();
        assertTrue(ball.getSpeedX() > 0);
        assertTrue(ball.getSpeedY() > 0);
    }

    /** Player2 should kick the ball to the left. */
    @Test
    public void player2ShouldKickTheBallToTheLeft(){
        putBallOnGround(player2.getPosX() - 100);
        match.pressKey(kick2);
        match.update();
        assertTrue(ball.getSpeedX() < 0);
        assertTrue(ball.getSpeedY() > 0);
    }

    /** Should not kick a ball that is far. */
    @Test
    public void shouldNotKickABallThatIsFar(){
        putBallOnGround(player1.getPosX() + 500);
        match.pressKey(kick1);
        match.update();
        assertEquals(0, ball.getSpeedX(), 0.001);
    }

    /** Should not kick a ball that is behind. */
    @Test
    public void shouldNotKickABallThatIsBehind(){
        putBallOnGround(player1.getPosX() - 100);
        match.pressKey(kick1);
        match.update();
        assertTrue(ball.getSpeedX() <= 0);
    }

    /** Should push the ball when walking into it. */
    @Test
    public void shouldPushTheBallWhenWalkingIntoIt(){
        putBallOnGround(player1.getPosX() + player1.getWidth() / 2 + ball.getRadius() + 5);
        match.pressKey(right1);
        for (int i = 0; i < 5; i++) {
            match.update();
        }
        assertTrue(ball.getSpeedX() > 0);
        assertTrue(ball.getPosX() > player1.getPosX());
    }

    /** Should not let the ball go through the body. */
    @Test
    public void shouldNotLetTheBallGoThroughTheBody(){
        putBallOnGround(player1.getPosX() + player1.getWidth() / 2 + ball.getRadius() + 5);
        match.pressKey(right1);
        for (int i = 0; i < 30; i++) {
            match.update();
            assertTrue(ball.getPosX() > player1.getPosX());
        }
    }

    /** Should bounce on the head. */
    @Test
    public void shouldBounceOnTheHead(){
        double top = player1.getPosY() + player1.getHeight();
        ball.moveTo(player1.getPosX(), top + ball.getRadius() + 2);
        ball.push(0, -6);
        match.update();
        match.update(); //The ball bounces
        assertTrue(ball.getSpeedY() > 0);
        assertTrue(ball.getPosY() > top);
    }

    /** Should not touch when the ball is away. */
    @Test
    public void shouldNotTouchWhenTheBallIsAway(){
        ball.moveTo(960, 800);
        ball.push(0, 0);
        match.update();
        assertEquals(0, ball.getSpeedX(), 0.001);
    }
}
