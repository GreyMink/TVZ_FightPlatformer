package main;

import audio.AudioPlayer;
import gamestates.*;
import gamestates.Menu;
//import network.Server;
import ui.AudioOptions;

import java.awt.*;

public class Game implements Runnable{

    private final GameWindow gameWindow;
    private final GamePanel gamePanel;
    private Thread gameThread;
    private final int FPS_SET = 60;
    private final int UPS_SET = 200;

    private Playing playing;
    private Menu menu;
    private GameOptions gameOptions;
    private AudioOptions audioOptions;
    private Lobby lobby;
    private ServerSelect serverSelect;
    private AudioPlayer audioPlayer;

    public static final int TILES_DEFAULT_SIZE = 32;
    public static final float SCALE = 1.5f;
    public static final int TILES_IN_WIDTH = 26;
    public static final int TILES_IN_HEIGHT = 14;
    public static final int TILES_SIZE = (int) (TILES_DEFAULT_SIZE * SCALE);
    public static final int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;
    public static final int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;

    public Game() {
        initClasses();
        gamePanel = new GamePanel(this);
        gameWindow = new GameWindow(gamePanel);
        gamePanel.setFocusable(true);
        gamePanel.requestFocus();

        startGameLoop();
    }

    private void initClasses() {
        audioOptions = new AudioOptions();
        menu = new Menu(this);
        playing = new Playing(this);
        lobby = new Lobby(this);
        serverSelect = new ServerSelect(this);
        gameOptions = new GameOptions(this);
    }

    private void startGameLoop(){
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void update(){
        switch (Gamestate.state) {
            case MENU -> menu.update();
            case PLAYING -> playing.update();
            case SELECT_LOBBY ->  lobby.update();
            case OPTIONS -> gameOptions.update();
            case SERVER_SELECT -> serverSelect.update();
            case QUIT -> System.exit(0);
        }
    }

    public void render(Graphics g){
        switch (Gamestate.state) {
            case MENU -> menu.draw(g);
            case PLAYING -> playing.draw(g);
            case SELECT_LOBBY -> lobby.draw(g);
            case SERVER_SELECT -> serverSelect.draw(g);
            case OPTIONS -> gameOptions.draw(g);
        }

    }

    //GameLoop
    @Override
    public void run() {

        double timePerFrame = 1000000000.0 / FPS_SET;
        double timePerUpdate = 1000000000.0 / UPS_SET;

        long previousTime = System.nanoTime();

        int frames = 0;
        int updates = 0;
        long lastCheck = System.currentTimeMillis();

        double deltaU = 0;
        double deltaF = 0;

        while(true){
            long currentTime = System.nanoTime();

            deltaU += (currentTime - previousTime) / timePerUpdate;
            deltaF += (currentTime - previousTime) / timePerFrame;
            previousTime = currentTime;

            if(deltaU >= 1){
                update();
                updates++;
                deltaU--;
            }

            if(deltaF >= 1){
                gamePanel.repaint();
                frames++;
                deltaF--;

            }

            if(System.currentTimeMillis() - lastCheck >= 1000){
                lastCheck = System.currentTimeMillis();
                System.out.println("FPS: " + frames + " | UPS: " + updates);
                frames=0;
                updates=0;
            }
        }
    }


    public void WindowFocusLost(){
        if(Gamestate.state == Gamestate.PLAYING){
            playing.getHostPlayer().resetDirBooleans();
        }
    }

    public Menu getMenu(){
        return menu;
    }
    public Playing getPlaying(){
        return playing;
    }
    public Lobby getLobby() {return lobby;}
    public ServerSelect getServerSelect(){return serverSelect;}
    public GameOptions getGameOptions(){
        return gameOptions;
    }
    public AudioOptions getAudioOptions(){
        return audioOptions;
    }

    public void startMatch(){Gamestate.state = Gamestate.PLAYING;}
//    public AudioPlayer getAudioPlayer(){
//        return audioPlayer;
//    }
}
