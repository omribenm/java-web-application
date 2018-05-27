CFLAGS:=-c -Wall -Weffc++ -g -std=c++11
LDFLAGS:=-lboost_system -lboost_locale -lboost_thread

all: echoClient
	g++ -o bin/run bin/connectionHandler.o bin/echoClient.o $(LDFLAGS) 

echoClient: bin/connectionHandler.o bin/echoClient.o
	
bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/echoClient.o: src/echoClient.cpp
	g++ $(CFLAGS) -o bin/echoClient.o src/echoClient.cpp
	
	
.PHONY: clean
clean:
	rm -f bin/*
