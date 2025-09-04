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
    private int aniTick, aniIndex, aniSpeed = 15;
    private int playerAction = IDLE;
    private boolean moving = false;
    private boolean attacking = false;
    private boolean projectileAttack = false;

    private boolean left, up, right, down, jump;
    private float playerSpeed = Game.SCALE;
    private int[][] lvlData;
//    private float xDrawOffset = 21 * Game.SCALE;
//    private float yDrawOffset = 4 * Game.SCALE;

    private final PlayerCharacter playerCharacter;
    private Playing playing;
    private Boolean invincibility = false;
    //endregion

    //region Gravity
    private float airSpeed = 0f;
    private float jumpSpeed = -3.0f * Game.SCALE;
    private float fallSpeedAfterCollision = 0.5f * Game.SCALE;
    private boolean inAir = false;
    //endregion

    //region Health Bar
    private BufferedImage statusBarImg;
    //max lives in match
    private int lives = 3;
    private int healthPercent = 0;

    private int statusBarWidth = (int)(192 * Game.SCALE);
    private int statusBarHeight = (int)(58 * Game.SCALE);
    private int statusBarX = (int)(10 * Game.SCALE);
    private int statusBarY = (int)(10 * Game.SCALE);

    private int healthBarWidth = (int)(150 * Game.SCALE);
    private int healthBarHeight = (int)(4 * Game.SCALE);
    private int healthBarXStart = (int)(34 * Game.SCALE);
    private int healthBarYStart = (int)(14 * Game.SCALE);

    private int maxHealth = 100;
    private int currentHealth = maxHealth;
    private int healthWidth = healthBarWidth;
    //endregion

    //region Attack Variables
    private ArrayList<BaseAttack> attacks;
    private BaseAttack currentAttack;
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
        super(0, 0, (int)(playerCharacter.spriteWIDTH * Game.SCALE), (int)(playerCharacter.spriteHEIGHT * Game.SCALE) );
        this.playerCharacter = playerCharacter;
        this.playing = playing;
        loadAnimations();

        //attack initialization
        attacks = new ArrayList<>();
//        attacks.add(new BasicAttack());
//        attacks.add(new PowerAttack());

        //Zamjeni status bar image
        statusBarImg = LoadSave.GetSpriteAtlas(LoadSave.STATUS_BAR);
        initHitBox((int)playerCharacter.hitbox.width,(int)playerCharacter.hitbox.height);
        initAttackBox();
    }

    public void setSpawn(Point spawn){
        this.x = spawn.x;
        this.y = spawn.y;
        hitBox.x = x;
        hitBox.y = y;
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x,y,(int)(20 * Game.SCALE), (int)(20 * Game.SCALE));
    }

    private void checkTrapCollision() {
        playing.checkTrapCollision(this);
    }

    private void checkAttack() {
        if(attackChecked || aniIndex != 1){
            return;
        }
        attackChecked = true;
        playing.checkEnemyHit(attackBox);
        playing.checkObjectHit(attackBox);
    }

    //region Updates
    public void update() {
        updateHealthBar();

        if(knockedOut){
            playing.setGameOver(true);
            return;
        }
        updateAttackBox();
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

    private void updateAttackBox() {
        if(right || dashActiveCheck && flipW == 1){
            attackBox.x = hitBox.x + hitBox.width + (int)(10 * Game.SCALE);
        }else if(left || dashActiveCheck && flipW == -1){
            attackBox.x = hitBox.x - hitBox.width - (int)(10 * Game.SCALE);
        }
        attackBox.y = hitBox.y + (10 * Game.SCALE);
    }

    private void updateHealthBar() {
        healthWidth = (int)((currentHealth / (float) maxHealth) * healthBarWidth);
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
        drawAttackBox(g);
        drawUI(g);
    }

    private void drawAttackBox(Graphics g) {
        g.setColor(Color.RED);
        g.drawRect((int)attackBox.x,(int)attackBox.y,(int)attackBox.width,(int)attackBox.height);
    }

    private void drawUI(Graphics g) {
        g.drawRect(statusBarX,statusBarY,statusBarWidth/2,statusBarHeight/2);

        //Proxy
        //g.drawImage(statusBarImg,statusBarX,statusBarY,statusBarWidth,statusBarHeight,null);
        //g.setColor(Color.RED);
        //g.fillRect(healthBarXStart + statusBarX, healthBarYStart + statusBarY, healthWidth, healthBarHeight);

        //Fonts
        g.setFont(g.getFont().deriveFont(30f));
        g.drawString(healthPercent + "%",statusBarX + statusBarWidth/4,statusBarY + statusBarHeight/4);
        //g.dispose();
    }

    public void changeHealth(int value) {

        //region Standard Health
        currentHealth += value;

        if(currentHealth <= 0){
            currentHealth = 0;
            //gameOver();
        }else if (currentHealth >= maxHealth){
            currentHealth = maxHealth;
        }
        //endregion

        healthPercent -= value;
        if(healthPercent >= 200){
            healthPercent = 200;
        }

    }

    public void knockBack(int damage){

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
            /*TODO:kasnije implementiraj switch case za combo attack: ATTACK_1 -> ATTACK_2 itd.
            modificiraj za ostale vrste napada (AirDown, AirUp itd.)
            switch (playerAction){
             case:ATTACK_1: playerAction = ATTACK_2;
             case:ATTACK_2: playerAction = ATTACK_3;
             case IDLE, RUNNING, FALLING: playerAction = ATTACK_1;
             }*/
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
        currentHealth = maxHealth;
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
        //9 mogućih animacija, najviše stanja u jednoj animaciji je 6
        animations = new BufferedImage[playerCharacter.rowPA][playerCharacter.colPA];
        for(int i = 0; i < animations.length; i++)
            for(int j = 0; j < animations[i].length;j++ ){
                animations[i][j] = img.getSubimage(j*playerCharacter.spriteWIDTH, i*playerCharacter.spriteHEIGHT, playerCharacter.spriteWIDTH, playerCharacter.spriteHEIGHT);
            }


    }

    public void loadLvlData(int[][] lvlData){
        this.lvlData = lvlData;
        if(!IsEntityOnFloor(hitBox, lvlData, this))
            inAir = true;
    }

    public void setAttacking(boolean attacking){
        this.attacking=attacking;
    }

    public void setPlayerAction(int playerAction){
        this.playerAction = playerAction;
    }

    public void setDashActiveCheck(boolean dashActiveCheck){
        this.dashActiveCheck = dashActiveCheck;
    }

    public Boolean getInvincibility() {return invincibility;}

    public void setInvincibility(Boolean invincibility) {
        this.invincibility = invincibility;
    }

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

    public boolean isUp() {
        return up;
    }

    public boolean isLeft() {
        return left;
    }

    public boolean isRight() {
        return right;
    }

    public boolean isDown() {
        return down;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public void setJump(boolean jump) {this.jump = jump;}
    //endregion

    public void powerAttack(MouseEvent e) {
            projectileAttack = true;
            lastMouseEvent = e;
        }

    public boolean getProjectileAttack() {
        return projectileAttack;
    }

    public void setProjectileAttack(boolean projectileAttack) {
        this.projectileAttack = projectileAttack;
    }
    public int getFlipW() {
        return flipW;
    }
    public MouseEvent getLastMouseEvent() {
        return lastMouseEvent;
    }

    public Playing getPlaying() {
        return playing;
    }
}
