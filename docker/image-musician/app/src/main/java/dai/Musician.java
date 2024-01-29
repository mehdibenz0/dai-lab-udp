package dai;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.*;

class Musician {
    private final static String IPADDRESS = "239.255.22.5";
    private final static int PORT = 9904;
    private static final Map<String, String> INSTRUMENT_SOUND_MAP = new HashMap<>();
    static {
        INSTRUMENT_SOUND_MAP.put("ti-ta-ti", "piano");
        INSTRUMENT_SOUND_MAP.put("pouet", "trumpet");
        INSTRUMENT_SOUND_MAP.put("trulu", "flute");
        INSTRUMENT_SOUND_MAP.put("gzi-gzi", "violin");
        INSTRUMENT_SOUND_MAP.put("boum-boum", "drum");
    }

    private String sound;
    private final UUID uuid;


    public Musician(String sound, UUID uuid) throws IOException {
        this.sound = sound;
        this.uuid = uuid;
    }

    public void run() {
        Gson gson = new Gson();
        String message = gson.toJson(this);
        byte[] payload = message.getBytes(UTF_8);
        DatagramPacket packet = new DatagramPacket(payload,
                payload.length, new InetSocketAddress(IPADDRESS, PORT));

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.send(packet);
            System.out.println("Sent packet: " + message);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        String instrument = args[0];
        if (args.length != 1){
            throw new IllegalArgumentException("Not enough or too many arguments.");
        }
        if (!INSTRUMENT_SOUND_MAP.containsKey(instrument)) {
            throw new IllegalArgumentException("Instrument not valid: ");
        }
        final int rythm = 1000;
        Musician musician = new Musician(INSTRUMENT_SOUND_MAP.get(instrument), UUID.randomUUID());
        while (true) {
                musician.run();
                try {
                    Thread.sleep(rythm);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

}
