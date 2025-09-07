package gamestates;

import entities.PlayerCharacter;
import levels.Level;
import main.Game;
import network.Client;
import network.Server;
import ui.MenuButton;
import ui.SelectButton;
import utils.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Lobby extends State implements StateMethods {

    //region Varijable
    private BufferedImage backgroundImg, backgroundImgBack;
    private int menuX, menuY, menuWidth, menuHeight;
    private MenuButton startMatchButton;

    //privremena polja za objekte (stages,playercharacters)
    private String[] stages = {"Stage1", "Stage2", "Stage3", "Stage4", "Stage5", "Stage6"};
    private String[] characters = {"Char1",  "Char2", "Char3", "Char4", "Char5", "Char6"};

    private ArrayList<Level> stagesList = new ArrayList<Level>(game.getPlaying().getLevelManager().getStages());
    private ArrayList<PlayerCharacter> playerCharacterList = new ArrayList<PlayerCharacter>(Arrays.asList(PlayerCharacter.values()));

    private int stageIndex = -1,
            characterIndex = -1;

    private static final int BUTTON_WIDTH = 120;
    private static final int BUTTON_HEIGHT = 80;
    private static final int GAP = 20;
    private static final int MAX_PER_ROW = 3;

    //Lista gumbi za određivanje stage i charactera
    private ArrayList<SelectButton> stagesButtons = new ArrayList<>();
    private ArrayList<SelectButton> characterButtons = new ArrayList<>();

    private Server server;
    private Client client;


    //endregion

    public Lobby(Game game) {
        super(game);

        loadButtons();
        loadBackground();
    }

    @Override
    public void update() {

        startMatchButton.update();
        for(SelectButton stage : stagesButtons){
            stage.update();
        }
        for(SelectButton character : characterButtons){
            character.update();
        }
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(backgroundImgBack, 0,0,Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
        //g.drawImage(backgroundImg,menuX,menuY,menuWidth,menuHeight,null);

        startMatchButton.draw(g);
        drawSelectionButtons(g);
    }

    private void loadBackground() {
        //backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.MENU_BACKGROUND);

        backgroundImgBack = new BufferedImage(Game.GAME_WIDTH, Game.GAME_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = (Graphics2D) backgroundImgBack.getGraphics();
        g2d.setColor(Color.BLUE);
        g2d.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
        g2d.dispose();

//        menuWidth = (int)(backgroundImg.getWidth() * Game.SCALE);
//        menuHeight = (int)(backgroundImg.getHeight() * Game.SCALE);
//        menuX = Game.GAME_WIDTH / 2 - menuWidth / 2;
//        menuY = (int) (45 * Game.SCALE);
    }

    private void loadButtons() {
        startMatchButton = new MenuButton(Game.GAME_WIDTH / 2, (int) (340 * Game.SCALE), 0, Gamestate.PLAYING);

        generateSelectionButtons();
    }

    private void generateSelectionButtons(){
        stagesButtons.clear();
        characterButtons.clear();

        int halfWidth = Game.GAME_WIDTH / 2;
        int yOffset = 100;
        int currentYStages = yOffset;
        int currentYCharacters = yOffset;


        for(int row = 0; row < stagesList.size(); ){
            int buttonsInRow = Math.min(MAX_PER_ROW, stagesList.size() - row);
            int rowWidth = buttonsInRow * BUTTON_WIDTH + (buttonsInRow - 1) * GAP;
            int startX = (halfWidth - rowWidth) / 2;

            for(int col = 0; col < buttonsInRow; col++){
                int x = startX + col * (BUTTON_WIDTH + GAP);
                stagesButtons.add(new SelectButton(x, currentYStages, BUTTON_WIDTH, BUTTON_HEIGHT, col, stagesList.get(row).getSelectImage()));
                row++;
            }
            currentYStages += BUTTON_HEIGHT + GAP;
        }

        for(int i = 0; i < playerCharacterList.size();){
            int buttonsInRow = Math.min(MAX_PER_ROW, playerCharacterList.size() - i);
            int rowWidth = buttonsInRow * BUTTON_WIDTH + (buttonsInRow - 1) * GAP;

            int startX = halfWidth + (halfWidth - rowWidth) / 2;

            for(int col = 0; col < buttonsInRow; col++){
                int x = startX + col * (BUTTON_WIDTH + GAP);
                characterButtons.add(new SelectButton(x, currentYCharacters, BUTTON_WIDTH, BUTTON_HEIGHT, col, LoadSave.GetSpriteAtlas(playerCharacterList.get(i).getSelectImage())));
                i++;
            }
            currentYCharacters += BUTTON_HEIGHT + GAP;

        }
    }

    private void drawSelectionButtons(Graphics g){;
        for (SelectButton stagesButton : stagesButtons) {
            stagesButton.draw(g);
        }

        for(SelectButton characterButton : characterButtons){
            characterButton.draw(g);
        }
    }

    private void resetButtons() {
        for(SelectButton stagesButton : stagesButtons){
            stagesButton.reset();
        }
        for(SelectButton characterButton : characterButtons){
            characterButton.reset();
        }
    }
    private void resetStageButtons() {
        for(SelectButton stagesButton : stagesButtons){
            stagesButton.reset();
        }
    }
    private void resetCharacterButtons() {
        for(SelectButton characterButton : characterButtons){
            characterButton.reset();
        }
    }

    public Server getServer() {return server;}
    public Client getClient() {return client;}
    public int getStageIndex() {return stageIndex;}
    public void setStageIndex(int stageIndex) {this.stageIndex = stageIndex;}
    public int getCharacterIndex() {return characterIndex;}
    public void setCharacterIndex(int characterIndex) {this.characterIndex = characterIndex;}
    public ArrayList<PlayerCharacter> getPlayerCharacterList() {return playerCharacterList;}

    public void setServerInstance(Server server) {this.server = server;}
    public void setClientInstance(Client client) {this.client = client;}

    //region Inputs
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
            if(isIn(e, startMatchButton)){
                if (server != null) {
                    //provjera da je odreden - default je -1
                    if(characterIndex != -1 && stageIndex != -1){
                        game.getPlaying().setPlayerCharacter(playerCharacterList.get(characterIndex));
                        game.getPlaying().resetAll();
                        System.out.println(playerCharacterList.get(characterIndex));
                        startMatchButton.setMousePressed(true);
                        server.setHostReady_CheckStart(true);
                    }
                } else if (client != null) {
                    if(characterIndex != -1 && stageIndex != -1){
                        client.sendReady();
                        System.out.println("Client is Ready");
                    }
                }

//                game.getPlaying().setPlayerCharacter(playerCharacterList.get(characterIndex));
//                game.getPlaying().resetAll();
//                System.out.println(playerCharacterList.get(characterIndex));
                startMatchButton.setMousePressed(true);
            }
            for(SelectButton stagesButton : stagesButtons){
                if(isIn(e, stagesButton) && client == null){
                    resetStageButtons();
                    stageIndex = stagesButton.getSelectIndex();
                    game.getPlaying().getLevelManager().setStageIndex(stageIndex);
                    if(server != null) {
                        server.broadcastStageSelection(stageIndex);
                    }
                    stagesButton.setMousePressed(true);
                }

            }
            for(SelectButton characterButton : characterButtons){
                if(isIn(e, characterButton)){
                    resetCharacterButtons();
                    characterButton.setMousePressed(true);
                    characterIndex = characterButton.getSelectIndex();
                    if (server != null) {
                        try {
                            server.broadcastCharacterSelection(0, characterIndex); // host player0
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    } else if (client != null) {
                        client.sendCharacterSelection(characterIndex);
                    }
                }
            }

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(isIn(e, startMatchButton)){
            if(startMatchButton.isMousePressed()){
                if((server != null && server.getPlayersReady()) || (client != null && client.getPlayersReady())){
                    startMatchButton.applyGameState();
                }

            }
        }
        startMatchButton.resetBooleans();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        startMatchButton.setMouseOver(false);

        if(isIn(e,startMatchButton))
            startMatchButton.setMouseOver(true);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
            if(server!=null)
                server.stop();
            Gamestate.state = Gamestate.MENU;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }


    //endregion
}
