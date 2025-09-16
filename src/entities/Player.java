package entities;

import entities.Attacks.*;
import gamestates.Playing;
import main.Game;
import utils.LoadSave;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static utils.Constants.GRAVITY;
import static utils.Constants.PlayerConstants.*;
import static utils.HelpMethods.*;

public class Player extends Entity{
    //region Variable
    private BufferedImage[][] animations;
    private int aniTick, aniIndex;
    private final int aniSpeed = 15;
    private int playerAction = IDLE;
    private boolean moving = false;

    private boolean attacking = false;
    private boolean projectileAttack = false;

    private boolean left, up, right, down, jump;
    private final float playerSpeed = Game.SCALE;
    private int[][] lvlData;
    private Point spawnPoint;

    private final PlayerCharacter playerCharacter;
    private final Playing playing;
    private Boolean invincibility = false;
    //endregion

    //region Gravity
    private float airSpeed = 0f;
    private final float jumpSpeed = -3.0f * Game.SCALE;
    private final float fallSpeedAfterCollision = 0.5f * Game.SCALE;
    private boolean inAir = false;
    //endregion

    //region Health
    private final int maxLives = 3;
    private int lives = maxLives;
    private int healthPercent = 0;
    //endregion

    //Damage + knockback
    private float knockbackVelX = 0;
    private float knockbackVelY = 0;
    private int knockbackFrames = 0;

    //region Attack Variables
    private ArrayList<BaseAttack> attacks;
    private int attackIndex;
    //endregion

    //Attack Hitbox
    private Rectangle2D.Float attackBox;

    private int flipX = 0;
    private int flipW = 1;
    private MouseEvent lastMouseEvent;

    private boolean attackChecked = false;
    private boolean dashActiveCheck = false;
    private boolean dashUsedCheck = false;
    private int dashTick;

    public Player(PlayerCharacter playerCharacter, Playing playing) {
        super(0, 50, (int)(playerCharacter.spriteWIDTH * Game.SCALE), (int)(playerCharacter.spriteHEIGHT * Game.SCALE) );
        this.playerCharacter = playerCharacter;
        this.playing = playing;
        loadAnimations();

        //attack initialization
        attacks = new ArrayList<>();
        for(AttackData data : playerCharacter.attackData){
            attacks.add(AttackFactory.createAttack(data));
        }
        System.out.println("attacks: " + attacks);
        initHitBox(playerCharacter.hitbox);
    }



    //region Updates
    public void update() {

        if(knockedOut){
            handleKnockout(spawnPoint);
            return;
        }
        updatePosition();

        if(moving){
            checkTrapCollision();
            if(dashActiveCheck){
                dashTick++;
                if(dashTick >= 25){
                    dashTick = 0;
                    dashActiveCheck = false;
                    dashUsedCheck = true;
                }
            }
        }
        if(attacking){
            checkAttack();
        }

        updateAnimationTick();
        setAnimation();
    }

    private void updateAnimationTick() {
        aniTick++;
        if(aniTick >= aniSpeed){
            aniTick = 0;
            aniIndex++;
            if(aniIndex >= playerCharacter.getSpriteAmount(playerAction)){
                aniIndex = 0;
                attacking=false;
                attackChecked = false;
            }
        }
    }

    private void updatePosition() {
        moving=false;

        if (knockbackFrames > 0) {
            // hitbox se krece sa knockback
            updateXPos(knockbackVelX);
            updateYPos(knockbackVelY);

            // uspori knockback za 10%
            knockbackVelX *= 0.9f;
            knockbackVelY *= 0.9f;

            knockbackFrames--;

            // preskače normalne kontrole dok se prima šteta + malo knockbackFrames
            return;
        }

        if(jump)
            jump();

        //provjera da li se pritišče gumb, da li je metoda potrebna
        if(!inAir){
            if(!dashActiveCheck)
                if((!left && !right) || (right && left)){
                    if(dashUsedCheck){dashUsedCheck = false;}
                    return;
                }
        }

        float xSpeed = 0;
        float ySpeed = 0;

        if(left){
            xSpeed -= playerSpeed;
            flipX = width;
            flipW = -1;
        }
        if (right){
            xSpeed += playerSpeed;
            flipX = 0;
            flipW = 1;
        }

        if(dashActiveCheck){
            //Ako Player ne pritišče gumb za usmjerenje uzima se flipW varijabla za određivanje smejra dasha/smjer gledanja spritea
            if(!left && !right){
                if(flipW == -1){
                    xSpeed = -playerSpeed;
                }else{
                    xSpeed = playerSpeed;
                }
            } else if(left && right){
                xSpeed = playerSpeed;
            }
            //Diagonalni dash
            if((!up && !down) || (up && down)){
                ySpeed = 0;
            }
            if(up){ySpeed -= playerSpeed;}
            if(down){ySpeed += playerSpeed;}

            ySpeed *= 3;
            xSpeed *= 3;

        }
        if(!inAir){
            if(!IsEntityOnFloor(hitBox, lvlData, this)) {
                inAir = true;
            }
            if(dashUsedCheck){
                dashUsedCheck = false;
            }
        }

        //!dashcheck zaustavlja kontrolu tijekom dasha
        if(inAir && !dashActiveCheck){
            if(CanMoveHere(hitBox.x, hitBox.y + airSpeed, hitBox.width, hitBox.height,lvlData, this)){
                hitBox.y += airSpeed;
                airSpeed += GRAVITY;
                updateXPos(xSpeed);
            }else{
                hitBox.y = GetEntityYPosRoofOrFloor(hitBox,airSpeed);
                if(airSpeed > 0){
                    resetInAir();
                }else{
                    airSpeed = fallSpeedAfterCollision;
                }
                updateXPos(xSpeed);
            }
        }else {
            if(dashActiveCheck){
                updateYPos(ySpeed);
            }
            updateXPos(xSpeed);
        }
        //Ako se metoda izvede, to jest nije prekinuta u prvom if statementu. Player se sigurno kreće
        moving = true;
    }

    private void updateXPos(float xSpeed) {
        if (CanMoveHere(hitBox.x + xSpeed, hitBox.y, hitBox.width, hitBox.height, lvlData,this)){
            hitBox.x += xSpeed;
        }else{
            hitBox.x = GetEntityXPosNextToWall(hitBox, xSpeed);
            if(dashActiveCheck){
                dashActiveCheck = false;
                dashTick = 0;
            }
        }
    }

    private void updateYPos(float ySpeed) {
        if (CanMoveHere(hitBox.x, hitBox.y + ySpeed, hitBox.width, hitBox.height, lvlData,this)){
            hitBox.y += ySpeed;
        }else{
            hitBox.y = GetEntityYPosRoofOrFloor(hitBox,ySpeed);
            if(dashActiveCheck){
                dashActiveCheck = false;
                dashTick = 0;
            }
        }
    }
    //endregion


    public void render(Graphics g){
        g.drawImage(animations[playerAction][aniIndex], (int)(hitBox.x - playerCharacter.xDrawOffset + flipX), (int)(hitBox.y - playerCharacter.yDrawOffset), width * flipW, height, null);
        drawHitbox(g);
    }

    private void checkTrapCollision() {playing.checkTrapCollision(this);}

    //region Attacks and damage
    private void checkAttack() {
        if(attackChecked || aniIndex != 1)return;
        attackChecked = true;

        if(attacks.isEmpty())return;
        BaseAttack current = attacks.get(attackIndex);
        current.execute(this);

        for(Player enemy : playing.getAllPlayers()){
            if(enemy == this)continue;
            if(current.getHitbox().intersects(enemy.getHitBox())){
                current.applyHit(this,enemy);
            }
        }

        playing.checkEnemyHit(attackBox);
        playing.checkObjectHit(attackBox);
    }

    public void addDamage(int value) {
        healthPercent += value;
        if(healthPercent >= 200){
            healthPercent = 200;
        }
    }

    public void knockBack(float attackDirX, float attackDirY, float attackPower){
        // attackDirX/Y: unit direction vector ( 1,0 za desno)
        // attackPower: umnozak snagom napada

        float damage = getHealthPercent();

        // osnovni knockback
        //BASE_KNOCKBACK;     // minimum pixels per frame
        //KNOCKBACK_SCALING;           // knockback per % damage

        float knockback = (BASE_KNOCKBACK + damage * KNOCKBACK_SCALING) * attackPower;

        // apply to velocity
        knockbackVelX = attackDirX * knockback;
        knockbackVelY = attackDirY * knockback;

        knockbackFrames = 20; // trajanje knockbacka (frames)
        inAir = true;         // player je u zraku
    }

    public void handleKnockout(Point spawnPoint) {
        loseLife();
        if (lives > 0) {
            // respawn
            setSpawn(spawnPoint);
            resetAll();        // resets velocity, damage %, states
        } else {
            // završava borba kada ostane bez života
            playing.checkMatchEnd();
        }
    }
    //endregion

    public void setSpawn(Point spawn){
        this.x = spawn.x;
        this.y = spawn.y;
        this.hitBox.x = x;
        this.hitBox.y = y;

        this.spawnPoint = spawn;
    }

    private void setAnimation() {
        int startAni = playerAction;

        if(moving)
            playerAction = RUNNING;
        else
            playerAction = IDLE;

        if(inAir){
            if(airSpeed < 0) {
                playerAction = JUMP;
            }else{
                playerAction = FALLING;
            }
        }

        if(dashActiveCheck){
            //zamjeni sa DASH
            playerAction = ATTACK_1;
            aniIndex = 1;
            aniTick = 0;
            return;
        }

        if(attacking){
//            switch (playerAction){
//             case:ATTACK_1: playerAction = ATTACK_2;
//             case:ATTACK_2: playerAction = ATTACK_3;
//             case IDLE, RUNNING, FALLING: playerAction = ATTACK_1;
//             }
            playerAction=ATTACK_1;
            //Prvi put se ulazi u animaciju, prije nije napadao -> "Brža" animacija
            if(startAni != ATTACK_1){
                aniIndex = 1;
                aniTick = 0;
            }
        }
        if(startAni != playerAction){
            resetAniTick();
        }
    }

    private void resetAniTick() {
        aniTick = 0;
        aniIndex = 0;
    }

    public void resetAll() {
        resetDirBooleans();
        inAir = false;
        attacking =false;
        moving = false;
        jump = false;
        knockedOut = false;
        playerAction = IDLE;
        healthPercent = 0;
        hitBox.x = x;
        hitBox.y = y;
        if(!IsEntityOnFloor(hitBox, lvlData, this)){
            inAir = true;
        }
    }

    private void resetInAir() {
        inAir = false;
        dashActiveCheck = false;
        airSpeed = 0;
    }

    private void loadAnimations() {
        BufferedImage img = LoadSave.GetSpriteAtlas(playerCharacter.playerAtlas);
        animations = new BufferedImage[playerCharacter.rowPA][playerCharacter.colPA];
        for(int i = 0; i < animations.length; i++)
            for(int j = 0; j < animations[i].length;j++ ){
                animations[i][j] = img.getSubimage(j*playerCharacter.spriteWIDTH, i*playerCharacter.spriteHEIGHT,
                        playerCharacter.spriteWIDTH, playerCharacter.spriteHEIGHT);
            }
    }

    public void loadLvlData(int[][] lvlData){
        this.lvlData = lvlData;
        if(!IsEntityOnFloor(hitBox, lvlData, this))
            inAir = true;
    }

    public void powerAttack(MouseEvent e) {
        projectileAttack = true;
        lastMouseEvent = e;
    }


    public void setHealth(int health){this.healthPercent = health;}
    public boolean isAttacking() {return attacking;}
    public void setAttacking(boolean attacking){this.attacking=attacking;}
    public void setPlayerAction(int playerAction){this.playerAction = playerAction;}
    public void setDashActiveCheck(boolean dashActiveCheck){this.dashActiveCheck = dashActiveCheck;}
    public Boolean getInvincibility() {return invincibility;}
    public void setInvincibility(Boolean invincibility) {this.invincibility = invincibility;}

    //region Movement
    public void resetDirBooleans(){
        left=false;
        right=false;
        up=false;
        down=false;
    }
    private void jump() {
        if(inAir) {
            return;
        }
        inAir = true;
        airSpeed = jumpSpeed;
    }
    public void dashMove(){
        if(dashActiveCheck){
            return;
        }
        if(!dashUsedCheck){
            dashActiveCheck = true;
        }

    }

    public boolean isUp() {return up;}
    public boolean isLeft() {return left;}
    public boolean isRight() {return right;}
    public boolean isDown() {return down;}
    public boolean isJump() {return jump;}
    public void setLeft(boolean left) {this.left = left;}
    public void setUp(boolean up) {this.up = up;}
    public void setDown(boolean down) {this.down = down;}
    public void setRight(boolean right) {this.right = right;}
    public void setAttackIndex(int index){this.aniIndex = index;}
    public void setJump(boolean jump) {this.jump = jump;}
    //endregion

    public boolean isInKnockback() {return knockbackFrames > 0;}
    public int getLives() {return lives;}
    public void loseLife() {lives--;}
    public void resetLives() {lives = maxLives;}
    public int getHealthPercent() {return healthPercent;}
    public boolean getProjectileAttack() {return projectileAttack;}
    public void setProjectileAttack(boolean projectileAttack) {this.projectileAttack = projectileAttack;}
    public int getAttackIndex(){return attackIndex;}
    public int getFlipW() {return flipW;}
    public MouseEvent getLastMouseEvent() {return lastMouseEvent;}
    public Playing getPlaying() {return playing;}
    public void setFlipW(int flipW) {this.flipW = flipW;}
    public PlayerCharacter getPlayerCharacter() { return playerCharacter; }
    public float getAirSpeed() {return airSpeed;}
    //region Network
    // Called by Server.applyClientInput() to set remote input safely
    public void setRemoteInputForOtherPlayer(boolean left, boolean right, boolean up, boolean down, boolean jump, boolean attack){
        // either convert to mask and call playing.setRemoteInputMask(mask)
    }
    //endregion
}
