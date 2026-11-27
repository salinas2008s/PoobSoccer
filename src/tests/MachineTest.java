package tests;

import static org.junit.Assert.*;
import org.junit.Test;
import domain.Ball;
import domain.PoobSoccer;
import domain.Match;
import domain.Player;
import domain.PoobSoccerException;

/**
 * Tests of the player against the machine and its four profiles.
 */
public class MachineTest {

    private int[] keys1 = {65, 68, 87, 83, 81};
    private int leftKey2 = 37;

    private PoobSoccer game = TestGame.create();

    /** Creates a match against the machine with the profile. */
    private Match createMatch(String profile) throws PoobSoccerException {
        return game.createMachineMatch("Dipper", "Rigby", "Stadium1", keys1, profile);
    }

    /** Plays some frames. */
    private void play(Match match, int frames){
        for (int i = 0; i < frames; i++) {
            match.update();
        }
    }

    /** Leaves the ball still on the ground at x. */
    private void putBall(Match match, double x){
        Ball ball = match.getBall();
        ball.moveTo(x, ball.getRadius());
        ball.push(0, 0);
    }

    /** The match against the machine should not be multiplayer. */
    @Test
    public void theMatchAgainstTheMachineShouldNotBeMultiplayer() throws Exception {
        Match match = createMatch("Offensive");
        assertFalse(match.isMultiplayer());
        assertEquals("PLAYING", game.getStatusMatch());
        assertEquals("Rigby", match.getPlayer2().getName());
    }

    /** Each profile should be created by its name. */
    @Test
    public void shouldCreateTheFourProfiles() throws Exception {
        String[] names = {"Clumsy", "Offensive", "Defensive", "Expert"};
        for (int i = 0; i < names.length; i++) {
            Match match = createMatch(names[i]);
            assertEquals(names[i], match.getPlayer2().getClass().getSimpleName());
        }
    }

    /** A profile that does not exist should throw the exception. */
    @Test
    public void aProfileThatDoesNotExistShouldThrowTheException(){
        try {
            createMatch("Genius");
            fail("It should not create a match with that profile");
        } catch (PoobSoccerException e) {
            assertEquals(PoobSoccerException.INVALID_PROFILE, e.getMessage());
        }
    }

    /** The machine should never receive the orders of the keyboard. */
    @Test
    public void theMachineShouldNeverReceiveTheKeyboard() throws Exception {
        Match match = createMatch("Defensive");
        putBall(match, 300);
        play(match, 300);
        double x = match.getPlayer2().getPosX();
        match.pressKey(leftKey2);
        play(match, 60);
        assertEquals(x, match.getPlayer2().getPosX(), 0.001);
    }

    /** The offensive should go to the ball. */
    @Test
    public void theOffensiveShouldGoToTheBall() throws Exception {
        Match match = createMatch("Offensive");
        putBall(match, 700);
        double start = match.getPlayer2().getPosX();
        play(match, 60);
        assertTrue(match.getPlayer2().getPosX() < start - 200);
    }

    /** The offensive should kick the ball to the rival goal. */
    @Test
    public void theOffensiveShouldKickTheBallToTheRivalGoal() throws Exception {
        Match match = createMatch("Offensive");
        match.getPlayer2().restart(1000);
        putBall(match, 930);
        play(match, 10);
        assertTrue(match.getBall().getSpeedX() < 0);
        assertTrue(match.getBall().getPosX() < 930);
    }

    /** The offensive should go around a ball that is behind it. */
    @Test
    public void theOffensiveShouldGoAroundABallBehindIt() throws Exception {
        Match match = createMatch("Offensive");
        match.getPlayer2().restart(1300);
        putBall(match, 1450);
        play(match, 120);
        //Pasa al otro lado del balon para poder patearlo hacia el arco rival
        assertTrue(match.getPlayer2().getPosX() > match.getBall().getPosX());
    }

    /** The defensive should stay near its goal when the ball is in the other half. */
    @Test
    public void theDefensiveShouldStayNearItsGoal() throws Exception {
        Match match = createMatch("Defensive");
        putBall(match, 400);
        play(match, 300);
        double goal = match.getRightGoal().getPosX();
        assertEquals(goal - 250, match.getPlayer2().getPosX(), 20);
    }

    /** The defensive should go to the ball when it is in its half. */
    @Test
    public void theDefensiveShouldGoToTheBallInItsHalf() throws Exception {
        Match match = createMatch("Defensive");
        match.getPlayer2().restart(1700);
        putBall(match, 1300);
        play(match, 60);
        assertTrue(match.getPlayer2().getPosX() < 1500);
    }

    /** The clumsy should move and stay inside the field. */
    @Test
    public void theClumsyShouldMoveAndStayInsideTheField() throws Exception {
        Match match = createMatch("Clumsy");
        putBall(match, 100);
        double start = match.getPlayer2().getPosX();
        boolean moved = false;
        for (int i = 0; i < 1000; i++) {
            match.update();
            double x = match.getPlayer2().getPosX();
            assertTrue(x >= 60 && x <= 1860);
            if (Math.abs(x - start) > 50) {
                moved = true;
            }
        }
        assertTrue(moved);
    }

    /** The expert should attack when nothing special happens. */
    @Test
    public void theExpertShouldAttackByDefault() throws Exception {
        Match match = createMatch("Expert");
        match.setGoalsToWin(5);
        putBall(match, 700);
        double start = match.getPlayer2().getPosX();
        play(match, 60);
        assertTrue(match.getPlayer2().getPosX() < start - 200);
    }

    /** The expert should defend when the rival is one goal from winning. */
    @Test
    public void theExpertShouldDefendWhenTheRivalIsAboutToWin() throws Exception {
        Match match = createMatch("Expert");
        match.setGoalsToWin(3);
        match.getScore().addGoalPlayer1();
        match.getScore().addGoalPlayer1();
        putBall(match, 400);
        play(match, 300);
        assertEquals(match.getRightGoal().getPosX() - 250, match.getPlayer2().getPosX(), 20);
    }

    /** The expert should protect the lead when there is little time left. */
    @Test
    public void theExpertShouldProtectTheLeadWithLittleTime() throws Exception {
        Match match = createMatch("Expert");
        match.startTimer(1);
        match.getScore().addGoalPlayer2();
        //Se pasa el tiempo hasta que quedan menos de 30 segundos, con el balon quieto lejos
        for (int i = 0; i < 2300; i++) {
            putBall(match, 400);
            match.update();
        }
        assertTrue(match.getTimer().getRemainingTime() < 30);
        assertEquals(match.getRightGoal().getPosX() - 250, match.getPlayer2().getPosX(), 20);
    }

    /** The expert should attack when it is losing and there is little time left. */
    @Test
    public void theExpertShouldAttackWhenLosingWithLittleTime() throws Exception {
        Match match = createMatch("Expert");
        match.startTimer(1);
        match.getScore().addGoalPlayer1();
        for (int i = 0; i < 2300; i++) {
            putBall(match, 400);
            match.update();
        }
        assertTrue(match.getPlayer2().getPosX() < 700);
    }

    /** The machine should be able to play a whole match with each profile. */
    @Test
    public void shouldPlayAWholeMatchWithEachProfile() throws Exception {
        String[] names = {"Clumsy", "Offensive", "Defensive", "Expert"};
        for (int i = 0; i < names.length; i++) {
            Match match = createMatch(names[i]);
            match.startTimer(1);
            for (int frame = 0; frame < 4000; frame++) {
                match.update();
                Player machine = match.getPlayer2();
                assertTrue(machine.getPosX() >= 60 && machine.getPosX() <= 1860);
                assertTrue(machine.getPosY() >= 0);
            }
            assertTrue(match.getTimer().getRemainingTime() == 0);
        }
    }

    /** Only the human player should win points against the machine. */
    @Test
    public void onlyThePlayerShouldWinPointsAgainstTheMachine() throws Exception {
        Match match = createMatch("Offensive");
        match.setGoalsToWin(1);
        match.getBall().moveTo(1883, match.getBall().getRadius());
        match.getBall().push(0, 0);
        match.update();
        game.givePoints();
        assertEquals(100, game.getStats1().getPoints());
        assertEquals(0, game.getStats2().getPoints());
    }
}
