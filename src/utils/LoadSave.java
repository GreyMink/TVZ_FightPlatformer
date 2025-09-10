package utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

public class LoadSave {

    public static final String PLAYER_PIRATE = "characters/player_sprites.png";
    public static final String PIRATE_SELECT = "characters/pirate_select_img.png";
    public static final String PLAYER_SOLDIER = "characters/player_soldier.png";
    public static final String SOLDIER_SELECT = "characters/soldier_select_img.png";
    public static final String PLAYER_KNIGHT = "characters/player_knight.png";
    public static final String KNIGHT_SELECT = "characters/knight_select.png";
    public static final String PLAYER_WEREWOLF = "characters/player_werewolf.png";
    public static final String WEREWOLF_SELECT = "characters/werewolf_select.png";
    public static final String PLAYER_SATYR = "characters/player_satyr.png";
    public static final String SATYR_SELECT = "characters/satyr_select.png";

    public static final String COMPLETED_IMG = "completed_sprite.png";
    public static final String LEVEL_ATLAS = "outside_sprites.png";
    public static final String LEVEL_ONE_ATLAS = "stages/level_one_data.png";
    public static final String MENU_BUTTONS = "button_atlas.png";
    public static final String MENU_BACKGROUND = "menu_background.png";
    public static final String MENU_BACKGROUND_IMG = "background_menu.png";
    public static final String PAUSE_BACKGROUND = "pause_menu.png";
    public static final String SOUND_BUTTONS = "sound_button.png";
    public static final String URM_BUTTONS = "urm_buttons.png";
    public static final String VOLUME_BUTTONS = "volume_buttons.png";
    public static final String STATUS_BAR = "health_power_bar.png";
    public static final String MATCH_END_IMG = "completed_sprite.png";
    public static final String OPTIONS_MENU = "options_background.png";

    //Objects
    public static final String CONTAINERS_ATLAS = "objects_sprites.png";
    public static final String TRAP_ATLAS = "trap_atlas.png";
    //Projectiles
    public static final String BALL_IMG = "ball.png";
    //Background
    public static final String TEMPLE_STAGE_BACKGROUND = "playing_bg_img.png";

    //region Getters
    public static BufferedImage GetSpriteAtlas(String fileName){
        BufferedImage img;
        InputStream is = LoadSave.class.getResourceAsStream("/" + fileName);
        try {
            img = ImageIO.read(is);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            try{
                is.close();
            }catch(IOException e) {
                e.printStackTrace();
            }
        }
        return img;
    }
    public static BufferedImage[] getAllStages(){
        URL url = LoadSave.class.getResource("/stages");
        File file = null;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        File[] files = file.listFiles();

        BufferedImage[] imgs = new BufferedImage[files.length];

        for(int i=0;i<imgs.length;i++){
            System.out.println("Stage name:" + imgs.length);
            try {
                imgs[i] = ImageIO.read(files[i]);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return imgs;
    }
    //endregion
}
