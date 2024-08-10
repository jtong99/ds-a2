import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Stack;

public class CalculatorImplementation extends UnicastRemoteObject implements Calculator {
    private Stack<Integer> stack;

    public CalculatorImplementation() throws RemoteException {
        super();
        this.stack = new Stack<>();
    }

    /*
     * Push a value to the stack
     * @param val: value from client
     * Expected output: the stack will include the val
     */
    @Override
    public void pushValue(int val) throws RemoteException {
        stack.push(val);
    }

    /*
     * Push an operation (min, max, lcm, gcd)
     * @param operator: operation from the client, it will be String "min", "max", "lcm", or "gcd"
     * Expected output:
     * - min: the stack will include the minimum value from the stack
     * - max: the stack will include the maximum value from the stack
     * - lcm: the stack will include the least common multiple value from the stack
     * - gcd: the stack will include the greatest common divisor value from the stack
     */
    @Override
    public void pushOperation(String operator) throws RemoteException {
        if (stack.isEmpty()) {
            throw new RemoteException("Stack is empty");
        }
    
        int result;
        switch (operator.toLowerCase()) {
            case "min":
                result = min();
                break;
            case "max":
                result = max();
                break;
            case "lcm":
                result = lcm();
                break;
            case "gcd":
                result = gdc();
                break;
            default:
                throw new RemoteException("Invalid operator: " + operator);
        }
    
        stack.clear();
        stack.push(result);
    }
    
    /**
     * Find the minimum value in the stack
     * Expected output: the minimum value from the stack
     * @return
     */
    private int min() {
        int min = stack.peek();
        for (int value : stack) {
            if (value < min) {
                min = value;
            }
        }
        return min;
    }
    
    /**
     * Find the maximum value in the stack
     * Expected output: the maximum value from the stack
     * @return
     */
    private int max() {
        int max = stack.peek();
        for (int value : stack) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
    
    /**
     * Find the least common multiple value in the stack
     * Expected output: the least common multiple value from the stack
     * In the loop, it multiplies the numbers then divides by their Greatest Common Divisor to reduce this to the smallest number
     * @return
     */
    private int lcm() {
        int lcm = stack.pop();
        while (!stack.isEmpty()) {
            int b = stack.pop();
            lcm = lcm * b / euclideanGCD(lcm, b);
        }
        return lcm;
    }

    /**
     * Find the greatest common divisor value in the stack
     * Expected output: the greatest common divisor value from the stack
     * In the loop, it uses the Euclidean algorithm to find the GCD of the numbers
     * @return
     */
    
    private int gdc() {
        int gcd = stack.pop();
        while (!stack.isEmpty()) {
            gcd = euclideanGCD(gcd, stack.pop());
        }
        return gcd;
    }
    
    private int euclideanGCD(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
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
    
    @Override
    public String displayStack() throws RemoteException {
        return stack.toString();
    }

    @Override
    public Calculator createCalculator() throws RemoteException {
        throw new UnsupportedOperationException("Unimplemented method 'createCalculator'");
    }
}