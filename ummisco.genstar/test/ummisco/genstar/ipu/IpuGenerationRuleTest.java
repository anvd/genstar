package ummisco.genstar.ipu;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class IpuGenerationRuleTest {

	/*
	public IpuGenerationRule(final ISingleRuleGenerator groupPopulationGenerator, final ISingleRuleGenerator componentPopulationGenerator, final String name, 
			final GenstarCSVFile groupControlledAttributesFile, final GenstarCSVFile groupControlTotalsFile, final GenstarCSVFile groupSupplementaryAttributesFile,
			final GenstarCSVFile componentControlledAttributesFile, final GenstarCSVFile componentControlTotalsFile, final GenstarCSVFile componentSupplementaryAttributesFile,
			final int maxIterations) throws GenstarException {
	 */
	
	@Test public void testIpuGenerationRuleWithNullComponentPopulationGenerator() throws GenstarException {
		fail("not yet implemented");
	}
	
	@Test public void testGetComponentControlledAttributes() throws GenstarException {
		fail("not yet implemented");
	}
	
	
	@Test public void testSetSampleData() throws GenstarException {
		fail("not yet implemented");
	}
}
