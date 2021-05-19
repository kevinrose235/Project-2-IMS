package com.revature.utils;

import java.util.Map;

public class DistanceMath {
	
	
	private DistanceMath() { /*Empty private constructor*/}
	
	public static double kmBetweenTwoCoords(Map<String, Double> coord1, Map<String, Double> coord2) {
		double lat1 = degreesToRadians(coord1.get("latitude"));
		double long1 = degreesToRadians(coord1.get("longitude"));
		double lat2 = degreesToRadians(coord2.get("latitude"));
		double long2 = degreesToRadians(coord2.get("longitude"));
		
		double latDiff = lat1 - lat2;
		double longDiff = long1 - long2;
		
		// Equation: 
		// d=2*asin(sqrt((sin((lat1-lat2)/2))^2 + cos(lat1)*cos(lat2)*(sin((lon1-lon2)/2))^2))
		double radianDistance = 2 * Math.asin(
					Math.sqrt(Math.pow(Math.sin(latDiff / 2), 2) +
							Math.cos(lat1) * Math.cos(lat2) * 
							Math.pow(Math.sin(longDiff / 2), 2)
							)
				);
		
		return radiansToKM(radianDistance);
	}
	
	public static double degreesToRadians(double degrees) {
		return (Math.PI / 180) * degrees;
	}
	
	public static double radiansToKM(double degrees) {
		return degrees * (180/Math.PI) * 60 * 1.852; //1.852 is the conversion ratio for km
	}
}
