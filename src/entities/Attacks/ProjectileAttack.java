package entities.Attacks;

import entities.Player;

import java.awt.geom.Rectangle2D;

public class ProjectileAttack extends BaseAttack {
    private AttackData attackData;

    public ProjectileAttack(AttackData attackData){
        super(attackData.name, attackData.damage, attackData.knockbackPower, attackData.startupFrames,
                attackData.activeFrames, attackData.recoveryFrames, attackData.hitbox);
        this.attackData=attackData;
    }


    @Override
    public void execute(Player player) {

    }
}
