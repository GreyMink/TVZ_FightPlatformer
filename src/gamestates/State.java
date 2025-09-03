package gamestates;

import main.Game;
import ui.MenuButton;
import ui.SelectButton;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class State {
    protected Game game;

    public State(Game game){
        this.game = game;
    }

    public Game getGame(){
        return game;
    }

    public boolean isIn(MouseEvent e, MenuButton menuButton){
        return menuButton.getBounds().contains(e.getX(), e.getY());
    }

    public boolean isIn(MouseEvent e, SelectButton selectButton){
        return selectButton.getBounds().contains(e.getX(),e.getY());
    }

    public boolean isIn(MouseEvent e, ArrayList<SelectButton> selectButtons){
        for(SelectButton selectButton : selectButtons){
            if(selectButton.getBounds().contains(e.getX(), e.getY())){
                return true;
            }
        }
        return false;
    }
}
