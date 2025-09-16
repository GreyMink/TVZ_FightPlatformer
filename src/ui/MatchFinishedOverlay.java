package ui;

import entities.PlayerCharacter;
import gamestates.Gamestate;
import gamestates.Playing;
import main.Game;
import utils.LoadSave;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static utils.Constants.UI.URMButtons.*;

public class MatchFinishedOverlay {

    private Playing playing;
    private UrmButton menuButton, lobbyButton;
    private BufferedImage img;
    private int bgX, bgY, bgWidth, bgHeight;
    private int winner;
    private String winnerCharacter;

    public MatchFinishedOverlay(Playing playing){
        this.playing = playing;
        initImg();
        initButtons();
    }

    private void initButtons() {
        int menuX = (int) (330 * Game.SCALE);
        int chSelectX = (int) (445 * Game.SCALE);
        int y = (int) (240 * Game.SCALE);
        lobbyButton = new UrmButton(chSelectX, y, URM_SIZE, URM_SIZE, 1);
        menuButton = new UrmButton(menuX, y, URM_SIZE, URM_SIZE,2);

    }

    private void initImg() {
        img = LoadSave.GetSpriteAtlas(LoadSave.MATCH_FINISHED_STONE);
        bgWidth = (int) (img.getWidth() * Game.SCALE);
        bgHeight = (int) (img.getHeight() * Game.SCALE);
        bgX = Game.GAME_WIDTH / 2 - bgWidth / 2;
        bgY = (int) (75 * Game.SCALE);
    }

    public void draw(Graphics g){
        g.drawImage(img,bgX,bgY, bgWidth, bgWidth,null);
        //player winner
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.setColor(new Color(122,116,104,255));
        g.drawString("Player " + winner,bgX+bgWidth/3,bgY+bgHeight/5);
        g.drawImage(LoadSave.GetSpriteAtlas(winnerCharacter),bgX + bgWidth/3-10,bgY + bgHeight/4,bgWidth/3,bgHeight/3,null);

        lobbyButton.draw(g);
        menuButton.draw(g);
    }

    public void update(){

    }

    private boolean isInButton(UrmButton b, MouseEvent e){
        return b.getBounds().contains(e.getX(),e.getY());
    }

    public void mouseMoved(MouseEvent e){
        menuButton.setMouseOver(false);
        lobbyButton.setMouseOver(false);

        if(isInButton(menuButton,e)){
            menuButton.setMouseOver(true);
        }else if(isInButton(lobbyButton,e)){
            lobbyButton.setMouseOver(true);
        }
    }

    public void mouseReleased(MouseEvent e){
        if(isInButton(menuButton,e)){
            if(menuButton.isMousePressed()){
                playing.resetAll();
                Gamestate.state =Gamestate.MENU;
            }
        }else if(isInButton(lobbyButton,e)){
            if(lobbyButton.isMousePressed()){
                playing.resetAll();
                Gamestate.state =Gamestate.SELECT_LOBBY;
            }
        }
        menuButton.resetBools();
        lobbyButton.resetBools();
    }

    public void mousePressed(MouseEvent e){
        if(isInButton(menuButton,e)){
            menuButton.setMousePressed(true);
        }else if(isInButton(lobbyButton,e)){
            lobbyButton.setMousePressed(true);
        }
    }

    public void setWinner(int winner) {this.winner = winner;}
    public int getWinner() {return winner;}
    public void setWinnerCharacter(PlayerCharacter winnerCharacter) {this.winnerCharacter = winnerCharacter.getSelectImage();}
}
