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
            new Rectangle2D.Float(0,0,20,27),
            21,4,
            PIRATE_SELECT,
            new AttackData[]{
                    new AttackData("Pirate Slash",AttackType.BASIC, 6, 3, 5, 12, 15, new Rectangle2D.Float(0,0,10,10), 5, 0),
                    new AttackData("Pirate Power Slash",AttackType.POWER, 12, 6, 15, 12, 25, new Rectangle2D.Float(0,0,10,10), 10, 0)
            }),
    SOLDIER(6,8, 8, 8,6,4,4,
            0,1,1,1,2,5,6,
            PLAYER_SOLDIER,7,8,100,100,
            new Rectangle2D.Float(0,0,12,18),
            44,37,
            SOLDIER_SELECT,
            new AttackData[]{
                    new AttackData("Soldier Stab", AttackType.BASIC, 5, 2, 4, 10, 12, new Rectangle2D.Float(0,0,10,10),  3, 0),
                    new AttackData("Soldier Power Shot",AttackType.POWER, 14, 7, 20, 10, 30, new Rectangle2D.Float(0,0,10,10),  10, 0)
            });

    final int spriteA_IDLE, spriteA_RUNNING, spriteA_JUMP, spriteA_FALLING, spriteA_ATTACK, spriteA_HIT, spriteA_DEAD /*,spriteA_DASH*/;
    final int rowIDLE,rowRUNNING, rowJUMP, rowFALLING, rowATTACK, rowHIT, rowDEAD/*, rowDASH*/;
    final String playerAtlas;
    final int rowPA, colPA, spriteWIDTH, spriteHEIGHT;
//    int hitboxWIDTH, hitboxHEIGHT;
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
