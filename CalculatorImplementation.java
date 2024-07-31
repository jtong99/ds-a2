import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Stack;

public class CalculatorImplementation extends UnicastRemoteObject implements Calculator {
    private Stack<Integer> stack;

    public CalculatorImplementation() throws RemoteException {
        super();
        this.stack = new Stack<>();
    }

    @Override
    public void pushValue(int val) throws RemoteException {
        stack.push(val);
    }

    @Override
    public void pushOperation(String operator) throws RemoteException {
        if (operator.equals("min")) {
            int min_rs = stack.peek();
            for(int i = 0; i < stack.size(); i++) {
                if (stack.get(i) < min_rs) {
                    min_rs = stack.get(i);
                    stack.pop();
                }
            }
        }
        }

    @Override
    public int pop() throws RemoteException {
        if (stack.isEmpty()) {
            throw new RemoteException("Stack is empty");
        }
        return stack.pop();
    }

    @Override
    public boolean isEmpty() throws RemoteException {
        return stack.isEmpty();
    }

    @Override
    public int delayPop(int millis) throws RemoteException {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RemoteException("Interrupted during delay", e);
        }
        return pop();
    }

    // Implement all the methods here, operating on this.stack
}