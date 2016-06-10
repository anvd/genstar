package idees.genstar.configuration;

import java.io.File;
import java.io.IOException;
import java.io.ObjectStreamException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import idees.genstar.datareader.ISurvey;
import idees.genstar.datareader.SurveyStaticFactory;
import idees.genstar.datareader.exception.InputFileNotSupportedException;

/**
 * 
 * TODO: complete java doc
 * 
 * WARNING: csv separator char is ',' by default
 * 
 * @author kevinchapuis
 *
 */
public class GSDataFile {

	private final String survey;
	private char csvSeparator = ',';
	
	private final GSMetaDataType dataFileType;
	
	private final int firstRowDataIndex;
	private final int firstColumnDataIndex;
	
	public GSDataFile(String survey, GSMetaDataType dataFileType,  int firstRowDataIndex, int firstColumnDataIndex){
		this.survey = survey;
		this.dataFileType = dataFileType;
		this.firstRowDataIndex = firstRowDataIndex;
		this.firstColumnDataIndex = firstColumnDataIndex;
	}
	
	public GSDataFile(File survey, GSMetaDataType dataFileType, int firstRowDataIndex, int firstColumnDataIndex){
		this(survey.getAbsolutePath(), dataFileType, firstRowDataIndex, firstColumnDataIndex);
	}
	
	public GSDataFile(String survey, GSMetaDataType dataFileType,  int firstRowDataIndex, int firstColumnDataIndex, char csvSeparator){
		this(survey, dataFileType, firstRowDataIndex, firstColumnDataIndex);
		this.csvSeparator = csvSeparator;
	}
	
// --------------------------- ACCESSOR --------------------------- //
	
	public String getSurveyFilePath() {
		return survey;
	}
	
	public GSMetaDataType getDataFileType(){
		return dataFileType;
	}
	
	public int getFirstRowDataIndex(){
		return firstRowDataIndex;
	}
	
	public int getFirstColumnDataIndex(){
		return firstColumnDataIndex;
	}
	
	public char getCsvSeparator(){
		return csvSeparator;
	}
	
	public void setCsvSeparator(char csvSeparator){
		this.csvSeparator = csvSeparator;
	}
	
	/**
	 * Give the survey associated with this file
	 * 
	 * @return {@link ISurvey} - a concrete survey with row and column access methods
	 * @throws InvalidFormatException
	 * @throws IOException
	 * @throws InputFileNotSupportedException
	 */
	public ISurvey getSurvey() throws InvalidFormatException, IOException, InputFileNotSupportedException{
		return SurveyStaticFactory.getSurvey(new File(survey), csvSeparator);
	}
		
	/*
	 * Method that enable a safe serialization / deserialization of this java class <br/>
	 * The serialization process end up in xml file that represents a particular java <br/>
	 * object of this class; and the way back from xml file to java object. 
	 */
	protected Object readResolve() throws ObjectStreamException {
		String survey = getSurveyFilePath();
		GSMetaDataType dataFileType = getDataFileType();
		char csvSeparator = getCsvSeparator();
		return new GSDataFile(survey, dataFileType, getFirstRowDataIndex(), getFirstColumnDataIndex(), csvSeparator);
	}
	
}
