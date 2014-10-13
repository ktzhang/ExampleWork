/* Kevin Zhang        Programming Assignment 4    Heap12Tester.java
 * PID: A10810276     Login: cs12spq
 */

/**
 * Public tester that tests Heap12 class and all its implementations.
 * This includes methods such as size, 
 * add, remove, equals, and so on. Checks all cases
 * @author Kevin
 *
 */
public class Heap12Tester extends junit.framework.TestCase {
	public static void main(String args[]) {
		junit.textui.TestRunner.main(new String[] { "Heap12Tester" });
	}

	// The capacity of the test generally
	private int capacity = 321;
	// Making the random numbers
	private int minimum = -100;
	private int range = 200;

	public void testAdd() {
		Heap12<Integer> heap = new Heap12<Integer>();

		// Repeat 30 times
		for (int h = 0; h < 30; h++) {
			for (int i = 0; i < capacity; i++) {

				// Adding null elements should throw
				try {
					heap.add(null);
					fail();
					// Do nothing
				} catch (NullPointerException E) {
				}

				// Create a random number
				int randomNum = minimum + (int) (Math.random() * range);

				// Add the random number
				heap.add(new Integer(randomNum));

				// Show that size increments as we add
				assertEquals(i + 1, heap.size());
				
				// Check not empty
				assertFalse(heap.isEmpty());
			}

			int minVal = minimum + range + 1;
			while (heap.peek() != null) {
				// Removing the integer
				Integer removed = heap.remove();
				// Check to see if it is smaller than previous remove
				if ((removed.compareTo(new Integer(minVal)) <= 0))
					minVal = removed;
				else
					// Fail the test if not
					fail();
			}
			//Check empty
			assertTrue(heap.isEmpty());
		}
	}

	public void testRemove() {
		// Initialze
		Heap12<Integer> heap = new Heap12<Integer>();

		assertNull(heap.remove());

		// Repeat 30 times
		for (int h = 0; h < 30; h++) {
			for (int i = 0; i < capacity; i++) {

				// Adding null elements should throw
				try {
					heap.add(null);
					fail();
					// Do nothing
				} catch (NullPointerException E) {
				}

				// Create a random number
				int randomNum = minimum + (int) (Math.random() * range);

				// Add the random number
				heap.add(new Integer(randomNum));

				// Show that size increments as we add
				assertEquals(i + 1, heap.size());
			}

			// Init min value of remove
			int minVal = minimum + range + 1;

			// Check size remains > 0
			while (heap.size() > 0) {
				// Check not empty
				assertFalse(heap.isEmpty());

				// Removing the integer
				Integer removed = heap.remove();

				// Checking that remove is not null
				assertNotNull(removed);

				// Check to see if it is smaller than previous remove
				if ((removed.compareTo(new Integer(minVal)) <= 0))
					minVal = removed;
				else
					// Fail the test if not
					fail();
			}

			// Checking that remove from empty is null
			assertNull(heap.remove());
			assertTrue(heap.isEmpty());

			// heap.printS();
		}
	}

	public void testPeek() {
		// Initialze
		Heap12<Integer> heap = new Heap12<Integer>();

		assertNull(heap.peek());

		// Repeat 30 times
		for (int h = 0; h < 30; h++) {

			Integer max = Integer.MIN_VALUE;
			for (int i = 0; i < capacity; i++) {
				// Create a random number
				Integer randomNum = minimum + (int) (Math.random() * range);

				// Changes the max num if nessesary
				if (randomNum.compareTo(max) > 0)
					max = randomNum;

				// Add the random number
				heap.add(new Integer(randomNum));

				// Make sure that peeking gets the max num
				assertNotNull(heap.peek());
				assertEquals(max, heap.peek());

				// Show that size increments as we add
				assertEquals(i + 1, heap.size());
			}

			// Init min value of remove
			int minVal = minimum + range + 1;

			// Check size remains > 0
			while (heap.size() > 0) {
				assertFalse(heap.isEmpty());

				// Removing the integer
				Integer removed = heap.remove();

				// Checking that remove is not null
				assertNotNull(removed);

				// Check to see if it is smaller than previous remove
				if ((removed.compareTo(new Integer(minVal)) <= 0))
					minVal = removed;
				else
					// Fail the test if not
					fail();
			}

			// Checking that remove from empty is null
			assertNull(heap.remove());
			assertTrue(heap.isEmpty());

			// heap.printS();
		}
	}

	public void testCopyEquals() {
		Heap12<Integer> heap = new Heap12<Integer>();

		// Execute this statement the amount of times the capacity is
		for (int i = 0; i < capacity; i++) {
			// Create a random number
			Integer randomNum = minimum + (int) (Math.random() * range);
			// Add the random number
			heap.add(new Integer(randomNum));
		}

		Heap12<Integer> heapCopy = new Heap12<Integer>(heap);

		// Check that they are equal and their hash codes are equals as well
		assertTrue(heapCopy.equals(heap));
		assertEquals(heap.hashCode(), heapCopy.hashCode());

	}

	/**
	 * Tests by adding random elements together and show that they are equal or
	 * not equal.
	 */
	public void testEquals() {
		// Initializing 2 different heap objects
		Heap12<Integer> heap = new Heap12<Integer>();
		heap.add(26);
		heap.add(2);
		heap.add(654);
		heap.add(2);
		heap.add(1);
		heap.add(22);
		heap.add(3);
		assertEquals(7, heap.size());

		Heap12<Integer> heap2 = new Heap12<Integer>();
		heap2.add(1);
		heap2.add(26);
		heap2.add(2);
		heap2.add(2);
		heap2.add(22);
		heap2.add(654);
		heap2.add(3);

		// Check that removing them is equal
		for (int i = 0; i < 7; i++) {
			assertEquals(heap.peek(), heap2.peek());
			assertEquals(heap.remove(), heap2.remove());
		}
		// Check that they are equal
		assertTrue(heap2.equals(heap));
		assertEquals(heap2.hashCode(), heap.hashCode());

		//Starting a new struture which checks if the leaves are same
		heap.add(3);
		heap.add(1);
		heap.add(2);

		heap2.add(3);
		heap2.add(2);
		heap2.add(1);

		// These should'nt be the same
		assertFalse(heap2.equals(heap));

	}

	/**
	 * Test case to test the sorting method of Heap12
	 */
	public void testSort() {
		Integer[] arr = new Integer[capacity];

		// Adding integers in reverse order
		for (int j = 0, i = capacity - 1; j < capacity; j++, i--)
			arr[j] = i;

		// Sorting
		Heap12.sort(arr);

		// Check if sort
		for (int i = 0; i < capacity; i++) {
			assertEquals(new Integer(i), arr[i]);
		}

		arr = new Integer[capacity];
		Integer[] arr2 = new Integer[capacity];

		//Performs random sorting 30 timse
		for (int h = 0; h < 30; h++) {
			for (int i = 0; i < capacity; i++) {
				// Create a random number
				int randomNum = minimum + (int) (Math.random() * range);
				// Add the random number
				arr[i] = new Integer(randomNum);
				arr2[i] = new Integer(randomNum);
			}

			//Calling sort array
			Heap12.sort(arr);
			
			//Set temp integer
			int temp = 0;
			
			//BubbleSort
			//Sort the outside method
			for (int i = 0; i < arr2.length; i++) {
				//Pointer loop
				for (int j = 1; j < (arr2.length - i); j++) {

					if (arr2[j - 1] > arr2[j]) {
						temp = arr2[j - 1];
						arr2[j - 1] = arr2[j];
						arr2[j] = temp;
					}
				}
			}
			
			//Check if the arrays are sorted
			for(int i = 0; i < arr.length; i++){
				if((int)arr[i] != (int)arr2[i]){
					fail();
				}
			}
		}

	}
}
