package tests;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import domain.Ball;
import domain.PoobSoccer;
import domain.Match;
import domain.Player;

/**
 * Tests of the goals and the score.
 */
public class GoalTest {

    private int[] keys1 = {65, 68, 87, 83, 81};
    private int[] keys2 = {37, 39, 38, 40, 17};
    private int right1 = 68;

    private Match match;
    private Ball ball;

    /** Creates a new match before each test. */
    @Before
    public void setUp(){
        match = TestGame.create().createMatch("Dipper", "Rigby", "Stadium1", keys1, keys2);
        ball = match.getBall();
    }

    /** Puts the ball still and plays one frame. */
    private void putBallAndPlay(double x, double y){
        ball.moveTo(x, y);
        ball.push(0, 0);
        match.update();
    }

    /** Should start zero to zero. */
    @Test
    public void shouldStartZeroToZero(){
        assertEquals(0, match.getScore().getGoalsPlayer1());
        assertEquals(0, match.getScore().getGoalsPlayer2());
    }

    /** Ball in the right goal is a goal for player1. */
    @Test
    public void ballInTheRightGoalIsAGoalForPlayer1(){
        putBallAndPlay(1883, ball.getRadius());
        assertEquals(1, match.getScore().getGoalsPlayer1());
        assertEquals(0, match.getScore().getGoalsPlayer2());
    }

    /** Ball in the left goal is a goal for player2. */
    @Test
    public void ballInTheLeftGoalIsAGoalForPlayer2(){
        putBallAndPlay(37, ball.getRadius());
        assertEquals(0, match.getScore().getGoalsPlayer1());
        assertEquals(1, match.getScore().getGoalsPlayer2());
    }

    /** Own goal should count for the rival. */
    @Test
    public void ownGoalShouldCountForTheRival(){
        Player player1 = match.getPlayer1();
        player1.restart(400);
        ball.moveTo(330, ball.getRadius());
        ball.push(-20, 0);
        for (int i = 0; i < 60; i++) {
            match.update();
        }
        assertEquals(0, match.getScore().getGoalsPlayer1());
        assertEquals(1, match.getScore().getGoalsPlayer2());
    }

    /** Ball half inside is not a goal. */
    @Test
    public void ballHalfInsideIsNotAGoal(){
        putBallAndPlay(match.getRightGoal().getPosX(), ball.getRadius());
        assertEquals(0, match.getScore().getGoalsPlayer1());
    }

    /** Ball over the goal is not a goal. */
    @Test
    public void ballOverTheGoalIsNotAGoal(){
        putBallAndPlay(1883, match.getRightGoal().getHeight() + 100);
        assertEquals(0, match.getScore().getGoalsPlayer1());
    }

    /** Ball should bounce on top of the goal. */
    @Test
    public void ballShouldBounceOnTopOfTheGoal(){
        ball.moveTo(1883, match.getRightGoal().getHeight() + ball.getRadius() + 2);
        ball.push(0, -6);
        match.update();
        match.update();
        assertTrue(ball.getSpeedY() > 0);
        assertEquals(0, match.getScore().getGoalsPlayer1());
    }

    /** Ball should bounce when only its edge touches the top. */
    @Test
    public void ballShouldBounceWhenOnlyItsEdgeTouchesTheTop(){
        double x = match.getRightGoal().getPosX() - ball.getRadius() / 2;
        ball.moveTo(x, match.getRightGoal().getHeight() + ball.getRadius() + 2);
        ball.push(0, -6);
        match.update();
        match.update();
        assertTrue(ball.getSpeedY() > 0);
        assertTrue(ball.getPosY() > match.getRightGoal().getHeight());
    }

    /** Ball should bounce down when it hits the top from inside. */
    @Test
    public void ballShouldBounceDownWhenItHitsTheTopFromInside(){
        ball.moveTo(match.getRightGoal().getPosX(), match.getRightGoal().getHeight() - ball.getRadius() - 2);
        ball.push(0, 10);
        match.update();
        assertTrue(ball.getSpeedY() < 0);
        assertTrue(ball.getPosY() < match.getRightGoal().getHeight());
    }

    /** Ball should bounce on the front corner of the right goal. */
    @Test
    public void ballShouldBounceOnTheFrontCornerOfTheRightGoal(){
        double cornerX = match.getRightGoal().getPosX();
        ball.moveTo(cornerX - ball.getRadius() - 3, match.getRightGoal().getHeight());
        ball.push(10, 0);
        match.update();
        assertTrue(ball.getSpeedX() < 0);
        assertEquals(0, match.getScore().getGoalsPlayer1());
    }

    /** Ball should bounce on the front corner of the left goal. */
    @Test
    public void ballShouldBounceOnTheFrontCornerOfTheLeftGoal(){
        double cornerX = match.getLeftGoal().getPosX() + match.getLeftGoal().getWidth();
        ball.moveTo(cornerX + ball.getRadius() + 3, match.getRightGoal().getHeight());
        ball.push(-10, 0);
        match.update();
        assertTrue(ball.getSpeedX() > 0);
        assertEquals(0, match.getScore().getGoalsPlayer2());
    }

    /** Ball far from the corner should not bounce. */
    @Test
    public void ballFarFromTheCornerShouldNotBounce(){
        ball.moveTo(960, match.getRightGoal().getHeight());
        ball.push(10, 0);
        match.update();
        assertEquals(10, ball.getSpeedX(), 0.001);
    }

    /** After a goal everything goes back to the start. */
    @Test
    public void afterAGoalEverythingGoesBackToTheStart(){
        Player player1 = match.getPlayer1();
        double startX = player1.getPosX();
        match.pressKey(right1);
        for (int i = 0; i < 10; i++) {
            match.update();
        }
        match.releaseKey(right1);
        assertTrue(player1.getPosX() > startX);

        putBallAndPlay(1883, ball.getRadius());
        assertEquals(startX, player1.getPosX(), 0.001);
        assertEquals(match.getStadium().getWidth() / 2, ball.getPosX(), 0.001);
        assertEquals(match.getBallStartHeight(), ball.getPosY(), 0.001);
    }

    /** Should count several goals. */
    @Test
    public void shouldCountSeveralGoals(){
        putBallAndPlay(1883, ball.getRadius());
        putBallAndPlay(1883, ball.getRadius());
        putBallAndPlay(37, ball.getRadius());
        assertEquals(2, match.getScore().getGoalsPlayer1());
        assertEquals(1, match.getScore().getGoalsPlayer2());
    }

    /** Receiving a goal should add a quarter of the power bar. */
    @Test
    public void receivingAGoalShouldAddAQuarterOfThePowerBar(){
        putBallAndPlay(1883, ball.getRadius());
        //Un cuarto de la barra de 5 niveles es un nivel y un cuarto
        assertEquals(1, match.getPlayer2().getEnergyPower());
        assertEquals(0, match.getPlayer1().getEnergyPower());
    }

    /** The power bar should not pass the maximum when receiving goals. */
    @Test
    public void thePowerBarShouldNotPassTheMaximum(){
        for (int i = 0; i < 6; i++) {
            putBallAndPlay(1883, ball.getRadius());
        }
        assertEquals(match.getPlayer2().getMaxPower(), match.getPlayer2().getEnergyPower());
        assertTrue(match.getPlayer2().hasFullPower());
    }

    /** The goals should be medium by default. */
    @Test
    public void goalsShouldBeMediumByDefault(){
        assertEquals(75, match.getLeftGoal().getWidth(), 0.001);
        assertEquals(320, match.getLeftGoal().getHeight(), 0.001);
    }

    /** Small goals should be lower. */
    @Test
    public void smallGoalsShouldBeLower(){
        match.setGoalSize("SMALL");
        assertEquals(240, match.getLeftGoal().getHeight(), 0.001);
        assertEquals(240, match.getRightGoal().getHeight(), 0.001);
    }

    /** Large goals should be wider and higher. */
    @Test
    public void largeGoalsShouldBeWiderAndHigher(){
        match.setGoalSize("LARGE");
        assertEquals(100, match.getRightGoal().getWidth(), 0.001);
        assertEquals(420, match.getRightGoal().getHeight(), 0.001);
        assertEquals(1920 - 100, match.getRightGoal().getPosX(), 0.001);
    }

    /** A ball over a small goal is not a goal, but with a large goal it is. */
    @Test
    public void theSizeShouldChangeWhatIsAGoal(){
        match.setGoalSize("SMALL");
        putBallAndPlay(1883, 300);
        assertEquals(0, match.getScore().getGoalsPlayer1());
        match.setGoalSize("LARGE");
        putBallAndPlay(1883, 300);
        assertEquals(1, match.getScore().getGoalsPlayer1());
    }
}
