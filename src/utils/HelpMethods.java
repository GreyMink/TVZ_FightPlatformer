package utils;

import entities.Player;
import main.Game;
import objects.GameContainer;
import objects.Projectile;
import objects.Spike;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static utils.Constants.ObjectConstants.*;

public class HelpMethods {
    //region MovementRegion
    public static boolean CanMoveHere(float x, float y, float width, float height, int[][] lvlData, Player player){
        if(!IsSolid(x,y,lvlData, player))
            if(!IsSolid(x+width,y+height, lvlData, player))
                if(!IsSolid(x+width,y,lvlData, player))
                    return !IsSolid(x, y + height, lvlData, player);
        return false;
    }

    public static boolean IsSolid(float x, float y, int[][] lvlData, Player player){
        //Ako Player hitbox izađe iz ekrane (GAME_WIDTH, GAME_WIDTH) promjeni dead variablu.
        if(x + player.getHitBox().width < 0 || x >= Game.GAME_WIDTH){
            player.setKO(true);
            return false;
        }
        if(y + player.getHitBox().height < 0 || y - player.getHitBox().height >= Game.GAME_HEIGHT){
            player.setKO(true);
            return false;
        }
        float xIndex = x / Game.TILES_SIZE;
        float yIndex = y / Game.TILES_SIZE;

        //korekcija indexa da ostaju unutar lvlData arraya pri izlazu playera iz ekrana
        if(yIndex > lvlData.length){
            yIndex =  lvlData.length-1;
        }
        if(xIndex > lvlData[0].length){
            xIndex =  lvlData[0].length-1;
        }

        int value =lvlData[(int) yIndex][(int) xIndex];
        if(value >= 48 || value <= 0 || value != 11){
            return true;
        }
        else {
            return false;
        }
    }

    public static float GetEntityXPosNextToWall(Rectangle2D.Float hitBox,float xSpeed){
        int currentTile = (int)(hitBox.x / Game.TILES_SIZE);

        if(xSpeed > 0){
            //Desno
            int tileXPos = currentTile * Game.TILES_SIZE;
            int xoffset = (int)(Game.TILES_SIZE - hitBox.width);
            return tileXPos + xoffset - 1;
        }else{
            //Lijevo
            return currentTile * Game.TILES_SIZE;
        }

    }

    public static float GetEntityYPosRoofOrFloor(Rectangle2D.Float hitBox,float airSpeed){
        int currentTile = (int)(hitBox.y / Game.TILES_SIZE);
        if(airSpeed > 0){
            //Down - touching Floor
            int tileYPos = currentTile * Game.TILES_SIZE;
            int yoffset = (int)(Game.TILES_SIZE - hitBox.height);
            return tileYPos + yoffset - 1;
        }else{
            //Up
            return currentTile * Game.TILES_SIZE;
        }
    }

    public static boolean IsEntityOnFloor(Rectangle2D.Float hitBox,int[][] lvlData, Player player){
        //Provjera da li su točke ispod donje lijeve i donje desne točke objekti
        if(!IsSolid(hitBox.x, hitBox.y + hitBox.height + 1, lvlData, player)){
            return IsSolid(hitBox.x + hitBox.width, hitBox.y + hitBox.height + 1, lvlData, player);
        }
        return true;
    }

    public static boolean IsFloor(Rectangle2D.Float hitbox, float xSpeed, int[][] lvlData, Player player){
        return IsSolid(hitbox.x + xSpeed, hitbox.y, lvlData, player);
    }
    //endregion

    //region ProjectileMovementChecking
    public static boolean isProjectileHittingStage(Projectile p, int[][] lvlData){
        return IsSolid(p.getHitBox().x + p.getHitBox().width/2, p.getHitBox().y + p.getHitBox().height/2, lvlData);
    }

    public static boolean CanMoveHere(float x, float y, float width, float height, int[][] lvlData){
        if(!IsSolid(x,y,lvlData))
            if(!IsSolid(x+width,y+height, lvlData))
                if(!IsSolid(x+width,y,lvlData))
                    return !IsSolid(x, y + height, lvlData);
        return false;
    }

    public static boolean IsSolid(float x, float y, int[][] lvlData){
        int maxWidth = lvlData[0].length * Game.TILES_SIZE;
        if(x < 0 || x >= maxWidth){return true;}
        if(y < 0 || y >= Game.GAME_HEIGHT){return true;}
        float xIndex = x / Game.TILES_SIZE;
        float yIndex = y / Game.TILES_SIZE;

        return IsTileSolid((int) xIndex, (int)yIndex, lvlData);

    }

    private static boolean IsTileSolid(int xTile, int yTile, int[][] lvlData) {
        int value = lvlData[yTile][xTile];

        if (value >= 48 || value < 0 || value != 11)
            return true;
        return false;
    }
    //endregion

    public static int[][] GetLevelData(BufferedImage img){
        int[][] lvlData = new int[Game.TILES_IN_HEIGHT][Game.TILES_IN_WIDTH];

        for (int j = 0; j < img.getHeight();j++){
            for (int i = 0; i < img.getWidth();i++){
                Color color = new Color(img.getRGB(i,j));
                int value = color.getRed();
                if(value >= 48){ value = 0;}
                lvlData[j][i] = value; //index za sprite
            }
        }
        return lvlData;
    }

    //region Getters
    public static Point GetPlayerSpawn(BufferedImage img){
        for(int i =0; i < img.getHeight(); i++){
            for(int j = 0; j < img.getWidth(); j++) {
                Color color = new Color(img.getRGB(j, i));
                int value = color.getGreen();
                if (value == 100) {
                    return new Point(j * Game.TILES_SIZE, i * Game.TILES_SIZE);
                }
            }
        }
        return new Point(Game.TILES_SIZE, Game.TILES_SIZE);
    }
    public static Point GetRemotePlayerSpawn(BufferedImage img){
        for(int i =0; i < img.getHeight(); i++){
            for(int j = 0; j < img.getWidth(); j++) {
                Color color = new Color(img.getRGB(j, i));
                int value = color.getGreen();
                if (value == 101) {
                    return new Point(j * Game.TILES_SIZE, i * Game.TILES_SIZE);
                }
            }
        }
        return new Point(Game.TILES_SIZE, Game.TILES_SIZE);
    }

    public static ArrayList<GameContainer> GetContainers(BufferedImage img){
        ArrayList<GameContainer> list = new ArrayList<>();
        for(int i =0;i<img.getHeight();i++){
            for(int j = 0; j<img.getWidth();j++){
                Color color = new Color(img.getRGB(j, i));
                int value = color.getBlue();
                if(value == BOX || value == BARREL){
                    list.add(new GameContainer(j * Game.TILES_SIZE,i * Game.TILES_SIZE, value));
                }
            }
        }
        return list;
    }

    public static ArrayList<Spike> getSpikes(BufferedImage img) {
        ArrayList<Spike> list = new ArrayList<>();
        for(int i =0; i < img.getHeight(); i++){
            for(int j = 0; j < img.getWidth(); j++) {
                Color color = new Color(img.getRGB(j, i));
                int value = color.getBlue();
                if (value == SPIKE) {
                    list.add(new Spike(j * Game.TILES_SIZE,i * Game.TILES_SIZE, SPIKE));
                }
            }
        }
        return list;
    }
    //endregion
}
