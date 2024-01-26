package dai;

import com.google.gson.GsonBuilder;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Musician {
    private String uuid;
    private String instrument;
    private String sound;
    private Date lastActivity;

    final static String IPADDRESS = "239.255.22.5";
    final static int PORT = 9904;

    private static final Map<String, String> INSTRUMENT_SOUND_MAP = new HashMap<>();

    public Musician(String instrument, String sound) {
        this.uuid = UUID.randomUUID().toString();
        this.instrument = instrument;
        this.sound = sound;
        this.lastActivity = new Date();
    }

    public static void main(String[] args) throws InterruptedException {
        try (DatagramSocket socket = new DatagramSocket()) {
            String instrument = args[0];
            if(args.length != 1) {
                throw new IllegalArgumentException("One argument required: instrument (piano, flute, drum, trumpet, violin)");
            }
            if (!INSTRUMENT_SOUND_MAP.containsKey(instrument)) {
                throw new IllegalArgumentException("Instrument not valid " + instrument);
            }
            
            String sound = INSTRUMENT_SOUND_MAP.get(instrument);
            Musician musician = new Musician(instrument, sound);

            String jsonPayload = String.format("{\"uuid\":\"%s\",\"instrument\":\"%s\",\"sound\":\"%s\"}", UUID.randomUUID(), sound, musician.lastActivity);
            String json = new GsonBuilder().create().toJson(jsonPayload);
            byte[] payload = json.getBytes(UTF_8);

            InetSocketAddress dest_address = new InetSocketAddress(IPADDRESS, PORT);
            DatagramPacket packet = new DatagramPacket(payload, payload.length, dest_address);
            socket.send(packet);
            System.out.println("Sent: " + json);

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

    public String getUuid() {
        return uuid;
    }

    public String getInstrument() {
        return instrument;
    }

    public Date getLastActivity() {
        return lastActivity;
    }
}
