# Assignment 1

## Student

- Name: Minh Duc Tong (John)
- ID: a1941699

### Overview

This project is developed by using Java RMI system to simulate a calculator server. I developed this system to allow the clients have their **OWN STACK**. This project allows user to manage their own stack with these methods like the requirements:

- pushValue
- pushOperation (min, max, lcm, gcd)
- Pop
- delayPop

To achieve the requirement that each client accesses their own stack, I used the **Factory design pattern** in Java. Whenever the client connects to the server, each client receives its own unique `CalculatorImplementation` instance which maintains its own separate stack. Clients can perform operations independently without interfering with each other.

### Project structure

#### Calculator.java

Interface defining the calculator operations

#### CalculatorImplementation.java

Function implementation:

- void pushValue(int val): used to push value onto the stack
- void pushOperation(String operator): used to pop all value and push the result from operator (min, max, lcm, gcd)
- int pop(): pop value from stack
- boolean isEmpty(): check stack is empty or not
- int delayPop(int millis): pop value after millis delayed
- String displayStack(): print the stack in terminal (used for improving UX)
- Calculator createCalculator(): create a new instance for operation, it will allow clients will have their own stack

#### CalculatorFactory.java

Interface and implementation for the Calculator factory. Each client will create an instance from this to have their own stack.

#### CalculatorServer.java

Server that using the RMI registry

#### CalculatorClient.java

Client for interacting with the calculator

When the client connects to server, the program will show a list of actions that client can select.

```
Choose an action:
1. Push Value
2. Push Operation
3. Pop
4. Delay Pop
5. Display current stack
6. Quit
Enter your choice (1-5): 1
Enter value to push: 12
Current stack: [12]
```

#### Prerequisites

Java Development Kit (JDK): JDK 8 or higher (I tested with CAT Suite machine successfully)

#### Run server and client

1. Start RMI Registry:

```
rmiregistry &
```

2. Build file:

```
javac -d . Calculator.java CalculatorImplementation.java CalculatorClient.java CalculatorServer.java
```

3. Run server:

```
java -classpath . -Djava.rmi.server.codebase=file:./ CalculatorServer
```

4. Run client:

```
java  -classpath . CalculatorClient
```

#### CalculatorTest.java

JUnit test cases for the calculator

My test result:

```
Thanks for using JUnit! Support its development at https://junit.org/sponsoring

╷
├─ JUnit Jupiter ✔
│  └─ CalculatorTest ✔
│     ├─ testMultipleClients() ✔
│     ├─ testGCD() ✔
│     ├─ testLCM() ✔
│     ├─ testMax() ✔
│     ├─ testMin() ✔
│     ├─ testPop() ✔
│     ├─ testLCMWithLongStack() ✔
│     ├─ testPushValue() ✔
│     ├─ testWrongOperatorAction() ✔
│     ├─ testDelayPop() ✔
│     ├─ testGCDWithLongStack() ✔
│     └─ testNegativeNumbers() ✔
└─ JUnit Vintage ✔

Test run finished after 3079 ms
[         3 containers found      ]
[         0 containers skipped    ]
[         3 containers started    ]
[         0 containers aborted    ]
[         3 containers successful ]
[         0 containers failed     ]
[        12 tests found           ]
[         0 tests skipped         ]
[        12 tests started         ]
[         0 tests aborted         ]
[        12 tests successful      ]
[         0 tests failed          ]
```

#### junit-platform-console-standalone-1.8.2.jar

JUnit library for running tests
