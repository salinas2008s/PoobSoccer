package presentation;

import java.awt.event.*;
import javax.swing.*;

/**
 * Screen to choose the character of each team.
 */
public class SelectionView extends Screen {

    private ButtonGroup teamOneGroup = new ButtonGroup();
    private ButtonGroup teamTwoGroup = new ButtonGroup();
    private final String[] characterNames = {"Dipper", "Gumball", "Jack", "Raven", "Rigby"};

    /** Creates the screen. The back button shows the previous screen. */
    public SelectionView(PoobSoccerGUI window, Screen previous){
        super(window);
        createBackground("lib/PoobSelectPlayer.png");

        //Equipo 1 a la izquierda y equipo 2 a la derecha, 3 personajes por fila
        for (int i = 0; i < characterNames.length; i++) {
            int column = i % 3;
            int row = i / 3;
            background.add(createCharacterButton(characterNames[i], scale(230 + column * 180), scale(290 + row * 280), teamOneGroup));
            background.add(createCharacterButton(characterNames[i], scale(1250 + column * 180), scale(290 + row * 280), teamTwoGroup));
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
                //Los dos equipos tienen que escoger personaje
                if (teamOneGroup.getSelection() == null || teamTwoGroup.getSelection() == null) {
                    showMessage("Both teams must choose a character.");
                    return;
                }
                window.setCharacters(teamOneGroup.getSelection().getActionCommand(),
                                     teamTwoGroup.getSelection().getActionCommand());
                window.showBallStadium(SelectionView.this);
            }
        });
    }

    /** Creates a button to choose a character. */
    private JToggleButton createCharacterButton(String name, int x, int y, ButtonGroup group){
        //Cuando esta seleccionado muestra la imagen 2 del personaje
        JToggleButton button = new JToggleButton(createIcon("lib/" + name + "1.png", scale(120), scale(200)));
        button.setSelectedIcon(createIcon("lib/" + name + "2.png", scale(120), scale(200)));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setActionCommand(name);
        button.setSize(scale(160), scale(260));
        button.setLocation(x, y);
        group.add(button);
        return button;
    }
}
