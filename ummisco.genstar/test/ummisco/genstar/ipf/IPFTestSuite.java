package ummisco.genstar.ipf;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ControlledAndSupplementaryAtttributesTest.class, ControlTotalsTest.class,
	GroupComponentSampleDataTest.class, IPFFactoryTest.class, IPFIterationTest.class,
	IPFTest.class, SampleDataGenerationRuleTest.class, SampleDataTest.class, SampleEntityPopulationTest.class, 
	SampleEntityTest.class, TwoWayIPFTest.class, TwoWayIterationTest.class, ThreeWayIPFTest.class, 
	ThreeWayIterationTest.class, FourWayIPFTest.class, FourWayIterationTest.class, FiveWayIPFTest.class, 
	FiveWayIterationTest.class, SixWayIPFTest.class, SixWayIterationTest.class })
public class IPFTestSuite {

}
