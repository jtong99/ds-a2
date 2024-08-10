import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Execution(ExecutionMode.CONCURRENT)
public class CalculatorTest {
    private static CalculatorFactory factory;
    private static Registry registry;

    @BeforeAll
    static void setUp() throws Exception {
        registry = LocateRegistry.createRegistry(2000);
        CalculatorFactoryImplementation factoryImpl = new CalculatorFactoryImplementation();
        registry.rebind("CalculatorFactory", factoryImpl);
        factory = (CalculatorFactory) registry.lookup("CalculatorFactory");
    }

    @Test
    void testPushValue() throws Exception {
        Calculator calc = factory.createCalculator();
        calc.pushValue(13);
        calc.pushValue(14);
        assertEquals("[13, 14]", calc.displayStack());
    }

    @Test
    void testPop() throws Exception {
        Calculator calc = factory.createCalculator();
        calc.pushValue(50);
        calc.pushValue(60);
        int popped = calc.pop();
        assertEquals(60, popped);
        assertEquals("[50]", calc.displayStack());
    }

    @Test
    void testMin() throws Exception {
        Calculator calc = factory.createCalculator();
        calc.pushValue(10);
        calc.pushValue(5);
        calc.pushValue(15);
        calc.pushOperation("min");
        assertEquals("[5]", calc.displayStack());
    }

    @Test
    void testMax() throws Exception {
        Calculator calc = factory.createCalculator();
        calc.pushValue(10);
        calc.pushValue(5);
        calc.pushValue(15);
        calc.pushOperation("max");
        assertEquals("[15]", calc.displayStack());
    }

    @Test
    void testGCD() throws Exception {
        Calculator calc = factory.createCalculator();
        calc.pushValue(48);
        calc.pushValue(18);
        calc.pushOperation("gcd");
        assertEquals("[6]", calc.displayStack());
    }

    @Test
    void testLCM() throws Exception {
        Calculator calc = factory.createCalculator();
        calc.pushValue(12);
        calc.pushValue(18);
        calc.pushOperation("lcm");
        assertEquals("[36]", calc.displayStack());
    }

    @Test
    void testDelayPop() throws Exception {
        Calculator calc = factory.createCalculator();
        calc.pushValue(42);
        
        long start = System.currentTimeMillis();
        int result = calc.delayPop(3000);
        long end = System.currentTimeMillis();
        
        assertEquals(42, result);
        assertTrue((end - start) >= 2990, "Delay pop should be more than 2990ms");
        assertTrue(calc.isEmpty());
    }

    @Test
    void testMultipleClients() throws Exception {
        int numClients = 3;
        CountDownLatch latch = new CountDownLatch(numClients);
        ExecutorService executor = Executors.newFixedThreadPool(numClients);

        for (int i = 0; i < numClients; i++) {
            final int clientId = i;
            executor.submit(() -> {
                try {
                    Calculator clientCalc = factory.createCalculator();
                    clientCalc.pushValue(clientId * 10);
                    clientCalc.pushValue(clientId * 10 + 5);
                    clientCalc.pushOperation("max");
                    int expectedMax = clientId * 10 + 5;
                    assertEquals("[" + expectedMax + "]", clientCalc.displayStack());
                    latch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        latch.await(); // Wait for all clients to finish
        executor.shutdown();
    }

    @Test
    void testWrongOperatorAction() throws Exception {
        Calculator calc = factory.createCalculator();
        calc.pushValue(10);
        assertThrows(RemoteException.class, () -> calc.pushOperation("wrong operator"));
    }

    @Test
    void testNegativeNumbers() throws Exception {
        Calculator calc = factory.createCalculator();
        calc.pushValue(-300);
        calc.pushValue(-400);
        calc.pushOperation("min");
        assertEquals("[-400]", calc.displayStack());
    }

    @Test
    void testLCMWithLongStack() throws Exception {
        Calculator calc = factory.createCalculator();
        int[] numbers = {12, 18, 24, 36, 48, 60};
        for (int num : numbers) {
            calc.pushValue(num);
        }
        calc.pushOperation("lcm");
        assertEquals("[720]", calc.displayStack());
        calc.pop();
        assertTrue(calc.isEmpty());
    }

    @Test
    void testGCDWithLongStack() throws Exception {
        Calculator calc = factory.createCalculator();
        int[] numbers = {48, 180, 360, 420};
        for (int num : numbers) {
            calc.pushValue(num);
        }
        calc.pushOperation("gcd");
        assertEquals("[12]", calc.displayStack());
        calc.pop();
        assertTrue(calc.isEmpty());
    }
}