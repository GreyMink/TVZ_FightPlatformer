package ui;

import gamestates.Gamestate;
import gamestates.Playing;
import main.Game;
import utils.Constants;
import utils.LoadSave;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.function.DoublePredicate;

import static utils.Constants.UI.URMButtons.*;

public class MatchFinishedOverlay {

    private Playing playing;
    private UrmButton menu, characterSelect;
    private BufferedImage img;
    private int bgX, bgY, bgW, bgH;

    public MatchFinishedOverlay(Playing playing){
        this.playing = playing;
        initImg();
        initButtons();
    }

    private void initButtons() {
        int menuX = (int) (330 * Game.SCALE);
        int chSelectX = (int) (445 * Game.SCALE);
        int y = (int) (210 * Game.SCALE);
        characterSelect = new UrmButton(chSelectX, y, URM_SIZE, URM_SIZE, 0);
        menu = new UrmButton(menuX, y, URM_SIZE, URM_SIZE,2);

    }

    private void initImg() {
        img = LoadSave.GetSpriteAtlas(LoadSave.COMPLETED_IMG);
        bgW = (int) (img.getWidth() * Game.SCALE);
        bgH = (int) (img.getHeight() * Game.SCALE);
        bgX = Game.GAME_WIDTH / 2 - bgW / 2;
        bgY = (int) (75 * Game.SCALE);
    }

    public void draw(Graphics g){
        g.drawImage(img,bgX,bgY,bgW,bgW,null);
        characterSelect.draw(g);
        menu.draw(g);
    }

    public void update(){

    }

    private boolean isInButton(UrmButton b, MouseEvent e){
        return b.getBounds().contains(e.getX(),e.getY());
    }

    public void mouseMoved(MouseEvent e){
        menu.setMouseOver(false);
        characterSelect.setMouseOver(false);

        if(isInButton(menu,e)){
            menu.setMouseOver(true);
        }else if(isInButton(characterSelect,e)){
            characterSelect.setMouseOver(true);
        }
    }

    public void mouseReleased(MouseEvent e){
        if(isInButton(menu,e)){
            if(menu.isMousePressed()){
                playing.resetAll();
                Gamestate.state =Gamestate.MENU;
            }
        }else if(isInButton(characterSelect,e)){
            if(characterSelect.isMousePressed()){
                playing.loadNextStage();
            }
        }
        menu.resetBools();
        characterSelect.resetBools();
    }

    public void mousePressed(MouseEvent e){
        if(isInButton(menu,e)){
            menu.setMousePressed(true);
        }else if(isInButton(characterSelect,e)){
            characterSelect.setMousePressed(true);
        }
    }
}
