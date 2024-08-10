import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Calculator extends Remote {
    /**
     * Pushes a value
     * @param val: value from client
     * Expected output: the stack will include the val
     * @throws RemoteException
     */
    void pushValue(int val) throws RemoteException;

    /**
     * Pushes an operation (min, max, lcm, gcd)
     * @param operator: operation from the client, it will be String "min", "max", "lcm", or "gcd"
     * Expected output:
     * - min: the stack will include the minimum value from the stack
     * - max: the stack will include the maximum value from the stack
     * - lcm: the stack will include the least common multiple value from the stack
     * - gcd: the stack will include the greatest common divisor value from the stack
     * @throws RemoteException
     */
    void pushOperation(String operator) throws RemoteException;

    /**
     * Pops the top value from the stack
     * Expected output: the stack will exclude the top value
     * @return // return the top value from the stack
     * @throws RemoteException
     */
    int pop() throws RemoteException;

    /**
     * Checks if the stack is empty
     * Expected output: the status of stack that is empty or not
     * @return // return true if the stack is empty, false otherwise
     * @throws RemoteException
     */
    boolean isEmpty() throws RemoteException;

    /**
     * Pops the top value from the stack after a delay
     * Expected output: the stack will exclude the top value after the delay
     * @param millis: delay in milliseconds -> I set the delay to 3000ms
     * @return
     * @throws RemoteException
     */
    int delayPop(int millis) throws RemoteException;

    /**
     * Displays the current stack to improve my application UX
     * @return // return the current stack
     * @throws RemoteException
     */
    String displayStack() throws RemoteException;

    /**
     * Creates a new calculator to allow the client to have its own stack
     * Expected output: the calculator object will be created
     * @return // return a new calculator
     * @throws RemoteException
     */
    Calculator createCalculator() throws RemoteException;
}