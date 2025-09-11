package entities.Attacks;

import objects.Projectile;

public class AttackFactory {
    public static BaseAttack createAttack(AttackData data){
        return switch (data.type){
            case BASIC -> new BasicAttack(data);
            case POWER -> new PowerAttack(data);
            case PROJECTILE ->  new ProjectileAttack(data);
        };
    }
}
