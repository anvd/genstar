package ummisco.genstar.ipf;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ControlledAndSupplementaryAtttributesTest.class, ControlTotalsTest.class,
	GroupComponentSampleDataTest.class, IPFFactoryTest.class, IPFIterationTest.class,
	IPFTest.class, SampleDataGenerationRuleTest.class,
	SampleDataTest.class, SampleEntityPopulationTest.class, SampleEntityTest.class,
	ThreeWayIPFTest.class, ThreeWayIterationTest.class, TwoWayIPFTest.class, TwoWayIterationTest.class })
public class IPFTestSuite {

}
