SRC_DIR = src
BIN_DIR = bin
LIB_DIR = external-libs

EXT_LIBS = $(wildcard $(LIB_DIR)/*.jar)
EXT_LIBS_CLASSPATH = $(subst $(LIB_DIR)/,,$(EXT_LIBS))

JAVAC = javac -cp $(SRC_DIR):$(EXT_LIBS) -d $(BIN_DIR) -source 1.8 -target 1.8 -Xlint:-options
JAVA = java -cp $(BIN_DIR):$(EXT_LIBS)

CLIENT_MAIN = client.ClientMain
SERVER_MAIN = server.ServerMain

all: client server
.PHONY: lib

client: lib
	$(JAVAC) $(SRC_DIR)/client/ClientMain.java

server: lib
	$(JAVAC) $(SRC_DIR)/server/*.java 

lib: 
	$(JAVAC) $(SRC_DIR)/lib/**/*.java

runServer:
	$(JAVA) $(SERVER_MAIN) 

serverJar: 
	echo "Manifest-Version: 1.0" > $(BIN_DIR)/server.mf
	echo "Main-Class: server.ServerMain" >> $(BIN_DIR)/server.mf
	echo "Class-Path: $(EXT_LIBS)" >> $(BIN_DIR)/server.mf

	jar -cvfm $(BIN_DIR)/server.jar $(BIN_DIR)/server.mf -C $(BIN_DIR) . $(LIB_DIR)/*

runClient:
	$(JAVA) $(CLIENT_MAIN) localhost 3000

clean:
	rm -rf $(BIN_DIR)/*