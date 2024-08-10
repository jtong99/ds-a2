import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class CalculatorClient {
    private CalculatorClient() {}

    public static void main(String[] args) {
        String host = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(host);

            /*
             * Get the CalculatorFactory from the registry
             * This will be used to allow the client to have its own stack
             */
            CalculatorFactory factory = (CalculatorFactory) registry.lookup("CalculatorFactory");

            Calculator stub = factory.createCalculator();
            
            /*
             * Client can interact with their own stacks using keyboard:
             * 1. Push value
             * 2. Push operation(min, max, lcm, gcd)
             * 3. Pop
             * 4. Delay pop
             * 5. Display current stack
             * 6. Quit
             */
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\nSelect action:");
                System.out.println("1. Push value");
                System.out.println("2. Push operation(min, max, lcm, gcd)");
                System.out.println("3. Pop");
                System.out.println("4. Delay pop");
                System.out.println("5. Display current stack");
                System.out.println("6. Quit");
                System.out.print("Select from 1 to 6: ");

                int selected = scanner.nextInt();
                scanner.nextLine();

                switch (selected) {
                    case 1:// return the top value from the stack
                        System.out.print("Enter value: ");
                        int value = scanner.nextInt();
                        stub.pushValue(value);
                        System.out.println("Current stack: " + stub.displayStack());
                        break;
                    case 2:
                        System.out.print("Enter operation (min, max, lcm, gcd): ");
                        String operation = scanner.nextLine();
                        stub.pushOperation(operation);
                        System.out.println("Current stack: " + stub.displayStack());
                        break;
                    case 3:
                        if (!stub.isEmpty()) {
                            stub.pop();
                        } else {
                            System.out.println("Stack is empty.");
                        }
                        System.out.println("Current stack: " + stub.displayStack());
                        break;
                    case 4:
                        // Delay pop for 3s
                        int delay = 3000;
                        if (!stub.isEmpty()) {
                            stub.delayPop(delay);
                        } else {
                            System.out.println("Stack is empty.");
                        }
                        System.out.println("Current stack: " + stub.displayStack());
                        break;
                    case 5:
                        System.out.println("Current stack: " + stub.displayStack());
                        break;
                    case 6:
                        System.out.println("See you again");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid! Select from 1-5.");
                }
            }
        } catch (Exception e) {
            System.err.println(e.toString());
            e.printStackTrace();
        }
    }
}