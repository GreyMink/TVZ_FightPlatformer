package objects;

import java.awt.geom.Rectangle2D;

import static java.lang.Math.sqrt;
import static utils.Constants.Projectiles.*;

public class Projectile {
    private Rectangle2D.Float hitBox;
    private double directionX, directionY;
    private boolean active = true;
    private double startX, startY;
    private double maxDistance;
    private final double speed = PROJECTILE_SPEED;

    public Projectile(int startX, int startY, int mouseX, int mouseY){
        hitBox = new Rectangle2D.Float(startX,startY,CANNONBALL_WIDTH,CANNONBALL_HEIGHT);
        this.startX = startX;
        this.startY = startY;

        double dx = mouseX - startX;
        double dy = mouseY - startY;
        double distance = Math.sqrt(dx * dx + dy * dy);


        directionX = dx/distance;
        directionY = dy/distance;

        this.maxDistance = 300;
    }

    public void updatePosition(){
        hitBox.x += (float) (directionX * speed);
        hitBox.y += (float) (directionY * speed);

        if(GetDistanceTraveled() >= maxDistance){
            active = false;
        }

    }

    private double GetDistanceTraveled() {
        double dx = hitBox.x - startX;
        double dy = hitBox.y - startY;
        return sqrt(dx * dx + dy * dy);
    }

    public Rectangle2D.Float getHitBox(){return hitBox;}
    public void setActive(boolean active){this.active = active;}
    public Boolean isActive(){return active;}
}
