import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class SocketServer {
    private ServerSocket server;
    private Socket client;
    private PrintWriter outLog;
    private BufferedReader inLog;

    public void start(int port) {
        this.close();
        try {
            this.server = new ServerSocket(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void close() {
        try {
            if (this.server != null) this.server.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Socket accept() throws IOException {
        if (this.server == null || this.server.isClosed()) {
            throw new IllegalStateException("Server closed");
        }

        this.server.setSoTimeout(1000);
        try {
            return this.server.accept();
        } catch (SocketTimeoutException e) {
            if(Thread.currentThread().isInterrupted()){
                throw new IOException("Socket interrupted error", e);
            }
            return null;
        } catch (SocketException e) {
            if ("Socket closed".equals(e.getMessage())) {
                System.out.println("Server closed, no longer accepting connections");
                return null;
            } else {
                throw e;
            }
        }
    }

    public int initializeSocketandGetLamport(String serverName, int portNumber) {
        this.close();
        try {
            this.client = new Socket(serverName, portNumber);
            
            this.outLog = new PrintWriter(client.getOutputStream(), true);
            
            this.inLog = new BufferedReader(new InputStreamReader(client.getInputStream()));
            
            String res = this.inLog.readLine();
            System.out.println("connecting socket");
            System.out.println("response");
            System.out.println(res);
            if (res == null) {
                throw new IOException("Server closed the connection unexpectedly.");
            }

            String[] resPlitted = res.split(":");
            String responseType = resPlitted[0].trim();
            String responseValue = resPlitted.length > 1 ? resPlitted[1].trim() : "";

            switch (responseType) {
                case "HTTP/1.1 503":
                    throw new IOException("Received 503 Service Unavailable from the server.");
                case "Lamport":
                    return Integer.parseInt(responseValue);
                default:
                    throw new IOException("Expected LamportClock value from server but received: " + res);
            }
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            this.close(); 
            throw new RuntimeException("Error socket", e);
        }
    }

    public String sendAndReceiveData(String serverName, int portNumber, String data, boolean isContentServer) {
        try {
            this.outLog.println(data);
            StringBuilder responseBuilder = new StringBuilder();
            String line;
            int contentLength = 0;
            boolean isHeader = true;
            
            // Read headers
            while (isHeader && (line = this.inLog.readLine()) != null) {
                if (line.startsWith("Content-Length: ")) {
                    contentLength = Integer.parseInt(line.split(":")[1].trim());
                }

                responseBuilder.append(line).append("\r\n");

                // Blank line indicates end of headers and start of body
                if (line.isEmpty()) {
                    isHeader = false;
                }
            }

            // Read body
            if (!isContentServer && contentLength > 0) {
                char[] bodyChars = new char[contentLength];
                this.inLog.read(bodyChars, 0, contentLength);
                responseBuilder.append(bodyChars);
            }
            return responseBuilder.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            this.close();
        }
    }

    public String request(Socket clientSocket) {
        StringBuilder requestBuilder = new StringBuilder();

        try {
            InputStream input = clientSocket.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            
            String line;
            int contentLength = 0;
            boolean isHeader = true;
            // Read headers
            while (isHeader && (line = in.readLine()) != null) {
                if (line.startsWith("Content-Length: ")) {
                    contentLength = Integer.parseInt(line.split(":")[1].trim());
                }
                requestBuilder.append(line).append("\r\n");
                if (line.isEmpty()) {
                    isHeader = false;
                }
            }
            // Read body
            if (contentLength > 0) {
                char[] bodyChars = new char[contentLength];
                int bytesRead = 0;
                while (bytesRead < contentLength) {
                    int result = in.read(bodyChars, bytesRead, contentLength - bytesRead);
                    if (result == -1) {
                        break; // end of stream reached
                    }
                    bytesRead += result;
                }
                requestBuilder.append(bodyChars);
            }
            
            return requestBuilder.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int init(String name, int port) {
        this.close();
        this.start(port);
        return 0;
    }

    public void response(String response, Socket clientSocket) { // Modified
        try {
            this.outLog = new PrintWriter(clientSocket.getOutputStream(), true);
            this.outLog.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.outLog.close();
        }
    }
}
