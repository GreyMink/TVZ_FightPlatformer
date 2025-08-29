package main;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

public class GameWindow extends JFrame {

    public GameWindow(GamePanel gamePanel) {

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(gamePanel);

        //odredi početnu lokaciju prozora na sredinu ekrana
        setResizable(false);
        pack();
        setLocationRelativeTo(null);

        //treba biti na dnu klase kako bi se prikazala grafika pri pokretanju
        setVisible(true);
        addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {

            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                gamePanel.getGame().WindowFocusLost();
            }
        });
    }

}
