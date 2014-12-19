package ummisco.genstar.data;

import java.util.SortedMap;
import java.util.TreeMap;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.AttributeValue;
import ummisco.genstar.metamodel.RangeValue;
import ummisco.genstar.metamodel.UniqueValue;
import ummisco.genstar.metamodel.DataType;

public class NationalLevelDistribution {

	public static final double[][] coupleAgeDifferences = {
		{ -16,  0.17 },
		{ -15,  0.06 },
		{ -14,  0.11 },
		{ -13,  0.1  },
		{ -12,  0.13 },
		{ -11,  0.15 },
		{ -10,  0.24 },
		{  -9,  0.29 },
		{  -8,  0.45 },
		{  -7,  0.58 },
		{  -6,  0.83 },
		{  -5,  1.17 },
		{  -4,  1.76 },
		{  -3,  2.68 },
		{  -2,  4.18 },
		{  -1,  6.65 },
		{   0, 10.47 },
		{   1, 11.87 },
		{   2, 11.91 },
		{   3, 10.59 },
		{   4,  8.75 },
		{   5,  6.8  },
		{   6,  5.08 },
		{   7,  3.76 },
		{   8,  2.75 },
		{   9,  2.04 },
		{  10,  1.58 },
		{  11,  1.03 },
		{  12,  0.87 },
		{  13,  0.64 },
		{  14,  0.71 },
		{  15,  0.35 },
		{  16,  0.29 },
		{  17,  0.25 },
		{  18,  0.21 },
		{  19,  0.14 },
		{  20,  0.12 },
		{  21,  0.35 },
	};
	public static SortedMap<AttributeValue, UniqueValue> coupleAgeDifferencesAttributeValuesMap = new TreeMap<AttributeValue, UniqueValue>();
	static {
		for (double[] entry : coupleAgeDifferences) {
			try {
				coupleAgeDifferencesAttributeValuesMap.put(new UniqueValue(DataType.INTEGER, Integer.toString((int) entry[0])), 
						new UniqueValue(DataType.DOUBLE, Double.toString(entry[1])) );
			} catch (GenstarException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public static final int[][] liveBirthOrder = {
		{ 10, 14,    210,     2,     1,     1 },
		{ 15, 19,  21478,  1834,   167,    15 },
		{ 20, 24,  98480, 24509,  5026,   996 },
		{ 25, 29, 171054, 72830, 19943,  5797 },
		{ 30, 34, 121837, 89380, 35133, 12679 },
		{ 35, 39,  53317, 33724, 21936, 13065 },
		{ 40, 44,  12545,  5214,  3533,  4329 },
		{ 45, 49,    593,   150,    97,   298 },
		{ 50, 70,     31,     5,     4,    13 },
	};
	public static SortedMap<AttributeValue, UniqueValue> firstBirthOrder, secondBirthOrder, thirdBirthOrder, fourthBirthOrder;
	static {
		firstBirthOrder = new TreeMap<AttributeValue, UniqueValue>();
		secondBirthOrder = new TreeMap<AttributeValue, UniqueValue>();
		thirdBirthOrder = new TreeMap<AttributeValue, UniqueValue>();
		fourthBirthOrder = new TreeMap<AttributeValue, UniqueValue>();
		
		for (int[] order : liveBirthOrder) {
			try {
				firstBirthOrder.put(new RangeValue(DataType.INTEGER, Integer.toString(order[0]), Integer.toString(order[1])), new UniqueValue(DataType.INTEGER, Integer.toString(order[2])));
				secondBirthOrder.put(new RangeValue(DataType.INTEGER, Integer.toString(order[0]), Integer.toString(order[1])), new UniqueValue(DataType.INTEGER, Integer.toString(order[3])));
				thirdBirthOrder.put(new RangeValue(DataType.INTEGER, Integer.toString(order[0]), Integer.toString(order[1])), new UniqueValue(DataType.INTEGER, Integer.toString(order[4])));
				fourthBirthOrder.put(new RangeValue(DataType.INTEGER, Integer.toString(order[0]), Integer.toString(order[1])), new UniqueValue(DataType.INTEGER, Integer.toString(order[5])));
			} catch (GenstarException e) {
				e.printStackTrace();
			}
		}
	}

}
