import java.util.*;

import com.google.gson.JsonObject;

public class ContentServer {
    private Lamport clock;
    private SocketServer socketServer;
    private String source;
    private JsonObject data;

    public ContentServer(SocketServer socket) {
        this.source = UUID.randomUUID().toString();
        this.socketServer = socket;
        this.clock = new Lamport();
    }

    public boolean isLoadFileSuccess(String filePath) {
        try {
            String fileContent = JsonHandling.read(filePath);
            this.data = JsonHandling.convertTextToJson(fileContent);
            return true;
        } catch (Exception e) {
            System.out.println("Error on loading file " + e.getMessage());
            return false;
        }
    }

    public void uploadData() {
        
        try {
            System.out.println("Upload data: ");
            int lamportClockServer = this.socketServer.initializeSocketandGetLamport("localhost", 4000);
            System.out.println("lamport data: ");
            this.clock.adjust(lamportClockServer);
            System.out.println("Updated Lamport clock 1: " + this.clock.getTime());
            String dataString = JsonHandling.prettier(this.data);
            
            String putRequest = "PUT /weather.json HTTP/1.1\r\n" +
                            "Content-Length: " + dataString.length() + "\r\n" +
                            "LamportClock: " + this.clock.getTime() + "\r\n" +
                            "Source: " + this.source + "\r\n" +
                            "\r\n" +
                            dataString;
            String res = this.socketServer.sendAndReceiveData("localhost", 4000, putRequest, true);
            System.out.println("Response data: ");
            System.out.println(res);
            if (res != null) {
                String[] lines = res.split("\r\n");
                for (String line : lines) {
                    if (line.startsWith("Lamport: ")) {
                        int serverClock = Integer.parseInt(line.split(": ")[1]);
                        this.clock.adjust(serverClock);
                        System.out.println("serverClock: " + serverClock);
                        System.out.println("Updated Lamport clock 2: " + this.clock.getTime());
                        break;
                    }
                }
                
                String statusCode = res.split(" ")[1];
                switch (statusCode) {
                    case "200":
                    case "201":
                        System.out.println("Weather data uploaded successfully.");
                        break;
                    case "503":
                        System.out.println("Service Unavailable.");
                        break;
                    case "500":
                        System.out.println("Technical error");
                        break;
                    default:
                        System.out.println("Unexpected response: " + res);
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Error while connecting to the server: " + e.getMessage());
            System.out.println("Retry in 15 second.");
        }
    }

    public static void main(String[] args) {
        int port;
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("No port provided");
            return;
        }

        String file = args[2];

        SocketServer socketHandler = new SocketServer();
        ContentServer server = new ContentServer(socketHandler);

        if (!server.isLoadFileSuccess(file)) {
            System.out.println("Error: Failed to load data from " + file);
            return;
        }
        server.uploadData();

        Thread monitorThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String input = scanner.nextLine();
                if ("SHUTDOWN".equalsIgnoreCase(input)) {
                    // shutdown();
                    scanner.close();
                    break;
                }
            }
        });
        monitorThread.start();
    }
}
