package ummisco.genstar.ipf;

import static org.junit.Assert.*;

import java.util.List;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;

@RunWith(JMockit.class)
public class IpfFactoryTest {

	@Test(expected = GenstarException.class) public void testCreateIPFWithNullGenerationRule() throws GenstarException {
		IpfFactory.createIPF(null); 
	}
	
	
	@Test public void testCreateTwoWayIPFSucessfully(@Mocked final IpfGenerationRule rule, @Mocked final List<AbstractAttribute> controlledAttributes,
			@Mocked final TwoWayIpf mockedIpf) throws GenstarException {
		
		new Expectations() {{
			rule.getControlledAttributes(); result = controlledAttributes;
			controlledAttributes.size(); result = 2;
		}};
		
		Ipf ipf = IpfFactory.createIPF(rule);
		assertTrue(ipf instanceof TwoWayIpf);
	}
	
	
	@Test public void testCreateThreeWayIPFSuccessfully(@Mocked final IpfGenerationRule rule, @Mocked final List<AbstractAttribute> controlledAttributes,
			@Mocked final ThreeWayIpf mockedIpf) throws GenstarException {
		
		new Expectations() {{
			rule.getControlledAttributes(); result = controlledAttributes;
			controlledAttributes.size(); result = 3;
		}};
		
		Ipf ipf = IpfFactory.createIPF(rule);
		assertTrue(ipf instanceof ThreeWayIpf);
	}
	
	
	@Test public void testCreateFourWayIPFSuccessfully(@Mocked final IpfGenerationRule rule, @Mocked final List<AbstractAttribute> controlledAttributes,
			@Mocked final FourWayIpf mockedIpf) throws GenstarException {

		new Expectations() {{
			rule.getControlledAttributes(); result = controlledAttributes;
			controlledAttributes.size(); result = 4;
		}};
		
		Ipf ipf = IpfFactory.createIPF(rule);
		assertTrue(ipf instanceof FourWayIpf);
	}
	
	
	@Test public void testCreateFiveWayIPFSuccessfully(@Mocked final IpfGenerationRule rule, @Mocked final List<AbstractAttribute> controlledAttributes,
			@Mocked final FiveWayIpf mockedIpf) throws GenstarException {

		new Expectations() {{
			rule.getControlledAttributes(); result = controlledAttributes;
			controlledAttributes.size(); result = 5;
		}};
		
		Ipf ipf = IpfFactory.createIPF(rule);
		assertTrue(ipf instanceof FiveWayIpf);
	}
	

	@Test public void testCreateFixWayIPFSuccessfully(@Mocked final IpfGenerationRule rule, @Mocked final List<AbstractAttribute> controlledAttributes,
			@Mocked final SixWayIpf mockedIpf) throws GenstarException {

		new Expectations() {{
			rule.getControlledAttributes(); result = controlledAttributes;
			controlledAttributes.size(); result = 6;
		}};
		
		Ipf ipf = IpfFactory.createIPF(rule);
		assertTrue(ipf instanceof SixWayIpf);
	}
}
