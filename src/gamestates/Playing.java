package gamestates;

import entities.Player;
import entities.PlayerCharacter;
import levels.LevelManager;
import main.Game;
import objects.ObjectManager;
import ui.GameOverOverlay;
import ui.MatchFinishedOverlay;
import ui.PauseOverlay;
import utils.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Playing extends State implements Statemethods{
    private Player player;
    private LevelManager levelManager;
    private ObjectManager objectManager;

    private PauseOverlay pauseOverlay;
    private GameOverOverlay gameOverOverlay;
    private MatchFinishedOverlay matchFinishedOverlay;

    private BufferedImage backgroundImg;

    private boolean paused = false;
    private boolean gameOver = false;
    private boolean matchEnd = false;

    public Playing(Game game) {
        super(game);
        initClasses();

        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.TEMPLE_STAGE_BACKGROUND);
        loadStageObjects();
    }

    public void loadNextStage(){

        levelManager.loadNextStage();
        player.setSpawn(levelManager.getCurrentStage().getPlayerSpawn());
        resetAll();
    }

    private void loadStageObjects() {
        objectManager.loadObjects(levelManager.getCurrentStage());
    }


    private void initClasses() {
        levelManager = new LevelManager(game);
        objectManager = new ObjectManager(this);

        //Default character
        //setPlayerCharacter(PlayerCharacter.PIRATE);

        pauseOverlay = new PauseOverlay(this);
        gameOverOverlay = new GameOverOverlay(this);
        matchFinishedOverlay = new MatchFinishedOverlay(this);
    }

    public void setPlayerCharacter(PlayerCharacter playerCharacter) {
        player = new Player(playerCharacter, this);
        player.loadLvlData(levelManager.getCurrentStage().getLvlData());
        player.setSpawn(levelManager.getCurrentStage().getPlayerSpawn());
    }

    @Override
    public void update() {
        if(paused){
            pauseOverlay.update();
        }else if(matchEnd){
            matchFinishedOverlay.update();
        }else if(!gameOver){
            levelManager.update();
            player.update();
            objectManager.update(levelManager.getCurrentStage().getLvlData(), player);
        }
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT,null);

        levelManager.draw(g);
        player.render(g);
        objectManager.draw(g);

        if(paused){
            pauseOverlay.draw(g);
        }else if(gameOver){
            gameOverOverlay.draw(g);
        }else if(matchEnd){
            matchFinishedOverlay.draw(g);
        }
    }

    public void resetAll(){
        gameOver = false;
        paused = false;
        matchEnd = false;
        player.resetAll();
        objectManager.resetAllObjects();
    }

    public void setGameOver(boolean gameOver){this.gameOver = gameOver;}

    public void setMatchEnd(boolean matchEnd){
        this.matchEnd = matchEnd;
    }

    public void checkEnemyHit(Rectangle2D.Float attackBox){
        //enemyManager.checkEnemyHit(attackBox);
    }

    //region Inputs
    @Override
    public void mouseClicked(MouseEvent e) {
        if(!gameOver){
            if(e.getButton() == MouseEvent.BUTTON1){
                player.setAttacking(true);
            }else if(e.getButton() == MouseEvent.BUTTON3){
                player.powerAttack(e);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(!gameOver){
            if(paused){
                pauseOverlay.mousePressed(e);
            }
            else if(matchEnd){
                matchFinishedOverlay.mousePressed(e);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(!gameOver){
            if(paused){
                pauseOverlay.mouseReleased(e);
            }else if(matchEnd){
                matchFinishedOverlay.mouseReleased(e);
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if(paused){
            pauseOverlay.mouseMoved(e);
        }else if(matchEnd){
            matchFinishedOverlay.mouseMoved(e);
        }
    }

    public void mouseDragged(MouseEvent e){
        if(!gameOver){
            if(paused){
                pauseOverlay.mouseDragged(e);
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(gameOver){
            gameOverOverlay.keyPressed(e);
        }else{
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A:
                    player.setLeft(true);
                    break;
                case KeyEvent.VK_D:
                    player.setRight(true);
                    break;
                case KeyEvent.VK_W:
                    player.setUp(true);
                    break;
                case KeyEvent.VK_S:
                    player.setDown(true);
                    break;
                case KeyEvent.VK_SPACE:
                    player.setJump(true);
                    break;
                case KeyEvent.VK_ESCAPE:
                    paused = !paused;
                    break;
                case KeyEvent.VK_SHIFT:
                    player.dashMove();
                    break;
                case KeyEvent.VK_P:
                    setMatchEnd(true);
                    break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(!gameOver){
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A:
                    player.setLeft(false);
                    break;
                case KeyEvent.VK_D:
                    player.setRight(false);
                    break;
                case KeyEvent.VK_W:
                    player.setUp(false);
                    break;
                case KeyEvent.VK_S:
                    player.setDown(false);
                    break;
                case KeyEvent.VK_SPACE:
                    player.setJump(false);
                    break;
                case KeyEvent.VK_SHIFT:
                    player.dashMove();
                    break;
            }
        }

    }
    //endregion

    public Player getPlayer(){
        return player;
    }

    public LevelManager getLevelManager(){return levelManager;}

    public ObjectManager getObjectManager(){return objectManager;}

    public void WindowFocusLost(){
        player.resetDirBooleans();
    }

    public void unpauseGame(){
        paused = false;
    }

    public void checkObjectHit(Rectangle2D.Float attackBox) {
        objectManager.checkObjectHit(attackBox);
    }

    public void checkTrapCollision(Player player) {
        objectManager.checkTrapCollision(player);
    }
}
