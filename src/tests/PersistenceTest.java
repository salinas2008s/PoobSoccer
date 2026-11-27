package tests;

import static org.junit.Assert.*;
import java.io.File;
import java.io.FileWriter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import domain.PoobSoccer;
import domain.Match;
import domain.Player;
import domain.PoobSoccerException;

/**
 * Tests of saving and opening a match and of keeping the progress of the players.
 */
public class PersistenceTest {

    private int[] keys1 = {65, 68, 87, 83, 81};
    private int[] keys2 = {37, 39, 38, 40, 17};
    private File matchFile = new File("PersistenceTest.poob");
    private File progressFile = new File("PersistenceTest.dat");

    private PoobSoccer game;
    private Match match;

    /** Creates a match by time with a few frames played. */
    @Before
    public void setUp() throws Exception {
        game = TestGame.create();
        game.setProgressFile(progressFile.getName());
        game.getStats1().addPoints(10000);
        game.getStats1().upgrade("Kick");
        game.getStats1().upgrade("Survival");
        match = game.createMatch("Dipper", "Jack", "Stadium2", keys1, keys2);
        match.startTimer(2);
        match.setBallStyle("Ball2");
        match.setGoalSize("LARGE");
        match.pressKey(keys1[1]);
        for (int i = 0; i < 100; i++) {
            match.update();
        }
        match.getPlayer2().receiveDamage(35);
    }

    /** Deletes the files of the tests. */
    @After
    public void tearDown(){
        matchFile.delete();
        progressFile.delete();
    }

    /** Should keep the score, the clock and the positions. */
    @Test
    public void shouldKeepTheScoreTheClockAndThePositions() throws Exception {
        game.saveMatch(matchFile);
        Match opened = TestGame.create().openMatch(matchFile);
        assertEquals(match.getScore().getGoalsPlayer1(), opened.getScore().getGoalsPlayer1());
        assertEquals(match.getScore().getGoalsPlayer2(), opened.getScore().getGoalsPlayer2());
        assertEquals(match.getTimer().getRemainingTime(), opened.getTimer().getRemainingTime());
        assertEquals(match.getPlayer1().getPosX(), opened.getPlayer1().getPosX(), 0.001);
        assertEquals(match.getPlayer2().getPosX(), opened.getPlayer2().getPosX(), 0.001);
        assertEquals(match.getBall().getPosX(), opened.getBall().getPosX(), 0.001);
        assertEquals(match.getBall().getPosY(), opened.getBall().getPosY(), 0.001);
    }

    /** Should keep the life and the upgrades of the players. */
    @Test
    public void shouldKeepTheLifeAndTheUpgrades() throws Exception {
        game.saveMatch(matchFile);
        Match opened = TestGame.create().openMatch(matchFile);
        Player player1 = opened.getPlayer1();
        Player player2 = opened.getPlayer2();
        assertEquals(match.getPlayer2().getLife(), player2.getLife(), 0.001);
        assertEquals(match.getPlayer1().getMaxLife(), player1.getMaxLife(), 0.001);
        assertEquals(match.getPlayer1().getKickMultiplier(), player1.getKickMultiplier(), 0.001);
        assertEquals("Dipper", player1.getName());
        assertEquals("Jack", player2.getName());
    }

    /** Should keep what was chosen for the match. */
    @Test
    public void shouldKeepWhatWasChosenForTheMatch() throws Exception {
        game.saveMatch(matchFile);
        Match opened = TestGame.create().openMatch(matchFile);
        assertEquals("Stadium2", opened.getStadium().getName());
        assertEquals("Ball2", opened.getBallStyle());
        assertEquals("LARGE", opened.getGoalSize());
        assertEquals(100, opened.getRightGoal().getWidth(), 0.001);
    }

    /** The opened match should go on playing. */
    @Test
    public void theOpenedMatchShouldGoOnPlaying() throws Exception {
        game.saveMatch(matchFile);
        PoobSoccer other = TestGame.create();
        Match opened = other.openMatch(matchFile);
        assertSame(opened, other.getActualMatch());
        assertEquals("PLAYING", other.getStatusMatch());
        double before = opened.getTimer().getRemainingTime();
        for (int i = 0; i < 120; i++) {
            opened.update();
        }
        assertTrue(opened.getTimer().getRemainingTime() < before);
    }

    /** The keys that were pressed when saving should not stay pressed. */
    @Test
    public void thePressedKeysShouldNotStayPressed() throws Exception {
        game.saveMatch(matchFile);
        Match opened = TestGame.create().openMatch(matchFile);
        double x = opened.getPlayer1().getPosX();
        opened.update();
        assertEquals(x, opened.getPlayer1().getPosX(), 0.001);
    }

    /** Should not save if there is no match. */
    @Test
    public void shouldNotSaveWithoutAMatch(){
        try {
            TestGame.create().saveMatch(matchFile);
            fail("It should not save without a match");
        } catch (PoobSoccerException e) {
            assertEquals(PoobSoccerException.NO_MATCH_TO_SAVE, e.getMessage());
            assertFalse(matchFile.exists());
        }
    }

    /** A file that is not a saved match should throw the exception. */
    @Test
    public void anInvalidFileShouldThrowTheException() throws Exception {
        FileWriter writer = new FileWriter(matchFile);
        writer.write("this is not a match");
        writer.close();
        try {
            TestGame.create().openMatch(matchFile);
            fail("It should not open an invalid file");
        } catch (PoobSoccerException e) {
            assertEquals(PoobSoccerException.INVALID_FILE, e.getMessage());
        }
    }

    /** An empty file should throw the exception. */
    @Test
    public void anEmptyFileShouldThrowTheException() throws Exception {
        matchFile.createNewFile();
        try {
            TestGame.create().openMatch(matchFile);
            fail("It should not open an empty file");
        } catch (PoobSoccerException e) {
            assertEquals(PoobSoccerException.INVALID_FILE, e.getMessage());
        }
    }

    /** A file that saves another kind of object should throw the exception. */
    @Test
    public void aFileWithAnotherKindOfObjectShouldThrowTheException() throws Exception {
        //Se guarda una lista en vez de un partido
        try (java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(new java.io.FileOutputStream(matchFile))) {
            out.writeObject(new java.util.ArrayList<String>());
        }
        try {
            TestGame.create().openMatch(matchFile);
            fail("It should not open that file");
        } catch (PoobSoccerException e) {
            assertEquals(PoobSoccerException.INVALID_FILE, e.getMessage());
        }
    }

    /** A file that does not exist should throw the exception. */
    @Test
    public void aMissingFileShouldThrowTheException(){
        try {
            TestGame.create().openMatch(new File("NoExisteEsteArchivo.poob"));
            fail("It should not open a missing file");
        } catch (PoobSoccerException e) {
            assertEquals(PoobSoccerException.OPEN_ERROR, e.getMessage());
        }
    }

    /** The points and the upgrades should be the same after saving and loading. */
    @Test
    public void thePointsAndTheUpgradesShouldBeTheSameAfterLoading() throws Exception {
        game.getStats2().addPoints(250);
        game.saveProgress();
        int points1 = game.getStats1().getPoints();
        int points2 = game.getStats2().getPoints();
        //El juego es uno solo, entonces se empieza de nuevo y se carga lo que se guardo
        PoobSoccer other = TestGame.create();
        other.setProgressFile(progressFile.getName());
        other.loadProgress();
        assertEquals(points1, other.getStats1().getPoints());
        assertEquals(points2, other.getStats2().getPoints());
        assertEquals(2, other.getStats1().getLevel("Kick"));
        assertEquals(2, other.getStats1().getLevel("Survival"));
        assertEquals(1, other.getStats1().getLevel("Speed"));
    }

    /** Without a progress file the game should start from the beginning. */
    @Test
    public void withoutAProgressFileTheGameShouldStartFromTheBeginning() throws Exception {
        PoobSoccer other = TestGame.create();
        other.setProgressFile("NoExisteElProgreso.dat");
        int points = other.getStats1().getPoints();
        other.loadProgress();
        assertEquals(points, other.getStats1().getPoints());
    }

    /** A progress file that is not valid should throw the exception. */
    @Test
    public void anInvalidProgressFileShouldThrowTheException() throws Exception {
        FileWriter writer = new FileWriter(progressFile);
        writer.write("nothing useful");
        writer.close();
        try {
            game.loadProgress();
            fail("It should not load an invalid file");
        } catch (PoobSoccerException e) {
            assertEquals(PoobSoccerException.INVALID_FILE, e.getMessage());
        }
    }
}
