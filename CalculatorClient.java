import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CalculatorClient {
    private CalculatorClient() {}

    public static void main(String[] args) {
        String host = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(2002);
            Calculator stub = (Calculator) registry.lookup("Calculator");

            // Test pushValue and pop
            stub.pushValue(5);
            stub.pushValue(10);
            System.out.println("Popped value: " + stub.pop());

            // Test isEmpty
            System.out.println("Is stack empty? " + stub.isEmpty());

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}