package idees.genstar.control;

public class ControlFrequency extends AControl<Double> {

	public ControlFrequency(Double control) {
		super(control);
	}

	@Override
	public AControl<Double> add(AControl<? extends Number> controlCombiner) {
		return this.add(controlCombiner.getValue().doubleValue());
	}
	
	@Override
	public AControl<Double> add(Double controlCombiner) {
		this.setValue(this.getValue() + controlCombiner);
		return this;
	}

	@Override
	public AControl<Double> multiply(AControl<? extends Number> controlMultiplier) {
		return this.multiply(controlMultiplier.getValue().doubleValue());
	}

	@Override
	public AControl<Double> multiply(Double controlMultiplier) {
		this.setValue(this.getValue() * controlMultiplier);
		return this;
	}
	
	@Override
	public Double getSum(AControl<? extends Number> controlSum) {
		return this.getValue().doubleValue() + controlSum.getValue().doubleValue();
	}
	
	@Override
	public Double getRowProduct(AControl<? extends Number> controlProd) {
		return this.getValue().doubleValue() * controlProd.getValue().doubleValue();
	}

	@Override
	public Double getRoundedProduct(AControl<? extends Number> controlProd) {
		return getRowProduct(controlProd);
	}

	@Override
	public boolean equalsVal(AControl<Double> val, double epsilon) {
		return Math.abs(this.getValue() - val.getValue()) / this.getValue() < epsilon;
	}

}
