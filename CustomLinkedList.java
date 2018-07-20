/**
 * Linked List class that is used to create a linked list of any type of object.
 * In order to used the you must call it as:
 * 
 * CustomLinkednList<type> *name* = CustomLinkedList<type>() - or -
 * CustomLinkednList<type> *name* = CustomLinkednList<type>( int )
 * 
 * @author Jesse Liddle - jaliddl2 - 200038471
 * @author Alex Micklow - ajmicklo - 200022174
 * @param <E>
 *            The object that this class will be using as the type.
 */
public class CustomLinkedList<E> {
	// The default size used to create the array if size is not entered
	private static final int DEFAULT_SIZE = 10;
	// An array of objects that is used to store the items
	private Object[] list;
	// The current capacity of the array
	private int capacity;
	// The current number of elements in the array
	private int size;

	/**
	 * Constructor for a custom linked list class that can be any type of object
	 * needed. This list starts out as the default size.
	 */
	public CustomLinkedList() {
		list = new Object[DEFAULT_SIZE];
		capacity = DEFAULT_SIZE;
		size = 0;
	}

	/**
	 * Constructor for a custom linked list class that is starting with a give
	 * size.
	 * 
	 * @param i
	 *            The size that the array will start as.
	 */
	public CustomLinkedList(int i) {
		list = new Object[i];
		capacity = i;
		size = 0;
	}

	/**
	 * This method is used to return the first object of the list.
	 * 
	 * @return Returns the first element in the array.
	 */
	@SuppressWarnings("unchecked")
	public E getFirst() {
		if (size == 0) {
			return null;
		} else {
			return (E) list[0];
		}
	}

	public int getSize() {
		return size;
	}

	/**
	 * This method is used to remove an object by the name of that object.
	 * 
	 * @param name
	 *            The name that it is looking to remove from the list.
	 * @return Returns the object that was removed from the list.
	 */
	@SuppressWarnings("unchecked")
	public E remove(String name) {
		// Create storage for item being removed
		E temp = null;

		// Check to see if there is a first item
		if (list[0] != null) {

			// Check type of the list
			if (list[0] instanceof Peers) {
				for (int i = 0; i < size; i++) {
					if (name.equals(((Peers) list[i]).getHostname())) {
						temp = (E) list[i];

						// Write over the node being replaced and move other
						// nodes forward
						for (int k = i; k < size; k++) {
							if (k + 1 <= capacity)
								list[k] = list[k + 1];
							else
								list[k] = null;
						}

						// Decrease size of list
						size--;
						break;
					}
				}
			} else if (list[0] instanceof RFC) {
				for (int i = 0; i < size; i++) {
					if (name.equals(((RFC) list[i]).getTitle())) {
						temp = (E) list[i];

						// Write over the node being replaced and move other
						// nodes forward
						for (int k = i; k < size; k++) {
							if (k + 1 <= capacity)
								list[k] = list[k + 1];
							else
								list[k] = null;
						}

						// Decrease size of list
						size--;
						break;
					}
				}
			}
		}

		// Return null or the object that was removed
		return (E) temp;
	}

	/**
	 * Method used to find either RFCs or Peers in the list.
	 * 
	 * @param name
	 *            The name that is being searched for.
	 * @return Return the object that matched if there is one.
	 */
	@SuppressWarnings("unchecked")
	public E find(String name) {
		E temp = null;
		// Check type of the list
		if (list[0] instanceof Peers) {
			for (int i = 0; i < size; i++) {
				String host = ((Peers) list[i]).getHostname().substring(1, name.length()+1);
				if (name.equals(host)) {
					temp = (E) list[i];
					break;
				}
			}
		} else if (list[0] instanceof RFC) {
			for (int i = 0; i < size; i++) {
				if (name.equals(((RFC) list[i]).getTitle())) {
					temp = (E) list[i];
					break;
				}
			}
		}
		return temp;
	}

	/**
	 * Method used to remove an object at a specific position in the list.
	 * 
	 * @param i
	 *            The index to remove the item from.
	 * @return Returns the value that was removed
	 */
	@SuppressWarnings("unchecked")
	public E removeAt(int i) {
		// Checks to see if ii is greater than size
		if (i > size) {
			return null;
		} else {
			// Copy element being removed
			E temp = (E) list[i - 1];

			// Write over the node being replaced and move other nodes forward
			for (int k = i - 1; k < size; k++) {
				if (k + 1 <= capacity)
					list[k] = list[k + 1];
				else
					list[k] = null;
			}
			// Decrease size of list
			size--;
			// Return element that was removed
			return temp;
		}
	}

	/**
	 * This method is used to return an element at a given position in the
	 * array.
	 * 
	 * @param i
	 *            The position of the element in the array
	 * @return Returns the element casted to type E
	 */
	@SuppressWarnings("unchecked")
	public E getElementAt(int i) {
		if (size < i || size == 0) {
			return null;
		} else {
			return (E) list[i - 1];
		}
	}

	/**
	 * Method used to add objects into the list. If the list is not big enough
	 * to add then this method calls the growList method to increase the size of
	 * the array.
	 * 
	 * @param obj
	 *            Object being added to the array.
	 */
	public void add(E obj) {
		if (size + 1 >= capacity) {
			growList();
		}

		list[size] = obj;
		size++;
	}

	/**
	 * Method used to grow the size of the list. This method doubles the size of
	 * the list.
	 */
	private void growList() {
		// Create new list with twice the size of the original
		Object[] newList = new Object[capacity * 2];

		// Copy each item from the old list to the new one
		for (int i = 0; i < size; i++) {
			newList[i] = list[i];
		}

		// Doubling the size of the capacity variable
		capacity *= 2;

		// Copies the new list to the old list
		list = newList;
	}
}
