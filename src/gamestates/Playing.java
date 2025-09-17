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
import java.util.ArrayList;

public class Playing extends State implements StateMethods {
    private int localPlayerIndex = 0;
    private ArrayList<Player> players = new ArrayList<Player>();
    private Player hostPlayer;

    //region States
    private LevelManager levelManager;
    private ObjectManager objectManager;
    private PauseOverlay pauseOverlay;
    private GameOverOverlay gameOverOverlay;
    private MatchFinishedOverlay matchFinishedOverlay;
    //endregion

    private final BufferedImage backgroundImg;

    //Game state / Player state variables
    private boolean paused = false;
    private boolean gameOver = false;
    private boolean matchEnd = false;

    //region Network variables
    private Player remotePlayer;// player 2 - kontrole preko network poruka
    private int hostNumber;
    private int remoteNumber;
    private volatile int latestRemoteInputMask = 0; // set by Server thread

        //info - network package - player pos
    private volatile boolean hasNetworkState = false;
    private volatile float net_p0x, net_p0y, net_p0vx, net_p0vy, net_p0health,
            net_p1x, net_p1y, net_p1vx, net_p1vy, net_p1health;
    //endregion

    public Playing(Game game) {
        super(game);
        initClasses();

        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.BACKGROUND_MOUNTAIN);
        loadStageObjects();
    }
    @Override
    public void update() {
        if(paused){
            pauseOverlay.update();
        }else if(matchEnd){
            matchFinishedOverlay.update();
        }else if(!gameOver){
            levelManager.update();

            applyRemoteInput();

            hostPlayer.update();
            remotePlayer.update();
            sendHostPlayerInput();

            objectManager.update(levelManager.getCurrentStage().getLvlData(), hostPlayer);
            objectManager.update(levelManager.getCurrentStage().getLvlData(), remotePlayer);
        }
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT,null);

        levelManager.draw(g);
        hostPlayer.render(g);
        remotePlayer.render(g);
        objectManager.draw(g);

        drawStatusBars(g);

        if(paused){
            pauseOverlay.draw(g);
        }else if(gameOver){
            gameOverOverlay.draw(g);
        }
        else if(matchEnd){
            matchFinishedOverlay.draw(g);
        }
    }

    private void initClasses() {
        levelManager = new LevelManager(game);
        objectManager = new ObjectManager(this);
        pauseOverlay = new PauseOverlay(this);
        gameOverOverlay = new GameOverOverlay(this);
        matchFinishedOverlay = new MatchFinishedOverlay(this);
    }

    public void resetAll(){
        gameOver = false;
        paused = false;
        matchEnd = false;
        if(hostPlayer != null){
            hostPlayer.resetLives();
            hostPlayer.resetAll();
        }
        if(remotePlayer != null){
            remotePlayer.resetLives();
            remotePlayer.resetAll();
        }
        objectManager.resetAllObjects();
    }
    public void loadNextStage(){
        levelManager.loadNextStage();
        objectManager.loadObjects(levelManager.getCurrentStage());
        hostPlayer.setSpawn(levelManager.getCurrentStage().getPlayerSpawn());
        if(remotePlayer != null){
            remotePlayer.setSpawn(levelManager.getCurrentStage().getRemotePlayerSpawn());
        }
        resetAll();
    }

    private void loadStageObjects() {objectManager.loadObjects(levelManager.getCurrentStage());}

    public void checkEnemyHit(Rectangle2D.Float attackBox){/*enemyManager.checkEnemyHit(attackBox);*/}

    //region Network
    public Player getRemotePlayer() {return remotePlayer;}
    public void setRemotePlayer(Player remotePlayer) {this.remotePlayer = remotePlayer;}
    public void setRemoteInputMask(int mask) {this.latestRemoteInputMask = mask;}


    // Pozvati u game update thread za određivanje inputa remoteplayera
    private void applyRemoteInput() {
        if(remotePlayer == null) return;
        remotePlayer.setLeft((latestRemoteInputMask & (1<<0)) != 0);
        remotePlayer.setRight((latestRemoteInputMask & (1<<1)) != 0);
        remotePlayer.setUp((latestRemoteInputMask & (1<<2)) != 0);
        remotePlayer.setDown((latestRemoteInputMask & (1<<3)) != 0);
        remotePlayer.setJump((latestRemoteInputMask & (1<<4)) != 0);
        if((latestRemoteInputMask & (1<<5)) != 0){
            remotePlayer.setAttacking(true);
        } else {
            remotePlayer.setAttacking(false);
        }
    }
    private void sendHostPlayerInput(){
        int mask = 0;
        if (hostPlayer.isLeft())   mask |= (1<<0);
        if (hostPlayer.isRight())  mask |= (1<<1);
        if (hostPlayer.isUp())     mask |= (1<<2);
        if (hostPlayer.isDown())   mask |= (1<<3);
        if (hostPlayer.isJump())   mask |= (1<<4);
        if (hostPlayer.isAttacking()) mask |= (1<<5);

        if(game.getLobby().getServer() != null){
            game.getLobby().getServer().sendInputUDP(System.nanoTime(), mask);
        }
        if(game.getLobby().getClient() != null){
            game.getLobby().getClient().sendInputUDP(System.nanoTime(), mask);
        }
    }
    public void applyNetworkStateFromServer(float p0x, float p0y, float p0vx, float p0vy, float p0health, float p1x, float p1y, float p1vx, float p1vy, float p1health) {
        this.net_p0x = p0x;
        this.net_p0y = p0y;
        this.net_p0vx = p0vx;
        this.net_p0vy = p0vy;
        this.net_p0health = p0health;

        this.net_p1x = p1x;
        this.net_p1y = p1y;
        this.net_p1vx = p1vx;
        this.net_p1vy = p1vy;
        this.net_p1health = p1health;
    }
    //endregion

    //region StatusBar
    private void drawStatusBars(Graphics g) {
        g.setFont(g.getFont().deriveFont(Font.BOLD, 24f));
        g.setColor(Color.RED);

        if(hostPlayer != null) {
            drawPlayerHUD(g, hostPlayer, hostNumber, hostNumber == 1 ? 20 : Game.GAME_WIDTH - 300, 40);
        }
        if(remotePlayer != null) {
            drawPlayerHUD(g, remotePlayer, remoteNumber, remoteNumber == 2 ? Game.GAME_WIDTH - 300 : 20, 40);
        }
    }

    private void drawPlayerHUD(Graphics g, Player player, int playerNumber, int x, int y) {
        int damage = player.getHealthPercent();
        int lives = player.getLives();

        int hudWidth = 300;
        int hudHeight = 60;

        g.setColor(new Color(0,0,0,150));
        g.fillRoundRect(x, y, hudWidth, hudHeight, 15, 15);

        //Širina raste sa štetom
        int barMaxWidth = 200;
        int barWidth = Math.min(barMaxWidth, damage * 2); // 0-200px za 0-100%
        int barHeight = 15;

        //Boja se mijenja iz zelene u crvenu promjenom štete
        float t = Math.min(1f, damage / 150f);
        Color barColor = new Color(
                (int)(255 * t),
                (int)(255 * (1 - t)),
                0);
        g.setColor(barColor);
        g.fillRect(x + 15, y + 30, barWidth, barHeight);

        //Obrub
        g.setColor(Color.WHITE);
        g.drawRect(x + 15, y + 30, barMaxWidth, barHeight);

        //Tekst
        g.setColor(Color.WHITE);
        g.drawString("P" + playerNumber + ": " + damage + "%", x + 15, y + 25);

        //prikaz života 
        g.setColor(Color.PINK);
        int heartX = x + hudWidth - 80;
        int heartY = y + 15;
        for(int i=0;i<lives;i++){
            drawHeart(g, heartX + i*20, heartY, 12);
        }
    }

    private void drawHeart(Graphics g, int x, int y, int size){
        int[] xs = {x, x+size/2, x+size, x+size/2};
        int[] ys = {y, y-size/2, y, y+size/2};
        g.fillPolygon(xs, ys, 4);
    }
    //endregion

    public void exitToMenu() {
        // tell network peer first
        if (game.getLobby().getClient() != null) {
            game.getLobby().getClient().sendExit();
        }
        if (game.getLobby().getServer() != null) {
            game.getLobby().getServer().sendExitToClients();
        }

        Gamestate.state = Gamestate.MENU;
    }


    //region Inputs
    @Override
    public void mouseClicked(MouseEvent e) {
        if(!gameOver){
            if(e.getButton() == MouseEvent.BUTTON1){
                hostPlayer.setAttackIndex(1);
                hostPlayer.setAttacking(true);

            }else if(e.getButton() == MouseEvent.BUTTON3){
                hostPlayer.setAttackIndex(2);
                hostPlayer.powerAttack(e);
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
                    hostPlayer.setLeft(true);
                    break;
                case KeyEvent.VK_D:
                    hostPlayer.setRight(true);
                    break;
                case KeyEvent.VK_W:
                    hostPlayer.setUp(true);
                    break;
                case KeyEvent.VK_S:
                    hostPlayer.setDown(true);
                    break;
                case KeyEvent.VK_SPACE:
                    hostPlayer.setJump(true);
                    break;
                case KeyEvent.VK_ESCAPE:
                    paused = !paused;
                    break;
                case KeyEvent.VK_SHIFT:
                    hostPlayer.dashMove();
                    break;
                case KeyEvent.VK_P:
                    exitToMenu();
                    break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(!gameOver){
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A:
                    hostPlayer.setLeft(false);
                    break;
                case KeyEvent.VK_D:
                    hostPlayer.setRight(false);
                    break;
                case KeyEvent.VK_W:
                    hostPlayer.setUp(false);
                    break;
                case KeyEvent.VK_S:
                    hostPlayer.setDown(false);
                    break;
                case KeyEvent.VK_SPACE:
                    hostPlayer.setJump(false);
                    break;
                case KeyEvent.VK_SHIFT:
                    hostPlayer.dashMove();
                    break;
            }
        }
    }
    //endregion

    //region Getters, Setters, Extensions
    public Player getHostPlayer(){return hostPlayer;}
    public Player getLocalPlayer(){return players.get(localPlayerIndex);}
    public Player getRemotePlayer(int index){return players.get(index);}
    public LevelManager getLevelManager(){return levelManager;}
    public ObjectManager getObjectManager(){return objectManager;}

    public void setPlayerCharacter(PlayerCharacter playerCharacter) {
        hostPlayer = new Player(playerCharacter, this);
        hostPlayer.loadLvlData(levelManager.getCurrentStage().getLvlData());
        Point spawn = levelManager.getCurrentStage().getPlayerSpawn();
        hostPlayer.setSpawn(spawn);
        System.out.println("SERVER hostPlayer spawn: "+spawn);
//        hostPlayer.setSpawn(levelManager.getCurrentStage().getPlayerSpawn());
    }

    public void setRemotePlayerCharacter(PlayerCharacter playerCharacter) {
        remotePlayer = new Player(playerCharacter, this);
        remotePlayer.loadLvlData(levelManager.getCurrentStage().getLvlData());
        Point spawn =levelManager.getCurrentStage().getRemotePlayerSpawn();
        remotePlayer.setSpawn(spawn);
        System.out.println("[server] remote player created: "+spawn);
//        remotePlayer.setSpawn(levelManager.getCurrentStage().getRemotePlayerSpawn());
    }

    public void setGameOver(boolean gameOver){this.gameOver = gameOver;}
    public void setRemoteNumber(int remoteNumber) {this.remoteNumber = remoteNumber;}
    public void setHostNumber(int hostNumber) {this.hostNumber = hostNumber;}
    public void setMatchEnd(boolean matchEnd){this.matchEnd = matchEnd;}
    public void checkObjectHit(Rectangle2D.Float attackBox) {objectManager.checkObjectHit(attackBox);}
    public void checkTrapCollision(Player player) {objectManager.checkTrapCollision(player);}
    public void unpauseGame(){paused = false;}

    public ArrayList<Player> getAllPlayers() {
        ArrayList<Player> allPlayers = new ArrayList<>();
        if(hostPlayer!=null){allPlayers.add(hostPlayer);}
        if(remotePlayer!=null){allPlayers.add(remotePlayer);}
        return allPlayers;
    }

    public void checkMatchEnd() {
        //provjera života
        int hostLives = hostPlayer.getLives();
        int remoteLives = remotePlayer.getLives();

        if (hostLives <= 0 || remoteLives <= 0) {
            // postavi varijablu za određivanje kraja borbe i prikaza MatchFinishedOverlay
            matchEnd = true;         // triggers MatchFinishedOverlay
            // optionally tell overlay who won:
            matchFinishedOverlay.setWinner(hostLives > 0 ? hostNumber : remoteNumber);
            matchFinishedOverlay.setWinnerCharacter(hostLives > 0 ? hostPlayer.getPlayerCharacter() : remotePlayer.getPlayerCharacter());
        }
    }
    //endregion
}
