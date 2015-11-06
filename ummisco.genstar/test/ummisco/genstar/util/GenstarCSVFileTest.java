package ummisco.genstar.util;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import ummisco.genstar.exception.GenstarException;

public class GenstarCSVFileTest {

	@Test(expected=IllegalArgumentException.class)
	public void testConstructorWithIllegalParameters() throws GenstarException {
		// final String csvFilePath, final String separator, final boolean withHeader
		new GenstarCSVFile(null, true);
		
		new GenstarCSVFile(null,",", true);
		new GenstarCSVFile("",null, true);
	}
	
	@Test(expected = GenstarException.class)
	public void testConstructorWithInvalidCSVFilePath() throws GenstarException {
		new GenstarCSVFile("invalid_file.csv", true);
	}
	
	@Test
	public void testValidCSVFilePath() throws GenstarException {
		GenstarCSVFile csvFile = new GenstarCSVFile("test_data/ummisco/genstar/util/people_sample.csv", true);
		
		assertTrue(csvFile.getColumns() == 4);
		assertTrue(csvFile.getRows() == 5);
		
		// Headers: Household Size,Household Income,Household Type,Number of Cars
		List<String> headers = csvFile.getHeaders();
		assertTrue(headers.size() == 4);
		assertTrue(headers.get(0).equals("Household Size"));
		assertTrue(headers.get(1).equals("Household Income"));
		assertTrue(headers.get(2).equals("Household Type"));
		assertTrue(headers.get(3).equals("Number of Cars"));
	}
	
	@Test
	public void testGetRowWithHeaders() throws GenstarException {
		GenstarCSVFile csvFile = new GenstarCSVFile("test_data/ummisco/genstar/util/people_sample.csv", true);
		
		// Row0: 1,high,type1,1
		List<String> row0 = csvFile.getRow(0);
		assertTrue(row0.get(0).equals("1"));
		assertTrue(row0.get(1).equals("high"));
		assertTrue(row0.get(2).equals("type1"));
		assertTrue(row0.get(3).equals("1"));
		
		// Row3: 2,high,type1,3
		List<String> row3 = csvFile.getRow(3);
		assertTrue(row3.get(0).equals("2"));
		assertTrue(row3.get(1).equals("high"));
		assertTrue(row3.get(2).equals("type1"));
		assertTrue(row3.get(3).equals("3"));		
	}
	
	@Test
	public void testGetRowWithoutHeaders() throws GenstarException {
		GenstarCSVFile csvFile = new GenstarCSVFile("test_data/ummisco/genstar/util/people_sample_without_headers.csv", false);
		
		assertTrue(csvFile.getHeaders() == null);
		assertTrue(csvFile.getRows() == 4);
		assertTrue(csvFile.getColumns() == 4);

		// Row0: 1,high,type1,1
		List<String> row0 = csvFile.getRow(0);
		assertTrue(row0.get(0).equals("1"));
		assertTrue(row0.get(1).equals("high"));
		assertTrue(row0.get(2).equals("type1"));
		assertTrue(row0.get(3).equals("1"));
		
		// Row3: 2,high,type1,3
		List<String> row3 = csvFile.getRow(3);
		assertTrue(row3.get(0).equals("2"));
		assertTrue(row3.get(1).equals("high"));
		assertTrue(row3.get(2).equals("type1"));
		assertTrue(row3.get(3).equals("3"));		
	}
	
	@Test public void testGetContent() throws GenstarException {
		GenstarCSVFile csvFile = new GenstarCSVFile("test_data/ummisco/genstar/util/people_sample.csv", true);
		List<List<String>> content1 = csvFile.getContent();
		
		assertTrue(content1.size() == 4);
		// Row0: 1,high,type1,1
		List<String> row0 = content1.get(0);
		assertTrue(row0.get(0).equals("1"));
		assertTrue(row0.get(1).equals("high"));
		assertTrue(row0.get(2).equals("type1"));
		assertTrue(row0.get(3).equals("1"));
		
		// Row3: 2,high,type1,3
		List<String> row3 = content1.get(3);
		assertTrue(row3.get(0).equals("2"));
		assertTrue(row3.get(1).equals("high"));
		assertTrue(row3.get(2).equals("type1"));
		assertTrue(row3.get(3).equals("3"));		
		
		
		csvFile = new GenstarCSVFile("test_data/ummisco/genstar/util/people_sample_without_headers.csv", false);
		content1 = csvFile.getContent();

		assertTrue(content1.size() == 4);
		// Row0: 1,high,type1,1
		row0 = content1.get(0);
		assertTrue(row0.get(0).equals("1"));
		assertTrue(row0.get(1).equals("high"));
		assertTrue(row0.get(2).equals("type1"));
		assertTrue(row0.get(3).equals("1"));
		
		// Row3: 2,high,type1,3
		row3 = content1.get(3);
		assertTrue(row3.get(0).equals("2"));
		assertTrue(row3.get(1).equals("high"));
		assertTrue(row3.get(2).equals("type1"));
		assertTrue(row3.get(3).equals("3"));		
		
	}
}
