package entities;

import gamestates.Playing;
import main.Game;
import utils.LoadSave;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;

import static utils.Constants.GRAVITY;
import static utils.Constants.ImageConstants.*;
import static utils.Constants.PlayerConstants.*;
import static utils.HelpMethods.*;

public class Player extends Entity{

    //region Variable
    private BufferedImage[][] animations;
    private int aniTick, aniIndex, aniSpeed = 15;
    private int playerAction = IDLE;
    private boolean moving = false, attacking =false;
    private boolean left, up, right, down, jump;
    private float playerSpeed = Game.SCALE;
    private int[][] lvlData;
    private float xDrawOffset = 21 * Game.SCALE;
    private float yDrawOffset = 4 * Game.SCALE;

    private AtomicBoolean invincibility = new AtomicBoolean(false);
    //endregion

    //region Gravity
    private float airSpeed = 0f;
    private float jumpSpeed = -3.0f * Game.SCALE;
    private float fallSpeedAfterCollision = 0.5f * Game.SCALE;
    private boolean inAir = false;
    //endregion

    //region Health Bar
    private BufferedImage statusBarImg;
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

    //Attack Hitbox
    private Rectangle2D.Float attackBox;

    private int flipX = 0;
    private int flipW = 1;

    private boolean attackChecked = false;
    private boolean dashActiveCheck = false;
    private boolean dashUsedCheck = false;
    private int dashTick;

    private Playing playing;


    public Player(float x, float y, int width, int height, Playing playing) {
        super(x, y, width, height);
        this.playing = playing;
        loadAnimations();
        initHitBox(x,y,20,27);
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
            if(aniIndex >= getSpriteAmount(playerAction)){
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

    public void render(Graphics g){

        g.drawImage(animations[playerAction][aniIndex], (int)(hitBox.x - xDrawOffset + flipX), (int)(hitBox.y - yDrawOffset), width * flipW, height, null);
        //drawHitbox(g);
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
        g.drawString("%",statusBarX ,statusBarY );
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

    private void jump() {
        if(inAir) {
            return;
        }
        inAir = true;
        airSpeed = jumpSpeed;
    }

    private void resetInAir() {
        inAir = false;
        dashActiveCheck = false;
        airSpeed = 0;
    }


    private void loadAnimations() {

        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);
        //9 mogućih animacija, najviše stanja u jednoj animaciji je 6
        animations = new BufferedImage[7][8];
        for(int i = 0; i < animations.length; i++)
            for(int j = 0; j < animations[i].length;j++ ){
                animations[i][j] = img.getSubimage(j*PLAYER_WIDTH, i*PLAYER_HEIGHT, PLAYER_WIDTH, PLAYER_HEIGHT);
            }
        statusBarImg = LoadSave.GetSpriteAtlas(LoadSave.STATUS_BAR);

    }

    public void loadLvlData(int[][] lvlData){
        this.lvlData = lvlData;
        if(!IsEntityOnFloor(hitBox, lvlData, this))
            inAir = true;
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
        hitBox.x = x;
        hitBox.y = y;
        if(!IsEntityOnFloor(hitBox, lvlData, this)){
            inAir = true;
        }
    }

    public void dashMove(){
        if(dashActiveCheck){
            return;
        }
        if(!dashUsedCheck){
            dashActiveCheck = true;
        }

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

    //region Movement
    public void resetDirBooleans(){
        left=false;
        right=false;
        up=false;
        down=false;
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
}
