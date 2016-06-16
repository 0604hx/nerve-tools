package org.nerve.tools.countries.bean;

import java.io.Serializable;

/**
 * org.nerve.tools.countries.bean
 * Created by zengxm on 2016/6/15.
 */
public class CountryBean implements Serializable{
	private String code;
	private String name;
	private double latitude;
	private double longitude;

	public String getCode() {
		return code;
	}

	public CountryBean setCode(String code) {
		this.code = code;
		return this;
	}

	public String getName() {
		return name;
	}

	public CountryBean setName(String name) {
		this.name = name;
		return this;
	}

	public double getLatitude() {
		return latitude;
	}

	public CountryBean setLatitude(double latitude) {
		this.latitude = latitude;
		return this;
	}

	public double getLongitude() {
		return longitude;
	}

	public CountryBean setLongitude(double longitude) {
		this.longitude = longitude;
		return this;
	}
}
