package presentation;

import java.awt.event.*;
import javax.swing.*;

/**
 * Screen to choose the game mode: by time or by goals.
 */
public class GameModeView extends Screen {

    private ButtonGroup gameModeGroup = new ButtonGroup();
    private final String[] minutesNames = {"1Minutes", "2Minutes", "3Minutes"};
    private final String[] goalsNames = {"5Goals", "7Goals", "10Goals"};

    /** Creates the screen. The back button shows the previous screen. */
    public GameModeView(PoobSoccerGUI window, Screen previous){
        super(window);
        createBackground("lib/PoobSelectGameMode.jpeg");

        //Un solo grupo para los 6 botones, asi solo se puede escoger uno
        for (int i = 0; i < minutesNames.length; i++) {
            background.add(createOptionButton(minutesNames[i], scale(270 + i * 480), scale(330), scale(420), scale(178), gameModeGroup));
        }
        for (int i = 0; i < goalsNames.length; i++) {
            background.add(createOptionButton(goalsNames[i], scale(270 + i * 480), scale(570), scale(420), scale(178), gameModeGroup));
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
                if (gameModeGroup.getSelection() == null) {
                    showMessage("You must choose a game mode.");
                    return;
                }
                //Se saca el numero del nombre del boton, por ejemplo 3Minutes da 3
                String option = gameModeGroup.getSelection().getActionCommand();
                if (option.endsWith("Minutes")) {
                    window.setMatchMode("BY_TIME", Integer.parseInt(option.replace("Minutes", "")));
                } else {
                    window.setMatchMode("BY_GOALS", Integer.parseInt(option.replace("Goals", "")));
                }
                window.startMatch();
            }
        });
    }
}
