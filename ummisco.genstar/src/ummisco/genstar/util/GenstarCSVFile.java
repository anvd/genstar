package ummisco.genstar.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ummisco.genstar.exception.GenstarException;


public class GenstarCSVFile {

	private String csvFilePath;
	
	private String separator = ",";
	
	private boolean hasHeader = false;
	
	private List<String> headers;
	
	private List<List<String>> fileContent;
	
	private int rows = 0, columns = 0;
	
	
	public GenstarCSVFile(final String csvFilePath, final boolean withHeader) throws GenstarException {
		this(csvFilePath, ",", withHeader);
	}
	
	public GenstarCSVFile(final String csvFilePath, final String separator, final boolean withHeader) throws GenstarException {
		if (csvFilePath == null || separator == null) { throw new IllegalArgumentException("csvFilePath, separator can not be null"); }
		
		this.csvFilePath = csvFilePath;
		this.separator = separator;
		hasHeader = withHeader;
		
		readContent();
	}
	
	private void readContent() throws GenstarException {
		if (fileContent == null) {
			
			CsvReader reader = null;
			try {
				reader = new CsvReader(csvFilePath, separator.charAt(0));
				if ( hasHeader ) {
					reader.readHeaders();
					headers = new ArrayList<String>();
					headers.addAll(Arrays.asList(reader.getHeaders()));
					
					columns = headers.size();
					rows = 1;
				}
				
				fileContent = new ArrayList<List<String>>();
				while (reader.readRecord()) {
					if (columns == 0) {
						columns = reader.getColumnCount();
						if (columns == 0) { throw new GenstarException("CSV file contains empty row."); }
					} else if (reader.getColumnCount() != columns) {  throw new GenstarException("CSV file contains rows with different numbers of columns (row: " + rows + ")."); }
					rows++;
					
					fileContent.add(Arrays.asList(reader.getValues()));
				}
			} catch (FileNotFoundException e) {
				throw new GenstarException(e);
			} catch (IOException e) {
				throw new GenstarException(e);
			} catch (Exception e) {
				throw new GenstarException(e);
			} finally {
				if ( reader != null ) {
					reader.close();
				}
			}
		}
	}
	
	public int getRows() {
		return rows;
	}
	
	public int getColumns() {
		return columns;
	}
	
	public List<String> getHeaders() {
		return headers;
	}
	
	public List<String> getRow(final int row) throws GenstarException {
		if (row < 0 || row > (fileContent.size() - 1)) { throw new GenstarException("'row' must be in range [0, " + (fileContent.size() - 1) + "]"); }
		return fileContent.get(row);
	}
	
	public List<List<String>> getContent() {
		return fileContent;
	}
	
	public String getPath() {
		return csvFilePath;
	}
}
