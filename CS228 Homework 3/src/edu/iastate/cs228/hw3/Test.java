package edu.iastate.cs228.hw3;

public class Test {

	public static void main(String[] args) {


		// First method tests
		PrimeFactorization a = new PrimeFactorization(25480);
		PrimeFactorization b = new PrimeFactorization(98);
		System.out.println(a.toString());
		System.out.println(b.toString());
		
		System.out.println(PrimeFactorization.dividedBy(b,a));
//		System.out.println(a.toString());
//		System.out.println(a.value());


		


//		PrimeFactorization test2 = new PrimeFactorization(2);
//		test.multiply(2);
//		System.out.println(test.toString());
//		System.out.println(test.value());
		
//		System.out.println(test.toString());
//		System.out.println(test2.toString());
//		System.out.println(test.gcd(test2).toString());
//		System.out.println(PrimeFactorization.gcd(test, test2));
//		test.dividedBy(2524);
//		System.out.println(test.toString());
//		System.out.println(PrimeFactorization.dividedBy(test, test2).toString());
//		
//		System.out.println(PrimeFactorization.Euclidean(1231, 4243));
//		System.out.println(test.toString());
//		System.out.println(test2.toString());
//		test.multiply(test2);
//		PrimeFactorization test3 = PrimeFactorization.multiply(test, test2);
//		System.out.println(test3.toString());


	}
	
	public static boolean prime(int n) {
		
	    // Corner case 
        if (n <= 1) return false; 
      
        // Check from 2 to n-1 
        for (int i = 2; i < Math.sqrt((double)n); i++) 
            if (n % i == 0) 
                return false; 
      
        return true; 
		
	}

}
