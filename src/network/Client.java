package network;

import gamestates.Gamestate;
import gamestates.Playing;
import main.Game;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

import static network.NetworkProtocol.*;

public class Client {
    private Socket tcpSocket;
    private DataOutputStream dos;
    private DataInputStream dis;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean running = false;

    private final Game game;
    private final Playing playing;

    private boolean clientReady = false;
    private boolean hostReady = false;

    public Client(Game game) {
        this.game = game;
        this.playing = game.getPlaying();
    }

    // Otkrivanje servera UDP protokolom
    public ArrayList<DiscoveredServer> discoverServers(int timeoutMs) throws IOException {
        ArrayList<DiscoveredServer> servers = new ArrayList<>();
        DatagramSocket sock = new DatagramSocket(8888);
        sock.setSoTimeout(timeoutMs);
        byte[] buffer = new byte[512];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        long end = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < end) {
            try {
                sock.receive(packet);
                DataInputStream dis = new DataInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));
                String name = dis.readUTF();
                int port = dis.readInt();
                int current = dis.readInt();
                int max = dis.readInt();
                DiscoveredServer ds = new DiscoveredServer(packet.getAddress().getHostAddress(), port, name, current, max);
                // provjera duplikata
                boolean exists = servers.stream().anyMatch(s -> s.address.equals(ds.address) && s.port == ds.port);
                if (!exists) servers.add(ds);
            } catch (SocketTimeoutException ignored) {
                break;
            }
        }
        sock.close();
        return servers;
    }

    public void connect(String host, int port) throws IOException {
        tcpSocket = new Socket(host, port);
        dos = new DataOutputStream(new BufferedOutputStream(tcpSocket.getOutputStream()));
        dis = new DataInputStream(new BufferedInputStream(tcpSocket.getInputStream()));
        running = true;

        // Start reader thread
        executor.submit(() -> {
            try {
                while (running && !tcpSocket.isClosed()) {
                    byte type = dis.readByte();
                    switch (type) {
                        case TYPE_INPUT -> {
                            long seq = dis.readLong();
                            int mask = dis.readInt();
                            // parse optional floats if you used them
                            // Apply client input to remote player (player index 1)
                            applyServerInput(mask);
                        }
                        case TYPE_STATE -> {
//                            long tick = dis.readLong();
                            float p0x = dis.readFloat();
                            float p0y = dis.readFloat();
//                            float p0vx = dis.readFloat();
//                            float p0vy = dis.readFloat();
                            float p0health = dis.readFloat();
                            float p1x = dis.readFloat();
                            float p1y = dis.readFloat();
//                            float p1vx = dis.readFloat();
//                            float p1vy = dis.readFloat();
                            float p1health = dis.readFloat();
                        }
                        case TYPE_CHAR_SELECT -> {
                            int playerIndex = dis.readInt();
                            int charIndex = dis.readInt();
                            System.out.println("Host send char data: "+ playerIndex + " " + charIndex);
                            if (playerIndex == 1) {
                                game.getPlaying().setPlayerCharacter(game.getLobby().getPlayerCharacterList().get(charIndex));
                            }
                            else{
                                game.getPlaying().setRemotePlayerCharacter(game.getLobby().getPlayerCharacterList().get(charIndex));
                            }
                        }
                        case TYPE_STAGE_SELECT -> {
                            int stageIndex = dis.readInt();
                            System.out.println("Host choose stage: " + stageIndex);
                            game.getLobby().setStageIndex(stageIndex);
                            game.getPlaying().getLevelManager().setStageIndex(stageIndex);
                        }
                        case TYPE_START -> {
                            game.getPlaying().getHostPlayer().setSpawn(game.getPlaying().getLevelManager().getCurrentStage().getRemotePlayerSpawn());
                            game.getPlaying().getRemotePlayer().setSpawn(game.getPlaying().getLevelManager().getCurrentStage().getPlayerSpawn());
                            game.getPlaying().getHostPlayer().setFlipW(-1);
                            Gamestate.state = Gamestate.PLAYING;
                        }
                    }
                }
            } catch (IOException e) {
                if (running) e.printStackTrace();
            } finally {
                disconnect();
            }
        });
    }

    private void applyServerInput(int mask) {
        if (game.getPlaying() != null) {
            game.getPlaying().setRemoteInputMask(mask);
        }
    }

    // send input (bitmask)
    public void sendInput(long seq, int mask) {
        if (dos == null) return;
        try {
            dos.writeByte(NetworkProtocol.TYPE_INPUT);
            dos.writeLong(seq);
            dos.writeInt(mask);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            disconnect();
        }
    }

    public void disconnect() {
        running = false;
        try { if (tcpSocket != null) tcpSocket.close(); } catch (IOException ignored) {}
    }
    public void sendCharacterSelection(int charIndex) {
        try {
            dos.writeByte(TYPE_CHAR_SELECT);
            dos.writeInt(1);
            dos.writeInt(charIndex);

            System.out.println("Client sending char index: "+charIndex);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendReady() {
        if (dos == null) return;
        try {
            clientReady=true;
            System.out.println("Client is sending ready msg...");
            dos.writeByte(TYPE_READY);
            dos.writeBoolean(clientReady);
            dos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isHostReady() {return hostReady;}
    public void setHostReady(boolean hostReady) {this.hostReady = hostReady;}
    public boolean isClientReady() {return clientReady;}
    public void setClientReady(boolean clientReady) {this.clientReady = clientReady;}
    public boolean getPlayersReady() {return hostReady && clientReady;}
    public void resetVariables() {
        hostReady = false;
        clientReady = false;
    }

    public static class DiscoveredServer {
        public final String address;
        public final int port;
        public final String name;
        public final int current;
        public final int max;

        public DiscoveredServer(String address, int port, String name, int current, int max){
            this.address = address; this.port = port; this.name = name; this.current = current; this.max = max;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DiscoveredServer other)) return false;
            return port == other.port &&
                    Objects.equals(address, other.address) &&
                    Objects.equals(name, other.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, address, port);
        }

        @Override
        public String toString(){
            return name + " - " + address + ":" + port + " (" + current + "/" + max + ")";
        }

        public String getAddress() {return address;}
        public int getPort() {return port;}
        public String getName() {return name;}
        public int getCurrent() {return current;}
        public int getMax() {return max;}
    }
}
