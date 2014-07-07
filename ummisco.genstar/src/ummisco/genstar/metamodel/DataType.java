package ummisco.genstar.metamodel;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public enum DataType {

	INTEGER("integer", 1, java.lang.Integer.class, "0"),
	FLOAT("float", 2, java.lang.Float.class, "0"),
	DOUBLE("double", 3, java.lang.Double.class, "0"),
	STRING("string", 4, java.lang.String.class, ""),
	BOOL("bool", 5, java.lang.Boolean.class, Boolean.TRUE.toString());
	
	private static Map<Integer, DataType> dataTypes = new HashMap<Integer, DataType>();
	static {
		dataTypes.put(INTEGER.id, INTEGER);
		dataTypes.put(FLOAT.id, FLOAT);
		dataTypes.put(DOUBLE.id, DOUBLE);
		dataTypes.put(STRING.id, STRING);
		dataTypes.put(BOOL.id, BOOL);
	}
	
	public static DataType getDataTypeByID(final int id) { return dataTypes.get(id); }
	
	
	private String name;
	
	private int id;
	
	private Class wrapperClass;
	
	private Constructor wrapperConstructor;
	
	private String defaultStringValue;

	private DataType(final String name, final int id, final Class wrapperClass, final String defaultStringValue) {
		this.name = name;
		this.id = id;
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
	
	public int getID() {
		return id;
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
		return name + " DataType";
	}
	
	public boolean isNumericValue() {
		return wrapperClass.getSuperclass().equals(Number.class);
	}
}
