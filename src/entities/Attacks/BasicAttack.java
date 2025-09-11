package entities.Attacks;

import entities.Player;

public class BasicAttack extends BaseAttack{
    private AttackData attackData;

    public BasicAttack(AttackData attackData){
        super(attackData.name, attackData.damage, attackData.knockbackPower, attackData.startupFrames,
                attackData.activeFrames, attackData.recoveryFrames, attackData.hitbox);
        this.attackData=attackData;
    }

    @Override
    public void execute(Player player) {
        // Position hitbox relative to player
        hitbox.x = (player.getHitBox().x + (player.getFlipW() == 1 ? player.getHitBox().width : -hitbox.width));
        hitbox.y = player.getHitBox().y;
        active = true;

        // Ovdje kontroliraj  startup/active/recovery frame counter u gameloop
        player.getPlaying().checkEnemyHit(hitbox);
    }
}
