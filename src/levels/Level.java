package levels;

import objects.GameContainer;
import objects.Spike;
import utils.HelpMethods;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

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
        createStageData();
        createContainers();
        createSpikes();
        calcPlayerSpawn();
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
}
