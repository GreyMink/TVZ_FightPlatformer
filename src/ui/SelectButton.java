package ui;

import utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static utils.Constants.UI.Buttons.*;

public class SelectButton extends BasicButton implements UImethods{

    private BufferedImage[] imgs;
    private BufferedImage buttonContentImg;
    private int xOffsetCenter = B_STONE_SERVER_WIDTH / 2;
    private int buttonState;
    private int selectIndex;

    private boolean mousePressed;

    public SelectButton(int x, int y, int width, int height, int selectIndex, BufferedImage buttonContentImg) {
        super(x, y, width, height);
        this.selectIndex = selectIndex;
        this.buttonContentImg = buttonContentImg;

        loadButtonImgs();
    }

    @Override
    public void update() {
        if(mousePressed){
            buttonState = 1;
        }else buttonState = 0;

    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(imgs[buttonState],x, y, B_STONE_SELECT_WIDTH,B_STONE_SELECT_HEIGHT, null);

        int contentWidth  = B_STONE_SELECT_WIDTH - 30;
        int contentHeight = B_STONE_SELECT_HEIGHT - 30;

// compute the center of the button
        int buttonCenterX = x + (B_STONE_SELECT_WIDTH / 2);
        int buttonCenterY = y + (B_STONE_SELECT_HEIGHT / 2);

// compute the top-left of the content so it’s centered
        int contentX = buttonCenterX - (contentWidth / 2);
        int contentY = buttonCenterY - (contentHeight / 2);
        g.drawImage(buttonContentImg, contentX, contentY, contentWidth, contentHeight, null);

    }

    @Override
    public void loadButtonImgs() {
        imgs = new BufferedImage[2];
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.SELECT_BUTTONS_STONE);
        for(int i = 0; i < imgs.length;i++){
            imgs[i] = temp.getSubimage(i * B_WIDTH_STONE_SELECT_DEFAULT, 0,B_WIDTH_STONE_SELECT_DEFAULT,B_HEIGHT_STONE_SELECT_DEFAULT);
        }
    }

    public void reset(){
        mousePressed = false;
    }

    //region Getters & Setters
    public boolean isMousePressed() {return mousePressed;}
    public void setMousePressed(boolean mousePressed) {this.mousePressed = mousePressed;}
    public int getSelectIndex() {return selectIndex;}
    public void setSelectIndex(int selectIndex) {this.selectIndex = selectIndex;}
    public void setButtonContentImg(BufferedImage buttonContentImg) {this.buttonContentImg = buttonContentImg;}
    //endregion
}
