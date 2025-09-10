package entities;

import gamestates.Playing;
import utils.LoadSave;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static utils.Constants.EnemyConstants.*;

public class EnemyManager {

    private Playing playing;
    private BufferedImage[][] enemyArray;
    private ArrayList<Enemy> enemyArrayList = new ArrayList<>();

    public EnemyManager(Playing playing){
        this.playing = playing;
//        loadEnemyImgs();
//        addEnemies();
    }

    private void loadEnemyImgs(){
//        enemyArray = new BufferedImage[5][9];
//        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.CRABBY_SPRITE);
//        for(int i = 0; i < enemyArray.length; i++){
//            for(int j = 0; j < enemyArray[i].length; j++){
//                enemyArray[i][j] = temp.getSubimage(j * ENEMY_WIDTH_DEFAULT, i * ENEMY_HEIGHT_DEFAULT, ENEMY_WIDTH_DEFAULT, ENEMY_HEIGHT_DEFAULT);
//            }
//        }
    }

    public void update(int[][] lvlData, Player player){
    }

    public void draw(Graphics g){

    }

    private void addEnemies(){
//        enemyArray = LoadSave.GetCrabs();
//        System.out.println("Size of enemy: " + enemyArray.size());
    }

    public void checkEnemyHit(Rectangle2D.Float attackBox){
//        for (Enemy e: enemyArrayList){
//            if(attackBox.intersects(e.getHitBox())){
//                e.hurt(10);
//                return;
//            }
//        }
    }

    public void resetAll(){

    }
}
