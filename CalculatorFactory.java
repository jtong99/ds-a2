import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/*
 * CalculatorFactory interface
 * This interface provides a method to create a new Calculator object
 * Calculator object is from the CalculatorImplementation class that allows the client to have its own stack
 * So when the client connects to the server, the server will create a new Calculator object for the client
 * The client will interact with the Calculator object to perform operations on its own stack
 */

public interface CalculatorFactory extends Remote {
    Calculator createCalculator() throws RemoteException;
}

class CalculatorFactoryImplementation extends UnicastRemoteObject implements CalculatorFactory {
    public CalculatorFactoryImplementation() throws RemoteException {
        super();
    }

    @Override
    public Calculator createCalculator() throws RemoteException {
        return new CalculatorImplementation();
    }
}
