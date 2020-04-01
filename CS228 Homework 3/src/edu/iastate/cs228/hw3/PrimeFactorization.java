package edu.iastate.cs228.hw3;

/**
 *  
 * @author
 *
 */

import java.util.ListIterator;

public class PrimeFactorization implements Iterable<PrimeFactor> {
	private static final long OVERFLOW = -1;
	private long value; // the factored integer
						// it is set to OVERFLOW when the number is greater than 2^63-1, the
						// largest number representable by the type long.

	/**
	 * Reference to dummy node at the head.
	 */
	private Node head;

	/**
	 * Reference to dummy node at the tail.
	 */
	private Node tail;

	private int size; // number of distinct prime factors

	// ------------
	// Constructors
	// ------------

	/**
	 * Default constructor constructs an empty list to represent the number 1.
	 * 
	 * Combined with the add() method, it can be used to create a prime
	 * factorization.
	 */
	public PrimeFactorization() {
		head = new Node(null);
		tail = new Node(null);
		head.next = tail;
		tail.previous = head;
		size = 0;
		value = 0;

	}

	/**
	 * Obtains the prime factorization of n and creates a doubly linked list to
	 * store the result. Follows the direct search factorization algorithm in
	 * Section 1.2 of the project description.
	 * 
	 * @param n
	 * @throws IllegalArgumentException if n < 1
	 */
	public PrimeFactorization(long n) throws IllegalArgumentException {

		head = new Node(null);
		tail = new Node(null);
		head.next = tail;
		tail.previous = head;
		size = 0;
		value = n;

		int multiplicity = 0;
		while (n % 2 == 0) {
			n /= 2;
			multiplicity++;
		}
		if (multiplicity != 0) {
			add(2, multiplicity);
		}

		for (int i = 3; i <= Math.sqrt(n); i += 2) {
			multiplicity = 0;
			// While i divides n, print i and divide n
			while (n % i == 0) {
				n /= i;
				multiplicity++;
			}
			if (multiplicity != 0) {
				add(i, multiplicity);
			}
		}

		// This condition is to handle the case whien
		// n is a prime number greater than 2
		if (n > 2)
			add((int) n, 1);

	}

	/**
	 * Copy constructor. It is unnecessary to verify the primality of the numbers in
	 * the list.
	 * 
	 * @param pf
	 */
	public PrimeFactorization(PrimeFactorization pf) {
		value = pf.value;
		head = pf.head;
		head = new Node(null);
		tail = new Node(null);
//		size = pf.size;
		head.next = tail;
		tail.previous = head;
		PrimeFactorizationIterator it = pf.iterator();
	
		while (it.hasNext()) {
			
			add(it.cursor.pFactor.prime, it.cursor.pFactor.multiplicity);
			it.next();

		}
	}

	/**
	 * Constructs a factorization from an array of prime factors. Useful when the
	 * number is too large to be represented even as a long integer.
	 * 
	 * @param pflist
	 */
	public PrimeFactorization(PrimeFactor[] pfList) {
		head = new Node(null);
		tail = new Node(null);
		head.next = tail;
		tail.previous = head;

		for (int i = 0; i < pfList.length; i++) {
			add(pfList[i].prime, pfList[i].multiplicity);

		}
	}

	// --------------
	// Primality Test
	// --------------

	/**
	 * Test if a number is a prime or not. Check iteratively from 2 to the largest
	 * integer not exceeding the square root of n to see if it divides n.
	 * 
	 * @param n
	 * @return true if n is a prime false otherwise
	 */
	public static boolean isPrime(long n) {
		if (n <= 1)
			return false;

		// Check from 2 to n-1
		for (int i = 2; i < Math.sqrt((double) n); i++)
			if (n % i == 0)
				return false;

		return true;
	}

	// ---------------------------
	// Multiplication and Division
	// ---------------------------

	/**
	 * Multiplies the integer v represented by this object with another number n.
	 * Note that v may be too large (in which case this.value == OVERFLOW). You can
	 * do this in one loop: Factor n and traverse the doubly linked list
	 * simultaneously. For details refer to Section 3.1 in the project description.
	 * Store the prime factorization of the product. Update value and size.
	 * 
	 * @param n
	 * @throws IllegalArgumentException if n < 1
	 */
	public void multiply(long n) throws IllegalArgumentException {
		int multiplicity = 0;
		PrimeFactorizationIterator it = iterator();
		while (n % 2 == 0) {

			n /= 2;
			multiplicity++;
		}
		if (multiplicity != 0) {
			if (containsPrimeFactor(2) == true) {

				while (it.cursor.pFactor.prime != 2) {
					it.next();
				}
				it.cursor.pFactor.multiplicity += multiplicity;
			}

			else {
				add(2, multiplicity);
			}

			for (int i = 3; i <= Math.sqrt(n); i += 2) {
				multiplicity = 0;
				// While i divides n, print i and divide n
				while (n % i == 0) {
					n /= i;
					multiplicity++;
				}
				if (multiplicity != 0) {
					if (containsPrimeFactor(2) == true) {

						while (it.cursor.pFactor.prime != i) {
							it.next();
						}
						it.cursor.pFactor.multiplicity += multiplicity;
					}

					else {
						while (i > it.cursor.pFactor.prime) {
							it.next();
						}
						add(i, multiplicity);
					}
				}
			}
		}
	}

	/**
	 * Multiplies the represented integer v with another number in the factorization
	 * form. Traverse both linked lists and store the result in this list object.
	 * See Section 3.1 in the project description for details of algorithm.
	 * 
	 * @param pf
	 */
	public void multiply(PrimeFactorization pf) {

		PrimeFactorizationIterator iter = iterator();
		PrimeFactorizationIterator it = pf.iterator();

		for (int i = 0; i < pf.size; i++) {
			if (this.containsPrimeFactor(it.cursor.pFactor.prime)) {
				while (it.cursor.pFactor.prime != iter.cursor.pFactor.prime) {
					iter.next();
				}
				iter.cursor.pFactor.multiplicity += it.cursor.pFactor.multiplicity;
			} else {
				while (it.cursor.pFactor.prime > iter.cursor.pFactor.prime) {
					iter.next();
				}
				add(it.cursor.pFactor.prime, it.cursor.pFactor.multiplicity);
			}
			it.next();
		}
	}

	/**
	 * Multiplies the integers represented by two PrimeFactorization objects.
	 * 
	 * @param pf1
	 * @param pf2
	 * @return object of PrimeFactorization to represent the product
	 */
	public static PrimeFactorization multiply(PrimeFactorization pf1, PrimeFactorization pf2) {
		PrimeFactorizationIterator iter = pf1.iterator();
		PrimeFactorizationIterator it = pf2.iterator();
		int smaller = Math.min(pf1.size, pf2.size);

		// p1 is bigger

		for (int i = 0; i < smaller; i++) {
			if (pf1.containsPrimeFactor(it.cursor.pFactor.prime)) {
				while (it.cursor.pFactor.prime != iter.cursor.pFactor.prime) {
					iter.next();
				}
				iter.cursor.pFactor.multiplicity += it.cursor.pFactor.multiplicity;
			} else {
				while (it.cursor.pFactor.prime > iter.cursor.pFactor.prime) {
					iter.next();
				}
				pf1.add(it.cursor.pFactor.prime, it.cursor.pFactor.multiplicity);
			}
			it.next();
		}

		if (pf1.size < pf2.size) {

			while (it.hasNext()) {
				pf1.add(it.cursor.pFactor.prime, it.cursor.pFactor.multiplicity);
				it.next();
			}
		}
		return pf1;
	}

	/**
	 * Divides the represented integer v by n. Make updates to the list, value, size
	 * if divisible. No update otherwise. Refer to Section 3.2 in the project
	 * description for details.
	 * 
	 * @param n
	 * @return true if divisible false if not divisible
	 * @throws IllegalArgumentException if n <= 0
	 */
	public boolean dividedBy(long n) throws IllegalArgumentException {
		if (this.value != -1 && this.value < n) {
			return false;
		}

		else {

			PrimeFactorization temp = new PrimeFactorization(n);
			dividedBy(temp);
			return true;
		}
	}

	/**
	 * Division where the divisor is represented in the factorization form. Update
	 * the linked list of this object accordingly by removing those nodes housing
	 * prime factors that disappear after the division. No update if this number is
	 * not divisible by pf. Algorithm details are given in Section 3.2.
	 * 
	 * @param pf
	 * @return true if divisible by pf false otherwise
	 */
	public boolean dividedBy(PrimeFactorization pf) {



		if (this.value != -1 && pf.value != -1 && this.value < pf.value) {
			return false;
		} else if ((this.value != -1 && pf.value == -1))
		//|| (this.value%pf.value != 0)
			{
			return false;
		} else if (this.value == pf.value) {
			clearList();
			this.add(1, 1);
			return true;
		}
		
		PrimeFactorization copy = new PrimeFactorization(this);
		PrimeFactorizationIterator iterCopy = copy.iterator();
		PrimeFactorizationIterator iterPf = pf.iterator();
		while (!(iterPf.cursor == pf.tail)) {	
			while ((iterCopy.cursor.pFactor.prime >= iterPf.cursor.pFactor.prime) == false && (!iterCopy.hasNext() && iterPf.hasNext()) == false) {
				iterCopy.next();
				if (iterCopy.cursor.pFactor == null) {
					return false;
				}
						//&& iterPf.hasNext();
			}
			
			
			if (!(iterCopy.hasNext()) && iterPf.hasNext()) {
				return false;
			}

			else {
				if (iterCopy.cursor.pFactor.prime > iterPf.cursor.pFactor.prime) {
					return false;
				} if (iterCopy.cursor.pFactor.prime == iterPf.cursor.pFactor.prime) {
					if (iterCopy.cursor.pFactor.multiplicity > iterPf.cursor.pFactor.multiplicity) {
						return false;
					}
				} if (iterCopy.cursor.pFactor.prime == iterPf.cursor.pFactor.prime) {
					if (iterCopy.cursor.pFactor.multiplicity >= iterPf.cursor.pFactor.multiplicity) {
						iterCopy.cursor.pFactor.multiplicity -= iterPf.cursor.pFactor.multiplicity;
						if (iterCopy.cursor.pFactor.multiplicity == 0) {
							copy.unlink(iterCopy.cursor);
							copy.size--;
						}
						iterCopy.next();
						iterPf.next();

					}
				}
			}
		}
		
		this.head = copy.head;
		this.tail = copy.tail;
		this.size = copy.size;
//		s
		return true;
	}

	/**
	 * Divide the integer represented by the object pf1 by that represented by the
	 * object pf2. Return a new object representing the quotient if divisible. Do
	 * not make changes to pf1 and pf2. No update if the first number is not
	 * divisible by the second one.
	 * 
	 * @param pf1
	 * @param pf2
	 * @return quotient as a new PrimeFactorization object if divisible null
	 *         otherwise
	 */
	public static PrimeFactorization dividedBy(PrimeFactorization pf1, PrimeFactorization pf2) {
		



		if (pf1.value != -1 && pf2.value != -1 && pf1.value < pf2.value) {
			return pf1;
		} else if ((pf1.value != -1 && pf2.value == -1))
		//|| (this.value%pf.value != 0)
			{
			return pf1;
		} else if (pf1.value == pf2.value) {
			pf1.clearList();
			pf1.add(1, 1);
			return pf1;
		}
		
		PrimeFactorization copy = new PrimeFactorization(pf1);
		PrimeFactorizationIterator iterCopy = copy.iterator();
		PrimeFactorizationIterator iterPf = pf2.iterator();
		while (!(iterPf.cursor == pf2.tail)) {		
			while ((iterCopy.cursor.pFactor.prime >= iterPf.cursor.pFactor.prime) == false && (!iterCopy.hasNext() && iterPf.hasNext()) == false) {
				iterCopy.next();
				if (iterCopy.cursor.pFactor == null) {
					return pf1;
				}
						//&& iterPf.hasNext();
			}
			
			
			if (!(iterCopy.hasNext()) && iterPf.hasNext()) {
				return pf1;
			}

			else {
				if (iterCopy.cursor.pFactor.prime > iterPf.cursor.pFactor.prime) {
					return pf1;
				} if (iterCopy.cursor.pFactor.prime == iterPf.cursor.pFactor.prime) {
					if (iterCopy.cursor.pFactor.multiplicity > iterPf.cursor.pFactor.multiplicity) {
						return pf1;
					}
				} if (iterCopy.cursor.pFactor.prime == iterPf.cursor.pFactor.prime) {
					if (iterCopy.cursor.pFactor.multiplicity >= iterPf.cursor.pFactor.multiplicity) {
						iterCopy.cursor.pFactor.multiplicity -= iterPf.cursor.pFactor.multiplicity;
						if (iterCopy.cursor.pFactor.multiplicity == 0) {
							copy.unlink(iterCopy.cursor);
							copy.size--;
						}
						iterCopy.next();
						iterPf.next();

					}
				}
			}
		}
		
		pf1.head = copy.head;
		pf1.tail = copy.tail;
		pf1.size = copy.size;
		return pf1;
	}

	// -----------------------
	// Greatest Common Divisor
	// -----------------------

	/**
	 * Computes the greatest common divisor (gcd) of the represented integer v and
	 * an input integer n. Returns the result as a PrimeFactor object. Calls the
	 * method Euclidean() if this.value != OVERFLOW.
	 * 
	 * It is more efficient to factorize the gcd than n, which can be much greater.
	 * 
	 * @param n
	 * @return prime factorization of gcd
	 * @throws IllegalArgumentException if n < 1
	 */
	public PrimeFactorization gcd(long n) throws IllegalArgumentException {
		// TODO
		return null;
	}

	/**
	 * Implements the Euclidean algorithm to compute the gcd of two natural numbers
	 * m and n. The algorithm is described in Section 4.1 of the project
	 * description.
	 * 
	 * @param m
	 * @param n
	 * @return gcd of m and n.
	 * @throws IllegalArgumentException if m < 1 or n < 1
	 */
	public static long Euclidean(long m, long n) throws IllegalArgumentException {
		// TODO
		return 0;
	}

	/**
	 * Computes the gcd of the values represented by this object and pf by
	 * traversing the two lists. No direct computation involving value and pf.value.
	 * Refer to Section 4.2 in the project description on how to proceed.
	 * 
	 * @param pf
	 * @return prime factorization of the gcd
	 */
	public PrimeFactorization gcd(PrimeFactorization pf) {
		// TODO
		return null;
	}

	/**
	 * 
	 * @param pf1
	 * @param pf2
	 * @return prime factorization of the gcd of two numbers represented by pf1 and
	 *         pf2
	 */
	public static PrimeFactorization gcd(PrimeFactorization pf1, PrimeFactorization pf2) {
		// TODO
		return null;
	}

	// ------------
	// List Methods
	// ------------

	/**
	 * Traverses the list to determine if p is a prime factor.
	 * 
	 * Precondition: p is a prime.
	 * 
	 * @param p
	 * @return true if p is a prime factor of the number v represented by this
	 *         linked list false otherwise
	 * @throws IllegalArgumentException if p is not a prime
	 */
	public boolean containsPrimeFactor(int p) throws IllegalArgumentException {
		PrimeFactorizationIterator it = iterator();
		for (int i = 0; i < size; i++) {

			if (p == it.cursor.pFactor.prime) {
				return true;
			}
			it.next();
		}
		return false;
	}

	// The next two methods ought to be private but are made public for testing
	// purpose. Keep
	// them public

	/**
	 * Adds a prime factor p of multiplicity m. Search for p in the linked list. If
	 * p is found at a node N, add m to N.multiplicity. Otherwise, create a new node
	 * to store p and m.
	 * 
	 * Precondition: p is a prime.
	 * 
	 * @param p prime
	 * @param m multiplicity
	 * @return true if m >= 1 false if m < 1
	 */
	public boolean add(int p, int m) {
		PrimeFactor n = new PrimeFactor(p, m);
		PrimeFactorizationIterator it = iterator();
		it.cursor = tail;
		it.add(n);

		if (m >= 1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Removes m from the multiplicity of a prime p on the linked list. It starts by
	 * searching for p. Returns false if p is not found, and true if p is found. In
	 * the latter case, let N be the node that stores p. If N.multiplicity > m,
	 * subtracts m from N.multiplicity. If N.multiplicity <= m, removes the node N.
	 * 
	 * Precondition: p is a prime.
	 * 
	 * @param p
	 * @param m
	 * @return true when p is found. false when p is not found.
	 * @throws IllegalArgumentException if m < 1
	 */
	public boolean remove(int p, int m) throws IllegalArgumentException {
		// TODO
		return false;
	}

	/**
	 * 
	 * @return size of the list
	 */
	public int size() {
		// TODO
		return 0;
	}

	/**
	 * Writes out the list as a factorization in the form of a product. Represents
	 * exponentiation by a caret. For example, if the number is 5814, the returned
	 * string would be printed out as "2 * 3^2 * 17 * 19".
	 */
	@Override
	public String toString() {
		PrimeFactorizationIterator it = iterator();
		it.cursor = head.next;

		String out = "";
		
		for (int i = 0; i< size-1; i++) {
			out += it.cursor.toString();
			it.next();
			out+= " * ";
		}
		out += it.cursor.toString();
		return out;
	}

	// The next three methods are for testing, but you may use them as you like.

	/**
	 * @return true if this PrimeFactorization is representing a value that is too
	 *         large to be within long's range. e.g. 999^999. false otherwise.
	 */
	public boolean valueOverflow() {
		return value == OVERFLOW;
	}

	/**
	 * @return value represented by this PrimeFactorization, or -1 if
	 *         valueOverflow()
	 */
	public long value() {
		return value;
	}

	public PrimeFactor[] toArray() {
		PrimeFactor[] arr = new PrimeFactor[size];
		int i = 0;
		for (PrimeFactor pf : this)
			arr[i++] = pf;
		return arr;
	}

	@Override
	public PrimeFactorizationIterator iterator() {
		return new PrimeFactorizationIterator();
	}

	/**
	 * Doubly-linked node type for this class.
	 */
	private class Node {
		public PrimeFactor pFactor; // prime factor
		public Node next;
		public Node previous;

		/**
		 * Default constructor for creating a dummy node.
		 */
		public Node() {

		}

		/**
		 * Precondition: p is a prime
		 * 
		 * @param p prime number
		 * @param m multiplicity
		 * @throws IllegalArgumentException if m < 1
		 */
		public Node(int p, int m) throws IllegalArgumentException {
			this.pFactor = new PrimeFactor(p, m);
		}

		/**
		 * Constructs a node over a provided PrimeFactor object.
		 * 
		 * @param pf
		 * @throws IllegalArgumentException
		 */
		public Node(PrimeFactor pf) {
			this.pFactor = pf;
		}

		/**
		 * Printed out in the form: prime + "^" + multiplicity. For instance "2^3".
		 * Also, deal with the case pFactor == null in which a string "dummy" is
		 * returned instead.
		 */
		@Override
		public String toString() {
			if (pFactor == null) {
				return "dummy";
			} else {
				return "" + pFactor.prime + "^" + pFactor.multiplicity;
			}

		}
	}

	private class PrimeFactorizationIterator implements ListIterator<PrimeFactor> {
		// Class invariants:
		// 1) logical cursor position is always between cursor.previous and cursor
		// 2) after a call to next(), cursor.previous refers to the node just returned
		// 3) after a call to previous() cursor refers to the node just returned
		// 4) index is always the logical index of node pointed to by cursor

		private Node cursor = head;
		private Node pending = null; // node pending for removal
		private int index = 0;

		// other instance variables ...

		/**
		 * Default constructor positions the cursor before the smallest prime factor.
		 */
		public PrimeFactorizationIterator() {
			cursor = head.next;
			index = 0;

		}

		@Override
		public boolean hasNext() {
			if (index < size) {
				
				return true;
			}
			return false;
		}

		@Override
		public boolean hasPrevious() {
			if (index > 0) {
				return true;
			}
			return false;
		}

		@Override
		public PrimeFactor next() {

			if (hasNext()) {
				pending = cursor;
				cursor = cursor.next;
				index++;
				return cursor.pFactor;
			}

			return null;
		}

		@Override
		public PrimeFactor previous() {
			if (hasPrevious()) {
				pending = cursor;
				cursor = cursor.previous;
				index--;
				return cursor.pFactor;
			}
			return null;
		}

		/**
		 * Removes the prime factor returned by next() or previous()
		 * 
		 * @throws IllegalStateException if pending == null
		 */
		@Override
		public void remove() throws IllegalStateException {
			unlink(cursor);
			size--;
		}

		/**
		 * Adds a prime factor at the cursor position. The cursor is at a wrong position
		 * in either of the two situations below:
		 * 
		 * a) pf.prime < cursor.previous.pFactor.prime if cursor.previous != head. b)
		 * pf.prime > cursor.pFactor.prime if cursor != tail.
		 * 
		 * Take into account the possibility that pf.prime == cursor.pFactor.prime.
		 * 
		 * Precondition: pf.prime is a prime.
		 * 
		 * @param pf
		 * @throws IllegalArgumentException if the cursor is at a wrong position.
		 */
		@Override
		public void add(PrimeFactor pf) throws IllegalArgumentException {
			Node n = new Node(pf);
			link(cursor.previous, n);
			index++;
			size++;
			pending = null;

		}

		@Override
		public int nextIndex() {
			return index;
		}

		@Override
		public int previousIndex() {
			return index - 1;
		}

		@Deprecated
		@Override
		public void set(PrimeFactor pf) {
			throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support set method");
		}

		// Other methods you may want to add or override that could possibly facilitate
		// other operations, for instance, addition, access to the previous element,
		// etc.
		//
		// ...
		//
	}

	// --------------
	// Helper methods
	// --------------

	/**
	 * Inserts toAdd into the list after current without updating size.
	 * 
	 * Precondition: current != null, toAdd != null
	 */
	private void link(Node current, Node toAdd) {
		if (current != null && toAdd != null) {
			toAdd.previous = current;
			toAdd.next = current.next;
			current.next.previous = toAdd;
			current.next = toAdd;

		}
	}

	/**
	 * Removes toRemove from the list without updating size.
	 */
	private void unlink(Node toRemove) {
		toRemove.previous.next = toRemove.next;
		toRemove.next.previous = toRemove.previous;
	}

	/**
	 * Remove all the nodes in the linked list except the two dummy nodes.
	 * 
	 * Made public for testing purpose. Ought to be private otherwise.
	 */
	public void clearList() {
		head.next = tail;
		tail.previous = head;
		size = 0;
	}

	/**
	 * Multiply the prime factors (with multiplicities) out to obtain the
	 * represented integer. Use Math.multiply(). If an exception is throw, assign
	 * OVERFLOW to the instance variable value. Otherwise, assign the multiplication
	 * result to the variable.
	 * 
	 */
	private void updateValue() {
		PrimeFactorizationIterator it = iterator();

		try {
			int val;
			while (it.hasNext()) {
				val = it.next().prime;
				for (int i = 0; i < it.next().multiplicity; i++) {
					val += Math.multiplyExact(val, val);
				}
				value += val;
			}
		}

		catch (ArithmeticException e) {
			value = OVERFLOW;
		}

	}
}
