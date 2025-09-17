package objects;

import main.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import static utils.Constants.ANI_SPEED;
import static utils.Constants.ObjectConstants.*;


public class GameObject {
    protected int x,y, objType;
    protected Rectangle2D.Float hitBox;
    protected boolean doAnimation, active = true;
    protected int aniTick, aniIndex;
    protected int xDrawOffset, yDrawOffset;

    public GameObject(int x, int y, int objType){
        this.x = x;
        this.y = y;
        this.objType = objType;
    }

    public void updateAnimationTick(){
        aniTick++;
        if(aniTick >= ANI_SPEED){
            aniTick=0;
            aniIndex++;
            if(aniIndex >= GetSpriteAmount(objType)){
                aniIndex = 0;
                doAnimation = false;
                active = false;

            }
        }
    }

    public void reset(){
        aniTick = 0;
        aniIndex = 0;
        active = true;
        doAnimation = false;
    }

    protected void initHitBox(int width, int height) {
        hitBox = new Rectangle2D.Float(x,y,(int) (width * Game.SCALE), (int)(height * Game.SCALE));
    }

    protected void drawHitbox(Graphics g){
        //debugging
        g.setColor(Color.PINK);
        g.drawRect((int) hitBox.x,(int) hitBox.y,(int) hitBox.width,(int) hitBox.height);
    }

    public int getObjType() {return objType;}
    public void setObjType(int objType) {this.objType = objType;}

    public int getxDrawOffset() {return xDrawOffset;}
    public int getyDrawOffset() {return yDrawOffset;}
    public Rectangle2D.Float getHitBox() {return hitBox;}
    public boolean isActive() {return active;}
    public void setActive(boolean isActive){this.active = isActive;}
    public int getAniIndex(){return aniIndex;}
    public void setAnimation(boolean doAnimation) {this.doAnimation = doAnimation;}
}
