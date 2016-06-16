package org.nerve.tools.countries;

import org.junit.Assert;
import org.junit.Test;

/**
 * org.nerve.tools.countries
 * Created by zengxm on 2016/6/15.
 */
public class CountriesTest {

	@Test
	public void toLatLong(){
		Assert.assertEquals("HK", Countries.get("HK").getCode());
		Assert.assertEquals("Taiwan", Countries.get("taiwan").getName());
		Assert.assertEquals("US", Countries.get("United States").getCode());
	}
}
