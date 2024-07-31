# Overview

To gain an understanding of how a distributed system works, this first assignment phase involves developing a simple Java RMI application. This will involve developing both the client and server side of a distributed application: a simple calculator server.

The calculator server operates a stack and clients push values and operations on to the stack. While, usually, each client should have its own stack, you may use a single stack on the server. You may also assume that operations are always sensible: that is, we will only push an operator after pushing at least on value and we will only pop when there is a value on the stack. You may also assume that the operator provided will only be one of the four displayed types and that the values are always integers.

Following the directions discussed in lectures, you should create a Java RMI Server that supports the following remote methods:

`void pushValue(int val);`

This method will take val and push it on to the top of the stack.

`void pushOperation(String operator);`

This method will push a String containing an operator ("min", "max", "lcm", "gcd") to the stack, which will cause the server to pop all the values on the stack and:

    for min - push the min value of all the popped values;
    for max - push the max value of all the popped values
    for lcm - push the least common multiple of all the popped values;
    for gcd - push the greatest common divisor of all the popped values.

`int pop();`

This method will pop the top of the stack and return it to the client.

`boolean isEmpty();` 

This method will return true if the stack is empty, false otherwise.

`int delayPop(int millis);` 

This method will wait millis milliseconds before carrying out the pop operation as above.


Importantly: Your implementation should use the following files:

```
Calculator.java - the interface that defines the remote operations implemented by your remote service.
CalculatorImplementation.java - the implementation class for the remote operations.
CalculatorServer.java - the server class.
CalculatorClient.java - a test client that should connect to the server, and test its operation.
You will need to create and add these files to your repository.
```

Don't forget to commit your work frequently and to submit before the due date!
Assessment

Your assignment will be marked out of 100 points, as following:

    50 points for the functionality of your code:

o All clients access the same stack on the server
o pushValue works – one client, many clients (more than 3)
o pushOperation works – one client, many clients (more than 3)
o pop works – one client, many clients (more than 3)
o delayPop works – one client, many clients (more than 3)

    35 points for the quality of your automated testing, both in testing the server with single and multiple clients.
    15 points for the quality of your code:

Code Quality Checklist 

Do!

```
o write comments above the header of each of your methods, describing
o what the method is doing, what are its inputs and expected outputs
o describe in the comments any special cases
o create modular code, following cohesion and coupling principles
```

Don’t!

```
o use magic numbers
o use comments as structural elements (see video)
o mis-spell your comments
o use incomprehensible variable names
o have long methods (not more than 80 lines)
o allow TODO blocks 
```
Bonus marks (extra 10 points)

    Clients have their own stack on the server: each client accesses their own stack, rather than the common one
