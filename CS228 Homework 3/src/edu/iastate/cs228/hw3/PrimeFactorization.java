package edu.iastate.cs228.hw3;

import java.awt.Cursor;

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
		value = 1;

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

		if (n < 1) {
			throw new IllegalArgumentException();
		}

		head = new Node(null);
		tail = new Node(null);
		head.next = tail;
		tail.previous = head;
		size = 0;
		value = n;

		int multiplicity = 0;
		// Checks if divisible by 2
		while (n % 2 == 0) {
			n /= 2;
			multiplicity++;
		}
		if (multiplicity != 0) {
			add(2, multiplicity);
		}

		// Iterates through all other possible numbers for primes
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
			updateValue();

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

		for (int i = 2; i <= Math.sqrt((double) n); i++)
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
		if (n < 1) {
			throw new IllegalArgumentException();
		}
		int multiplicity = 0;
		PrimeFactorizationIterator it = iterator();
		PrimeFactor temp;

		// Uses the same ideas as the constructor for the prime factorization.
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
		}

		for (int i = 3; i <= Math.sqrt(n); i += 2) {
			multiplicity = 0;
			while (n % i == 0) {
				n /= i;
				multiplicity++;
			}

			if (multiplicity != 0) {
				// If the cursor is already in the factorization
				if (containsPrimeFactor(i) == true) {

					while (it.cursor.pFactor.prime != i) {
						it.next();
					}
					it.cursor.pFactor.multiplicity += multiplicity;
				}

				// If a new node is necessary
				else {
					temp = new PrimeFactor(i, multiplicity);

					boolean stop = false;

					for (int j = 0; j < size; j++) {

						if (it.cursor.pFactor == null) {
							break;
						}
						if (i > it.cursor.pFactor.multiplicity) {
							break;
						}
						it.next();
					}
					it.add(temp);
				}
			}
		}
		// The final prime factor
		if (n != 1) {
			if (containsPrimeFactor((int) n) == true) {

				while (it.cursor.pFactor.prime != (int) n) {
					it.next();
				}
				it.cursor.pFactor.multiplicity += 1;
			}

			else {
				while ((int) n > it.cursor.pFactor.prime) {
					it.next();
				}
				temp = new PrimeFactor((int) n, multiplicity);
				it.add(temp);
			}
		}

		updateValue();
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

		// Same ideas as previous class. If the prime is already in the factorization,
		// it adds the multiplicity, if not,
		// adds a new node.
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
				iter.add(it.cursor.pFactor);
			}
			it.next();
		}

		updateValue();
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

		// Almost exactly same as the previous method, just static, but you already know
		// that =D
		for (int i = 0; i < pf2.size; i++) {
			if (pf1.containsPrimeFactor(it.cursor.pFactor.prime)) {
				while (it.cursor.pFactor.prime != iter.cursor.pFactor.prime) {
					iter.next();
				}
				iter.cursor.pFactor.multiplicity += it.cursor.pFactor.multiplicity;
			} else {
				while (it.cursor.pFactor.prime > iter.cursor.pFactor.prime) {
					iter.next();
				}
				iter.add(it.cursor.pFactor);
			}
			it.next();
		}

		pf1.updateValue();
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
		if (n <= 0) {
			throw new IllegalArgumentException();
		}
		
		if (this.value != -1 && this.value < n) {
			return false;
		}

		else {

			PrimeFactorization temp = new PrimeFactorization(n);
			dividedBy(temp);
			updateValue();
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
		} else if ((this.value != -1 && pf.value == -1)) {
			return false;
		} else if (this.value == pf.value) {
			clearList();
			this.add(1, 1);
			return true;
		}

		PrimeFactorization copy = new PrimeFactorization(this);
		PrimeFactorizationIterator iterCopy = copy.iterator();
		PrimeFactorizationIterator iterPf = pf.iterator();

		// Repeats while the cursor != the tail
		// And this just goes off the documentation.
		while (!(iterPf.cursor == pf.tail)) {
			while ((iterCopy.cursor.pFactor.prime >= iterPf.cursor.pFactor.prime) == false
					&& (!iterCopy.hasNext() && iterPf.hasNext()) == false) {
				iterCopy.next();
				if (iterCopy.cursor.pFactor == null) {
					return false;
				}
			}

			if (!(iterCopy.hasNext()) && iterPf.hasNext()) {
				return false;
			}

			else {
				if (iterCopy.cursor.pFactor.prime > iterPf.cursor.pFactor.prime) {
					return false;
				}
				if (iterCopy.cursor.pFactor.prime == iterPf.cursor.pFactor.prime) {
					if (iterCopy.cursor.pFactor.multiplicity < iterPf.cursor.pFactor.multiplicity) {
						return false;
					}
				}
				if (iterCopy.cursor.pFactor.prime == iterPf.cursor.pFactor.prime) {
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
		updateValue();
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
			return null;
		} else if ((pf1.value != -1 && pf2.value == -1)) {
			return null;
		} else if (pf1.value == pf2.value) {
			pf1.clearList();
			pf1.add(1, 1);
			return pf1;
		}

		// Same thing as last method.
		PrimeFactorization copy = new PrimeFactorization(pf1);
		PrimeFactorizationIterator iterCopy = copy.iterator();
		PrimeFactorizationIterator iterPf = pf2.iterator();
		while (!(iterPf.cursor == pf2.tail)) {
			while ((iterCopy.cursor.pFactor.prime >= iterPf.cursor.pFactor.prime) == false
					&& (!iterCopy.hasNext() && iterPf.hasNext()) == false) {
				iterCopy.next();
				if (iterCopy.cursor.pFactor == null) {
					return null;
				}
			}

			if (!(iterCopy.hasNext()) && iterPf.hasNext()) {
				return null;
			}

			else {
				if (iterCopy.cursor.pFactor.prime > iterPf.cursor.pFactor.prime) {
					return null;
				}
				if (iterCopy.cursor.pFactor.prime == iterPf.cursor.pFactor.prime) {
					if (iterCopy.cursor.pFactor.multiplicity < iterPf.cursor.pFactor.multiplicity) {
						return null;
					}
				}
				if (iterCopy.cursor.pFactor.prime == iterPf.cursor.pFactor.prime) {
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
		pf1.updateValue();
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
		if (n < 1) {
			throw new IllegalArgumentException();
		}

		PrimeFactorization p;
		// If the value is not overflow, it factors it using Euclidean, if not, uses
		// gcd(PrimeFactor pf)
		if (this.value != OVERFLOW) {
			long gcd = Euclidean(this.value, n);
			p = new PrimeFactorization(gcd);
			return p;
		} else {
			p = new PrimeFactorization(n);
			return gcd(p);
		}

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

		if (m < 1 || n < 1) {
			throw new IllegalArgumentException();
		}
		long temp = 0;
		while (n != 0) {
			temp = n;
			n = m % n;
			m = temp;
		}
		return m;
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

		PrimeFactorization p = new PrimeFactorization();
		PrimeFactorizationIterator iter = iterator();
		PrimeFactorizationIterator iterPf;

		int prime;
		int mul;
		int pfPrime;
		int pfMul;
		// cycles through both iterators, and if it finds a match, it adds it to p.
		for (int i = 0; i < size; i++) {

			prime = iter.cursor.pFactor.prime;
			mul = iter.cursor.pFactor.multiplicity;

			iterPf = pf.iterator();

			for (int j = 0; j < pf.size; j++) {

				pfPrime = iterPf.cursor.pFactor.prime;
				pfMul = iterPf.cursor.pFactor.multiplicity;

				if (pfPrime == prime) {
					p.add(prime, Math.min(pfMul, mul));
				}

				iterPf.next();
			}

			iter.next();
		}
		p.updateSize();
		p.updateValue();
		return p;
	}

	/**
	 * 
	 * @param pf1
	 * @param pf2
	 * @return prime factorization of the gcd of two numbers represented by pf1 and
	 *         pf2
	 */
	public static PrimeFactorization gcd(PrimeFactorization pf1, PrimeFactorization pf2) {

		PrimeFactorization p = new PrimeFactorization();
		PrimeFactorizationIterator iterPf1 = pf1.iterator();
		PrimeFactorizationIterator iterPf2;

		int prime;
		int mul;
		int pfPrime;
		int pfMul;
		// Same as last method, just static.
		for (int i = 0; i < pf1.size; i++) {

			prime = iterPf1.cursor.pFactor.prime;
			mul = iterPf1.cursor.pFactor.multiplicity;

			iterPf2 = pf2.iterator();

			for (int j = 0; j < pf2.size; j++) {

				pfPrime = iterPf2.cursor.pFactor.prime;
				pfMul = iterPf2.cursor.pFactor.multiplicity;

				if (pfPrime == prime) {
					p.add(prime, Math.min(pfMul, mul));
				}

				iterPf2.next();
			}

			iterPf1.next();
		}
		p.updateSize();
		p.updateValue();
		return p;
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
		if (isPrime(p) == false) {
			throw new IllegalArgumentException();
		}
		// This just looks at every node, and if it contains p, returns true, else,
		// returns false.
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
		// Adds a new node at the end of the list.
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

		if (m < 1) {
			throw new IllegalArgumentException();
		}
		// Finds the prime p, and then subracts m from the multiplicity.
		if (isPrime(p) == true) {
			int i = 0;
			int prime;
			PrimeFactorizationIterator it = iterator();

			for (i = 0; i < size; i++) {
				prime = it.cursor.pFactor.prime;
				if (prime == p) {
					it.cursor.pFactor.multiplicity -= m;
					if (it.cursor.pFactor.multiplicity <= 0) {
						unlink(it.cursor);
						size--;
					}
					return true;

				} else {
					it.next();

				}
			}
			return false;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @return size of the list
	 */
	public int size() {

		return size;
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
		if (size == 0) {
			return "" + 1;
		}
		// Calls toString on each node and adds it together.
		String out = "";

		for (int i = 0; i < size - 1; i++) {
			out += it.cursor.toString();
			it.next();
			out += " * ";
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

	/**
	 * Creates an array of the current PrimeFactorization
	 * @return The PrimeFactorization Array.
	 */
	public PrimeFactor[] toArray() {
		PrimeFactor[] arr = new PrimeFactor[size];
		int i = 0;
		for (PrimeFactor pf : this)
			arr[i++] = pf;
		return arr;
	}
	
	/**
	 * The iterator for the current object.
	 */
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
			if (m < 1)
				throw new IllegalArgumentException();

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
				return pFactor.toString();
				// return "" + pFactor.prime + "^" + pFactor.multiplicity;
			}

		}
	}

	private class PrimeFactorizationIterator implements ListIterator<PrimeFactor> {

		private Node cursor = head; //Head node
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
		/**
		 * Returns true if there is a next node.
		 */
		@Override
		public boolean hasNext() {
			if (index < size) {

				return true;
			}
			return false;
		}
		/**
		 * Returns whether there is a previous node.
		 */
		@Override
		public boolean hasPrevious() {
			if (index > 0) {
				return true;
			}
			return false;
		}
		/**
		 * Advances the cursor and returns the new cursor.
		 */
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
		/**
		 * Sets the cursor to the previous, and returns the new cursor.
		 */
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
			if (pending == null)
				throw new IllegalStateException();

			unlink(pending);
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
			if (cursor.previous != head) {
				if (pf.prime < cursor.previous.pFactor.prime) {
					throw new IllegalArgumentException();
				}
			}
			if (cursor != tail) {

				if (pf.prime > cursor.pFactor.prime) {
					throw new IllegalArgumentException();
				}
			}

			Node n = new Node(pf);
			link(cursor.previous, n);
			index++;
			size++;
			pending = null;

		}
		/**
		 * Returns the current index
		 */
		@Override
		public int nextIndex() {
			return index;
		}
		/**
		 * Returns the previous index
		 */
		@Override
		public int previousIndex() {
			return index - 1;
		}
		
		@Deprecated
		@Override
		public void set(PrimeFactor pf) {
			throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support set method");
		}

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
			updateSize();

			long val = 1;
			long v;
			int prime;
			int mul;

			for (int j = 0; j < size; j++) {
				//looks at every node and multiplies out the prime with the multiplicity.
				mul = it.cursor.pFactor.multiplicity;
				prime = it.cursor.pFactor.prime;
				v = prime;

				for (int i = 0; i < mul - 1; i++) {
					v = Math.multiplyExact(v, prime);
				}

				it.next();
				val = Math.multiplyExact(v, val);

			}

			value = val;
		}

		catch (ArithmeticException e) {
			value = OVERFLOW;
		}

	}

	/**
	 * Updates the size of the PrimeFactorization
	 */
	private void updateSize() {
		size = 1;
		PrimeFactorizationIterator it = iterator();
		while (it.cursor.next.pFactor != null) {
			size++;
			it.next();

		}

	}
}
