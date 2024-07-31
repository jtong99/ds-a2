import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

public class CalculatorServer {
    public static void main(String args[]) {
        try {
            // Create an instance of the implementation
            CalculatorImplementation obj = new CalculatorImplementation();
            
            // Create or get the registry
            Registry registry = LocateRegistry.createRegistry(2002);
            
            // Bind the implementation instance to a name in the registry
            registry.rebind("Calculator", obj);
            
            System.out.println("Calculator Server is ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}