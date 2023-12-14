SRC_DIR = src
BIN_DIR = bin
LIB_DIR = lib

JAVAC = javac -d $(BIN_DIR) -cp $(LIB_DIR)
JAVA = java -cp $(BIN_DIR)

CLIENT_MAIN = client.Client
SERVER_MAIN = server.Server

all: client server

client: 
	$(JAVAC) $(JFLAGS) $(SRC_DIR)/client/ClientMain.java

server: 
	$(JAVAC) $(JFLAGS) $(SRC_DIR)/server/ServerMain.java


clean:
	rm -rf $(BIN_DIR)/*
