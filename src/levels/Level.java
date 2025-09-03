package levels;

import main.Game;
import objects.GameContainer;
import objects.Spike;
import utils.HelpMethods;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static utils.Constants.ObjectConstants.*;
import static utils.HelpMethods.GetLevelData;
import static utils.HelpMethods.GetPlayerSpawn;

public class Level {
    private BufferedImage img;
    private int[][] lvlData;

    private ArrayList<GameContainer> containers;
    private ArrayList<Spike> spikes;

    private Point playerSpawn;

    public Level(BufferedImage img){

        this.img = img;
        lvlData = new int[img.getHeight()][img.getWidth()];
        loadLevel();

        createStageData();
        createContainers();
        createSpikes();
        calcPlayerSpawn();
    }

    private void loadLevel(){
        for(int y = 0; y < img.getHeight(); y++){
            for(int x = 0; x < img.getWidth(); x++){
                Color c = new Color(img.getRGB(x, y));
                int red = c.getRed();
                int green = c.getGreen();
                int blue = c.getBlue();

                loadLevelData(red, x, y);
                loadEntities(green, x, y);
                loadEntities(blue, x, y);
            }
        }

    }
    private void loadLevelData(int redValue, int x, int y) {
        if(redValue >= 50)
            lvlData[y][x] = 0;
        else
            lvlData[y][x] = redValue;
    }

    private void loadEntities(int greenValue, int x, int y) {
        switch(greenValue){
            case 100 -> playerSpawn = new Point(x * Game.TILES_SIZE, y * Game.TILES_SIZE);
        }
//        if(greenValue == 100)
//            playerSpawn = new Point(x * Game.TILES_SIZE, y * Game.TILES_SIZE);
    }
    private void loadObjects(int blueValue, int x, int y) {
        switch(blueValue){
            case BOX, BARREL -> containers.add(new GameContainer(x * Game.TILES_SIZE, y * Game.TILES_SIZE, blueValue));
            case SPIKE -> spikes.add(new Spike(x * Game.TILES_SIZE, y * Game.TILES_SIZE, blueValue));
        }
    }



    //Create
    public void calcPlayerSpawn(){playerSpawn = GetPlayerSpawn(img);}
    private void createStageData() {lvlData = GetLevelData(img);}
    private void createSpikes(){spikes = HelpMethods.getSpikes(img);}
    private void createContainers() {containers = HelpMethods.GetContainers(img);}

    //Getter
    public ArrayList<GameContainer> getContainers(){return containers;}
    public ArrayList<Spike> getSpikes(){return spikes;}
    public int getSpriteIndex(int x, int y){
        return  lvlData[y][x];
    }
    public int[][] getLvlData(){
        return lvlData;
    }
    public Point getPlayerSpawn(){return playerSpawn;}
    public BufferedImage getSelectImage() {return img;}
}
