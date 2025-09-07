package inputs;

import gamestates.Gamestate;
import main.GamePanel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseInputs implements MouseListener, MouseMotionListener {

    private final GamePanel gamePanel;
    public MouseInputs(GamePanel gamePanel){
        this.gamePanel = gamePanel;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        switch (Gamestate.state){
            case PLAYING:
                gamePanel.getGame().getPlaying().mouseClicked(e);
                break;
            default:
                break;
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {
        switch (Gamestate.state) {
            case MENU -> gamePanel.getGame().getMenu().mousePressed(e);
            case PLAYING -> gamePanel.getGame().getPlaying().mousePressed(e);
            case SELECT_LOBBY ->  gamePanel.getGame().getLobby().mousePressed(e);
            case SERVER_SELECT -> gamePanel.getGame().getServerSelect().mousePressed(e);
            case OPTIONS -> gamePanel.getGame().getGameOptions().mousePressed(e);
            default -> {
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        switch (Gamestate.state) {
            case MENU -> gamePanel.getGame().getMenu().mouseReleased(e);
            case PLAYING -> gamePanel.getGame().getPlaying().mouseReleased(e);
            case SELECT_LOBBY ->  gamePanel.getGame().getLobby().mouseReleased(e);
            case SERVER_SELECT -> gamePanel.getGame().getServerSelect().mouseReleased(e);
            case OPTIONS -> gamePanel.getGame().getGameOptions().mouseReleased(e);
            default -> {
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        switch (Gamestate.state) {
            case PLAYING -> gamePanel.getGame().getPlaying().mouseDragged(e);
            case OPTIONS -> gamePanel.getGame().getGameOptions().mouseDragged(e);
            default -> {
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        switch (Gamestate.state) {
            case MENU -> gamePanel.getGame().getMenu().mouseMoved(e);
            case PLAYING -> gamePanel.getGame().getPlaying().mouseMoved(e);
            case SELECT_LOBBY -> gamePanel.getGame().getLobby().mouseMoved(e);
            case SERVER_SELECT -> gamePanel.getGame().getServerSelect().mouseMoved(e);
            case OPTIONS -> gamePanel.getGame().getGameOptions().mouseMoved(e);

        }
    }
}
