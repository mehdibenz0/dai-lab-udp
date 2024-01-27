package dai;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Musician {
    private static UUID uuid;
    private String instrument;
    private String sound;
    private long lastActivity;

    final static String IPADDRESS = "239.255.22.5";
    final static int PORT = 9904;

    private static final Map<String, String> INSTRUMENT_SOUND_MAP = new HashMap<>();

    public Musician(UUID uuid, String sound) {
        this.uuid = uuid;
        this.sound = sound;
    }

    public static void main(String[] args) throws InterruptedException {

        try (DatagramSocket socket = new DatagramSocket()) {
            if (args.length != 1) {
                throw new IllegalArgumentException("One argument required: instrument (piano, flute, drum, trumpet, violin)");
            }
    
            String instrument = args[0];
            if (!INSTRUMENT_SOUND_MAP.containsKey(instrument)) {
                throw new IllegalArgumentException("Instrument not valid: " + instrument);
            }
    
            String sound = INSTRUMENT_SOUND_MAP.get(instrument);
            Musician musician = new Musician(uuid, sound);
    
            // Serialize musician object to JSON
            String jsonPayload = musician.toJson();
    
            byte[] payload = jsonPayload.getBytes(UTF_8);
    
            InetSocketAddress dest_address = new InetSocketAddress(IPADDRESS, PORT);
            DatagramPacket packet = new DatagramPacket(payload, payload.length, dest_address);
            socket.send(packet);
    
            Thread.sleep(1000);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    


    static {
        INSTRUMENT_SOUND_MAP.put("piano", "ti-ta-ti");
        INSTRUMENT_SOUND_MAP.put("flute", "trulu");
        INSTRUMENT_SOUND_MAP.put("drum", "boum-boum");
        INSTRUMENT_SOUND_MAP.put("trumpet", "pouet");
        INSTRUMENT_SOUND_MAP.put("violin", "gzi-gzi");
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
