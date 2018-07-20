import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * This class is the client side of the P2P system. This is used for client
 * computers to connect to the Server and send it the information about the RFC
 * that are stored locally.
 * 
 * @author Jesse Liddle - jaliddl2 - 200038471
 * @author Alex Micklow - ajmicklo - 200022174
 */
public class Client {
	// This is the port number that the server is listening to.
	private static final int SERVER_PORT = 7734;
	// List of the RFCs that are stored locally
	private static String[] StoredFRC;
	// Creates a blank IP Address string for storage
	private static String IP_Address;
	// Port number for other clients to connect to this client.
	private static int Port_Number;
	// Set a min number for the second port search
	private static final int MIN_PORT = 41000;
	// Set a max number for the second port search
	private static final int MAX_PORT = 50000;
	// P2P-CI Version number
	private static final String VERSION_NUMBER = "P2P-CI/1.0";
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
	// The head pointer for the linked list of files
	private static RFCFiles head;
	// Socket used for clients to connect to this client
	protected static Socket sock;

	/**
	 * Main method of the program.
	 * 
	 * @param args
	 *            Command line parameters entered at runtime.
	 */
	public static void main(String[] args) {
		// Set head pointer to null
		head = null;

		// Checks to see if user specified an IP Address
		if (args.length <= 1) {
			IP_Address = "152.14.143.245";
		} else {
			IP_Address = args[0];
		}

		try {
			sock = new Socket(IP_Address, SERVER_PORT);
			// DEBUGGING--------------------------------------------------------------
			System.out.println("Connected");
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if (sock != null) {

			// Thread that is used to gracefully exit "ctrl+c"
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					try {
						Thread.sleep(200);
						System.out.println("Shutting down ...");
						try {
							sock.close();
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

			// Used to get a port number for other client connections
			Port_Number = findPort();

			try {
				// Create output and input stream
				OutputStream out = sock.getOutputStream();
				InputStream in = sock.getInputStream();
				// Create buffer
				byte[] buffer = new byte[4096];

				// Convert port number to string and send
				String port = "" + Port_Number;
				buffer = port.getBytes();
				out.write(buffer);
				out.flush();

				File[] files = findFiles();

				// DEBUGGING--------------------------------------------------------------
				for (int i = 0; i < files.length; i++) {
					System.out.println(files[i].getName());
				}

				createList(files);
				
				RFCFiles temp = head;
				
				while( temp != null ) {
					System.out.println( temp.getVolumeNumber() + " " + temp.getTitle() );
					temp = temp.getNext();
				}

				listeningPort listen = new listeningPort(Port_Number);
				listen.start();
				
				temp = head;
				
				// Send RFC files to server
				while( temp != null ) {
					String addRFC = "ADD RFC " + temp.getVolumeNumber() + " P2P-CI/1.0";
					buffer = addRFC.getBytes();
					out.write(buffer);
					out.flush();
					
					String host = "Host: " + InetAddress.getLocalHost();
					buffer = host.getBytes();
					out.write(buffer);
					out.flush();
					
					port = "Port: " + Port_Number;
					buffer = port.getBytes();
					out.write(buffer);
					out.flush();
					
					String title = "Title: " + temp.getTitle();
					buffer = title.getBytes();
					out.write(buffer);
					out.flush();
					
					temp = temp.next;
				}
				
				// Console scanner
				Scanner console = new Scanner( System.in );
				String line = null;
				int len;
				
				while( true ) {
					line = console.nextLine();
					
					buffer = line.getBytes();
					out.write(buffer);
					out.flush();
					
					len = in.read( buffer );
					if( len > 0 ) {
						System.out.println( new String( buffer, 0, len ));
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private static void createList(File[] files) {
		int fileCount = files.length;
		Scanner fileScan = null;
		Scanner lineScan = null;
		String Title = null;
		int VolumeNumber;
		String temp;
		int MAX_DATE = 2100;
		int MIN_DATE = 1900;

		for (int i = 0; i < fileCount; i++) {
			if (files[i].exists()) {
				try {
					fileScan = new Scanner(files[i]);
					if (fileScan.hasNextLine()) {
						String line = fileScan.nextLine();
						lineScan = new Scanner(line);
						while (!lineScan.hasNextInt()) {
							if (fileScan.hasNextLine()) {
								line = fileScan.nextLine();
								lineScan = new Scanner(line);
							}
						}
						String dateLine = lineScan.nextLine();
						Scanner dateScan = new Scanner( dateLine );
						
						int date = dateScan.nextInt();
						if (date >= MIN_DATE && date <= MAX_DATE) {
							while (fileScan.hasNextLine()) {
								line = fileScan.nextLine();
								if (!line.isEmpty()) {
									Title = line;
									break;
								}
							}
						} else {
							if( dateScan.hasNextInt() ) {
								date = dateScan.nextInt();
							}
						}
						
						while( fileScan.hasNextLine() ) {
							line = fileScan.nextLine();
							if( !line.isEmpty() )
								break;
						}
						
						Title = line.trim();

						String filename = files[i].getName();

						String vol = filename.substring(3, filename.length() - 4);

						VolumeNumber = Integer.parseInt(vol);

						RFCFiles rfc = new RFCFiles(Title, VolumeNumber, files[i]);

						if (head == null) {
							head = rfc;
						} else {
							RFCFiles tempList = head;
							
							while (tempList.hasNext()) {
								tempList = tempList.getNext();
							}

							tempList.setNext(rfc);

							head = tempList;
						}

					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	/**
	 * This method is used to find any RFC files that are stored locally. This
	 * method looks in the current directory
	 */
	private static File[] findFiles() {
		File f = new File("./");
		File[] matchingFiles = f.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith("rfc") && name.endsWith(".txt");
			}
		});
		return matchingFiles;
	}

	/**
	 * This method is used to generate a port number that other peers can
	 * connect to this peer on.
	 * 
	 * @return Returns the number of an open port.
	 */
	private static int findPort() {
		int portNumber = MIN_PORT;
		while (!available(portNumber) && portNumber < MAX_PORT) {
			portNumber++;
		}
		return portNumber;
	}

	/**
	 * This method is used to test ports to see if the port is open or not.
	 * 
	 * @param port
	 *            The number of the port that is being checked currently.
	 * @return Returns if the port is open or not.
	 */
	private static boolean available(int port) {
		Socket s = null;
		try {
			s = new Socket("localhost", port);
			return false;
		} catch (IOException e) {
			return true;
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (IOException e) {
					throw new RuntimeException("You should handle this error.", e);
				}
			}
		}
	}

	/**
	 * This is a method used to create a way to store the RFC that have the
	 * title, volume number, and actual file all linked to each other.
	 * 
	 * @author Jesse Liddle - jaliddl2 - 200038471
	 * @author Alex Micklow - ajmicklo - 200022174
	 */
	static class RFCFiles {
		// The title of the RFC
		private static String title;
		// The volume number of the RFC
		private static int volumeNumber;
		// The actual file containing the RFC
		private static File rfc;
		// Link to the next issue that is stored
		private static RFCFiles next;

		/**
		 * This is a constructor method used to create a storage method for the
		 * RFCs
		 */
		public RFCFiles(String title, int volumeNumber, File rfc) {
			this.title = title;
			this.volumeNumber = volumeNumber;
			this.rfc = rfc;
			next = null;
		}

		public boolean hasNext() {
			if (next == null) {
				return false;
			} else {
				return true;
			}
		}

		/**
		 * Setter method used to set the link to the next rfc issue.
		 */
		public void setNext(RFCFiles rfc) {
			next = rfc;
		}

		/**
		 * Getter method used to get the link to the next rfc issue.
		 */
		public RFCFiles getNext() {
			return next;
		}

		/**
		 * Getter method used to get the volume number of the RFC.
		 */
		public int getVolumeNumber() {
			return volumeNumber;
		}

		/**
		 * Getter method used to get the title of the RFC.
		 */
		public String getTitle() {
			return title;
		}

		/**
		 * Getter method used to get the RFC File.
		 */
		public File getFile() {
			return rfc;
		}
	}

	/**
	 * This class is used to crate a thread that allows for the client to be
	 * waiting on other connects from other clients to download an RFC.
	 * 
	 * @author Jesse Liddle - jaliddl2 - 200038471
	 * @author Alex Micklow - ajmicklo - 200022174
	 */
	static class listeningPort extends Thread {
		private int portNumber;

		public void run() {
			try {
				ServerSocket serSock = new ServerSocket(portNumber);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while (true) {
				ClientDownloads cd = new ClientDownloads(sock);
				cd.run();
			}
		}

		public listeningPort(int portNumber) {
			this.portNumber = portNumber;
		}
	}

	/**
	 * Class used to create threads each time a peer connects to this client
	 * server to download an RFC from this client.
	 * 
	 * @author Jesse Liddle - jaliddl2 - 200038471
	 * @author Alex Micklow - ajmicklo - 200022174
	 */
	static class ClientDownloads extends Thread {
		// The socket for the connection to the client
		private Socket sock;

		public ClientDownloads(Socket sock) {
			this.sock = sock;
		}

		public void run() {
			// Gets input from client
			try {
				InputStream input = sock.getInputStream();
				OutputStream out = sock.getOutputStream();

				byte[] buffer = new byte[4096];
				int len = input.read(buffer);

				String in = new String(buffer, 0, len);

				Scanner scan = new Scanner(in);

				if (scan.hasNext()) {
					String method = scan.next();

					if (scan.hasNext()) {
						String RFC = scan.next() + " " + scan.next();

						if (scan.hasNext()) {
							String version = scan.next();

							// Checks the version of P2P-CI
							if (version.equalsIgnoreCase(VERSION_NUMBER)) {
								// Grabs client host name
								len = input.read(buffer);
								String clientHost = new String(buffer, 0, len);
								// Grabs client OS
								len = input.read(buffer);
								String clientOS = new String(buffer, 0, len);

								Scanner rfcName = new Scanner(RFC);
								int volNum = rfcName.nextInt();

								String filename = "rfc" + volNum + ".txt";
								File rfc = new File(filename);

								if (rfc.exists()) {
									String message = VERSION_NUMBER + " " + OK_STATUS + " " + OK_PHRASE + "\n";
									buffer = message.getBytes();
									out.write(buffer);
									out.flush();

									// TODO Send file here
								} else {
									// Bad Request format error
									String err = VERSION_NUMBER + " " + NOT_FOUND_STATUS + " " + NOT_FOUND_PHRASE
											+ "\n";
									buffer = err.getBytes();
									out.write(buffer);
									out.flush();
								}

							} else {
								// Version does not match Error
								String err = VERSION_NUMBER + " " + NOT_SUPPORTED_STATUS + " " + NOT_FOUND_PHRASE
										+ "\n";
								buffer = err.getBytes();
								out.write(buffer);
								out.flush();
							}
						} else {
							// Bad Request format error
							String err = VERSION_NUMBER + " " + BAD_REQUEST_STATUS + " " + BAD_REQUEST_PHRASE + "\n";
							buffer = err.getBytes();
							out.write(buffer);
							out.flush();
						}
					} else {
						// Bad Request format error
						String err = VERSION_NUMBER + " " + BAD_REQUEST_STATUS + " " + BAD_REQUEST_PHRASE + "\n";
						buffer = err.getBytes();
						out.write(buffer);
						out.flush();
					}
				} else {
					// Bad Request format error
					String err = VERSION_NUMBER + " " + BAD_REQUEST_STATUS + " " + BAD_REQUEST_PHRASE + "\n";
					buffer = err.getBytes();
					out.write(buffer);
					out.flush();
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
