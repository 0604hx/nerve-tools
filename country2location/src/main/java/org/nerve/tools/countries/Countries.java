package org.nerve.tools.countries;

import com.alibaba.fastjson.JSON;
import org.nerve.tools.countries.bean.CountryBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * org.nerve.tools.countries
 * Created by zengxm on 2016/6/15.
 */
public class Countries {

	private static final String JSON_LOCATION="/countries_latitude_longitude.json";
	private static Map<String,CountryBean> countryMap;

	/**
	 * @param nameOrCode    country name(e.g:Hong Kong) or code(e.g:HK)
	 * @return              CountryBean
	 */
	public final static CountryBean get(String nameOrCode){
		if(nameOrCode==null||nameOrCode.trim().length()==0)
			throw new IllegalArgumentException("illegal country name or code!");
		if(countryMap==null){
			buildMap();
		}
		return countryMap.get(nameOrCode.toLowerCase());
	}

	private static void buildMap(){
		countryMap=new ConcurrentHashMap<>();
		//加载json
		try(InputStream is=Countries.class.getResourceAsStream(JSON_LOCATION)) {
			BufferedReader isr=new BufferedReader(new InputStreamReader(is));
			StringBuilder sb=new StringBuilder();
			isr.lines().forEach(sb::append);

			List<CountryBean> beanList=JSON.parseArray(sb.toString(), CountryBean.class);
			System.out.println("load "+beanList.size()+" countries!");
			beanList.forEach(b->{
				countryMap.put(b.getCode().toLowerCase(), b);
				countryMap.put(b.getName().toLowerCase(), b);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
