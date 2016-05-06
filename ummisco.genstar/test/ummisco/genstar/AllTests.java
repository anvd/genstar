package ummisco.genstar;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ummisco.genstar.metamodel.AttributeInferenceGenerationRuleTest;
import ummisco.genstar.metamodel.EntityTest;
import ummisco.genstar.metamodel.FrequencyDistributionGenerationRuleTest;
import ummisco.genstar.metamodel.PopulationTest_Elementary;
import ummisco.genstar.metamodel.SingleRuleGeneratorTest;
import ummisco.genstar.metamodel.SyntheticPopulationGeneratorTest_Bondy;
import ummisco.genstar.metamodel.SyntheticPopulationTest_Bondy;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequencyTest;
import ummisco.genstar.metamodel.attributes.EntityAttributeValueTest;
import ummisco.genstar.metamodel.attributes.RangeValueTest;
import ummisco.genstar.metamodel.attributes.RangeValuesAttributeTest;
import ummisco.genstar.metamodel.attributes.UniqueValueTest;
import ummisco.genstar.metamodel.attributes.UniqueValuesAttributeTest;
import ummisco.genstar.metamodel.population.Population;

@RunWith(Suite.class)
@SuiteClasses({ AttributeInferenceGenerationRuleTest.class, EntityAttributeValueTest.class,
	Population.class, EntityTest.class, RangeValuesAttributeTest.class, 
	UniqueValuesAttributeTest.class,  AttributeValuesFrequencyTest.class, 
	FrequencyDistributionGenerationRuleTest.class,
	PopulationTest_Elementary.class,  RangeValueTest.class, SingleRuleGeneratorTest.class,
	SyntheticPopulationGeneratorTest_Bondy.class, SyntheticPopulationTest_Bondy.class,
	UniqueValueTest.class })
public class AllTests {

}
