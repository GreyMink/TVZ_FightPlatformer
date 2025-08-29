package objects;

import entities.Player;
import gamestates.Gamestate;
import gamestates.Playing;
import levels.Level;
import utils.LoadSave;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static utils.Constants.ObjectConstants.*;

public class ObjectManager {

    private Playing playing;
    private BufferedImage[][] containerImgs;
    private BufferedImage spikeImg;
    private ArrayList<GameContainer> containers;
    private ArrayList<Spike> spikes;

    public ObjectManager(Playing playing){
        this.playing = playing;
        loadImgs();
    }

    public void loadObjects(Level newStage) {
        containers = newStage.getContainers();
        spikes = newStage.getSpikes();
    }

    private void loadImgs() {
        BufferedImage containerSprite = LoadSave.GetSpriteAtlas(LoadSave.CONTAINERS_ATLAS);
        containerImgs = new BufferedImage[2][8];

        for(int i = 0; i < containerImgs.length; i++){
            for(int j = 0; j < containerImgs[i].length; j++){
                containerImgs[i][j] = containerSprite.getSubimage(40 * j, 30 * i,40,30);
            }
        }

        spikeImg = LoadSave.GetSpriteAtlas(LoadSave.TRAP_ATLAS);
    }

    public void checkTrapCollision(Player player){
        for(Spike s : spikes){
            if(s.getHitBox().intersects(player.getHitBox())){
                player.changeHealth(-10);
            }
        }
    }
    public void checkObjectCollision(Rectangle2D.Float hitBox){
    }
    public void checkObjectHit(Rectangle2D.Float attackBox){
        for (GameContainer gc : containers){
            if(gc.isActive()){
                if(gc.getHitBox().intersects(attackBox)){
                    gc.setAnimation(true);
                }
            }
        }
    }


    public void update(){
        for(GameContainer gc: containers){
            if(gc.isActive()){
                gc.update();
            }
        }
    }

    public void draw(Graphics g){
        drawContainer(g);
        drawTraps(g);
    }

    private void drawTraps(Graphics g) {
        for(Spike s : spikes){
            g.drawImage(spikeImg,(int) s.getHitBox().x,(int) (s.getHitBox().y - s.getyDrawOffset()), SPIKE_WIDTH, SPIKE_HEIGHT, null);
        }
    }

    private void drawContainer(Graphics g) {
        for(GameContainer gc: containers){
            if(gc.isActive()){
                int type = 0;
                if(gc.getObjType() == BARREL){
                    type = 1;
                }
                g.drawImage(containerImgs[type][gc.getAniIndex()],
                        (int) gc.getHitBox().x - gc.getxDrawOffset(),
                        (int) gc.getHitBox().y - gc.getyDrawOffset(),
                        CONTAINER_WIDTH,
                        CONTAINER_HEIGHT,
                        null);
            }
        }
    }


    public void resetAllObjects() {
        for(GameContainer gc: containers){
            gc.reset();
        }
    }
}
