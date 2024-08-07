import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

public class Server {
    public static void main(String args[]) {
        try {
            // Create an instance of the implementation
            SumImplementation obj = new SumImplementation();
            
            // Create or get the registry
            Registry registry = LocateRegistry.createRegistry(2002);
            
            // Bind the implementation instance to a name in the registry
            registry.rebind("Sum", obj);
            
            System.out.println("Sum Server is ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}