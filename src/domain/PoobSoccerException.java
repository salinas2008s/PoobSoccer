package domain;

/**
 * Exception for the errors of the game.
 */
public class PoobSoccerException extends Exception{
    public static final String INCOMPLETE_CONFIG = "You must choose all the options before playing.";
    public static final String INVALID_GOALS = "The number of goals must be greater than 0.";
    public static final String NOT_ENOUGH_POINTS = "You do not have enough points for this upgrade.";
    public static final String MAX_LEVEL = "This stat is already at the max level.";
    public static final String NO_MATCH_TO_SAVE = "There is no match to save.";
    public static final String SAVE_ERROR = "The game could not be saved in that file.";
    public static final String OPEN_ERROR = "The file could not be read.";
    public static final String INVALID_FILE = "That file is not a saved match of POOBSoccer.";
    public static final String INVALID_PROFILE = "That profile of the machine does not exist.";
    public static final String OLD_VERSION = "That file was saved with another version of the game and cannot be opened.";

    /** Creates the exception with its message. */
    public PoobSoccerException(String message){
        super(message);
    }

}
