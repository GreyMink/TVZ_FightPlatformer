package ui;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ServerButton extends BasicButton implements UImethods{

    private BufferedImage buttonImg;
    private final String serverName;

    // buttonstate za stanja gumba - normal, pressed, hover
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
        if(buttonState == 0){
            g.setColor(new Color(188, 86, 82));
            g.drawRect(x, y, width, height);
            g.fillRect(x, y, width, height);

            g.setColor(Color.YELLOW);
            g.drawRect(x+10, y+10, width - 20, height - 20);
            g.fillRect(x+10, y+10, width - 20, height - 20);

            g.setColor(Color.BLUE);
            g.setFont(new Font("Sans Serif", Font.BOLD, 25));
            g.drawString(serverName, x+width-20, y+height-10);

//            g.drawImage(buttonImg, x+10, y+10, width - 20, height - 20, null);
        }
        else if(buttonState == 1){
            g.setColor(Color.LIGHT_GRAY);
            g.drawRect(x, y, width, height);
            g.fillRect(x, y, width, height);

            g.setColor(Color.YELLOW);
            g.drawRect(x+10, y+10, width - 20, height - 20);
            g.fillRect(x+10, y+10, width - 20, height - 20);

            g.setColor(Color.BLUE);
            g.setFont(new Font("Sans Serif", Font.BOLD, 25));
            g.drawString(serverName, x+width-20, y+height-10);

//            g.drawImage(buttonImg, x+10, y+10, width - 20, height - 20, null);
        }
    }

    @Override
    public void loadButtonImgs() {

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
