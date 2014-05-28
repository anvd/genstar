package ummisco.genstar.metamodel;

import java.lang.reflect.Constructor;

public enum ValueType {

	INTEGER("integer", java.lang.Integer.class, "0"),
	FLOAT("float", java.lang.Float.class, "0"),
	DOUBLE("double", java.lang.Double.class, "0"),
	STRING("string", java.lang.String.class, ""),
	BOOL("bool", java.lang.Boolean.class, Boolean.TRUE.toString());
	
	
	private String name;
	
	private Class wrapperClass;
	
	private Constructor wrapperConstructor;
	
	private String defaultStringValue;

	private ValueType(final String name, final Class wrapperClass, final String defaultStringValue) {
		this.name = name;
		this.wrapperClass = wrapperClass;
		this.defaultStringValue = defaultStringValue;

		try {
			wrapperConstructor = wrapperClass.getConstructor(String.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(wrapperClass.getName() + " doesn't have a constructor accepting a String argument.");
		}
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Class getWrapperClass() {
		return wrapperClass;
	}

	public boolean isValueValid(final String value) {
		try {
			wrapperConstructor.newInstance(value);
		} catch (final Exception ex) {
			return false;
		}
		
		return true;
	}

	public Comparable getComparableValue(final String value) {
		try {
			return (Comparable) wrapperConstructor.newInstance(value);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String getDefaultStringValue() {
		return defaultStringValue;
	}
	
	@Override
	public String toString() {
		return name + " ValueType";
	}
	
	public boolean isNumericValue() {
		return wrapperClass.getSuperclass().equals(Number.class);
	}
}
