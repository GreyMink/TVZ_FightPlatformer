package ui;

import gamestates.Gamestate;
import utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static utils.Constants.UI.Buttons.*;

public class ServerButton extends BasicButton implements UImethods{

    private int xOffsetCenter = B_STONE_SERVER_WIDTH / 2;
    private BufferedImage[] imgs;
    private final String serverName;

    // buttonstate za stanja gumba - normal, pressed
    private int buttonState;
    private int selectIndex;

    private boolean mousePressed;

    public ServerButton(int x, int y, int width, int height, int selectIndex, String serverName) {
        super(x, y, width, height);
        this.selectIndex = selectIndex;
        this.serverName = serverName;

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
        g.drawImage(imgs[buttonState], x - xOffsetCenter, y, B_STONE_SERVER_WIDTH,B_STONE_SERVER_HEIGHT, null);

        //Tekst u gumbu
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 25));

        int textX = (x - xOffsetCenter - xOffsetCenter/3) + B_STONE_SERVER_WIDTH / 2;
        int textY = y + (B_STONE_SERVER_HEIGHT / 2);

        g.drawString(serverName, textX, textY);
    }

    @Override
    public void loadButtonImgs() {
        imgs = new BufferedImage[2];
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.SERVER_SELECT_BUTTON_STONE);
        for(int i = 0; i < imgs.length;i++){
            imgs[i] = temp.getSubimage(i * B_WIDTH_STONE_SERVER_DEFAULT, 0,B_WIDTH_STONE_SERVER_DEFAULT,B_HEIGHT_STONE_SERVER_DEFAULT);
        }
    }

    public void reset(){
        mousePressed = false;
    }

    //region Getters & Setters
    public boolean isMousePressed() {
        return mousePressed;
    }

    public void setMousePressed(boolean mousePressed) {
        this.mousePressed = mousePressed;
    }

    public int getSelectIndex() {
        return selectIndex;
    }

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
    }
    //endregion
}
