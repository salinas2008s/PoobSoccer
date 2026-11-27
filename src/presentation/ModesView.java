package presentation;

import java.awt.event.*;
import javax.swing.*;

/**
 * Screen to choose the modality: player vs player or player vs machine.
 */
public class ModesView extends Screen {

    /** Creates the modality screen. */
    public ModesView(PoobSoccerGUI window){
        super(window);
        createBackground("lib/KickoffDifferents.png");

        //Los dos botones quedan centrados en la pantalla, uno debajo del otro
        JButton playerVsPlayerButton = createImageButton("lib/PlayerVSPlayer.png", 1000, 250);
        playerVsPlayerButton.setLocation(scale(460), scale(270));
        background.add(playerVsPlayerButton);
        playerVsPlayerButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                //Contra otro jugador no hay perfil
                window.setProfile(null);
                window.showSelection(ModesView.this);
            }
        });

        JButton playerVsAiButton = createImageButton("lib/PlayerVsAI.png", 980, 240);
        playerVsAiButton.setLocation(scale(470), scale(570));
        background.add(playerVsAiButton);
        playerVsAiButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                window.showProfile(ModesView.this);
            }
        });

        //Vuelve a la pantalla de inicio
        JButton backButton = createBackButton();
        background.add(backButton);
        backButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                window.showHome();
            }
        });
    }
}
