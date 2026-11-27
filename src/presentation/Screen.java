package presentation;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Base of all the screens. It has the helpers to build images and buttons.
 */
public abstract class Screen extends JPanel {

    protected PoobSoccerGUI window;
    protected Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    protected JLabel background;

    /** Creates a screen of the window. */
    public Screen(PoobSoccerGUI window){
        this.window = window;
    }

    /** Puts an image as the background of the screen, the other components go on top of it. */
    protected void createBackground(String file){
        Image scaled = new ImageIcon(file).getImage().getScaledInstance(screenSize.width, screenSize.height, Image.SCALE_SMOOTH);
        background = new JLabel(new ImageIcon(scaled));
        background.setLayout(null);
        add(background);
    }

    /** Scales a size made for a 1920 px wide screen. */
    protected int scale(int value){
        return value * screenSize.width / 1920;
    }

    /** Loads an image with the given size. */
    protected ImageIcon createIcon(String path, int width, int height){
        Image image = new ImageIcon(path).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }

    /** Creates a button that is only an image. The size is in the 1920 px design. */
    protected JButton createImageButton(String file, int width, int height){
        JButton button = new JButton(createIcon(file, scale(width), scale(height)));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setSize(scale(width), scale(height));
        return button;
    }

    /** Creates the back button in the top left corner. */
    protected JButton createBackButton(){
        JButton button = createImageButton("lib/backButton.png", 80, 80);
        button.setLocation(scale(20), scale(20));
        return button;
    }

    /** Creates the confirm button at the bottom center. */
    protected JButton createConfirmButton(){
        JButton button = createImageButton("lib/confirm.png", 300, 170);
        button.setLocation(screenSize.width / 2 - scale(150), scale(800));
        return button;
    }

    /** Creates a button with an image that can be selected. The size and position are in pixels. */
    protected JToggleButton createOptionButton(String name, int x, int y, int width, int height, ButtonGroup group){
        JToggleButton button = new JToggleButton(createIcon("lib/" + name + ".png", width, height));
        button.setContentAreaFilled(false);
        //El borde amarillo solo se ve cuando el boton esta seleccionado
        button.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 4));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e){
                button.setBorderPainted(button.isSelected());
            }
        });
        button.setActionCommand(name);
        button.setSize(width, height);
        button.setLocation(x, y);
        group.add(button);
        return button;
    }

    /** Creates a label with an image of the given size. The position and size are in the 1920 px design. */
    protected JLabel createImageLabel(String file, int x, int y, int width, int height){
        JLabel label = new JLabel(createIcon(file, scale(width), scale(height)));
        label.setBounds(scale(x), scale(y), scale(width), scale(height));
        return label;
    }

    /** Shows a message to the user. */
    protected void showMessage(String message){
        JOptionPane.showMessageDialog(window, message);
    }
}
