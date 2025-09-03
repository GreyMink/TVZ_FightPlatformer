package ui;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class SelectButton extends PauseButton implements UImethods{

    private BufferedImage buttonImg;
    private BufferedImage buttonContentImg;

    private int buttonState;
    private int selectIndex;

    private boolean mousePressed;

    public SelectButton(int x, int y, int width, int height, int selectIndex, BufferedImage buttonContentImg) {
        super(x, y, width, height);
        this.selectIndex = selectIndex;
        this.buttonContentImg = buttonContentImg;

        loadButtonImgs();
    }

    //temp za unutarnje slike
    public SelectButton(int x, int y, int width, int height, int selectIndex) {
        super(x, y, width, height);
        this.selectIndex = selectIndex;

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
        if(buttonState == 0){
            g.setColor(new Color(188, 86, 82));
            g.drawRect(x, y, width, height);
            g.fillRect(x, y, width, height);

//            g.setColor(Color.YELLOW);
//            g.drawRect(x+10, y+10, width - 20, height - 20);
//            g.fillRect(x+10, y+10, width - 20, height - 20);
            g.drawImage(buttonContentImg, x+10, y+10, width - 20, height - 20, null);
        }
        else if(buttonState == 1){
            g.setColor(Color.BLUE);
            g.drawRect(x, y, width, height);
            g.fillRect(x, y, width, height);

//            g.setColor(Color.YELLOW);
//            g.drawRect(x+10, y+10, width - 20, height - 20);
//            g.fillRect(x+10, y+10, width - 20, height - 20);
            g.drawImage(buttonContentImg, x+10, y+10, width - 20, height - 20, null);
        }
    }

    @Override
    public void loadButtonImgs() {

    }

    public void reset(){
        mousePressed = false;
    }

    private boolean isIn(MouseEvent e, SelectButton selectButton){
        return selectButton.getBounds().contains(e.getX(),e.getY());
    }

    //region Getters & Setters
    public boolean isMousePressed() {return mousePressed;}
    public void setMousePressed(boolean mousePressed) {this.mousePressed = mousePressed;}
    public int getSelectIndex() {return selectIndex;}
    public void setSelectIndex(int selectIndex) {this.selectIndex = selectIndex;}
    public void setButtonContentImg(BufferedImage buttonContentImg) {this.buttonContentImg = buttonContentImg;}
    public void setButtonImg(BufferedImage buttonImg) {this.buttonImg = buttonImg;}
    //endregion
}
