package entities;

import main.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public abstract class Entity {

    protected float x, y;
    protected int width;
    protected int height;
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

    protected void initHitBox(Rectangle2D.Float hitBox) {
        this.hitBox = new Rectangle2D.Float(
                //hitBox.x i hitBox.y su offset
                hitBox.x,
                hitBox.y,
                hitBox.width,
                hitBox.height);
    }

    public void setKO(Boolean knockedOut){
        this.knockedOut = knockedOut;
    }
    public void setPosition(int x, int y){this.x = x; this.y = y;}
/*    protected void updateHitbox(){
        hitBox.x = (int) x;
        hitBox.y = (int) y;
    }*/

    public Rectangle2D.Float getHitBox(){
        return hitBox;
    }
    public float getX() {return x;}
    public float getY() {return y;}
    public int getWidth() {return width;}
    public int getHeight() {return height;}


}
