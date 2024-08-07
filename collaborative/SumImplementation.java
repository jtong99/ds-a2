import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class SumImplementation extends UnicastRemoteObject implements Sum {

    public SumImplementation() throws RemoteException {
        super();
    }

    @Override
    public int sum(int a, int b) throws RemoteException {
        return a + b;
    }
}