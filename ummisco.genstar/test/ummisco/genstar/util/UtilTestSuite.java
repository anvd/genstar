package ummisco.genstar.util;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	AttributeUtilsTest.class, FrequencyDistributionUtilsTest.class, GenstarCsvFileTest.class, 
	GenstarUtilsTest.class, IpfUtilsTest.class, IpuUtilsTest.class
})
public class UtilTestSuite {

}
