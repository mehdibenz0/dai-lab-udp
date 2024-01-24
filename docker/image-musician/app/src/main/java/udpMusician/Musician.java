import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Musician {
    private String instrument;
    private String sound;
    private String uuid;

    public Musician(String instrument, String uuid) {
        this.instrument = instrument;
        this.sound = getSoundForInstrument(instrument);
        this.uuid = uuid;
    }

    public void playSound() {
        // Emit a sound every second (simulation)
        System.out.println(sound);
        sendDatagram(); // Send UDP datagram
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String getSoundForInstrument(String instrument) {
        switch (instrument) {
            case "piano":
                return "ti-ta-ti";
            case "trumpet":
                return "pouet";
            case "flute":
                return "trulu";
            case "violin":
                return "gzi-gzi";
            case "drum":
                return "boum-boum";
            default:
                return "unknown-sound";
        }
    }    

    private void sendDatagram() {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress group = InetAddress.getByName("239.255.22.5");
            int port = 9904;

            // Create JSON payload for UDP datagram
            String jsonPayload = String.format("{\"uuid\":\"%s\",\"sound\":\"%s\"}", uuid, sound);
            byte[] data = jsonPayload.getBytes();

            // Create UDP datagram packet
            DatagramPacket packet = new DatagramPacket(data, data.length, group, port);

            // Send the UDP datagram
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
