package ummisco.genstar.ipf;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ControlledAndSupplementaryAtttributesTest.class, ControlTotalsTest.class,
	FiveWayIpfTest.class, FiveWayIterationTest.class, FourWayIpfTest.class, FourWayIterationTest.class, 
	GroupComponentSampleDataTest.class, IpfFactoryTest.class, IpfIterationTest.class,
	IpfTest.class, SampleDataGenerationRuleTest.class, SampleDataTest.class, 
	SixWayIpfTest.class, SixWayIterationTest.class, ThreeWayIpfTest.class, 
	ThreeWayIterationTest.class, TwoWayIpfTest.class, TwoWayIterationTest.class })
public class IpfTestSuite {

}
