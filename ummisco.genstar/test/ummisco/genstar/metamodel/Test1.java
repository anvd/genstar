package ummisco.genstar.metamodel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ummisco.genstar.util.SharedInstances;

public class Test1 {

	private String name;
	
	public Test1() {}
	
	@Override public String toString() {
		return name;
	}
	
	@Test public void testA() {
		List<Test1> tests = new ArrayList<Test1>();
		Test1 holder = null;

		for (int i=0; i<10; i++) { 
			holder = new Test1();
			holder.name = "Test " + i;
			tests.add(holder);
		}
		
		for (Test1 t : tests) {
			System.out.println("Iterate through " + t);
			
			holder = t; 
			tests.remove(t);
			if (SharedInstances.RandomNumberGenerator.nextInt(10) > 5) {
				tests.add(holder);
			}
		}
	}
}
