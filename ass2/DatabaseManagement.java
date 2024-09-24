import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class DatabaseManagement {
    private static final long EXPIRE_SAVE = 40000;

    private static final String MAIN_DATA = "data" + File.separator + "data.json";
    private static final String MAIN_DATA_BACKUP = "data" + File.separator + "data_backup.json";
    private static final String TIMESTAMP_FILE_PATH = "data" + File.separator + "sender.json";
    private static final String TIMESTAMP_BACKUP_FILE_PATH = "data" + File.separator + "sender_backup.json";
    
    private Map<String, LinkedList<WeatherFormat>> weatherData = new ConcurrentHashMap<>();
    private Map<String, Long> senderTimestamp = new ConcurrentHashMap<>();
    private static DatabaseManagement db;

    private DatabaseManagement() {
        if (this.db != null) {
            throw new RuntimeException("use get db method");
        }
        this.loadData();
    }

    public static DatabaseManagement initialize() {
        if (db == null) {
            db = new DatabaseManagement();
        }
       return db;
    }
    

    // Load data from the main data file
    public void loadData() {
        Map<String, LinkedList<WeatherFormat>> loadedWeatherData = loadObjectFromFile(MAIN_DATA, MAIN_DATA_BACKUP, 
            new TypeToken<ConcurrentHashMap<String, LinkedList<WeatherFormat>>>(){}.getType());

        Map<String, Long> loadedSenderTimestamp = loadObjectFromFile(TIMESTAMP_FILE_PATH, TIMESTAMP_BACKUP_FILE_PATH, 
            new TypeToken<ConcurrentHashMap<String, Integer>>(){}.getType());

        if (loadedWeatherData != null) {
            this.weatherData = loadedWeatherData;
        }

        if (loadedSenderTimestamp != null) {
            this.senderTimestamp = loadedSenderTimestamp;
        }
    }

    private <T> T loadObjectFromFile(String filePath, String backupFilePath, Type type) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs(); // Creates the directory structure if not present.
                file.createNewFile();
                // Create an empty JSON object as the initial content
                Files.write(Paths.get(filePath), "{}".getBytes());
            }

            String jsonData = new String(Files.readAllBytes(Paths.get(filePath)));
            return JsonHandling.convertObject(jsonData, type);
        } catch (IOException e) {
            System.out.println("Error reading from main file: " + e.getMessage());
            System.out.println("Attempting to read from backup file...");
            try {
                String backupData = new String(Files.readAllBytes(Paths.get(backupFilePath)));
                return JsonHandling.convertObject(backupData, type);
            } catch (IOException ex) {
                System.out.println("Error reading from backup file: " + ex.getMessage());
                return null;
            }
        }
    }

    public void saveData(String key, WeatherFormat data) {
        try {
            this.weatherData.computeIfAbsent(key, e -> new LinkedList<>()).add(data);
            String json = JsonHandling.convertJSON(this.weatherData);
            Files.write(Paths.get(MAIN_DATA_BACKUP), json.getBytes());
            Files.move(Paths.get(MAIN_DATA_BACKUP), Paths.get(MAIN_DATA), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveTime(String key, long value) {
        this.senderTimestamp.put(key, value);
    }

    public Map<String, LinkedList<WeatherFormat>> getWeatherData() {
        return this.weatherData;
    }
    public Long getSenderTimestamp(String k) {
        return this.senderTimestamp.get(k);
    }
}
