package objects;

import entities.Player;
import gamestates.Playing;
import levels.Level;
import utils.LoadSave;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static utils.Constants.ObjectConstants.*;
import static utils.Constants.Projectiles.*;
import static utils.HelpMethods.*;

public class ObjectManager {

    private Playing playing;
    private BufferedImage[][] containerImgs;
    private BufferedImage spikeImg, cannonBallImg;
    private ArrayList<GameContainer> containers;
    private ArrayList<Spike> spikes;
    private ArrayList<Projectile> projectiles = new ArrayList<>();

    public ObjectManager(Playing playing){
        this.playing = playing;
        loadImgs();
    }

    public void loadObjects(Level newStage) {
        containers = newStage.getContainers();
        spikes = newStage.getSpikes();
        projectiles.clear();
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

        cannonBallImg = LoadSave.GetSpriteAtlas(LoadSave.BALL_IMG);

    }


    public void update(int[][] lvlData, Player player){
        for(GameContainer gc: containers){
            if(gc.isActive()){
                gc.update();
            }
        }
        updateProjectiles(lvlData, player);
        if(player.getProjectileAttack()){
            shootProjectile(player);
            player.setProjectileAttack(false);
        }
    }

    private void updateProjectiles(int[][] lvlData, Player player) {
        for(Projectile p : projectiles){
                if(p.isActive()){
                    p.updatePosition();
                }
                if(isProjectileHittingStage(p, lvlData)){
                    p.setActive(false);
                }
        }
    }

    public void draw(Graphics g){
        drawContainer(g);
        drawTraps(g);
        drawProjectiles(g);
    }

    private void drawProjectiles(Graphics g) {
        for(Projectile p : projectiles){
            if(p.isActive())
                g.drawImage(cannonBallImg, (int) p.getHitBox().x, (int) p.getHitBox().y, CANNONBALL_WIDTH, CANNONBALL_HEIGHT, null);
        }
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

    public boolean isBlockedByActiveContainer(Rectangle2D.Float area) {
        if (containers == null) return false;

        for (GameContainer gc : containers) {
            if (gc.isActive() && gc.getHitBox().intersects(area)) {
                return true;
            }
        }
        return false;
    }

    public void checkTrapCollision(Player player){
        for(Spike s : spikes){
            if(s.getHitBox().intersects(player.getHitBox()) && !player.getInvincibility()){
                player.addDamage(10);
                player.knockBack(s.getHitBox().x < player.getHitBox().x ? 1f : -1f, -1, 2);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        player.setInvincibility(true);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        player.setInvincibility(false);
                    }
                }).start();
            }
        }
    }
    public void checkProjectileCollision(Player player){
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


    private void shootProjectile(Player player){
        int directionX = player.getFlipW();

        projectiles.add(new Projectile((int)player.getHitBox().x, (int)player.getHitBox().y, player.getLastMouseEvent().getX(),player.getLastMouseEvent().getY()));
    }
    public ArrayList<GameContainer> getContainers() {return containers;}
}
