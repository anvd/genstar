package ummisco.genstar.ipu;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	IpuControlledAndSupplementaryAttributesTest.class, IpuControlTotalsTest.class,
	IpuGenerationRuleTest.class, IpuTest.class, 
})
public class IpuTestSuite {

}
