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
    public static final String MENU_BACKGROUND_IMG = "backgrounds/background_menu.png";
    public static final String PAUSE_BACKGROUND = "pause_menu.png";
    public static final String SOUND_BUTTONS = "sound_button.png";
    public static final String URM_BUTTONS = "urm_buttons.png";
    public static final String VOLUME_BUTTONS = "volume_buttons.png";
    public static final String MATCH_END_IMG = "completed_sprite.png";
    public static final String OPTIONS_MENU = "options_background.png";

    //UI
    public static final String MENU_BACKGROUND_STONE = "UI/MENU_Stone.png";
    public static final String MENU_BUTTONS_STONE = "UI/Menu_Buttons_Stone.png";
    public static final String SERVER_SEARCH_MENU_STONE = "UI/Server_Search_MENU_Stone.png";
    public static final String SERVER_SELECT_BUTTON_STONE = "UI/Server_select_Button_Stone.png";
    public static final String SELECT_BUTTONS_STONE = "UI/Select_Button_Stone.png";
    public static final String URM_BUTTONS_STONE = "UI/URM_Buttons_Stone.png";
    public static final String MATCH_FINISHED_STONE = "UI/MenuFinished_Stone.png";
    public static final String LOBBY_BACKGROUND_STONE = "UI/Lobby_Half_Stone.png";
    public static final String LOBBY_INFO_BACKGROUND = "UI/Info_bg.png";

    //Backgrounds
    public static final String BACKGROUND_SKY = "backgrounds/background_sky_with_clouds.png";
    public static final String BACKGROUND_TREE_SHADE = "backgrounds/background_tree_shade.png";
    public static final String BACKGROUND_AURORA_NIGHT = "backgrounds/background_aurora_night.png";
    public static final String BACKGROUND_STONE_HENGE = "backgrounds/background_stone_henge.png";
    public static final String BACKGROUND_MOUNTAIN = "backgrounds/background_mountain.png";

    //Objects
    public static final String CONTAINERS_ATLAS = "objects/objects_sprites.png";
    public static final String TRAP_ATLAS = "objects/trap_atlas.png";
    //Projectiles
    public static final String BALL_IMG = "objects/ball.png";
    //Background
    public static final String TEMPLE_STAGE_BACKGROUND = "backgrounds/playing_bg_img.png";

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
