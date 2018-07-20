/**
 * This class is used to create an object type named Peers. This object is used
 * to store information about the peers that are connected to the server.
 * 
 * @author Jesse Liddle - jaliddl2 - 200038471
 * @author Alex Micklow - ajmicklo - 200022174
 */
public class Peers {
	// The hostname of the peer connection.
	private String hostname;
	// The port number that other peers can use to connect to.
	private int portNumber;

	/**
	 * Constructor method for an object of type Peers.
	 * 
	 * @param hostname
	 *            This is the hostname of the peer.
	 * @param portNumber
	 *            This is the port number other peers can connect to this peer
	 *            on
	 */
	public Peers(String hostname, int portNumber) {
		this.hostname = hostname;
		this.portNumber = portNumber;
	}

	/**
	 * Getter method used to get the hostname of this peer.
	 * 
	 * @return Returns the host name of the peer.
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * Getter method used to get the port number of this peer.
	 * 
	 * @return Returns the port number of the peer.
	 */
	public int getPortNumber() {
		return portNumber;
	}
}
