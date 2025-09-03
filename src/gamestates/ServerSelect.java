package gamestates;

import main.Game;
import ui.MenuButton;
import ui.ServerButton;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static utils.Constants.UI.Buttons.B_WIDTH;

public class ServerSelect extends State implements Statemethods{

    private BufferedImage serverImg, serverImgBack;
    private int menuX, menuY, menuWidth, menuHeight;
    private int selectX, selectY, selectWidth, selectHeight;
//    private ArrayList<?> servers;
    private int serverIndex = 0;

    private final String[] tempServers = {"Ivanova Arena", "Arianina Arena", "Koji Vrag?"};
    private ArrayList<ServerButton> serverButtons = new ArrayList<>();
    private MenuButton[] menuButtons = new  MenuButton[2];

    private static final int BUTTON_WIDTH = 120;
    private static final int BUTTON_HEIGHT = 80;
    private static final int GAP = 20;


    public ServerSelect(Game game) {
        super(game);

        loadButtons();
        loadBackgrounds();
    }

    private void loadBackgrounds() {
        serverImgBack = new BufferedImage(Game.GAME_WIDTH, Game.GAME_HEIGHT, BufferedImage.TYPE_INT_RGB);

//        menuX = Game.GAME_WIDTH / 2 - menuWidth / 2;
//        menuY = (int) (45 * Game.SCALE);
    }

    private void loadButtons() {
        int halfWidth = Game.GAME_WIDTH / 2;
        int yOffset = (int) (340 * Game.SCALE);
        int GAP = 20;
        //2 gumba u redu * njihova širina + razmak
        int rowWidth = 2 * menuWidth + GAP;
        int exitX = (halfWidth - rowWidth);
        int joinX = exitX + B_WIDTH + GAP;
        //EXIT Button
        menuButtons[0] = new MenuButton(exitX, yOffset, 2, Gamestate.MENU);
        //JOIN Button
        menuButtons[1] = new MenuButton(joinX,  yOffset, 0, Gamestate.SELECT_LOBBY);


        //Server Buttons
        int xServerSelect = Game.GAME_WIDTH / 4;
        int yServerOffset = (int) (80 * Game.SCALE);

        for(int i = 0; i < tempServers.length; i++){
            serverButtons.add(new ServerButton(xServerSelect, ((i+1) * yServerOffset) + GAP, halfWidth, BUTTON_HEIGHT, i,tempServers[i]));
        }
    }

    @Override
    public void update() {
        for(MenuButton button : menuButtons) {
            button.update();
        }
        for(ServerButton serverButton : serverButtons) {
            serverButton.update();
        }
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(serverImgBack, 0,0,Game.GAME_WIDTH, Game.GAME_HEIGHT, null);

        for(MenuButton button : menuButtons) {
            button.draw(g);
        }
        for(ServerButton serverButton : serverButtons) {
            serverButton.draw(g);
        }
    }

    //region Inputs
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        for(MenuButton button : menuButtons) {
            if(isIn(e, button)){
                if(button == menuButtons[1] && serverIndex != 0){
                    //UMETNI KOD ZA SLANJE PODATAKA SERVERU SA ODABRANIM INDEKSOM/ADRESSOM
                    //serverIndex
                    button.setMousePressed(true);
                }else if(button == menuButtons[0]){
                    button.setMousePressed(true);
                    break;
                }

            }
        }
        for(ServerButton serverButton : serverButtons) {
            if(isIn(e, serverButton)){
                //TEMP ZA PRIMJER
                serverIndex = serverButton.getSelectIndex();
                serverButton.setMousePressed(true);
                break;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        for(MenuButton button : menuButtons) {
            if(isIn(e, button)){
                if(button.isMousePressed()){
                    button.applyGameState();
                    break;
                }
            }
        }
        for(MenuButton button : menuButtons) {
            button.resetBooleans();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        for(MenuButton button : menuButtons) {
            button.setMouseOver(false);
            break;
        }
        for(MenuButton button : menuButtons) {
            if(isIn(e,button)){
                button.setMouseOver(true);
                break;
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER){
            Gamestate.state = Gamestate.MENU;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
    //endregion
}
