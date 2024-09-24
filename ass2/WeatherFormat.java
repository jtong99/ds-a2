import com.google.gson.JsonObject;

public class WeatherFormat {
    private int lamport;
    private String source;
    private JsonObject data;

    public WeatherFormat(int lamport, String source, JsonObject data) {
        this.lamport = lamport;
        this.source = source;
        this.data = data;
    }

    
}
