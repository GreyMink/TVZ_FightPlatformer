package entities.Attacks;

import entities.Player;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public abstract class BaseAttack {
    protected AttackData attackData;


    protected boolean active;

    public BaseAttack(AttackData attackData){
        this.attackData = attackData;
    }

    //region Proširenje sa AttackPhase
//    protected int frameCounter = 0;
//    protected AttackPhase phase = AttackPhase.IDLE;
//    protected Rectangle2D.Float currentHitbox = null;
//
//    public enum AttackPhase {IDLE,STARTUP, ACTIVE, RECOVERY}
//    public void start(){
//        frameCounter = 0;
//        phase = AttackPhase.STARTUP;
//        currentHitbox = null;
//    }
//
//    public void update(Player owner){
//        if(phase == AttackPhase.IDLE)return;
//
//        frameCounter++;
//
//        switch(phase){
//            case STARTUP ->{
//                if(frameCounter >= attackData.startupFrames){
//                    phase = AttackPhase.ACTIVE;
//                    frameCounter = 0;
//                    spawnHitbox(owner);
//                }
//            }
//            case ACTIVE -> {
//                if(frameCounter >= attackData.activeFrames){
//                    phase = AttackPhase.RECOVERY;
//                    frameCounter = 0;
//                    currentHitbox = null;
//                }else{
//                    updateHitbox(owner);
//                }
//            }
//            case RECOVERY -> {
//                if(frameCounter >= attackData.recoveryFrames){
//                    phase = AttackPhase.IDLE;
//                    frameCounter = 0;
//                    currentHitbox = null;
//                }
//            }
//        }
//    }
//
//    private void spawnHitbox(Player owner){
//        float facing = owner.getFlipW();
//        float hbX = owner.getHitBox().x + facing * attackData.offsetX;
//        float hbY = owner.getHitBox().y + facing * attackData.offsetY;
//        currentHitbox = new Rectangle2D.Float(hbX, hbY, attackData.hitbox.width, attackData.hitbox.height);
//    }
//
//    private void updateHitbox(Player owner){
//        if(currentHitbox == null)return;
//        float facing = owner.getFlipW();
//        currentHitbox.x = owner.getHitBox().x + facing * attackData.offsetX;
//        currentHitbox.y = owner.getHitBox().y + facing * attackData.offsetY;
//    }
    //endregion

    public void applyHit(Player attacker, Player target){
        float facing = attacker.getFlipW(); // 1 desno, -1 lijevo
        float angleDeg;
//        if(this instanceof BasicAttack basicAttack){
//            angleDeg = basicAttack.getAttackData().knockbackPower; //treba biti knockbackAngle
//        }
//
//        float angleRad = (float)Math.toRadians(angleDeg);
//        float dirX = facing * (float)Math.cos(angleRad);
//        float dirY = (float)-Math.sin(angleRad); // negativno za gore

        float dirX = facing;
        float dirY = -1;

        // apply damage
        target.addDamage(attackData.damage);
        // apply knockback
        target.knockBack(dirX, dirY, attackData.knockbackPower);
    }
    public abstract void execute(Player player);

    public void render(Graphics g){
        if(active){
            g.drawRect((int)attackData.hitbox.x, (int)attackData.hitbox.y, (int)attackData.hitbox.width, (int)attackData.hitbox.height);
        }
    }



//    public boolean isActive() {return phase == AttackPhase.ACTIVE;}
    public int getDamage() {return attackData.damage;}
    public float getKnockbackPower() {return attackData.knockbackPower;}
    public Rectangle2D.Float getHitbox() {return attackData.hitbox;}
}
