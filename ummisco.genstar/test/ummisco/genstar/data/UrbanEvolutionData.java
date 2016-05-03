package ummisco.genstar.data;

import java.util.HashMap;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.AttributeInferenceGenerationRule;
import ummisco.genstar.metamodel.FrequencyDistributionGenerationRule;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.SampleFreeGenerator;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.metamodel.attributes.UniqueValuesAttribute;

public class UrbanEvolutionData {

	/*
	string wc_bd <- "Working class House";
	string uc_bd <- "Upper class House";
	string app_bd <- "Appartment building";
	string indu_bd <- "Industrial zone";
	 */
	// INFO : 120 buildings in shapefile
	
	private String[] build_type_values = {
		"Working class House",
		"Upper class House",
		"Appartment building",
		"Industrial zone"
	};
	private int[] build_type_nb = { 50, 50, 50, 50 };
	
	
	
	// building_color
	// building_type -> building_color
	// map<string, rgb> building_color <- map([wc_bd ::rgb(255,220,220),uc_bd ::rgb(255,0,0),app_bd ::rgb("blue"),indu_bd ::rgb("green")]);
	private String[] building_color_values = {
//		"[255,220,220]", FIXME add DataType.COLOR
		"red",
//		"[255,0,0]", FIXME add DataType.COLOR
		"black",
		"blue",
		"green"
	};
	
	
	// building_size
	// building_type -> building_size
	// map<string, float> building_size <-map([wc_bd ::20.0,uc_bd ::30.0,app_bd ::50.0,indu_bd::100.0]);
	private int[] building_size_values = { 2, 3, 5, 10 };
	
	
	// building_height
	// building_type -> building_height
	// map<string, float> building_height <-map([wc_bd ::5.0,uc_bd ::8.0,app_bd ::20.0,indu_bd::7.0]);
	private float[] building_height_values = { 5.0f, 8.0f, 20.0f, 7.0f };
	
	// create rules
	private SampleFreeGenerator buildingPopulationGenerator;
	private FrequencyDistributionGenerationRule generationRule1;
	private AttributeInferenceGenerationRule generationRule2, generationRule3, generationRule4;
	
	public UrbanEvolutionData() throws GenstarException {
		buildingPopulationGenerator = new SampleFreeGenerator("Building population", 120);
		
		// create attributes +
		UniqueValuesAttribute buildingTypeAttr = new UniqueValuesAttribute(buildingPopulationGenerator, "type", DataType.STRING);
		for (String type : build_type_values) { buildingTypeAttr.add(new UniqueValue(DataType.STRING, type)); }
		buildingPopulationGenerator.addAttribute(buildingTypeAttr);
		
		UniqueValuesAttribute buildingColorAttr = new UniqueValuesAttribute(buildingPopulationGenerator, "color", DataType.STRING);
		for (String color : building_color_values) { buildingColorAttr.add(new UniqueValue(DataType.STRING, color)); }
		buildingPopulationGenerator.addAttribute(buildingColorAttr);
		
		UniqueValuesAttribute buildingSizeAttr = new UniqueValuesAttribute(buildingPopulationGenerator, "size", DataType.INTEGER);
		for (int size : building_size_values) { buildingSizeAttr.add(new UniqueValue(DataType.INTEGER, Integer.toString(size))); }
		buildingPopulationGenerator.addAttribute(buildingSizeAttr);
		
		UniqueValuesAttribute buildingHeightAttr = new UniqueValuesAttribute(buildingPopulationGenerator, "height", DataType.FLOAT);
		for (float height : building_height_values) { buildingHeightAttr.add(new UniqueValue(DataType.FLOAT, Float.toString(height))); }
		buildingPopulationGenerator.addAttribute(buildingHeightAttr);
		// create attributes -
		
		
		// create generation rules +
		
		// rule1 :
		generationRule1 = new FrequencyDistributionGenerationRule(buildingPopulationGenerator, "Building type generation rule");
		generationRule1.appendOutputAttribute(buildingTypeAttr);
		
		generationRule1.generateAttributeValuesFrequencies();
		
		buildingPopulationGenerator.appendGenerationRule(generationRule1);
		
		// set frequency
		Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		for (int i=0; i < build_type_nb.length; i++) {
			attributeValues.put(buildingTypeAttr, new UniqueValue(DataType.STRING, build_type_values[i]));
			generationRule1.setFrequency(attributeValues, build_type_nb[i]);
		}
		
		// rule2 : building_type -> building_color
		generationRule2 = new AttributeInferenceGenerationRule(buildingPopulationGenerator, "building_type -> building_color generation rule", buildingTypeAttr, buildingColorAttr);
		buildingPopulationGenerator.appendGenerationRule(generationRule2);
		
		// set inference data
		Map<AttributeValue, AttributeValue> rule2InferenceData = new HashMap<AttributeValue, AttributeValue>();
		AttributeValue inferringValue, inferredValue;
		for (int i=0; i<building_color_values.length; i++) {
			inferringValue = buildingTypeAttr.getInstanceOfAttributeValue(new UniqueValue(DataType.STRING, build_type_values[i]));
			inferredValue = buildingTypeAttr.getInstanceOfAttributeValue(new UniqueValue(DataType.STRING, building_color_values[i]));
			
			if (inferringValue == null || inferredValue == null) {
				throw new GenstarException("Some attribute values are not contained in the inferring attribute or inferred attribute");
			}
			
			rule2InferenceData.put(inferringValue, inferredValue);
		}
		generationRule2.setInferenceData(rule2InferenceData);
		
		// rule3 : building_type -> building_size
		generationRule3 = new AttributeInferenceGenerationRule(buildingPopulationGenerator, "build_type -> building_size generation rule", buildingTypeAttr, buildingSizeAttr);
		buildingPopulationGenerator.appendGenerationRule(generationRule3);
		
		// set inference data
		Map<AttributeValue, AttributeValue> rule3InferenceData = new HashMap<AttributeValue, AttributeValue>();
		for (int i=0; i<building_size_values.length; i++) {
			
			inferringValue = buildingTypeAttr.getInstanceOfAttributeValue(new UniqueValue(DataType.STRING, build_type_values[i]));
			inferredValue = buildingTypeAttr.getInstanceOfAttributeValue(new UniqueValue(DataType.INTEGER, Integer.toString(building_size_values[i])));
			
			if (inferringValue == null || inferredValue == null) {
				throw new GenstarException("Some attribute values are not contained in the inferring attribute or inferred attribute");
			}
			
			rule3InferenceData.put(inferringValue, inferredValue);
		}
		generationRule3.setInferenceData(rule3InferenceData);
		
		// rule4 : build_type -> building_height
		generationRule4 = new AttributeInferenceGenerationRule(buildingPopulationGenerator, "build_type -> building_height generation rule", buildingTypeAttr, buildingHeightAttr);
		buildingPopulationGenerator.appendGenerationRule(generationRule4);
		
		// set inference data
		Map<AttributeValue, AttributeValue> rule4InferenceData = new HashMap<AttributeValue, AttributeValue>();
		for (int i=0; i<building_height_values.length; i++) {
			inferringValue = buildingTypeAttr.getInstanceOfAttributeValue(new UniqueValue(DataType.STRING, build_type_values[i]));
			inferredValue = buildingTypeAttr.getInstanceOfAttributeValue(new UniqueValue(DataType.FLOAT, Float.toString(building_height_values[i])));

			if (inferringValue == null || inferredValue == null) {
				throw new GenstarException("Some attribute values are not contained in the inferring attribute or inferred attribute");
			}

			rule4InferenceData.put(inferringValue, inferredValue);
		}
		generationRule4.setInferenceData(rule4InferenceData);
		
		// create generation rules -
		
	}
	
	public ISyntheticPopulationGenerator getPopulationGenerator() {
		return buildingPopulationGenerator;
	}
}
