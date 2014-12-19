package ummisco.genstar.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;
import ummisco.genstar.exception.GenstarException;

public class GenstarFileUtil {
	public static String csvSeparator = ",";

	public static void writeCSVFile(final String fileName, final List<String[]> allLines) throws GenstarException {

		try {
			File csvFile = new File(fileName);
			FileWriter fileWriter = new FileWriter(csvFile);
			
			CSVWriter csvFileWriter = new CSVWriter(fileWriter, csvSeparator.charAt(0), CSVWriter.NO_QUOTE_CHARACTER);
			csvFileWriter.writeAll(allLines);
			csvFileWriter.close();
		} catch (IOException e) {
			throw new GenstarException(e);
		}
	}
	
	public static void main(final String[] args) throws Exception {
		int[][] age_ranges_4 = {
			{  0,  0,  9354,  8790 },
			{  1,  4, 36413, 34162 },
			{  5,  9, 44112, 41547 },
			{ 10, 14, 43203, 40575 },
			{ 15, 17, 31641, 29527 },
			{ 18, 19, 28150, 27708 },
			{ 20, 24, 66280, 63182 },
			{ 25, 29, 61072, 58324 },
			{ 30, 34, 53169, 51271 },
			{ 35, 39, 50447, 48529 },
			{ 40, 44, 44788, 44767 },
			{ 45, 49, 35478, 38882 },
			{ 50, 54, 26664, 33869 },
			{ 55, 59, 19680, 24671 },
			{ 60, 64, 11479, 13264 },
			{ 65, 69,  7585, 11378 },
			{ 70, 74,  6580, 10474 },
			{ 75, 79,  6949,  8791 },
			{ 80, 84,  4305,  5243 },
			{ 85, 99,  2257,  3875 }
		};
		
		List<String[]> lines = new ArrayList<String[]>();
		
		String[] header = new String[3];
		header[0] = "Age range";
		header[1] = "Male";
		header[2] = "Female";
		lines.add(header);
		
		String[] line;
		for (int rowIndex = 0; rowIndex < age_ranges_4.length; rowIndex++) {
			line = new String[3];
			
			line[0] = new String("[" + age_ranges_4[rowIndex][0] + " : " + age_ranges_4[rowIndex][1] + "]");  // age range
			line[1] = new String(Integer.toString(age_ranges_4[rowIndex][2])); // male
			line[2] = new String(Integer.toString(age_ranges_4[rowIndex][3])); // female

			lines.add(line);
		} 
		
		GenstarFileUtil.writeCSVFile("test.csv", lines);
	}
}
