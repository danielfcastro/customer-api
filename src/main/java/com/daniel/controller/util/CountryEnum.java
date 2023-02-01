package com.daniel.controller.util;
/**
 * Country Enumeration relating Country Name and International Phone Code
 * @author dfcastro
 *
 */
public enum CountryEnum {
	
    CAMEROON(237),
    ETHIOPIA(251),
    MOROCCO(212),
    MOZAMBIQUE(258),
    UGANDA(256);
	
	private int countryCode;
    
	CountryEnum(int code) {
        this.countryCode = code;
    }
    
    public int getCountryCode() {
    	return this.countryCode;
    }
}
