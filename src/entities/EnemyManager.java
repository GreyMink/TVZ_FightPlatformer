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
    private BufferedImage[][] crabbyArr;
    private ArrayList<Crabby> crabbyArmy = new ArrayList<>();

    public EnemyManager(Playing playing){
        this.playing = playing;
        loadEnemyImgs();
        addEnemies();
    }

    private void loadEnemyImgs(){
        crabbyArr = new BufferedImage[5][9];
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.CRABBY_SPRITE);
        for(int i = 0; i < crabbyArr.length; i++){
            for(int j = 0; j < crabbyArr[i].length; j++){
                crabbyArr[i][j] = temp.getSubimage(j * CRABBY_WIDTH_DEFAULT, i * CRABBY_HEIGHT_DEFAULT, CRABBY_WIDTH_DEFAULT, CRABBY_HEIGHT_DEFAULT);
            }
        }
    }

    public void update(int[][] lvlData, Player player){
        for (Crabby c: crabbyArmy){
            if(c.isActive()){
                c.update(lvlData, player);
            }

        }
    }

    public void draw(Graphics g){
        drawCrabs(g);
    }

    private void drawCrabs(Graphics g) {
        for (Crabby c: crabbyArmy){
            g.drawImage(crabbyArr[c.getEnemyState()][c.getAniIndex()], (int)c.getHitBox().x - CRABBY_DRAWOFFSET_X, (int)c.getHitBox().y - CRABBY_DRAWOFFSET_Y, CRABBY_WIDTH, CRABBY_HEIGHT, null );

        }
    }

    private void addEnemies(){
        crabbyArmy = LoadSave.GetCrabs();
        System.out.println("Size of crabs: " + crabbyArmy.size());
    }

    public void checkEnemyHit(Rectangle2D.Float attackBox){
        for (Crabby c: crabbyArmy){
            if(attackBox.intersects(c.getHitBox())){
                c.hurt(10);
                return;
            }
        }
    }

    public void resetAll(){
        for (Crabby c : crabbyArmy){
            c.resetEnemy();
        }
    }
}
