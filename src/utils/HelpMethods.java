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
        boolean ignorePlatforms = player.isInKnockback();
        if(!IsSolid(x,y,lvlData, player,ignorePlatforms))
            if(!IsSolid(x+width,y+height, lvlData, player,ignorePlatforms))
                if(!IsSolid(x+width,y,lvlData, player,ignorePlatforms))
                    return !IsSolid(x, y + height, lvlData, player,ignorePlatforms);
        return false;
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
        boolean left = IsFloor(hitBox.x, hitBox.y + hitBox.height + 1, lvlData, player);
        boolean right = IsFloor(hitBox.x + hitBox.width, hitBox.y + hitBox.height + 1, lvlData, player);
        return left || right;
    }

    public static boolean IsFloor(float x, float y, int[][] lvlData, Player player){
        int xIndex = (int)(x / Game.TILES_SIZE);
        int yIndex = (int)(y / Game.TILES_SIZE);

        xIndex = Math.max(0, Math.min(xIndex, lvlData[0].length - 1));
        yIndex = Math.max(0, Math.min(yIndex, lvlData.length - 1));

        int tile = lvlData[yIndex][xIndex];
        // prazan prostor
        if (tile == 11) return false;

        if (tile >= 48 || tile <= 0) return true;

        // uvijek zaustavlja odozgo za animacije hodanja
        return true;
    }


    // prolaz kroz 1 tile
    private static boolean isPlatformTile(int tile) {
        // every tile other than 11 (air) is a platform/floor tile
        return tile > 0 && tile < 48;
    }

    public static boolean IsSolid(float x, float y, int[][] lvlData, Player player, boolean ignorePlatforms) {
        // ko check
        if (x + player.getHitBox().width < 0 || x >= Game.GAME_WIDTH) {
            player.setKO(true);
            return false;
        }
        if (y + player.getHitBox().height < 0 || y - player.getHitBox().height >= Game.GAME_HEIGHT) {
            player.setKO(true);
            return false;
        }

        int xIndex = (int)(x / Game.TILES_SIZE);
        int yIndex = (int)(y / Game.TILES_SIZE);

        //korekcija indexa da ostaju unutar lvlData arraya pri izlazu playera iz ekrana
        xIndex = Math.max(0, Math.min(xIndex, lvlData[0].length - 1));
        yIndex = Math.max(0, Math.min(yIndex, lvlData.length - 1));

        int tile = lvlData[yIndex][xIndex];


        Rectangle2D.Float testRect = new Rectangle2D.Float(x, y, 1, 1);
        if (player.getPlaying().getObjectManager().isBlockedByActiveContainer(testRect)) {
            return true;
        }

        // prazan prostor
        if (tile == 11) return false;
        // normalan neprolazni tile
        if (tile >= 48 || tile <= 0) return true;

        // ako je igrač u knockbacku
        if (ignorePlatforms) {
            return false;
        }

        // Prolazni tile
        if (isPlatformTile(tile)) {

            // prolaz kroz
            float playerBottom = player.getHitBox().y + player.getHitBox().height;
            float tileTop = yIndex * Game.TILES_SIZE;

            //provjera ako je player ispod - može prolaziti
            if (playerBottom < tileTop - 1) return false;

            // provjeri da li su 2 tile-a ispod prohodna za igrača (dva radi veličina likova)
            if (yIndex + 1 < lvlData.length && yIndex + 2 < lvlData.length) {
                int below1 = lvlData[yIndex + 1][xIndex];
                int below2 = lvlData[yIndex + 2][xIndex];
                if (below1 == 11 && below2 == 11) {
                    // treba biti prohodan samo pri kretanju odozdo prema gore
                    if (player.getAirSpeed() > 0) {
                        return true;  // padne na tile
                    } else {
                        return false; // kretnja prema gore (skakanje)
                    }
                }
            }
        }
        // default: solid
        return true;
    }
    //endregion

    //region ProjectileMovementChecking
    public static boolean isProjectileHittingStage(Projectile p, int[][] lvlData){
        return IsSolid(p.getHitBox().x + p.getHitBox().width/2, p.getHitBox().y + p.getHitBox().height/2, lvlData);
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
