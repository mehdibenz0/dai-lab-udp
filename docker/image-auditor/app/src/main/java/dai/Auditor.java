package dai;

import com.google.gson.Gson;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Auditor {

    private static final int UDP_PORT = 9904;
    private static final String MULTICAST_ADDRESS = "239.255.22.5";
    private static final int TCP_PORT = 2205;
    private final Map<String, Musician> activeMusicians = new ConcurrentHashMap<>();
    private static final Map<String, String> SOUND_TO_INSTRU_MAP = new HashMap<>();
    private final int ACTIVITY_TIME_LIMIT = 5000;

    static {
        SOUND_TO_INSTRU_MAP.put("ti-ta-ti", "piano");
        SOUND_TO_INSTRU_MAP.put("pouet", "trumpet");
        SOUND_TO_INSTRU_MAP.put("trulu", "flute");
        SOUND_TO_INSTRU_MAP.put("gzi-gzi", "violin");
        SOUND_TO_INSTRU_MAP.put("boum-boum", "drum");
    }

    private static class MusicianData {
        String uuid;
        String sound;
    }

    private static class Musician {
        String uuid;
        String instrument;
        long lastActivity;

        Musician(String uuid, String instrument, long lastActivity){
            this.uuid = uuid;
            this.instrument = instrument;
            this.lastActivity = lastActivity;
        }
    }
    public static void main(String[] args) {
        Auditor auditor = new Auditor();
        auditor.startUDPListener();
        auditor.startTCPServer();
    }

    private void startUDPListener() {
        new Thread(() -> {
            try (MulticastSocket socket = new MulticastSocket(UDP_PORT)) {
                InetSocketAddress group_address =  new InetSocketAddress(MULTICAST_ADDRESS, UDP_PORT);
                NetworkInterface netif = NetworkInterface.getByName("eth0");
                socket.joinGroup(group_address, netif);
                while (true) {
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    String received = new String(packet.getData(), 0, packet.getLength());
                    processMessage(received);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void processMessage(String messageJson) {
        MusicianData musicianData = new Gson().fromJson(messageJson, MusicianData.class);
        String instrument = SOUND_TO_INSTRU_MAP.get(musicianData.sound);

        if (instrument == null) {
            System.out.println("Unknown sound received: " + musicianData.sound);
            return;
        }
        // Add musician to the list
        Musician musician = new Musician( musicianData.uuid, instrument, System.currentTimeMillis());
        activeMusicians.put(musician.uuid, musician);
        // Remove inactive musicians
        long currentTime = System.currentTimeMillis();
        activeMusicians.entrySet().removeIf(entry -> currentTime - entry.getValue().lastActivity > ACTIVITY_TIME_LIMIT);
    }

    private void startTCPServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(TCP_PORT)) {
                while (true) {
                    try (Socket clientSocket = serverSocket.accept();
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                        // Remove inactive musicians
                        long currentTime = System.currentTimeMillis();
                        activeMusicians.entrySet().removeIf(entry -> currentTime - entry.getValue().lastActivity > ACTIVITY_TIME_LIMIT);
                        // Send active musicians
                        String jsonResponse = new Gson().toJson(new ArrayList<>(activeMusicians.values()));
                        out.println(jsonResponse);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
