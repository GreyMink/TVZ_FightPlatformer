package gamestates;

import main.Game;
import network.Client;
import ui.MenuButton;
import ui.SelectButton;
import ui.ServerButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static utils.Constants.UI.Buttons.B_WIDTH;

public class ServerSelect extends State implements StateMethods {

    private BufferedImage serverImg, serverImgBack;
    private int menuX, menuY, menuWidth, menuHeight;
    private int selectX, selectY, selectWidth, selectHeight;
    private ArrayList<Client.DiscoveredServer> servers;
    private Client client;
    private ScheduledExecutorService discoveryExecutor;
    private int serverIndex;

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

    private void loadBackgrounds() {
        serverImgBack = new BufferedImage(Game.GAME_WIDTH, Game.GAME_HEIGHT, BufferedImage.TYPE_INT_RGB);
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
//        int xServerSelect = Game.GAME_WIDTH / 4;
//        int yServerOffset = (int) (80 * Game.SCALE);
//
//        if(servers != null) {
//            for(int i = 0; i < servers.size(); i++){
//                serverButtons.add(new ServerButton(xServerSelect, ((i+1) * yServerOffset) + GAP, halfWidth, BUTTON_HEIGHT, i, servers.get(i).getName()));
//            }
//        }
    }

    private void rebuildServerButtons() {
        int oldSelectedIndex = serverIndex;

        serverButtons.clear();
        int halfWidth = Game.GAME_WIDTH / 2;
        int xServerSelect = Game.GAME_WIDTH / 4;
        int yServerOffset = (int) (80 * Game.SCALE);

        System.out.println("Server Select:" + servers.getFirst().getName());
        for (int i = 0; i < servers.size(); i++) {
            ServerButton button = new ServerButton(
                    xServerSelect,
                    ((i + 1) * yServerOffset) + GAP,
                    halfWidth,
                    BUTTON_HEIGHT,
                    i,
                    servers.get(i).getName()
                );
            if(i == oldSelectedIndex) {
                serverButtons.add(button);
            }
        }
    }

    private void resetServerButtons() {
        for(ServerButton serverButton : serverButtons){
            serverButton.reset();
        }
    }

    public void startDiscovery() {
        if (client == null) return;
//        stopDiscovery();
        System.out.println("Start discovery");

        discoveryExecutor = Executors.newSingleThreadScheduledExecutor();
        discoveryExecutor.scheduleAtFixedRate(() -> {
            try {
                System.out.println("Discovery attempt");
                ArrayList<Client.DiscoveredServer> discovered = client.discoverServers(1000);
                synchronized (this) {
                    if(servers == null || !servers.equals(discovered)){
                        SwingUtilities.invokeLater(() -> {
                            servers = discovered;
                            System.out.println("Servers: " + discovered);
                            rebuildServerButtons();
                        });

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 5, TimeUnit.SECONDS); // refresh every 5s
    }

    public void stopDiscovery() {
        if (discoveryExecutor != null && !discoveryExecutor.isShutdown()) {
            discoveryExecutor.shutdownNow();
        }
    }

    public void setClientInstance(Client client){this.client = client;startDiscovery();}
    public void setServers(ArrayList<Client.DiscoveredServer> servers) {this.servers = servers;}

    //region Inputs
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        for(MenuButton button : menuButtons) {
            if(isIn(e, button)){
                if(button.getState() == Gamestate.SELECT_LOBBY){
                    try {
                        System.out.println("Server connect:" + servers.get(serverIndex).getName());
                        client.connect(servers.get(serverIndex).getAddress(),servers.get(serverIndex).getPort());
                        button.setMousePressed(true);
                        game.getLobby().setClientInstance(client);
                        stopDiscovery();
                        button.applyGameState();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }else if(button == menuButtons[0]){
                    button.setMousePressed(true);
                    break;
                }

            }
        }
        for(ServerButton serverButton : serverButtons) {
            if(isIn(e, serverButton)){
                resetServerButtons();
                serverIndex = serverButton.getSelectIndex();
                //Control point
                System.out.println("Serverindex: " + serverIndex);
                System.out.println(servers.get(serverIndex).getName());

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
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
            try {
                System.out.println("Checking for new servers....");
                servers = client.discoverServers(1000);
                System.out.println("Discovered servers: "+ servers);
                rebuildServerButtons();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
    //endregion
}
