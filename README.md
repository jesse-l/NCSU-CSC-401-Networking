# CSC-401 P2P Project

- Jesse Liddle - jaliddl2 - 200038471
- Alex Micklow - ajmicklo - 200022174

This project is a peer-to-peer sharing system.  There is a server that the peer connects to, by passing it's hostname and an open port that it is listening to, and then they push a list of the files(RFC) they are sharing to that server.  From there if there is another peer that wants to download the file it simply gets the hostname and port number of the host for the file and downloads it from the peer.

The server is listening on port 7734 for all incoming connections to the server.  When a client connects to the server it first hands the server it's hostname and an open port number that the client is listening for other peers to connect to it over.  If a peer wants to download an RFC from another peer it tells the server which RFC edition it wants then it sends the information about that host back to the client and then the client establishes a connection with the other peer to download the file.

To run the server side program first you must compile these files:
- Peers.java
- RFC.java
- CustomLinkedList.java
- Server.java

Once all of those file have been compiled you can start the server by running the Server file.  Once the server is running you can end it by pressing crtl+c at any time and it will display a message "Shutting down" and will close the open server port and exit.

To run the client side program first make sure that the RFC files are in the same directory as the Client.java file.  Then compile and run the Client.java file and the client will be running.

