package ummisco.genstar.ipf;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ControlTotalsTest.class, IPFTest.class, SampleDataTest.class, 
	ThreeWayIPFTest.class, TwoWayIPFTest.class })
public class IPFTestSuite {

}
