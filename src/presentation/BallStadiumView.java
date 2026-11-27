package presentation;

import java.awt.event.*;
import javax.swing.*;

/**
 * Screen to choose the ball, the stadium and the size of the goals.
 */
public class BallStadiumView extends Screen {

    private ButtonGroup ballGroup = new ButtonGroup();
    private ButtonGroup stadiumGroup = new ButtonGroup();
    private ButtonGroup goalGroup = new ButtonGroup();
    private final String[] ballNames = {"Ball1", "Ball2"};
    private final String[] stadiumNames = {"Stadium1", "Stadium2", "Stadium3"};
    private final String[] goalSizes = {"SMALL", "MEDIUM", "LARGE"};

    /** Creates the screen. The back button shows the previous screen. */
    public BallStadiumView(PoobSoccerGUI window, Screen previous){
        super(window);
        createBackground("lib/PoobSelectBallStadium.png");

        //Balones arriba, estadios en el medio y arcos abajo
        for (int i = 0; i < ballNames.length; i++) {
            background.add(createOptionButton(ballNames[i], scale(770 + i * 240), scale(215), scale(140), scale(140), ballGroup));
        }
        for (int i = 0; i < stadiumNames.length; i++) {
            //Centrado entre los balones (terminan en 355) y el arco grande (empieza en 644)
            background.add(createOptionButton(stadiumNames[i], scale(420 + i * 380), scale(410), scale(320), scale(180), stadiumGroup));
        }
        //Los tres arcos usan la misma imagen, cada uno mas grande que el anterior y alineados por abajo
        for (int i = 0; i < goalSizes.length; i++) {
            int goalWidth = 110 + i * 45;
            int goalHeight = goalWidth * 403 / 485;
            JToggleButton goal = createOptionButton("goalLeft", scale(660 + i * 300 - goalWidth / 2), scale(810 - goalHeight), scale(goalWidth), scale(goalHeight), goalGroup);
            goal.setActionCommand(goalSizes[i]);
            background.add(goal);
        }

        JButton backButton = createBackButton();
        background.add(backButton);
        backButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                window.showScreen(previous);
            }
        });

        JButton confirmButton = createConfirmButton();
        background.add(confirmButton);
        confirmButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if (ballGroup.getSelection() == null || stadiumGroup.getSelection() == null || goalGroup.getSelection() == null) {
                    showMessage("You must choose a ball, a stadium and a goal size.");
                    return;
                }
                window.setBallStadium(ballGroup.getSelection().getActionCommand(),
                                      stadiumGroup.getSelection().getActionCommand(),
                                      goalGroup.getSelection().getActionCommand());
                window.showGameMode(BallStadiumView.this);
            }
        });
    }
}
