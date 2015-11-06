package ummisco.genstar.ipf;

import static org.junit.Assert.*;

import java.util.List;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.AbstractAttribute;

@RunWith(JMockit.class)
public class IPFFactoryTest {

	@Test(expected = GenstarException.class) public void testCreateIPFWithNullGenerationRule() throws GenstarException {
		IPFFactory.createIPF(null); 
	}
	
	
	@Test public void testCreateTwoWayIPFSucessfully(@Mocked final SampleDataGenerationRule rule, @Mocked final List<AbstractAttribute> controlledAttributes,
			@Mocked final TwoWayIPF mockedIpf) throws GenstarException {
		
		new Expectations() {{
			rule.getControlledAttributes(); result = controlledAttributes;
			controlledAttributes.size(); result = 2;
		}};
		
		IPF ipf = IPFFactory.createIPF(rule);
		assertTrue(ipf instanceof TwoWayIPF);
	}
	
	
	@Test public void testCreateThreeWayIPFSuccessfully(@Mocked final SampleDataGenerationRule rule, @Mocked final List<AbstractAttribute> controlledAttributes,
			@Mocked final ThreeWayIPF mockedIpf) throws GenstarException {
		
		new Expectations() {{
			rule.getControlledAttributes(); result = controlledAttributes;
			controlledAttributes.size(); result = 3;
		}};
		
		IPF ipf = IPFFactory.createIPF(rule);
		assertTrue(ipf instanceof ThreeWayIPF);
	}
}
