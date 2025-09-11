package entities.Attacks;

import entities.Player;

public class PowerAttack extends BaseAttack{

    public PowerAttack(AttackData attackData) {
        super(attackData);
    }

    @Override
    public void execute(Player player) {
        attackData.hitbox.x = (player.getHitBox().x + (player.getFlipW() == 1 ? player.getHitBox().width : -attackData.hitbox.width));
        attackData.hitbox.y = player.getHitBox().y;
        active = true;

        // In a game loop you’d manage startup/active/recovery frame counters here
        player.getPlaying().checkEnemyHit(attackData.hitbox);

    }
}
