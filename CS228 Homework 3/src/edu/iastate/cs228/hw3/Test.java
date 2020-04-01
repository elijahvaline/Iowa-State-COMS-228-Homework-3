package edu.iastate.cs228.hw3;

public class Test {

	public static void main(String[] args) {
		PrimeFactorization test = new PrimeFactorization(234);
		PrimeFactorization test2 = new PrimeFactorization(2);
		test.dividedBy(2524);
		System.out.println(test.toString());
		System.out.println(PrimeFactorization.dividedBy(test, test2).toString());
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
