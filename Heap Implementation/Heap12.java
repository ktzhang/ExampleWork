/* Kevin Zhang        Programming Assignment 4    Heap12.java
 * PID: A10810276     Login: cs12spq
 */

/**
 * Heap12 class is a heap data structure using an array as its
 * backing structure. It stores it so that the highest priority
 * is on top, while the lowest is on bottom. The final structure
 * is like a tree system.
 * 
 * @author Kevin Zhang A10810276
 *
 * @param <E>
 */
public class Heap12<E extends Comparable<? super E>> implements PQueue<E> {
	// Creating the backing structure
	private E[] backing;
	private int size, capacity;

	/**
	 * Public default constructor that makes a new Heap12 object with default
	 * capacity of 5.
	 */
	@SuppressWarnings("unchecked")
	public Heap12() {
		// Initializing variables
		capacity = 5;
		// Creating a new backing structure
		backing = (E[]) new Comparable[capacity];
		// set size to be zero
		size = 0;
	}

	/**
	 * Public constructor that takes in a heap object and then copies
	 * it element by element.
	 * 
	 * @param copy Heap12 object to be copied
	 */
	@SuppressWarnings("unchecked")
	public Heap12(Heap12<?> copy) {
		// Create new backing structure with same length
		backing = (E[]) new Comparable[copy.backing.length];

		// Adding elements
		for (int i = 0; i < copy.backing.length; i++) {
			backing[i] = (E) copy.backing[i];
		}

		// Set the other variables to be the same
		size = copy.size;
		capacity = copy.capacity;

	}

	/**
	 * Private constructor that takes in an array and soft copy's it
	 * used mainly for sort method.
	 * 
	 * @param array Takes in array
	 */
	private Heap12(E[] array) {
		backing = array;
		size = 0;
		capacity = array.length;
	}

	/**
	 * Adds the specified element to this PQueue. <br>
	 * PRECONDITION: none <br>
	 * POSTCONDITION: the element has been added to this PQueue, none of the
	 * other elements have been changed, and the size is increased by 1.
	 * 
	 * @param e
	 *            The element to add.
	 * @throws NullPointerException
	 *             if the specified element is <tt>null</tt>.
	 */
	public void add(E e) {
		// Check if null element added
		if (e == null)
			throw new NullPointerException();
		
		// Checks if it is full and increases capacity if needed
		isFull();

		//Adding element
		backing[size] = e;

		//Moving it up the tree
		bubbleUp(size);

		// Increments size
		size++;
	}

	/**
	 * Private method which moves a node up or down a tree based on its
	 * priority.
	 * 
	 * @param index
	 *            The element index which is to be sorted.
	 */
	private void bubbleUp(int index) {
		// Initializing parent node
		int parentNode = (index - 1) / 2;

		// Check if not root or if not higher priority
		if (index > 0 && (backing[index].compareTo(backing[parentNode]) > 0)) {
			// performing the swap
			E temp = backing[index];
			backing[index] = backing[parentNode];
			backing[parentNode] = temp;

			// Move cursor to parent
			bubbleUp(parentNode);
		}
	}

	/**
	 * Removes and returns the highest priority element in this PQueue. If more
	 * than one element has the same highest priority, one of them is removed
	 * and returned. <br>
	 * PRECONDITION: this PQueue contains at least one element. <br>
	 * POSTCONDITION: the highest priority element has been removed from this
	 * PQueue, none of the other elements have been changed, and the size is
	 * decreased by 1.
	 * 
	 * @return The highest priority element in this PQueue, or <tt>null</tt> if
	 *         none.
	 */
	public E remove() {
		//Check if empty
		if (isEmpty())
			return null;

		// Store backing into temp variable
		E temp = backing[0];

		// Switching the last element with the front of array
		backing[0] = backing[size - 1];

		// Setting last element to null
		backing[size - 1] = null;

		// Bubbling down the front element
		bubbleDown(0);

		// Decrements size
		size--;

		// Returning the previous highest priority, which is backing[0]
		return temp;
	}

	/**
	 * Private method which moves a node up or down a tree based on its
	 * priority.
	 * 
	 * @param index
	 *            The element index which is to be sorted.
	 */
	private void bubbleDown(int index) {
		// Check if root or if not greater
		int leftChild = index * 2 + 1; // left child index
		int rightChild = index * 2 + 2; // right child index
		boolean childrenExist; // Check if child exists

		// Check if you can reach the left child also if it is not null
		try {
			// for(int i = 0; i < backing.length; i++)
			// System.out.print(backing[i] + " ");
			// System.out.println("");
			childrenExist = leftChild < size && backing[leftChild] != null;
			// Catching just in case throws index out of bounds
		} catch (Exception e) {

			childrenExist = false;
		}

		// System.out.println(childrenExist);
		// Initialize max index
		int maxIndex = index;
		if (childrenExist) {
			if (backing[maxIndex].compareTo(backing[leftChild]) < 0)
				maxIndex = leftChild;
			try {
				if (backing[maxIndex].compareTo(backing[rightChild]) < 0)
					maxIndex = rightChild;
			} catch (Exception e) {
			}
		}

		if (maxIndex != index) {
			// performing the swap
			E temp = backing[index];
			backing[index] = backing[maxIndex];
			backing[maxIndex] = temp;

			// Move cursor to child
			bubbleDown(maxIndex);
		}
	}

	/**
	 * Returns the element in this PQueue that would be returned by
	 * <tt>remove()</tt>. <br>
	 * PRECONDITION: this PQueue contains at least one element. <br>
	 * POSTCONDITION: the PQueue is unchanged.
	 * 
	 * @return The highest priority element in this PQueue, or <tt>null</tt> if
	 *         none.
	 * @see #remove()
	 */
	public E peek() {
		// Return null if empty
		if (isEmpty())
			return null;
		// Return first element if not
		else
			return backing[0];
	}

	/**
	 * Returns the number of elements in this PQueue. <br>
	 * PRECONDITION: none <br>
	 * POSTCONDITION: the PQueue is unchanged.
	 * 
	 * @return the number of elements in this PQueue.
	 */
	public int size() {
		return size;
	}

	/**
	 * Determine if this PQueue contains any elements. <br>
	 * PRECONDITION: none <br>
	 * POSTCONDITION: the PQueue is unchanged.
	 * 
	 * @return <tt>true</tt> if this PQueue is empty, <tt>false</tt> otherwise.
	 */
	public boolean isEmpty() {
		// Check if size less than or equal 0
		return size <= 0;
	}

	/**
	 * Checks if backing structure is full. If it is, it will double the backing
	 * structure length using a deep copy. Used mainly in add() method
	 * 
	 * @return <tt>true</tt> if it is full, <tt>false</tt> if not.
	 */
	private boolean isFull() {
		// Checks if full
		if (size >= capacity) {
			// Creates an array thats 2wice as big
			int newCapacity = capacity * 2;

			// Creates a temp array thats tiwce the length
			@SuppressWarnings("unchecked")
			E[] temp = (E[]) new Comparable[newCapacity];

			// Creates a deep copy of the array
			for (int i = 0; i < backing.length; i++) {
				temp[i] = backing[i];
			}

			// Set new capacity
			capacity = newCapacity;

			// Changes pointer of backing array
			backing = temp;

			// Returns that it's full
			return true;

		} else {
			// Otherwise it isn't full
			return false;
		}

	}

	/**
	 * Method that sorts the array passed in using Heap as a data 
	 * structure. Makes a copy of the array.
	 * 
	 * @param array The array that's passed in
	 */
	@SuppressWarnings("unchecked")
	public static <T extends java.lang.Comparable<? super T>> void sort(
			T[] array) {
		@SuppressWarnings({ "rawtypes" })
		Heap12 heap = new Heap12(array);
		// Adding to the array
		for (int i = 0; i < array.length; i++) {
			// Adds to the backing structure using the method
			heap.add(array[i]);
		}

		// Removing from array
		for (int j = 0; j < array.length; j++) {
			// Removes element from the front and adds it to the back
			array[array.length - (j + 1)] = (T) heap.remove();
		}
	}

	/*
	 * Creates a unique hash code for a heap object.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		for(int i = 0; i < backing.length; i++) {
			if(backing[i] != null) {
				//Hashcode to store both the value of the backing 
				result = prime * result + (backing[i].hashCode());
				//And the index
				result = prime * result + i;
			}
		}
		//Finally add the size
		result = prime * result + size*51;
		return result;
	}

	/**
	 * Checks to see if the objects are equal or not using 
	 * many different methods.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}

		// Check if class is instance of before casting
		if (!(obj instanceof Heap12)) {
			return false;
		}
		// Checking if the class is equal
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		
		@SuppressWarnings("rawtypes")
		Heap12 comp = (Heap12) obj;

		if (size != comp.size) {
			return false;
		}

		// loop through to check if not equals
		for (int i = 0; i < this.size; i++) {
			if (!backing[i].equals(comp.backing[i]))
				return false;
		}

		// Return true if everything passes
		return true;
	}
}
