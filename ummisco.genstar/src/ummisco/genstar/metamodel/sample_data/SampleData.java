package ummisco.genstar.metamodel.sample_data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.population.Entity;
import ummisco.genstar.metamodel.population.PopulationType;
import ummisco.genstar.util.GenstarCsvFile;
import ummisco.genstar.util.GenstarUtils;

public class SampleData extends AbstractSampleData implements ISampleData { // TODO change to CSVSampleData
	
	private List<AbstractAttribute> attributes;
	
	private GenstarCsvFile data = null;
	
	
	public SampleData(final String populationName, final List<AbstractAttribute> attributes, GenstarCsvFile data) throws GenstarException {
		if (populationName == null || populationName.isEmpty()) { throw new GenstarException("Parameter populationName can neither be null nor empty"); }
		if (attributes == null) { throw new GenstarException("Parameter attributes can not be null"); }
		if (data == null) { throw new GenstarException("Parameter data can not be null"); }
		
		Set<AbstractAttribute> attributesSet = new HashSet<AbstractAttribute>(attributes);
		if (attributesSet.size() < attributes.size()) { throw new GenstarException("Some attributes are duplicated"); }

		this.populationName = populationName;
		this.attributes = new ArrayList<AbstractAttribute>();
		this.attributes.addAll(attributes);
		this.data = data;
		
		this.sampleEntityPopulation = GenstarUtils.loadSinglePopulation(PopulationType.SAMPLE_DATA_POPULATION, populationName, attributes, data);
	}
	
	@Override
	public void recodeIdAttributes(final Entity targetEntity) throws GenstarException {
		// TODO find the (only) ID attribute then recode it
	}
}
