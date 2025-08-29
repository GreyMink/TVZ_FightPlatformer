package ui;

import utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;
import static utils.Constants.UI.PauseButtons.*;

public class SoundButton extends PauseButton implements UImethods{

    private BufferedImage[][] soundImgs;
    private boolean mouseOver, mousePressed, muted;

    private int rowIndex, colIndex;


    public SoundButton(int x, int y, int width, int height) {
        super(x, y, width, height);
        loadButtonImgs();
    }

    /*private void loadSoundImgs() {
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.SOUND_BUTTONS);
        soundImgs = new BufferedImage[2][3];
        for(int j = 0; j < soundImgs.length; j++){
            for (int i = 0; i < soundImgs[j].length; i++){
                soundImgs[j][i] = temp.getSubimage(i * SOUND_SIZE_DEFAULT, j * SOUND_SIZE_DEFAULT,SOUND_SIZE_DEFAULT,SOUND_SIZE_DEFAULT);
            }
        }
    }*/

    @Override
    public void loadButtonImgs(){
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.SOUND_BUTTONS);
        soundImgs = new BufferedImage[2][3];
        for(int j = 0; j < soundImgs.length; j++){
            for (int i = 0; i < soundImgs[j].length; i++){
                soundImgs[j][i] = temp.getSubimage(i * SOUND_SIZE_DEFAULT, j * SOUND_SIZE_DEFAULT,SOUND_SIZE_DEFAULT,SOUND_SIZE_DEFAULT);
            }
        }
    }

    @Override
    public void update(){
        colIndex = 0;

        if(muted){
            rowIndex = 1;
        }
        else
            rowIndex = 0;


        if(mouseOver){
            colIndex = 1;
        }
        if(mousePressed){
            colIndex = 2;
        }
    }

    @Override
    public void draw(Graphics g){
        g.drawImage(soundImgs[rowIndex][colIndex], x,y,width,height,null);
    }



    public boolean isMouseOver() {
        return mouseOver;
    }

    public void setMouseOver(boolean mouseOver) {
        this.mouseOver = mouseOver;
    }

    public boolean isMousePressed() {
        return mousePressed;
    }

    public void setMousePressed(boolean mousePressed) {
        this.mousePressed = mousePressed;
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public void resetBools(){
        mouseOver = false;
        mousePressed = false;
    }
}
