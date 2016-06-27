package ummisco.genstar.example;

import java.util.Map;
import java.util.Properties;

import ummisco.genstar.GenstarService;

public class IpuControlTotalsGenerator1 {

	
	public IpuControlTotalsGenerator1() {}
	
	public static void main(String[] args) throws Exception {
		String propertiesFilePath = "example_data/IpuControlTotalsGenerator1/ipu_control_totals_1.properties";
		
		// 0. Load the property file
		Properties ipuControlTotalsProperties = GenstarService.loadPropertyFile(propertiesFilePath);
		
		
		// 1. Generate the Ipu control totals
		Map<String, String> ipuControlTotalsFilePaths = GenstarService.generateIpuControlTotalsFromPopulationData(ipuControlTotalsProperties);
		for (String populationName : ipuControlTotalsFilePaths.keySet()) {
			System.out.println("Control totals of \'" + populationName + "\' population is saved to \'" + ipuControlTotalsFilePaths.get(populationName) + "\'");
		}
		System.out.println("Please open those files to observe the result");
	}
	 
}
