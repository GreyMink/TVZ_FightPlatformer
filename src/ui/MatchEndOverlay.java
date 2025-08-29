package ui;

import gamestates.Playing;
import main.Game;
import utils.Constants;
import utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static utils.Constants.UI.URMButtons.URM_SIZE;


//NEPOTREBNO??
public class MatchEndOverlay {

    private Playing playing;
    private UrmButton menu, rematch;
    private BufferedImage img;
    private int backGroundX, backGroundY, backGroundWidth, backGroundHeight;

    public MatchEndOverlay(Playing playing){
        this.playing = playing;
        initImg();
        initButtons();
    }

    private void initButtons() {
        int menuX = (int) (330 * Game.SCALE);
        int rematchX = (int) (445 * Game.SCALE);
        int y = (int)(195 * Game.SCALE);

        //0 i 2 su redovi u slici koji se koriste
        menu = new UrmButton(menuX, y, URM_SIZE, URM_SIZE, 2);
        rematch = new UrmButton(rematchX, y, URM_SIZE, URM_SIZE, 0);
    }

    private void initImg(){
        img = LoadSave.GetSpriteAtlas(LoadSave.MATCH_END_IMG);
        backGroundX = Game.GAME_WIDTH / 2;
        backGroundY = (int) (75 * Game.SCALE);
        backGroundWidth = (int) (img.getWidth() * Game.SCALE);
        backGroundHeight = (int) (img.getHeight() * Game.SCALE);
    }

    public void update(){

    }
    public void draw(Graphics g){

    }
    public void mouseMoved(){

    }
    public void mouseReleased(){

    }
    public void mousePressed(){

    }

}
