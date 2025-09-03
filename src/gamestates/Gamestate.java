package gamestates;

public enum Gamestate {
    PLAYING, MENU, OPTIONS, QUIT, PAUSE, SELECT_LOBBY;

    public static Gamestate state = MENU;
}
