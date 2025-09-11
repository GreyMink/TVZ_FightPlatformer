package entities.Attacks;

import entities.Player;

public class BasicAttack extends BaseAttack{

    public BasicAttack(AttackData attackData){
        super(attackData);
    }

    @Override
    public void execute(Player player) {
        // Postavi hitbox ovisno o lokaciji igrača
        attackData.hitbox.x = (player.getHitBox().x + (player.getFlipW() == 1 ? player.getHitBox().width : -attackData.hitbox.width));
        attackData.hitbox.y = player.getHitBox().y;
        active = true;

        // Ovdje kontroliraj  startup/active/recovery frame counter u gameloop
        player.getPlaying().checkEnemyHit(attackData.hitbox);
    }

    public AttackData getAttackData(){return attackData;}
}
