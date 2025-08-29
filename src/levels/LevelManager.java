package levels;

import gamestates.Gamestate;
import main.Game;
import utils.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static main.Game.TILES_SIZE;

public class LevelManager {

    private Game game;
    private BufferedImage[] levelSprite;
    private ArrayList<Level> stages;
    private int stageIndex = 0;

    public LevelManager(Game game){
        this.game = game;
        importOutsideSprites();
        stages = new ArrayList<>();
        buildAllStages();
    }

    private void buildAllStages() {
        BufferedImage[] allStages = LoadSave.getAllStages();
        for(BufferedImage img : allStages){
            stages.add(new Level(img));
        }
    }

    private void importOutsideSprites() {
        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);
        levelSprite = new BufferedImage[48]; //12 width * 4 height
        for (int j = 0; j < 4; j++){
            for(int i = 0; i < 12; i++){
                int index = j*12 + i;
                levelSprite[index] = img.getSubimage(i * 32, j * 32, 32, 32);
            }
        }
    }

    public void draw(Graphics g){

        for(int j = 0; j < Game.TILES_IN_HEIGHT; j++){
            for (int i = 0; i < Game.TILES_IN_WIDTH; i++){
                int index = stages.get(stageIndex).getSpriteIndex(i, j);
                g.drawImage(levelSprite[index], TILES_SIZE*i,TILES_SIZE*j,TILES_SIZE, TILES_SIZE,null);
            }
        }
    }

    public void update(){

    }

    public Level getCurrentLevel(){
        return stages.get(stageIndex);
    }

    public void loadNextLevel(){
        stageIndex++;
        if(stageIndex>=stages.size()){
            stageIndex =0;
            Gamestate.state = Gamestate.MENU;
        }

        Level newStage = stages.get(stageIndex);
        game.getPlaying().getPlayer().loadLvlData(newStage.getLvlData());
        game.getPlaying().getObjectManager().loadObjects(newStage);
    }
}
