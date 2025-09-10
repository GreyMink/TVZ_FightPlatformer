package entities.Attacks;

import entities.Player;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public abstract class BaseAttack {
    protected String name;
    protected int damage;
    protected int knockback;
    protected int startupFrames;
    protected int activeFrames;
    protected int recoveryFrames;
    protected Rectangle2D.Float hitbox;

    protected boolean active;

    public BaseAttack(String name, int damage, int knockback,
                      int startup, int active, int recovery,
                      Rectangle2D.Float hitbox){
        this.name = name;
        this.damage = damage;
        this.knockback = knockback;
        this.startupFrames = startup;
        this.activeFrames = active;
        this.recoveryFrames = recovery;
        this.hitbox = hitbox;
    }

    public abstract void execute(Player player);

    public void render(Graphics g){
        if(active){
            g.drawRect((int)hitbox.x, (int)hitbox.y, (int)hitbox.width, (int)hitbox.height);
        }
    }

    public boolean isActive() {
        return active;
    }
    public int getDamage() {
        return damage;
    }
    public int getKnockback() {
        return knockback;
    }
    public Rectangle2D.Float getHitbox() {
        return hitbox;
    }
}
