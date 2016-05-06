package ummisco.genstar.metamodel.population;

public enum PopulationType {

	SYNTHETIC_POPULATION("Synthetic population"),
	SAMPLE_DATA_POPULATION("Sample data population");
	
	private String name;

	private PopulationType(final String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
