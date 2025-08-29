package entities;

import main.Game;

import static utils.Constants.EnemyConstants.*;

public class Crabby extends Enemy{
    public Crabby(float x, float y) {
        super(x, y, CRABBY_WIDTH, CRABBY_HEIGHT, CRABBY);
        initHitBox(x, y, 22,19);
    }

    public void update(int[][] lvlData, Player player){
        updateBehavior(lvlData,player);
    }

    private void updateBehavior(int[][] lvlData, Player player){
        switch (enemyState){
            case IDLE:
                newState(RUNNING);
                break;
            case RUNNING:
            case ATTACK:
                if(aniIndex == 0){
                    attackchecked = false;
                }
                if(aniIndex == 3 && !attackchecked){
                    checkEnemyHit(attackBox, player);
                }
            default:
                break;
        }
    }


}
