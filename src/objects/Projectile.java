package objects;

import java.awt.geom.Rectangle2D;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static utils.Constants.Projectiles.*;

public class Projectile {
    private Rectangle2D.Float hitBox;
    private int directionX, directionY;
    private boolean active = true;
    private int startX, startY;
    private double maxDistance;
    private double speed = PROJECTILE_SPEED;

    public Projectile(int startX, int startY, int directionX){
        hitBox = new Rectangle2D.Float(startX,startY,CANNONBALL_WIDTH,CANNONBALL_HEIGHT);
        this.startX = startX;
        this.startY = startY;
        this.maxDistance = sqrt(startX^2 + startY^2);
        this.directionX = directionX;
    }

    public void updatePosition(){
        hitBox.x += directionX * PROJECTILE_SPEED;
    }

    public Rectangle2D.Float getHitBox(){return hitBox;}
    public void setActive(boolean active){this.active = active;}
    public Boolean isActive(){return active;}
}
