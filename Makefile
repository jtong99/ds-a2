# build code
build:
	javac -d . Calculator.java CalculatorImplementation.java CalculatorClient.java CalculatorServer.java CalculatorFactory.java

# Clean up compiled files
clean:
	rm -f *.class

# Run RMI registry
registry:
	rmiregistry &

# Run the server
server: $(CLASSES)
	java -classpath . -Djava.rmi.server.codebase=file:./ CalculatorServer

# Run the client
client: $(CLASSES)
	java  -classpath . CalculatorClient