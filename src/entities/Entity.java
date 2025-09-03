package entities;

import main.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public abstract class Entity {

    protected float x, y;
    protected int width,height;
    protected Rectangle2D.Float hitBox;
    protected boolean knockedOut = false;


    public Entity(float x, float y, int width, int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    protected void drawHitbox(Graphics g){
        //debugging
        g.setColor(Color.PINK);
        g.drawRect((int) hitBox.x,(int) hitBox.y,(int) hitBox.width,(int) hitBox.height);
    }

    protected void initHitBox(int width, int height) {
        hitBox = new Rectangle2D.Float(x,y,(int)(width * Game.SCALE),(int)(height * Game.SCALE));
    }

    public void setKO(Boolean knockedOut){
        this.knockedOut = knockedOut;
    }

/*    protected void updateHitbox(){
        hitBox.x = (int) x;
        hitBox.y = (int) y;
    }*/

    public Rectangle2D.Float getHitBox(){
        return hitBox;
    }


}
