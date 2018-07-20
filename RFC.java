/**
 * This class is used to create an object of type RFC. This is used to store
 * information about the RFCs that are stored on the peers.
 * 
 * @author Jesse Liddle - jaliddl2 - 200038471
 * @author Alex Micklow - ajmicklo - 200022174
 */
public class RFC {
	// The title of the RFC
	private String title;
	// The volume number of the RFC
	private int volumeNumber;
	// The hostname of the peer hosting the RFC
	private String hostname;

	/**
	 * Constructor method that is used to create a RFC object.
	 * 
	 * @param volumeNumber
	 *            The volume number of the RFC.
	 * @param title
	 *            The title of the RFC.
	 * @param hostname
	 *            The hostname of the peer hosting the RFC.
	 */
	public RFC(int volumeNumber, String title, String hostname) {
		this.volumeNumber = volumeNumber;
		this.title = title;
		this.hostname = hostname;
	}

	/**
	 * Getter method used to get the title of this RFC.
	 * 
	 * @return Returns the title of the RFC.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Getter method used to get the volume number of this RFC.
	 * 
	 * @return Return the volume number of the RFC.
	 */
	public int getVolumeNumber() {
		return volumeNumber;
	}

	/**
	 * Getter method used to get the hostname of the peer hosting this RFC.
	 * 
	 * @return Returns the hostname of the peer hosting the RFC.
	 */
	public String getHostname() {
		return hostname;
	}

}
