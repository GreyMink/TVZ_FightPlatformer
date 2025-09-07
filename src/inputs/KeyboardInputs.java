package inputs;

import gamestates.Gamestate;
import main.GamePanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardInputs implements KeyListener{

    private final GamePanel gamePanel;
    public KeyboardInputs(GamePanel gamePanel){
        this.gamePanel = gamePanel;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (Gamestate.state) {
            case MENU -> gamePanel.getGame().getMenu().keyPressed(e);
            case PLAYING -> gamePanel.getGame().getPlaying().keyPressed(e);
            case SELECT_LOBBY -> gamePanel.getGame().getLobby().keyPressed(e);
            case SERVER_SELECT -> gamePanel.getGame().getServerSelect().keyPressed(e);
            case OPTIONS -> gamePanel.getGame().getGameOptions().keyPressed(e);
            default -> {
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (Gamestate.state) {
            case MENU -> gamePanel.getGame().getMenu().keyReleased(e);
            case PLAYING -> gamePanel.getGame().getPlaying().keyReleased(e);
            case SELECT_LOBBY ->  gamePanel.getGame().getLobby().keyReleased(e);
            case SERVER_SELECT -> gamePanel.getGame().getServerSelect().keyReleased(e);
            case OPTIONS -> gamePanel.getGame().getGameOptions().keyReleased(e);
            default -> {
            }
        }
    }
}
