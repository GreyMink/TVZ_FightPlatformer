package entities.Attacks;

import java.awt.geom.Rectangle2D;

public class AttackData {
    public String name;
    public AttackType type;
    public int damage;
    public int knockback;
    public int startupFrames;
    public int activeFrames;
    public int recoveryFrames;
//    public final int hitboxWidth;
//    public final int hitboxHeight;
    public Rectangle2D.Float hitbox;
    public float offsetX;
    public float offsetY;

    public AttackData(String name,AttackType type, int damage, int knockback,
                      int startupFrames, int activeFrames, int recoveryFrames,
                      Rectangle2D.Float hitbox, float offsetX, float offsetY) {
        this.name = name;
        this.type = type;
        this.damage = damage;
        this.knockback = knockback;
        this.startupFrames = startupFrames;
        this.activeFrames = activeFrames;
        this.recoveryFrames = recoveryFrames;
        this.hitbox = hitbox;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
}
