package ui;

import gamestates.Gamestate;
import gamestates.Playing;
import main.Game;
import utils.LoadSave;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static utils.Constants.UI.PauseButtons.*;
import static utils.Constants.UI.URMButtons.*;
import static utils.Constants.UI.VolumeButtons.*;

public class PauseOverlay {
    private Playing playing;
    private AudioOptions audioOptions;
    private BufferedImage backgroundImg;
    private int backgX, backgY, backgW, backgH;
    private UrmButton menuButton, rematchButton, unpauseButton;


    public PauseOverlay(Playing playing){
        this.playing = playing;
        loadBackground();
        audioOptions = playing.getGame().getAudioOptions();
        createUrmButtons();

    }

    private void createUrmButtons() {
        int menuX = (int)(313 * Game.SCALE);
        int replayX = (int) (387 * Game.SCALE);
        int unpauseX = (int)(462 * Game.SCALE);
        int bY = (int)(325 * Game.SCALE);

        menuButton = new UrmButton(menuX, bY, URM_SIZE, URM_SIZE, 2);
        rematchButton = new UrmButton(replayX, bY, URM_SIZE, URM_SIZE, 1);
        unpauseButton = new UrmButton(unpauseX, bY, URM_SIZE, URM_SIZE, 0);
    }


    private void loadBackground() {
        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PAUSE_BACKGROUND);
        backgW = (int) (backgroundImg.getWidth() * Game.SCALE);
        backgH = (int) (backgroundImg.getHeight() * Game.SCALE);
        backgX = Game.GAME_WIDTH / 2 - backgW / 2;
        backgY = (int) (25 * Game.SCALE);

    }

    public void update(){
        menuButton.update();
        rematchButton.update();
        unpauseButton.update();

        audioOptions.update();
    }

    public void draw(Graphics g){
        g.drawImage(backgroundImg,backgX,backgY,backgW,backgH,null);

        menuButton.draw(g);
        rematchButton.draw(g);
        unpauseButton.draw(g);

        audioOptions.draw(g);
    }
    //region Inputs
    public void mousePressed(MouseEvent e) {
        if (isIn(e,menuButton)){
            menuButton.setMousePressed(true);
        } else if (isIn(e, rematchButton)){
            rematchButton.setMousePressed(true);
        }else if (isIn(e,unpauseButton)){
            unpauseButton.setMousePressed(true);
        } else {
            audioOptions.mousePressed(e);
        }
    }

    public void mouseReleased(MouseEvent e) {
        if(isIn(e,menuButton)){
            if(menuButton.isMousePressed()){
                Gamestate.state = Gamestate.MENU;
                playing.unpauseGame();
            }
        } else if(isIn(e, rematchButton)){
            if(rematchButton.isMousePressed()){
                playing.resetAll();
                playing.unpauseGame();
            }
        } else if(isIn(e,unpauseButton)){
            if(unpauseButton.isMousePressed()){
                playing.unpauseGame();
            }
        } else {
            audioOptions.mouseReleased(e);
        }


        menuButton.resetBools();
        rematchButton.resetBools();
        unpauseButton.resetBools();

    }

    public void mouseMoved(MouseEvent e) {
        menuButton.setMouseOver(false);
        rematchButton.setMouseOver(false);
        unpauseButton.setMouseOver(false);



        if (isIn (e, menuButton)){
            menuButton.setMouseOver(true);
        }else if (isIn (e, rematchButton)){
            rematchButton.setMouseOver(true);
        }else if (isIn (e, unpauseButton)){
            unpauseButton.setMouseOver(true);
        }else {
            audioOptions.mouseMoved(e);
        }
    }
    //Slider
    public void mouseDragged(MouseEvent e){
        audioOptions.mouseDragged(e);
    }
    private boolean isIn(MouseEvent e, PauseButton pauseButton){
        return pauseButton.getBounds().contains(e.getX(),e.getY());
    }


    //endregion



}
