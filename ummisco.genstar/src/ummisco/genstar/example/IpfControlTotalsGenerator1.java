package ummisco.genstar.example;

import java.util.Properties;

import ummisco.genstar.GenstarService;

public class IpfControlTotalsGenerator1 {

	public IpfControlTotalsGenerator1() {}
	
	public static void main(String[] args) throws Exception {
		String propertiesFilePath = "example_data/IpfControlTotalsGenerator1/household_1_control_totals.properties";
		
		// 0. Load the property file
		Properties ipfControlTotalsProperties = GenstarService.loadPropertyFile(propertiesFilePath);
		
		
		// 1. Generate the Ipf control totals
		String ipfControlTotalsFilePath = GenstarService.generateIpfControlTotalsFromPopulationData(ipfControlTotalsProperties);
		System.out.println("Ipf control totals are generated and saved to " + ipfControlTotalsFilePath + ". Please open the file to observe the control totals.");
		
	}
}
