
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Auditor {
    /*private List<Musician> activeMusicians;

    public Auditor(List<Musician> activeMusicians) {
        this.activeMusicians = activeMusicians;
    }
*/
    public void startTcpServer() {
        try (ServerSocket serverSocket = new ServerSocket(2205)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();

                sendJsonPayload(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendJsonPayload(Socket clientSocket) {
        try (OutputStream outputStream = clientSocket.getOutputStream()) {
            String jsonPayload = generateJsonPayload();

            outputStream.write(jsonPayload.getBytes());
            outputStream.flush();

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized String generateJsonPayload() {
        Gson gson = new GsonBuilder().create();
        //String jsonPayload = gson.toJson(activeMusicians);

        return null;
    
}
}
