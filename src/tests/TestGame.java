package tests;

import domain.PoobSoccer;

/**
 * Helper of the tests. The game is only one, so each test starts it again from zero.
 */
public class TestGame {

    /** Returns the game without a match, points or upgrades. */
    public static PoobSoccer create(){
        PoobSoccer game = PoobSoccer.getInstance();
        game.restart();
        return game;
    }
}
