package ui;

import gamestates.Gamestate;
import main.Game;

import java.awt.*;
import java.awt.event.MouseEvent;

import static utils.Constants.UI.PauseButtons.*;
import static utils.Constants.UI.VolumeButtons.*;

public class AudioOptions {

    private VolumeButton volumeButton;
    private SoundButton musicButton, sfxButton;

    public AudioOptions(){
        createSoundButtons();
        createVolumeButton();
    }

    public void update(){
        musicButton.update();
        sfxButton.update();

        volumeButton.update();
    }

    public void draw(Graphics g){
        musicButton.draw(g);
        sfxButton.draw(g);

        volumeButton.draw(g);
    }

    private void createSoundButtons() {
        int soundX = (int) (450 * Game.SCALE);
        int musicY = (int) (140 * Game.SCALE);
        int sfxY = (int) (186 * Game.SCALE);
        musicButton = new SoundButton(soundX,musicY,SOUND_SIZE,SOUND_SIZE);
        sfxButton= new SoundButton(soundX,sfxY,SOUND_SIZE,SOUND_SIZE);
    }

    private void createVolumeButton() {
        int volumeX = (int)( 309 * Game.SCALE);
        int volumeY = (int) (278 * Game.SCALE);
        volumeButton = new VolumeButton(volumeX, volumeY, SLIDER_WIDTH, VOLUME_HEIGHT);
    }

    //region Inputs
    public void mousePressed(MouseEvent e) {
        if(isIn(e, musicButton)){
            musicButton.setMousePressed(true);
        }else if (isIn(e,sfxButton)){
            sfxButton.setMousePressed(true);
        }else if (isIn(e,volumeButton)){
            volumeButton.setMousePressed(true);
        }
    }

    public void mouseReleased(MouseEvent e) {
        if(isIn(e,musicButton)){
            if(musicButton.isMousePressed()){
                musicButton.setMuted(!musicButton.isMuted());
            }
        }else if(isIn(e,sfxButton)){
            if(sfxButton.isMousePressed()){
                sfxButton.setMuted(!sfxButton.isMuted());
            }
        }

        musicButton.resetBools();
        sfxButton.resetBools();
        volumeButton.resetBools();
    }

    public void mouseMoved(MouseEvent e) {
        musicButton.setMouseOver(false);
        sfxButton.setMouseOver(false);

        if(isIn(e,musicButton)){
            musicButton.setMouseOver(true);
        }else if (isIn (e,sfxButton)){
            sfxButton.setMouseOver(true);
        }else if (isIn (e, volumeButton)){
            volumeButton.setMouseOver(true);
        }
    }

    public void mouseDragged(MouseEvent e){
        if (volumeButton.isMousePressed()){
            volumeButton.changeSliderX(e.getX());
        }
    }

    //endregion
    private boolean isIn(MouseEvent e, PauseButton pauseButton){
        return pauseButton.getBounds().contains(e.getX(),e.getY());
    }
}
