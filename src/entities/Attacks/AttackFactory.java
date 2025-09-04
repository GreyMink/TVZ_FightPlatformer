package entities.Attacks;

public class AttackFactory {
    public static BaseAttack createAttack(AttackData data){
        return switch (data.type){
            case BASIC -> new BasicAttack(data);
            case POWER -> new PowerAttack(data);
        };
    }
}
