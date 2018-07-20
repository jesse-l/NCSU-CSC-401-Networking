import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Scanner;

/**
 * This class is used to run the back end server that the peers are connecting
 * to.
 * 
 * @author Jesse Liddle - jaliddl2 - 200038471
 * @author Alex Micklow - ajmicklo - 200022174
 *
 */
public class Server {
	// The port that this server will be listening on.
	private static final int PORT_NUMBER = 7734;
	// Status code for OK
	private static final int OK_STATUS = 200;
	private static final String OK_PHRASE = "OK";
	// Status code for Bad Request
	private static final int BAD_REQUEST_STATUS = 400;
	private static final String BAD_REQUEST_PHRASE = "Bad Request";
	// Status code for Not Found
	private static final int NOT_FOUND_STATUS = 404;
	private static final String NOT_FOUND_PHRASE = "Not Found";
	// Status code for P2P-CI Version Not Supported
	private static final int NOT_SUPPORTED_STATUS = 505;
	private static final String NOT_SUPPORTED_PHRASE = "P2P-CI Version Not Supported";
	// P2P-CI Version number
	private static final String VERSION_NUMBER = "P2P-CI/1.0";
	// Socket that the server is listening on for connections
	protected static ServerSocket serverSocket;
	// List of peer connections to this server
	protected static CustomLinkedList<Peers> peerConnections;
	// List of RFCs that are held by the peers
	protected static CustomLinkedList<RFC> rfcList;

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Make a socket and listen for new connections.
		serverSocket = null;
		// Create default list of client connections
		peerConnections = new CustomLinkedList<Peers>();
		// Create default list of rfc stored in clients
		rfcList = new CustomLinkedList<RFC>();

		// Debugging------------------------------------------------------------------------------------------
		rfcList.add(new RFC(1, "test", "10.0.0.9"));
		rfcList.add(new RFC(2, "test2", "10.0.0.9"));
		rfcList.add(new RFC(3, "test3", "10.0.0.9"));
		peerConnections.add(new Peers("/10.0.0.9", 65432));

		try {
			serverSocket = new ServerSocket(PORT_NUMBER);
		} catch (IOException e) {
			System.err.println("Can't create socket: " + e);
			System.exit(-1);
		}

		// Thread that is used to gracefully exit "ctrl+c"
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					Thread.sleep(200);
					System.out.println("Shouting down ...");
					try {
						serverSocket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		// Keep trying to accept new connections and serve them.
		while (true) {
			try {
				// Try to get a new client connection.
				Socket sock = serverSocket.accept();

				HandleClients hc = new HandleClients(sock);
				hc.start();

			} catch (IOException e) {
				System.err.println("Lost client connection: " + e);
			}
		}

	}

	/**
	 * Class used to handle the connection to the peer clients.
	 * 
	 * @author Jesse Liddle - jaliddl2 - 200038471
	 * @author Alex Micklow - ajmicklo - 200022174
	 */
	static class HandleClients extends Thread {
		// The socket for the connection to the client
		private Socket sock;
		private String IP_Address;

		/**
		 * This is for creating a thread to deal with each new connection to the
		 * server.
		 * 
		 * @param sock
		 *            The socket of the new connection.
		 */
		public HandleClients(Socket sock) {
			this.sock = sock;
		}

		/**
		 * Method used to control the thread operations
		 */
		public void run() {
			try {
				// Grab IP Address of remote connection
				SocketAddress IP = sock.getRemoteSocketAddress();
				IP_Address = IP.toString();
				// DEBUGGING------------------------------------------------------------------------------------------
				System.out.println(IP_Address);

				// Create input stream from client
				InputStream input = sock.getInputStream();
				// Opening output stream to be used with TCP
				OutputStream output = sock.getOutputStream();
				// Create buffer to handle input stream
				byte[] buffer = new byte[1024];
				// length of data read in
				int len = input.read(buffer);

				// Create string from buffer
				String str = new String(buffer, 0, len);
				// DEBUGGING------------------------------------------------------------------------------------------
				System.out.println(str);

				// Grabs open port number
				Integer prtNum = Integer.parseUnsignedInt(str.trim());
				// Creates new peer and adds it to list
				peerConnections.add(new Peers(IP_Address, prtNum));

				// Reads more from client
				len = input.read(buffer);
				// Create null scanner
				Scanner scan = null;
				while (len >= 0) {
					// Creates string from buffer
					str = new String(buffer, 0, len);
					// Scanner for the string
					scan = new Scanner(str);
					// Grabs first word, AKA method
					String method = scan.next();
					// DEBUGGING------------------------------------------------------------------------------------------
					System.out.println(method);

					// Checks to see what the method is
					if (method.equalsIgnoreCase("list")) {
						if (scan.hasNext()) {
							String hostname = scan.next();
							if (scan.hasNext()) {
								String p2pVer = scan.next();
								if (p2pVer.equalsIgnoreCase(VERSION_NUMBER)) {
									str = VERSION_NUMBER + " " + OK_STATUS + " " + OK_PHRASE + "\n";

									listRFC(str);
								} else {
									// Bad Request format
									String err = VERSION_NUMBER + " " + NOT_SUPPORTED_STATUS + " "
											+ NOT_SUPPORTED_PHRASE + "\n";
									buffer = err.getBytes();
									output.write(buffer);
									output.flush();

									// Example of request format
									String example = "List All <Version>\n";
									buffer = example.getBytes();
									output.write(buffer);
									output.flush();
								}
							} else {
								// Bad Request format
								String err = VERSION_NUMBER + " " + BAD_REQUEST_STATUS + " " + BAD_REQUEST_PHRASE
										+ "\n";
								buffer = err.getBytes();
								output.write(buffer);
								output.flush();

								// Example of request format
								String example = "List All <Version>\n";
								buffer = example.getBytes();
								output.write(buffer);
								output.flush();
							}
						} else {
							// Bad Request format
							String err = VERSION_NUMBER + " " + BAD_REQUEST_STATUS + " " + BAD_REQUEST_PHRASE + "\n";
							buffer = err.getBytes();
							output.write(buffer);
							output.flush();

							// Example of request format
							String example = "List All <Version>\n";
							buffer = example.getBytes();
							output.write(buffer);
							output.flush();
						}
					} else if (method.equalsIgnoreCase("ADD")) {
						// Adding an RFC to the list
						if (scan.hasNext()) {
							String hostname = scan.next();
							if (scan.hasNext()) {
								String p2pVer = scan.next();
								if (p2pVer.equalsIgnoreCase(VERSION_NUMBER)) {
									str = VERSION_NUMBER + " " + OK_STATUS + " " + OK_PHRASE + "\n";

									listRFC(str);
								} else {
									// Bad Request format
									String err = VERSION_NUMBER + " " + NOT_SUPPORTED_STATUS + " "
											+ NOT_SUPPORTED_PHRASE + "\n";
									buffer = err.getBytes();
									output.write(buffer);
									output.flush();

									// Example of request format
									String example = "List All <Version>\n";
									buffer = example.getBytes();
									output.write(buffer);
									output.flush();
								}
							} else {
								// Bad Request format
								String err = VERSION_NUMBER + " " + BAD_REQUEST_STATUS + " " + BAD_REQUEST_PHRASE
										+ "\n";
								buffer = err.getBytes();
								output.write(buffer);
								output.flush();

								// Example of request format
								String example = "List All <Version>\n";
								buffer = example.getBytes();
								output.write(buffer);
								output.flush();
							}
						} else {
							// Bad Request format
							String err = VERSION_NUMBER + " " + BAD_REQUEST_STATUS + " " + BAD_REQUEST_PHRASE + "\n";
							buffer = err.getBytes();
							output.write(buffer);
							output.flush();

							// Example of request format
							String example = "List All <Version>\n";
							buffer = example.getBytes();
							output.write(buffer);
							output.flush();
						}
					} else if (method.equalsIgnoreCase("LOOKUP")) {

					} else if (method.equalsIgnoreCase("GET")) {

					} else {

						String err = "Enter a valid method: ADD, LOOKUP, or LIST\n";
						buffer = err.getBytes();
						output.write(buffer);
						output.flush();

						String example = "<Method> <RFC Number> <Version>";
						buffer = example.getBytes();
						output.write(buffer);
						output.flush();
					}

					// Reads more input from client
					len = input.read(buffer);
				}

				try {
					// Close scanner
					if (scan != null)
						scan.close();
					// Close socket
					sock.close();
					// Remove connection from active list
					peerConnections.remove(IP_Address);
					System.out.println("Connection Closed");
				} catch (Exception e1) {
					// Do nothing
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void addRFC() {

		}

		/**
		 * This method is used to list the rfc that the server is currently
		 * storing.
		 */
		private void listRFC(String str) {
			// Create a byte stream to be used for printing output to TCP
			byte[] buffer = new byte[1024];
			OutputStream output;

			try {
				// Opening output stream to be used with TCP
				output = sock.getOutputStream();

				buffer = str.getBytes();
				output.write(buffer);
				output.flush();

				// Traverse array of RFCs stored in peers
				for (int i = 1; i <= rfcList.getSize(); i++) {
					// Grabs RFC number
					String rfcNum = "" + rfcList.getElementAt(i).getVolumeNumber();
					// Grabs RFC title
					String rfcTitle = rfcList.getElementAt(i).getTitle();
					// Grabs RFC hostname
					String rfcHost = rfcList.getElementAt(i).getHostname();
					// Grabs RFC host's port
					String hostPort = "" + peerConnections.find(rfcList.getElementAt(i).getHostname()).getPortNumber();

					// Combines information into one string
					str = rfcNum + " " + rfcTitle + " " + rfcHost + " " + hostPort + "\n";

					// Copies the string to the byte array
					buffer = str.getBytes();
					// Writes the array to the client connection
					output.write(buffer);
					// Flushes the output stream
					output.flush();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}