package network;

public class NetworkProtocol {
    public static final byte TYPE_INPUT = 1;
    public static final byte TYPE_STATE = 2;
    public static final byte TYPE_PING = 3;

    //Lobby select
    public static final byte TYPE_CHAR_SELECT = 4;
    public static final byte TYPE_STAGE_SELECT = 5; // samo server odlucuje
    public static final byte TYPE_READY = 6;
    public static final byte TYPE_START = 7;
    public static final byte TYPE_EXIT_PLAY = 8;
}
