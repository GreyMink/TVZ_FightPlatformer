package gamestates;

import main.Game;
import ui.AudioOptions;
import ui.PauseButton;
import ui.UrmButton;
import utils.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static utils.Constants.UI.URMButtons.*;

public class GameOptions extends State implements Statemethods {

    private AudioOptions audioOptions;
    private BufferedImage backgroundImage, optionsBackgroundImage;
    private int backgroundX,backgroundY, backgroundW,backgroundH;
    private UrmButton menuButton;

    public GameOptions(Game game) {
        super(game);
        loadImgs();
        loadButtons();
        audioOptions = game.getAudioOptions();
    }

    @Override
    public void update() {
        menuButton.update();
        audioOptions.update();
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(backgroundImage,0,0,Game.GAME_WIDTH,Game.GAME_HEIGHT, null);
        g.drawImage(optionsBackgroundImage, backgroundX, backgroundY, backgroundW, backgroundH, null);

        menuButton.draw(g);
        audioOptions.draw(g);
    }

    private void loadButtons() {
        int menuX = (int)(387 * Game.SCALE);
        int menuY = (int)(325 * Game.SCALE);

        menuButton = new UrmButton(menuX,menuY,URM_SIZE, URM_SIZE, 2);
    }

    private void loadImgs() {
        backgroundImage = LoadSave.GetSpriteAtlas(LoadSave.MENU_BACKGROUND_IMG);
        optionsBackgroundImage = LoadSave.GetSpriteAtlas(LoadSave.OPTIONS_MENU);

        backgroundW = (int)(optionsBackgroundImage.getWidth()*Game.SCALE);
        backgroundH = (int)(optionsBackgroundImage.getHeight()*Game.SCALE);
        backgroundX = Game.GAME_WIDTH / 2 - backgroundW / 2;
        backgroundY = (int) (Game.SCALE * 33);
    }



    //region Input
    private boolean isIn(MouseEvent e, PauseButton pauseButton){
        return pauseButton.getBounds().contains(e.getX(),e.getY());
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(isIn(e,menuButton)){
            menuButton.setMousePressed(true);
        }else{
            audioOptions.mousePressed(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(isIn(e,menuButton)){
            if(menuButton.isMousePressed()){
                Gamestate.state = Gamestate.MENU;
            }
        }else{
            audioOptions.mouseReleased(e);
        }

        menuButton.resetBools();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        menuButton.setMouseOver(false);

        if(isIn(e,menuButton)){
            menuButton.setMouseOver(true);
        }else{
            audioOptions.mouseMoved(e);
        }


    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
            Gamestate.state = Gamestate.MENU;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public void mouseDragged(MouseEvent e) {
        audioOptions.mouseDragged(e);
    }

    //endregion
}
