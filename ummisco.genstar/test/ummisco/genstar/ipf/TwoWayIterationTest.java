package ummisco.genstar.ipf;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;

@RunWith(JMockit.class)
public class TwoWayIterationTest {
	
	@Test public void testInitializeObjectSuccessfully(@Mocked final TwoWayIPF ipf) throws GenstarException {
		
		final double[][] data = {
				{ 1, 2 },
				{ 3, 4 },
				{ 5, 6 }
		};
		
		final int[] rowControls = { 10, 20, 30 };
		
		final int[] columnControls = { 30, 40 };
		
		new Expectations() {{
			ipf.getData(); result = data;
			ipf.getControls(0); result = rowControls;
			ipf.getControls(1); result = columnControls;
		}};
		
		TwoWayIteration iteration = new TwoWayIteration(ipf);
		assertTrue(iteration.getIteration() == 0);
		
		double[][] iterationData = iteration.getCopyData();
		for (int row=0; row<data.length; row++) {
			for (int column=0; column<data[0].length; column++) {
				assertTrue(data[row][column] == iterationData[row][column]);
			}
		}
	}
	
	@Test(expected = NullPointerException.class) public void testInitializeObjectWithNullData(@Mocked final TwoWayIPF ipf) throws GenstarException {
		final int[] rowControls = { 10, 20, 30 };
		
		final int[] columnControls = { 30, 40 };
		
		new Expectations() {{
			ipf.getData(); result = null;
			ipf.getControls(0); result = rowControls;
			ipf.getControls(1); result = columnControls;
		}};
		
		new TwoWayIteration(ipf);
	}
	
	@Test(expected = GenstarException.class) public void testInitializeObjectWithZeroMarginals(@Mocked final TwoWayIPF ipf, @Mocked final AbstractAttribute attribute, 
			@Mocked final AttributeValue attributeValue) throws GenstarException {
		final double[][] data = {
				{ 0, 2 },
				{ 0, 4 },
				{ 0, 6 }
		};
		
		final int[] rowControls = { 10, 20, 30 };
		
		final int[] columnControls = { 30, 40 };
		
		final List<AttributeValue> attributeValues = new ArrayList<AttributeValue>();
		attributeValues.add(attributeValue);
		
		new Expectations() {{
			ipf.getData(); result = data;
			ipf.getControls(0); result = rowControls;
			ipf.getControls(1); result = columnControls;
			
			ipf.getAttributeValues(anyInt); result = attributeValues;
			attributeValues.get(0); result = attributeValue;
			attribute.getNameOnData(); result = "dummy attribute name";
			attributeValue.toCsvString(); result = "dummy CSV string";
		}};
		
		new TwoWayIteration(ipf);
	}

	@Test(expected = NullPointerException.class) public void testInitializeObjectWithNullRowControls(@Mocked final TwoWayIPF ipf) throws GenstarException {
		final double[][] data = {
				{ 1, 2 },
				{ 3, 4 },
				{ 5, 6 }
		};
		
		final int[] columnControls = { 30, 40 };
		
		new Expectations() {{
			ipf.getData(); result = data;
			ipf.getControls(0); result = null;
			ipf.getControls(1); result = columnControls;
		}};
		
		new TwoWayIteration(ipf);
	}


	@Test(expected = NullPointerException.class) public void testInitializeObjectWithNullColumnControls(@Mocked final TwoWayIPF ipf) throws GenstarException {
		final double[][] data = {
				{ 1, 2 },
				{ 3, 4 },
				{ 5, 6 }
		};
		
		final int[] rowControls = { 10, 20, 30 };
		
		new Expectations() {{
			ipf.getData(); result = data;
			ipf.getControls(0); result = rowControls;
			ipf.getControls(1); result = null;
		}};
		
		new TwoWayIteration(ipf);
	}
	

	@Test public void testNextIteration(@Mocked final TwoWayIPF ipf) throws GenstarException {
		final double[][] data = {
				{ 1, 2 },
				{ 3, 4 },
				{ 5, 6 }
		};
		
		final int[] rowControls = { 10, 20, 30 };
		
		final int[] columnControls = { 30, 40 };
		
		new Expectations() {{
			ipf.getData(); result = data;
			ipf.getControls(0); result = rowControls;
			ipf.getControls(1); result = columnControls;
		}};
		
		TwoWayIteration iteration0 = new TwoWayIteration(ipf);
		TwoWayIteration iteration1 = iteration0.nextIteration();
		
		double[][] data0 = iteration0.getCopyData();
		double[][] data1 = iteration1.getCopyData();
		
		// 1. compute row adjustments on data0 then use row adjustments to adjust data0
		for (int row=0; row<data.length; row++) {
			double rowMarginal = 0;
			for (int column=0; column<data[0].length; column++) { rowMarginal += data0[row][column]; }			
			double rowAdjustment = ((double)rowControls[row]) / rowMarginal;
			
			for (int column=0; column<data[0].length; column++) { data0[row][column] = data0[row][column] * rowAdjustment; }
		}
		
		
		// 2. compute column adjustments on data0 then use column adjustments to adjust data0
		for (int column=0; column<data[0].length; column++) {
			double columnMarginal = 0;
			for (int row=0; row<data.length; row++) { columnMarginal += data0[row][column]; }			
			double columnAdjustment = ((double)columnControls[column]) / columnMarginal;
			
			for (int row=0; row<data.length; row++) { data0[row][column] = data0[row][column] * columnAdjustment; }
		}
		
		
		// verify that data1 is correctly computed from data0
		for (int row=0; row<data0.length; row++) {
			for (int column=0; column<data0[0].length; column++) {
				assertTrue(data0[row][column] == data1[row][column]);
			}
		}
	}

	@Test public void testGetNbOfEntitiesToGenerate(@Mocked final TwoWayIPF ipf) throws GenstarException {
		final double[][] data = {
				{ 1, 2 },
				{ 3, 4 },
				{ 5, 6 }
		};
		
		final int[] rowControls = { 10, 20, 30 };
		
		final int[] columnControls = { 30, 40 };
		
		new Expectations() {{
			ipf.getData(); result = data;
			ipf.getControls(0); result = rowControls;
			ipf.getControls(1); result = columnControls;
		}};
		
		TwoWayIteration iteration0 = new TwoWayIteration(ipf);
		TwoWayIteration iteration1 = iteration0.nextIteration();

		
		// data0
		double[][] data0 = iteration0.getCopyData();
		int sumData0 = 0;
		for (int row=0; row<data0.length; row++) {
			for (int column=0; column<data0[0].length; column++) {
				sumData0 += Math.round(data0[row][column]);
			}
		}
		assertTrue(sumData0 == iteration0.getNbOfEntitiesToGenerate());
		
		
		// data1
		double[][] data1 = iteration1.getCopyData();
		int sumData1 = 0;
		for (int row=0; row<data1.length; row++) {
			for (int column=0; column<data1[0].length; column++) {
				sumData1 += Math.round(data1[row][column]);
			}
		}
		assertTrue(sumData1 == iteration1.getNbOfEntitiesToGenerate());
	}
}
