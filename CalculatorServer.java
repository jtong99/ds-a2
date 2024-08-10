import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.registry.LocateRegistry;

public class CalculatorServer {
    public static void main(String args[]) {
        String host = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            CalculatorFactory factory = new CalculatorFactoryImplementation();
            registry.rebind("CalculatorFactory", factory);
            
            System.out.println("Calculator Server is ready");
        } catch (Exception e) {
            System.err.println(e.toString());
            e.printStackTrace();
        }
    }
}