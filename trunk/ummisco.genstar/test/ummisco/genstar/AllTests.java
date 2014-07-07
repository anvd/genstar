package ummisco.genstar;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ummisco.genstar.metamodel.AttributeInferenceGenerationRuleTest;
import ummisco.genstar.metamodel.EntityAttributeValueTest;
import ummisco.genstar.metamodel.EntityTest;
import ummisco.genstar.metamodel.AttributeValuesFrequencyTest;
import ummisco.genstar.metamodel.FrequencyDistributionGenerationRuleTest;
import ummisco.genstar.metamodel.RangeValuesAttributeTest;
import ummisco.genstar.metamodel.UniqueValuesAttributeTest;
import ummisco.genstar.metamodel.PopulationTest_Elementary;
import ummisco.genstar.metamodel.RangeValueTest;
import ummisco.genstar.metamodel.SyntheticPopulationGeneratorTest_Bondy;
import ummisco.genstar.metamodel.SyntheticPopulationTest_Bondy;
import ummisco.genstar.metamodel.UniqueValueTest;

@RunWith(Suite.class)
@SuiteClasses({ AttributeInferenceGenerationRuleTest.class, EntityAttributeValueTest.class,
	EntityTest.class, RangeValuesAttributeTest.class, 
	UniqueValuesAttributeTest.class,  AttributeValuesFrequencyTest.class, 
	FrequencyDistributionGenerationRuleTest.class,
	PopulationTest_Elementary.class,  RangeValueTest.class,
	SyntheticPopulationGeneratorTest_Bondy.class, SyntheticPopulationTest_Bondy.class,
	UniqueValueTest.class })
public class AllTests {

}
