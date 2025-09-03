package gamestates;

public enum Gamestate {
    PLAYING, MENU, OPTIONS, QUIT, PAUSE, SELECT_LOBBY, SERVER_SELECT;

    public static Gamestate state = MENU;
}
