package presentation;

import java.awt.event.*;
import javax.swing.*;

/**
 * Screen to choose the profile of the machine.
 */
public class ProfileView extends Screen {

    private ButtonGroup profileGroup = new ButtonGroup();
    private final String[] profileNames = {"Clumsy", "Offensive", "Defensive", "Expert"};

    /** Creates the screen. The back button shows the previous screen. */
    public ProfileView(PoobSoccerGUI window, Screen previous){
        super(window);
        createBackground("lib/PoobSelectAI.jpeg");

        //Cuatro perfiles en dos filas de dos botones, las imagenes se llaman igual que el perfil
        for (int i = 0; i < profileNames.length; i++) {
            int column = i % 2;
            int row = i / 2;
            background.add(createOptionButton(profileNames[i], scale(330 + column * 780), scale(290 + row * 260), scale(480), scale(238), profileGroup));
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
                if (profileGroup.getSelection() == null) {
                    showMessage("You must choose a profile for the machine.");
                    return;
                }
                window.setProfile(profileGroup.getSelection().getActionCommand());
                window.showSelection(ProfileView.this);
            }
        });
    }
}
