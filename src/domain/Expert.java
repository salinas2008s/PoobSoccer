package domain;

/**
 * Machine that decides if it attacks or defends by looking at the state of the match.
 */
public class Expert extends AIPlayer {

    private int shortTime;       //Segundos que faltan para que se considere poco tiempo

    /** Creates the expert machine. */
    public Expert(String name, Character character, double posX){
        super(name, character, posX);
        shortTime = 30;
    }

    /** Looks at the score, the time and the positions, and plays like an offensive or a defensive. */
    @Override
    protected void play(){
        Match match = getMatch();
        Ball ball = match.getBall();
        Player rival = match.getPlayer1();
        int goalsMachine = match.getScore().getGoalsPlayer2();
        int goalsRival = match.getScore().getGoalsPlayer1();
        boolean timeIsShort = match.hasTimer() && match.getTimer().getRemainingTime() < shortTime;
        //El rival esta a un gol de ganar
        boolean rivalAboutToWin = match.getGoalsToWin() > 0 && goalsRival == match.getGoalsToWin() - 1;

        if (match.isSuddenDeath() || (timeIsShort && goalsMachine < goalsRival)) {
            //Va perdiendo o esta empatado sin tiempo: tiene que atacar
            attack();
        } else if ((timeIsShort && goalsMachine > goalsRival) || rivalAboutToWin) {
            //Cuida la ventaja o evita que le hagan el gol que decide el partido
            defend();
        } else if (Math.abs(rival.getPosX() - ball.getPosX()) < Math.abs(getPosX() - ball.getPosX())
                   && ball.getPosX() > match.getStadium().getWidth() / 2) {
            //El rival esta mas cerca del balon y el balon esta en su mitad: se protege
            defend();
        } else {
            attack();
        }
    }
}
