import java.io.*;
import com.google.gson.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * AggregationServer
 */
public class AggregationServer {
    private SocketServer socketServer;
    private Lamport clock;
    private int port;
    private boolean isDown;
    private LinkedBlockingQueue<Socket> reqQueue;
    private List<String> recentWeatherData = new ArrayList<>();
    private static DatabaseManagement db = DatabaseManagement.initialize();
    private long EXPIRY = 40000; // 40 seconds

    public AggregationServer(SocketServer socketServer) {
        this.socketServer = socketServer;
        this.clock = new Lamport();
        this.reqQueue = new LinkedBlockingQueue<>();

    }

    public boolean isUp() {
        try {
            Socket ping = new Socket();
            ping.connect(new InetSocketAddress("localhost", port), 1000); // 1 second timeout
            ping.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    public void start(int port) {
        System.out.println("AggregationServer started on: " + port);
        this.port = port;
        this.socketServer.start(port);
        this.initializeAcceptThread();
    }

    private void initializeAcceptThread() {
        new Thread(() -> {
            while (!isDown) {
                try {
                    Socket clientSocket = socketServer.accept();
                    if (clientSocket != null) {
                        handleClientSocket(clientSocket);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void accept(Socket clientSocket) {
        try {
            System.out.println(this.port + " received socket: " + clientSocket);
            this.reqQueue.put(clientSocket);
            String c = "Lamport: " + this.clock.getTime();
            PrintWriter send = new PrintWriter(clientSocket.getOutputStream(), true);
            send.println(c);
            send.flush();
            this.clock.tick();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleClientSocket(Socket clientSocket) {
        try {
            this.accept(clientSocket);
            handleData(clientSocket);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                clientSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }


    private void handleData(Socket clientSocket) {
        try {
            String req = this.socketServer.request(clientSocket);
            System.out.println(req);
            if (req != null) {
                
                String responseData = normalizeReq(req);
                System.out.println("Response data to client: " + responseData);
                this.socketServer.response(responseData, clientSocket);
                
            }
        } catch(Exception e) {
            e.printStackTrace(); // Depending on your use-case, you might want to handle this differently.
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String normalizeReq(String requestData) {
        String[] lines = requestData.split("\r\n");
        String requestType = lines[0].split(" ")[0].trim();

        Map<String, String> headers = new HashMap<>();
        StringBuilder contentBuilder = new StringBuilder();

        boolean readingContent = false;

        for (int i = 1; i < lines.length; i++) {
            if (!readingContent) {
                if (lines[i].isEmpty()) {
                    readingContent = true;
                } else {
                    String[] headerParts = lines[i].split(": ", 2);
                    headers.put(headerParts[0], headerParts[1]);
                }
            } else {
                contentBuilder.append(lines[i]);
            }
        }

        String content = contentBuilder.toString();
        switch (requestType.toUpperCase()) {
            case "GET":
            return "";
            case "PUT":
            return handlePutRequest(content, headers);
            default:
            return formatRes("400 Bad Request", null);
        }
    }

    private String handlePutRequest(String content, Map<String, String> headers) {
        try {
            JsonObject jsonData = JsonHandling.convertObject(content, JsonObject.class);
            String id = getIdData(jsonData);
            if (id == null && id.isEmpty()) {
                return formatRes("500 Internal Server Error", null);
            }
            String source = headers.get("Source");
            WeatherFormat newWeatherData = new WeatherFormat(this.getLamport(headers), source, jsonData);
            db.saveData(id, newWeatherData);

            long currTime = System.currentTimeMillis();
            Long latest = db.getSenderTimestamp(source);

            db.saveTime(source, currTime);
            if (latest == null || (currTime - latest) > EXPIRY) {
                return formatRes("201 HTTP_CREATED", null);
            } else return formatRes("200 OK", null);
            
        } catch (Exception e) {
            System.out.println(e);
            return formatRes("500 Internal Server Error", null);
        }
    }

    public String getIdData(JsonObject jsonData) {
        return jsonData.has("id") ? jsonData.get("id").getAsString() : null;
    }

    public int getLamport(Map<String, String> headers) {
        int lamport = Integer.parseInt(headers.getOrDefault("LamportClock", "-1"));
        this.clock.adjust(lamport);
        this.clock.tick();
        return lamport;
    }

    private String formatRes(String status, JsonObject jsonData) {
        StringBuilder res = new StringBuilder();

        res.append("HTTP/1.1 ").append(status).append("\r\n");
        res.append("Lamport: ").append(this.clock.getTime()).append("\r\n");
        if (jsonData != null) {
            String prettyData = JsonHandling.prettier(jsonData);
            res.append("Content-Type: application/json\r\n");
            res.append("Content-Length: ").append(prettyData.length()).append("\r\n");
            res.append("\r\n");
            res.append(prettyData);
        } else {
            res.append("\r\n");
        }

        return res.toString();
    }

    private void saveDataToFile(String data){
        String rootDir = "data/";

        // Create the data directory if it doesn't exist
        File directory = new File(rootDir);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                System.out.println("Data directory created: " + rootDir);
            } else {
                System.err.println("Failed to create data directory: " + rootDir);
                return;
            }
        }

        // Store the data in a file with a unique filename based on the server ID and
        // timestamp
        try {
            String fileName = rootDir + "_" + System.currentTimeMillis() + ".json";
            FileWriter fileWriter = new FileWriter(fileName);
            fileWriter.write(data);
            fileWriter.close();
        } catch (IOException e) {
            System.err.println("Error storing data: " + e.getMessage());
        }
    }
    public static void main(String[] args) {
        int port = 4000;
        SocketServer socketServer = new SocketServer();
        AggregationServer aggregationServer = new AggregationServer(socketServer);
        aggregationServer.start(port);
        
    }
}