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
    private final ExecutorService executor = Executors.newFixedThreadPool(2);
    private volatile boolean running = false;

    //UDP
    private DatagramSocket udpSocket;
    private InetAddress serverUdpAddress;
    private int serverUdpPort;
    private long lastInputSeqFromServer = -1;
    private long seqCounter = 0;


    private boolean clientReady = false;
    private boolean hostReady = false;

    private final Game game;
    private final Playing playing;


    public Client(Game game) {
        this.game = game;
        this.playing = game.getPlaying();

        game.getPlaying().setHostNumber(2);
        game.getPlaying().setRemoteNumber(1);
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
        //otvara tcp port i input/output streamove za komunikaciju
        tcpSocket = new Socket(host, port);
        dos = new DataOutputStream(new BufferedOutputStream(tcpSocket.getOutputStream()));
        dis = new DataInputStream(new BufferedInputStream(tcpSocket.getInputStream()));

        //otvara udp port na 1 milisekundu
        udpSocket = new DatagramSocket(0); // port 0 -> dodijeli slobodan port
        udpSocket.setSoTimeout(20);
        // pošalji info o udp portu serveru
        dos.writeInt(udpSocket.getLocalPort());
        dos.flush();

        serverUdpPort = dis.readInt();
        serverUdpAddress = InetAddress.getByName(host);
        System.out.println("Server UDP port: " + serverUdpPort);
        running = true;

        executor.submit(this::udpReceiveLoop);
        // pokrće se TCP
        executor.submit(this::tcpReaderLoop);
    }

    private void tcpReaderLoop() {
        try {
            while (running && !tcpSocket.isClosed()) {
                byte type = dis.readByte();
                switch (type) {
                    case TYPE_CHAR_SELECT -> {
                        System.out.println("server send char data");
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
                        System.out.println("Stage send stage data");
                        int stageIndex = dis.readInt();
                        System.out.println("Host choose stage: " + stageIndex);
                        game.getLobby().setStageIndex(stageIndex);
                        game.getPlaying().getLevelManager().setStageIndex(stageIndex);
                    }
                    case TYPE_READY -> {
                        boolean hostReady = dis.readBoolean();
                        System.out.println("Client received ready: " + hostReady);
                        setHostReady(hostReady);
                    }
                    case TYPE_START -> {
                        System.out.println("Client received start");
                        game.getPlaying().getHostPlayer().setSpawn(game.getPlaying().getLevelManager().getCurrentStage().getRemotePlayerSpawn());
                        game.getPlaying().getRemotePlayer().setSpawn(game.getPlaying().getLevelManager().getCurrentStage().getPlayerSpawn());
                        game.getPlaying().getHostPlayer().setFlipW(-1);
                        Gamestate.state = Gamestate.PLAYING;
                    }
                    case TYPE_EXIT ->{
                        System.out.println("Received exit message from server");
                        try {
                            tcpSocket.close();
                            udpSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Gamestate.state = Gamestate.MENU;
                        playing.setMatchEnd(false);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Lost connection to server: " + e.getMessage());
            closeConnections();
            Gamestate.state = Gamestate.MENU;
        } finally {
            disconnect();
        }
    }

    private void closeConnections() {
        try { tcpSocket.close(); } catch (IOException ignored) {}
        udpSocket.close();
    }

    private void udpReceiveLoop() {
        byte[] buf = new byte[512];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        while (running) {
            try {
                udpSocket.receive(packet);
                DataInputStream dis = new DataInputStream(
                        new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));
                byte type = dis.readByte();
                switch (type) {
                    case TYPE_INPUT -> {
                        long seq = dis.readLong();
                        int mask = dis.readInt();
                        if (seq > lastInputSeqFromServer) {
                            lastInputSeqFromServer = seq;
                            applyServerInput(mask);
                        }
                    }
                    case TYPE_STATE -> {
                        long tick = dis.readLong();
                        float p0x = dis.readFloat();
                        float p0y = dis.readFloat();
                        float p1x = dis.readFloat();
                        float p1y = dis.readFloat();
                    }
                }
            } catch (SocketTimeoutException ignored) {
            } catch (IOException e) {
                if (running) e.printStackTrace();
            }
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
                    data, data.length, serverUdpAddress, serverUdpPort);
            udpSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void applyServerInput(int mask) {
        if (game.getPlaying() != null) {
            game.getPlaying().setRemoteInputMask(mask);
        }
    }

    public void sendExit() {
        try {
            dos.writeByte(TYPE_EXIT);
            dos.flush();
            System.out.println("Client sent exit message.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        running = false;
        if (udpSocket != null) udpSocket.close();
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

    public void setClientReady_CheckStart(boolean ready) {
        clientReady = ready;
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

    private void checkStartGame() {
        System.out.println("Starting game - host : " + hostReady + "- client: " + clientReady);
        if (hostReady && clientReady) {
            game.getPlaying().resetAll();
            // stvara instance igrača i prebacuje ekran na odabranu arenu
            game.getPlaying().getHostPlayer().setSpawn(game.getPlaying().getLevelManager().getCurrentStage().getRemotePlayerSpawn());
            game.getPlaying().getRemotePlayer().setSpawn(game.getPlaying().getLevelManager().getCurrentStage().getPlayerSpawn());
            game.getPlaying().getHostPlayer().setFlipW(-1);
            Gamestate.state = Gamestate.PLAYING;
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
        checkStartGame();
    }

    public boolean isHostReady() {return hostReady;}
    public void setHostReady(boolean hostReady) {
        this.hostReady = hostReady;
        checkStartGame();
    }
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
