package network;

import gamestates.Gamestate;
import main.Game;
import gamestates.Playing;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static network.NetworkProtocol.*;
import static utils.Constants.NetworkConstants.*;

public class Server {
    private final int tcpPort;
    //  referenca na instancu
    private final Game game;
    private final Playing playing;
    private ServerSocket serverSocket;
    private volatile Socket clientSocket;
    private ExecutorService executor = Executors.newCachedThreadPool();
    private AtomicBoolean running = new AtomicBoolean(false);
    private DataOutputStream dos;

    //UDP discovery
    private DatagramSocket announceSocket;
    private ScheduledExecutorService announcer;

    private boolean clientReady = false;
    private boolean hostReady = false;

    public Server(Game game, int tcpPort) {
        this.game = game;
        this.tcpPort = tcpPort;
        this.playing = game.getPlaying();

        game.getPlaying().setHostNumber(1);
        game.getPlaying().setRemoteNumber(2);
    }

    public void start() throws IOException {
        running.set(true);
        serverSocket = new ServerSocket(tcpPort);
        startAnnouncer();
        System.out.println("Server started on port " + tcpPort);

        // accept client in background
        executor.submit(() -> {
            try {
                System.out.println("Server: waiting for client on port " + tcpPort);
                clientSocket = serverSocket.accept();
                System.out.println("Server: client connected: " + clientSocket.getInetAddress());
                handleClient(clientSocket);
            } catch (IOException e) {
                if (running.get()) e.printStackTrace();
            }
        });
    }

    private void startAnnouncer() throws SocketException {
        announceSocket = new DatagramSocket();
        announceSocket.setBroadcast(true);
        announcer = Executors.newSingleThreadScheduledExecutor();
        announcer.scheduleAtFixedRate(() -> {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(baos);
                dos.writeUTF("MyGameServer"); // server name
                dos.writeInt(tcpPort);
                dos.writeInt(1); // currentPlayers (host)
                dos.writeInt(MAX_PLAYERS); // maxPlayers
                byte[] data = baos.toByteArray();
                DatagramPacket packet = new DatagramPacket(data, data.length,
                        InetAddress.getByName("255.255.255.255"), 8888);
                announceSocket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void handleClient(Socket socket) throws IOException {
        try {
            DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            executor.submit(() -> {
                try {
                    while (running.get() && !socket.isClosed()) {
                        byte type = dis.readByte();
                        switch (type) {
                            case TYPE_INPUT -> {
                                long seq = dis.readLong();
//                                int playerIndex = dis.readInt();
                                int mask = dis.readInt();
                                applyClientInput(mask);
                            }
                            case TYPE_CHAR_SELECT -> {
                                int playerIndex = dis.readInt();
                                int charIndex = dis.readInt();
                                game.getPlaying().setRemotePlayerCharacter(game.getLobby().getPlayerCharacterList().get(charIndex));
                                broadcastCharacterSelection(1, charIndex);
                            }
                            case TYPE_READY -> {
                                boolean ready = dis.readBoolean();
                                setClientReady(ready);
                            }
                            case TYPE_EXIT_PLAY ->{
                            }
                        }
                    }
                } catch (IOException e) {
                    if (running.get()) e.printStackTrace();
                }
            });
            while (running.get() && !socket.isClosed()) {
//                sendStateSnapshot(dos);
                // 20 updates/sec
                Thread.sleep(50);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            resetVariables();
            closeClient();
        }
    }

    private void applyClientInput(int mask) {
        if (game.getPlaying() != null) {
            game.getPlaying().setRemoteInputMask(mask);
        }
    }
    public void sendInput(long seq, int mask) {
        if (dos == null) return;
        try {
            dos.writeByte(NetworkProtocol.TYPE_INPUT);
            dos.writeLong(seq);
            dos.writeInt(mask);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            closeClient();
        }
    }

    private void sendStateSnapshot(DataOutputStream dos) throws IOException {
        // Build snapshot from the server's authoritative state
        // grab local player (host) and remote player (client)
        // NB: you must ensure playing.getPlayer() returns host and you have remotePlayer object accessible
        // For example: playing maintains player0 and player1.
        dos.writeByte(NetworkProtocol.TYPE_STATE);
        dos.writeLong(System.currentTimeMillis()); // server tick

        // host (player 0)
        float p0x = playing.getHostPlayer().getX();
        float p0y = playing.getHostPlayer().getY();
//        float p0vx = playing.getPlayer().getVx();
//        float p0vy = playing.getPlayer().getVy();
//        float p0health = playing.getHostPlayer().getHealth();
        dos.writeFloat(p0x);
        dos.writeFloat(p0y);
        //dos.writeFloat(p0vx);
        //dos.writeFloat(p0vy);
//        dos.writeFloat(p0health);

        // remote (player 1) - you will need a second player reference
        // (if you store remote player in playing.getRemotePlayer())
        if (playing.getRemotePlayer() != null) {
            dos.writeFloat(playing.getRemotePlayer().getX());
            dos.writeFloat(playing.getRemotePlayer().getY());
            //dos.writeFloat(playing.getRemotePlayer().getVx());
            //dos.writeFloat(playing.getRemotePlayer().getVy());
//            dos.writeFloat(playing.getRemotePlayer().getHealth());
        } else {
            // send placeholders
            dos.writeFloat(0);
            dos.writeFloat(0);
            //dos.writeFloat(0);
            //dos.writeFloat(0);
            dos.writeFloat(100);
        }

        dos.flush();
    }

    public void broadcastStageSelection(int stageIndex) {
        if (clientSocket == null) return;
        try {
             synchronized (dos) {
                 dos.writeByte(TYPE_STAGE_SELECT);
                 dos.writeInt(stageIndex);
                 dos.flush();
             }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void broadcastCharacterSelection(int playerId, int charIndex) throws IOException {
        if (clientSocket == null) return;
        try {
            synchronized (dos) {
                dos.writeByte(TYPE_CHAR_SELECT);
                dos.writeInt(playerId);
                dos.writeInt(charIndex);
                dos.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void checkStartGame() {
        if (hostReady && clientReady) {
            try {
//                DataOutputStream dos = new DataOutputStream(
//                        new BufferedOutputStream(clientSocket.getOutputStream()));
                dos.writeByte(TYPE_START);
                dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Also change local Gamestate to PLAYING
            game.getPlaying().getHostPlayer().setSpawn(game.getPlaying().getLevelManager().getCurrentStage().getPlayerSpawn());

            game.getPlaying().getRemotePlayer().setSpawn(game.getPlaying().getLevelManager().getCurrentStage().getRemotePlayerSpawn());
            game.getPlaying().getRemotePlayer().setFlipW(-1);
            Gamestate.state = Gamestate.PLAYING;
        }
    }
    public void closeClient() {
        try { if (clientSocket != null) clientSocket.close(); } catch (IOException ignored) {}
        clientSocket = null;
        clientReady = false;
    }
    public void stop() {
        running.set(false);
        try { if (serverSocket != null) serverSocket.close(); } catch (IOException ignored) {}
        if (announcer != null) announcer.shutdownNow();
        if (announceSocket != null) announceSocket.close();
        executor.shutdownNow();
    }

    public void setHostReady_CheckStart(boolean ready) {
        hostReady = ready;
        checkStartGame();
    }
    public void setClientReady(boolean ready) {
        clientReady = ready;
        checkStartGame();
    }
    public boolean getPlayersReady() {return hostReady && clientReady;}
    public void resetVariables() {
        hostReady = false;
        clientReady = false;
    }
}
