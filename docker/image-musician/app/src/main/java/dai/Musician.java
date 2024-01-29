package dai;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.google.gson.Gson;

public class Musician {

    private static final String MULTICAST_ADDRESS = "239.255.22.5";
    private static final int PORT = 9904;
    private final UUID uuid;
    private final String instrumentSound;
    private static final Map<String, String> INSTRU_TO_SOUND_MAP = new HashMap<>();

    static {
        INSTRU_TO_SOUND_MAP.put("piano", "ti-ta-ti");
        INSTRU_TO_SOUND_MAP.put("trumpet", "pouet");
        INSTRU_TO_SOUND_MAP.put("flute", "trulu");
        INSTRU_TO_SOUND_MAP.put("violin", "gzi-gzi");
        INSTRU_TO_SOUND_MAP.put("drum", "boum-boum");
    }

    private static class MusicianData {
        String uuid;
        String sound;

        MusicianData(String uuid, String sound){
            this.uuid = uuid;
            this.sound = sound;
        }
    }

    public Musician(String instrument) {
        this.uuid = UUID.randomUUID();
        this.instrumentSound = INSTRU_TO_SOUND_MAP.getOrDefault(instrument, "Unknown Instrument");
    }

    public static void main(String[] args) {
        String instrument = args[0];
        if (args.length != 1){
            throw new IllegalArgumentException("Not enough or too many arguments.");
        }
        if (!INSTRU_TO_SOUND_MAP.containsKey(instrument)) {
            throw new IllegalArgumentException("Instrument not valid: ");
        }
        new Musician(instrument).play();
    }

    public void play() {
        try (DatagramSocket socket = new DatagramSocket();){
            while (true) {
                String messageJson = new Gson().toJson(new MusicianData(uuid.toString(), instrumentSound));
                byte[] buffer = messageJson.getBytes();
                InetSocketAddress dest_address = new InetSocketAddress(MULTICAST_ADDRESS, PORT);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, dest_address);
                socket.send(packet);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
