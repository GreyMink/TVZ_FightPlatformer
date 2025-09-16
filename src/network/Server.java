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
    //UDP messaging
    private DatagramSocket udpSocket;
    private InetAddress clientUdpAddress;
    private int clientUdpPort;
    private long lastInputSeqFromClient = -1;
    private long seqCounter = 0;

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
        udpSocket = new DatagramSocket(0); // 0 -> bilo koji slobodni port
        startAnnouncer();
        System.out.println("Server started on TCP " + tcpPort + " UDP " + udpSocket.getLocalPort());
        executor.submit(this::udpReceiveLoop);

        // accept client in background
        executor.submit(() -> {
            try {
                System.out.println("Server: waiting for client on port " + tcpPort);
                clientSocket = serverSocket.accept();
                System.out.println("Server: client connected: " + clientSocket.getInetAddress());
                handleClientHandshake(clientSocket);
                handleClientTCP(clientSocket);
            } catch (IOException e) {if (running.get()) e.printStackTrace();}
        });
    }

    private void udpReceiveLoop() {
        byte[] buf = new byte[512];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        while (running.get()) {
            try {
                udpSocket.receive(packet);
                DataInputStream dis = new DataInputStream(
                        new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));
                byte type = dis.readByte();
                switch (type) {
                    case TYPE_INPUT -> {
                        long seq = dis.readLong();
                        int mask = dis.readInt();
                        // odbaci duplikate
                        if (seq > lastInputSeqFromClient) {
                            lastInputSeqFromClient = seq;
                            applyClientInput(mask);
                        }
                    }


                }
            } catch (SocketTimeoutException ignored) {
            } catch (IOException e) {
                if (running.get()) e.printStackTrace();
            }
        }
    }

    private void handleClientHandshake(Socket clientSocket) throws IOException {
        DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
        //čitaj klijent UDP port
        int clientPort = dis.readInt();
        clientUdpPort = clientPort;
        clientUdpAddress = clientSocket.getInetAddress();
        //šalje serverov UDP port klijentu
        dos.writeInt(udpSocket.getLocalPort());
        dos.flush();
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

    private void handleClientTCP(Socket socket) throws IOException {
        try {
            DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            executor.submit(() -> {
                try {
                    while (running.get() && !socket.isClosed()) {
                        byte type = dis.readByte();
                        switch (type) {
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
                            case TYPE_EXIT ->{
                                System.out.println("Client disconnected");
                                broadcastExit();
                                try {
                                    clientSocket.close();
                                    udpSocket.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
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
    public void sendInputUDP(long seq, int mask) {
        if (udpSocket == null) return;
        seqCounter++;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            out.writeByte(TYPE_INPUT);
            out.writeLong(seq);
            out.writeInt(mask);
            byte[] data = baos.toByteArray();
            DatagramPacket packet = new DatagramPacket(
                    data, data.length, clientUdpAddress, clientUdpPort);
            udpSocket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
                closeClient();
            }
    }
    public void sendStateSnapshotUDP() {
        if (clientUdpAddress == null) return;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            out.writeByte(TYPE_STATE);
            out.writeLong(System.currentTimeMillis());
            out.writeFloat(playing.getHostPlayer().getX());
            out.writeFloat(playing.getHostPlayer().getY());
            if (playing.getRemotePlayer() != null) {
                out.writeFloat(playing.getRemotePlayer().getX());
                out.writeFloat(playing.getRemotePlayer().getY());
            } else {
                out.writeFloat(0); out.writeFloat(0);
            }
            byte[] data = baos.toByteArray();
            DatagramPacket packet = new DatagramPacket(
                    data, data.length, clientUdpAddress, clientUdpPort);
            udpSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastStageSelection(int stageIndex) {
        if (clientSocket == null) {
            System.out.println("Stage broadcast - No clientSocket");
            return;}
        System.out.println("Stage broadcast - sending clientSocket");
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

    private void broadcastExit() {
        synchronized (dos) {
            try {
                dos.writeByte(TYPE_EXIT);
                dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void sendExitToClients() {
        synchronized (dos) {
            try {
                dos.writeByte(TYPE_EXIT);
                dos.flush();
                System.out.println("Server broadcast exit");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeClient() {
        try { if (clientSocket != null) clientSocket.close(); } catch (IOException ignored) {}
        clientSocket = null;
        clientReady = false;
    }
    public void stop() {
        running.set(false);
        if (udpSocket != null) udpSocket.close();
        try { if (serverSocket != null) serverSocket.close(); } catch (IOException ignored) {}
        if (announcer != null) announcer.shutdownNow();
        if (announceSocket != null) announceSocket.close();
        executor.shutdownNow();
    }

    public void sendReady(boolean ready) {
        hostReady = ready;
        if (dos != null) {
            try {
                synchronized (dos) {
                    dos.writeByte(TYPE_READY);
                    dos.writeBoolean(ready);
                    dos.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Server send ready");
        checkStartGame();
    }
    public void setClientReady(boolean ready) {
        clientReady = ready;
        checkStartGame();
    }
    public void checkStartGame() {
        System.out.println("Starting game - host : " + hostReady + "- client: " + clientReady);
        if (hostReady && clientReady) {
            try {
                synchronized (dos){
                    dos.writeByte(TYPE_START);
                    dos.flush();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            game.getPlaying().resetAll();
            // stvara instance igrača i prebacuje ekran na odabranu arenu
            game.getPlaying().getHostPlayer().setSpawn(game.getPlaying().getLevelManager().getCurrentStage().getPlayerSpawn());
            game.getPlaying().getRemotePlayer().setSpawn(game.getPlaying().getLevelManager().getCurrentStage().getRemotePlayerSpawn());
            game.getPlaying().getRemotePlayer().setFlipW(-1);
            Gamestate.state = Gamestate.PLAYING;
        }
    }
    public boolean getPlayersReady() {return hostReady && clientReady;}
    public void resetVariables() {
        hostReady = false;
        clientReady = false;
    }
    public boolean isClientReady() {return clientReady;}
    public boolean isHostReady() {return hostReady;}
    public Socket getClientSocket() {return clientSocket;}
}
