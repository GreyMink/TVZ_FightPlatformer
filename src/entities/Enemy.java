package entities;

import main.Game;

import java.awt.geom.Rectangle2D;

import static utils.Constants.ANI_SPEED;
import static utils.Constants.Directions.*;
import static utils.Constants.EnemyConstants.*;
import static utils.HelpMethods.*;

public abstract class Enemy extends Entity {

    protected int aniIndex, enemyState, enemyType;
    protected int aniTick = 25;
    protected boolean firstUpdate = true;
    protected boolean inAir;
    protected float fallSpeed;
    protected int walkDir = LEFT;
    protected float walkSpeed = 1.0f * Game.SCALE;
    protected int maxHealth, currentHealth;
    protected boolean active = true;
    protected boolean attackchecked;
    protected Rectangle2D.Float attackBox;

    public Enemy(float x, float y, int width, int height, int enemyType) {
        super(x, y, width, height);
        this.enemyType = enemyType;
        initHitBox(new Rectangle2D.Float(x,y,width,height));
        maxHealth = GetMaxHealth(enemyType);
        currentHealth = maxHealth;
    }

    private void updateAnimationTick(){
        aniTick++;
        if(aniTick >= ANI_SPEED){
            aniTick=0;
            aniIndex++;
            if(aniIndex >= GetSpriteAmount(enemyType, enemyState)){
                aniIndex = 0;

                switch (enemyState){
                    case ATTACK, HIT -> enemyState = IDLE;
                    case DEAD -> active = false;
                }
            }
        }
    }

    public void update(int[][] lvlData, Player player){
        updateMove(lvlData);
        updateAnimationTick();

    }

    private void updateMove(int[][] lvlData){
        if(firstUpdate){
            /*if(!IsEntityOnFloor(hitBox, lvlData)){
                inAir = true;
            }*/
            firstUpdate = false;
        }
        if(inAir){
            /*if(CanMoveHere(hitBox.x, hitBox.y + fallSpeed, hitBox.width, hitBox.height, lvlData )){
                hitBox.y += fallSpeed;
                fallSpeed += gravity;
            }else{
                inAir = false;
                hitBox.y = GetEntityYPosRoofOrFloor(hitBox,fallSpeed);
            }*/
        }else{
            switch (enemyState){
                case IDLE:
                    break;
                case RUNNING:
                    float xSpeed = 0;
                    if(walkDir == LEFT){
                        xSpeed -= walkSpeed;
                    }else{
                        xSpeed += walkSpeed;
                    }

                    /*if(CanMoveHere(hitBox.x + xSpeed, hitBox.y, hitBox.width, hitBox.height,lvlData)){
                        if(IsFloor(hitBox, xSpeed, lvlData)){
                            hitBox.x += xSpeed;
                            return;
                        }
                    }*/
                    changeWalkDir();
                    break;
            }
        }

    }

    private void changeWalkDir() {
        if(walkDir == LEFT){
            walkDir = RIGHT;
        }else{
            walkDir = LEFT;
        }
    }

    protected void newState(int enemyState){
        this.enemyState = enemyState;
        aniTick = 0;
        aniIndex = 0;
    }

    protected void checkEnemyHit(Rectangle2D.Float attackBox, Player player) {
        if(attackBox.intersects(player.hitBox)){
            player.changeHealth(-GetEnemyDamage(enemyType));
        }
        attackchecked = true;
    }

    public void hurt(int amount){
        currentHealth -= amount;
        if(currentHealth <= 0){
            newState(DEAD);
        }else{
            newState(HIT);
        }
    }

    public void resetEnemy(){
        hitBox.x = x;
        hitBox.y = y;
        firstUpdate = true;
        currentHealth = maxHealth;
        active = true;
        newState(IDLE);
        fallSpeed=0;
    }

    //region Getters & Setters
    public int getAniIndex() {
        return aniIndex;
    }

    public int getEnemyState() {
        return enemyState;
    }

    public void setEnemyState(int enemyState) {
        this.enemyState = enemyState;
    }

    public boolean isActive(){
        return active;
    }
    //endregion

}
