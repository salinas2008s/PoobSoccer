package tests;

import static org.junit.Assert.*;
import org.junit.Test;
import domain.Ball;
import domain.PoobSoccer;
import domain.Match;

/**
 * Tests of the ball: gravity and bounces.
 */
public class BallTest {

    private double gravity = 0.8;
    private double fieldWidth = 1920;
    private double fieldHeight = 1080;
    private int[] keys1 = {65, 68, 87, 83, 81};
    private int[] keys2 = {37, 39, 38, 40, 17};

    /** Should fall. */
    @Test
    public void shouldFall(){
        Ball ball = new Ball(960, 600);
        for (int i = 0; i < 10; i++) {
            ball.move(gravity, fieldWidth, fieldHeight);
        }
        assertTrue(ball.getPosY() < 600);
    }

    /** Should bounce lower each time. */
    @Test
    public void shouldBounceLowerEachTime(){
        Ball ball = new Ball(960, 600);
        double[] heights = new double[2];
        for (int bounce = 0; bounce < 2; bounce++) {
            //Primero cae hasta que rebota y despues sube hasta el punto mas alto
            while (ball.getSpeedY() <= 0) {
                ball.move(gravity, fieldWidth, fieldHeight);
            }
            while (ball.getSpeedY() > 0) {
                ball.move(gravity, fieldWidth, fieldHeight);
                heights[bounce] = Math.max(heights[bounce], ball.getPosY());
            }
        }
        assertTrue(heights[0] < 600);
        assertTrue(heights[1] < heights[0]);
    }

    /** Should not go under the ground. */
    @Test
    public void shouldNotGoUnderTheGround(){
        Ball ball = new Ball(960, 600);
        for (int i = 0; i < 1000; i++) {
            ball.move(gravity, fieldWidth, fieldHeight);
            assertTrue(ball.getPosY() >= ball.getRadius());
        }
    }

    /** Should end stopped on the ground. */
    @Test
    public void shouldEndStoppedOnTheGround(){
        Ball ball = new Ball(960, 600);
        for (int i = 0; i < 2000; i++) {
            ball.move(gravity, fieldWidth, fieldHeight);
        }
        assertTrue(ball.isStopped());
        assertEquals(ball.getRadius(), ball.getPosY(), 0.001);
    }

    /** Should bounce on the sides. */
    @Test
    public void shouldBounceOnTheSides(){
        Ball ball = new Ball(960, 300);
        ball.push(-40, 0);
        for (int i = 0; i < 200; i++) {
            ball.move(gravity, fieldWidth, fieldHeight);
            assertTrue(ball.getPosX() >= ball.getRadius());
            assertTrue(ball.getPosX() <= fieldWidth - ball.getRadius());
        }
    }

    /** Should change direction when hitting a wall. */
    @Test
    public void shouldChangeDirectionWhenHittingAWall(){
        Ball ball = new Ball(100, 300);
        ball.push(-40, 0);
        for (int i = 0; i < 5; i++) {
            ball.move(gravity, fieldWidth, fieldHeight);
        }
        assertTrue(ball.getSpeedX() > 0);
    }

    /** Should not go through the ceiling. */
    @Test
    public void shouldNotGoThroughTheCeiling(){
        Ball ball = new Ball(960, 600);
        ball.push(0, 80);
        for (int i = 0; i < 50; i++) {
            ball.move(gravity, fieldWidth, fieldHeight);
            assertTrue(ball.getPosY() <= fieldHeight - ball.getRadius());
        }
    }

    /** Should start the match with the ball in the center. */
    @Test
    public void shouldStartTheMatchWithTheBallInTheCenter(){
        Match match = TestGame.create().createMatch("Dipper", "Rigby", "Stadium1", keys1, keys2);
        assertEquals(match.getStadium().getWidth() / 2, match.getBall().getPosX(), 0.001);
        assertEquals(match.getBallStartHeight(), match.getBall().getPosY(), 0.001);
        match.update();
        assertTrue(match.getBall().getPosY() < match.getBallStartHeight());
    }
}
