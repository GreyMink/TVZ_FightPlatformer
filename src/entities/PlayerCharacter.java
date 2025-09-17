package entities;

import entities.Attacks.AttackData;
import entities.Attacks.AttackType;
import main.Game;

import java.awt.geom.Rectangle2D;

import static utils.Constants.PlayerConstants.*;
import static utils.LoadSave.*;

public enum PlayerCharacter {

    PIRATE(5,6, 3, 1,3,4,8,
            0,1,2,3,4,5,6,
            PLAYER_PIRATE, 7,8,64,40,
            new Rectangle2D.Float(0,0,20,30),
            21,13,
            PIRATE_SELECT,
            new AttackData[]{
                    new AttackData("Pirate Slash",AttackType.BASIC, 6, 3, 0, 2, 0, new Rectangle2D.Float(0,0,30,30), 10, 0),
                    new AttackData("Pirate Power Slash",AttackType.POWER, 12, 6, 15, 12, 25, new Rectangle2D.Float(0,0,10,10), 10, 0)
            }),
    KNIGHT(7,8,3,1,6,4,12,
            0,1,2,3,4,6,7,
            PLAYER_KNIGHT, 8, 12, 64,64,
            new Rectangle2D.Float(0,0,30,45),
            22,30,
            KNIGHT_SELECT,
            new AttackData[]{
                    new AttackData("Knight Slash", AttackType.BASIC, 5, 2,4, 1,0, new Rectangle2D.Float(0,0,40,40),40,0),
                    new AttackData("Knight Power Slash", AttackType.POWER, 15, 10, 2, 1, 1, new Rectangle2D.Float(0,0, 50,30), 30,0)
            }),
    WEREWOLF(8,9,6,1,6,2,2,
            0,1,2,3,4,6,7,
            PLAYER_WEREWOLF, 8,9,64,64,
            new Rectangle2D.Float(0,0,32,32),
            18,40,
            WEREWOLF_SELECT,
            new AttackData[]{
                    new AttackData("Werewolf slash", AttackType.BASIC,8,3,3,2,1, new Rectangle2D.Float(0,0,50,50),60,50),
                    new AttackData("Werewolf lunge", AttackType.POWER, 20,15,2,2,2, new Rectangle2D.Float(0,0,50,50),60,50)
            }),
    SATYR(7,12,3,1,4,4,4,
            0,1,2,3,4,6,7,
            PLAYER_SATYR, 8,12, 64, 64,
            new Rectangle2D.Float(0,0,15,38),
            27,38,
            SATYR_SELECT,
            new AttackData[]{
                    new AttackData("Magic Missile", AttackType.POWER, 4, 1, 1,1,1, new Rectangle2D.Float(0,0,50,50),60,50),
                    new AttackData("Fire Ball", AttackType.POWER, 15, 5, 2, 1,1, new Rectangle2D.Float(0,0,50,50),60,50)
            });

    final int spriteA_IDLE, spriteA_RUNNING, spriteA_JUMP, spriteA_FALLING, spriteA_ATTACK, spriteA_HIT, spriteA_DEAD /*,spriteA_DASH*/;
    final int rowIDLE,rowRUNNING, rowJUMP, rowFALLING, rowATTACK, rowHIT, rowDEAD/*, rowDASH*/;
    final String playerAtlas;
    final int rowPA, colPA, spriteWIDTH, spriteHEIGHT;
    final Rectangle2D.Float hitbox;
    final int xDrawOffset, yDrawOffset;
    int playerIndex;
    final String select_image;
    AttackData[] attackData;

    //Potencijalno izbaciti row varijable ako karakteri budu imali standardizirane stateove
    PlayerCharacter(int spriteA_IDLE, int spriteA_RUNNING, int spriteA_JUMP, int spriteA_FALLING, int spriteA_ATTACK, int spriteA_HIT, int spriteA_DEAD,
                    int rowIDLE, int rowRUNNING, int rowJUMP, int rowFALLING, int rowATTACK, int rowHIT, int rowDEAD,
                    String playerAtlas, int rowPA, int colPA, int spriteWIDTH, int spriteHEIGHT,
                    Rectangle2D.Float hitbox,
                    int xDrawOffset, int yDrawOffset,
                    String select_image,
                    AttackData[] data) {
        this.spriteA_IDLE = spriteA_IDLE;
        this.spriteA_RUNNING = spriteA_RUNNING;
        this.spriteA_JUMP = spriteA_JUMP;
        this.spriteA_FALLING = spriteA_FALLING;
        this.spriteA_ATTACK = spriteA_ATTACK;
        this.spriteA_HIT = spriteA_HIT;
        this.spriteA_DEAD = spriteA_DEAD;

        this.rowIDLE = rowIDLE;
        this.rowRUNNING = rowRUNNING;
        this.rowJUMP = rowJUMP;
        this.rowFALLING = rowFALLING;
        this.rowATTACK = rowATTACK;
        this.rowHIT = rowHIT;
        this.rowDEAD = rowDEAD;

        this.playerAtlas = playerAtlas;
        this.rowPA = rowPA;
        this.colPA = colPA;
        this.spriteWIDTH = spriteWIDTH;
        this.spriteHEIGHT = spriteHEIGHT;

        this.hitbox = hitbox;

        this.xDrawOffset = (int)(xDrawOffset * Game.SCALE);
        this.yDrawOffset = (int)(yDrawOffset * Game.SCALE);

        this.select_image = select_image;
        this.attackData = data;
    }

    public int getSpriteAmount(int playerAction){
        return switch (playerAction) {
            case IDLE -> spriteA_IDLE;
            case RUNNING -> spriteA_RUNNING;
            case JUMP -> spriteA_JUMP;
            case FALLING -> spriteA_FALLING;
            case ATTACK_1 -> spriteA_ATTACK;
            case HIT -> spriteA_HIT;
//            case DASH -> spriteA_DASH;
            case DEAD -> spriteA_DEAD;
            default -> throw new IllegalStateException("Unexpected value: " + playerAction);
        };
    }

    public int getRowIndex(int playerAction){
        return switch (playerAction) {
            case IDLE -> rowIDLE;
            case RUNNING -> rowRUNNING;
            case JUMP -> rowJUMP;
            case FALLING -> rowFALLING;
            case ATTACK_1 -> rowATTACK;
            case HIT -> rowHIT;
//            case DASH -> rowDASH;
            case DEAD -> rowDEAD;
            default -> throw new IllegalStateException("Unexpected value: " + playerAction);
        };
    }

    public String getSelectImage() {
        return select_image;
    }
    public AttackData[] getAttackData() {
        return attackData;
    }
}
