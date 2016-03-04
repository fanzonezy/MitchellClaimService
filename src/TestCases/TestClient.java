package TestCases;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestClient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 
		Result result1 = JUnitCore.runClasses(TestUnitForCreate.class);
	    for (Failure failure : result1.getFailures()) {
	        System.out.println(failure.toString());
	    }
	    System.out.println(result1.wasSuccessful());

	    Result result2 = JUnitCore.runClasses(TestUnitForReadByCalimNumber.class);
	    for (Failure failure : result2.getFailures()) {
	         System.out.println(failure.toString());
	    }
	    System.out.println(result2.wasSuccessful());
	   
	    Result result3 = JUnitCore.runClasses(TestUnitForReadByLossDate.class);
	    for (Failure failure : result3.getFailures()) {
	        System.out.println(failure.toString());
	    }
	    System.out.println(result3.wasSuccessful());
	    
		Result result4 = JUnitCore.runClasses(TestUnitForUpdate.class);
		for (Failure failure : result4.getFailures()) {
			System.out.println(failure.toString());
		}
		System.out.println(result4.wasSuccessful());
	    
	    Result result5 = JUnitCore.runClasses(TestUnitForReadVehicle.class);
	    for (Failure failure : result5.getFailures()) {
	        System.out.println(failure.toString());
	    }
	    System.out.println(result5.wasSuccessful());
    
	    Result result6 = JUnitCore.runClasses(TestUnitForDelete.class);
	    for (Failure failure : result6.getFailures()) {
	        System.out.println(failure.toString());
	    }
	    System.out.println(result6.wasSuccessful());
	     
	}
}
