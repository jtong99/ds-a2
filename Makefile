# Run RMI registry
registry:
	rmiregistry &

# build:
# 	javac -d . Calculator.java CalculatorImplementation.java CalculatorClient.java CalculatorServer.java CalculatorFactory.java

# Run the server
server: $(CLASSES)
	java -classpath . -Djava.rmi.server.codebase=file:./ CalculatorServer

# Run the client
client: $(CLASSES)
	java  -classpath . CalculatorClient