package ummisco.genstar.metamodel;

import ummisco.genstar.exception.AttributeException;
import ummisco.genstar.exception.GenerationException;
import ummisco.genstar.exception.GenstarException;

public class AttributeInferenceGenerationRule extends GenerationRule implements AttributeChangedListener {

	private InferredAttribute inferredAttribute;
	

	public AttributeInferenceGenerationRule(final ISyntheticPopulationGenerator population, final String name, final InferredAttribute inferredAttribute) throws GenstarException {
		super(population, name);
		
		if (inferredAttribute == null) { throw new GenstarException("'inferredAttribute' parameter can not be null"); }
		if (!population.equals(inferredAttribute.populationGenerator)) {
			throw new GenstarException("Incoherence of populations between AttributeInferenceGenerationRule's population ("
					+ population.getName() + ") and inferredAttribute's population (" + inferredAttribute.populationGenerator.getName() + ")");
		}
		if (!population.containAttribute(inferredAttribute)) { throw new GenstarException("inferredAttribute has not been added to the population yet!"); }
		
		this.inferredAttribute = inferredAttribute;
		this.inferredAttribute.addAttributeChangedListener(this);
	}
	
	public InferredAttribute getInferredAttribute() {
		return inferredAttribute;
	}

	@Override
	public void generate(final Entity entity) throws GenerationException {
		if (entity == null) { throw new GenerationException("'entity' parameter can not be null"); }
		
		AbstractAttribute inferringAttribute = inferredAttribute.getInferringAttribute();
		String inferringAttributeName = inferringAttribute.getNameOnEntity();
		EntityAttributeValue inferringAttrValueOnEntity = entity.getEntityAttributeValue(inferringAttributeName);
		
		if (inferringAttrValueOnEntity != null) {
			for (AttributeValue inferringAttrValueOnData : inferredAttribute.getInferenceData().keySet()) {
				if (inferringAttrValueOnEntity.isValueMatch(inferringAttrValueOnData)) {
					try {
						entity.putAttributeValue(new EntityAttributeValue(inferredAttribute, inferredAttribute.getInferredAttributeValue(inferringAttrValueOnData)));
					} catch (AttributeException e) {
						throw new GenerationException(e);
					}
					
					break;
				}
				// TODO use default value if nothing matches?
			}
		} else {
			// ? default value!!
		}
	}

	@Override
	public void attributeChanged(final AttributeChangedEvent event) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
}
