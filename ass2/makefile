JAVA = java
JAVAC = javac
LIB = lib
SRC = .
OUT = .
CP = -cp $(LIB)/gson-2.10.1.jar:$(OUT)/

MAIN_SOURCES = $(wildcard *.java)

AGGREGATION_SERVER = AggregationServer
CONTENT_SERVER = ContentServer

# Targets and their actions
all: compile-all

compile-all:
	@$(JAVAC) $(CP) $(MAIN_SOURCES)

aggregation: all
	@$(JAVA) $(CP) $(AGGREGATION_SERVER)

contentserver: all
	@$(JAVA) $(CP) $(CONTENT_SERVER) localhost 4567 $(SRC)/data2.txt