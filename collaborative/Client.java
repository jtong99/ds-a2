import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {
    private Client() {}

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(2002);
            Sum stub = (Sum) registry.lookup("Sum");

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.print("Enter the first number: ");
                int num1 = scanner.nextInt();

                System.out.print("Enter the second number: ");
                int num2 = scanner.nextInt();

                int result = stub.sum(num1, num2);
                System.out.println("Sum value: " + result);

                System.out.print("Do you want to calculate another sum? (y/n): ");
                String choice = scanner.next().toLowerCase();
                if (!choice.equals("y")) {
                    break;
                }
            }

            scanner.close();

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}