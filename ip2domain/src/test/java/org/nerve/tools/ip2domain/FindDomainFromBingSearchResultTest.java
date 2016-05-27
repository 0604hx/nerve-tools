package org.nerve.tools.ip2domain;

import org.jsoup.Jsoup;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 从Bing search api 中找出域名
 *
 * 测试的数据来自 data/xxxx.txt
 *
 * 逐行读取数据，然后进行正则的匹对
 *
 * org.nerve.tools.ip2domain
 * Created by zengxm on 2016/5/25.
 */
public class FindDomainFromBingSearchResultTest{
	protected List<String> regs;

	protected static String PATH;

	private Path getPath(String location){
		if(PATH==null){
			try{
				PATH=Paths.get(this.getClass().getClassLoader().getResource("").toURI()).toString();
			}catch (Exception e){}
		}

		return Paths.get(PATH, location);
	}

	@Before
	public void loadRegs() throws IOException, URISyntaxException {
		regs=Files.readAllLines(getPath("regs.txt"));
		System.out.println(regs.size()+" regular loaded!");
	}

	@Test
	public void findDomainFromTxt() throws IOException{
		Set<String> domains=new HashSet<>();
		List<String> txtLines=Files.readAllLines(getPath("data/125.39.240.113.txt"));
		txtLines.forEach(l-> regs.forEach(r->{
			Pattern p=Pattern.compile(r);
			Matcher m=p.matcher(l);
			while(m.find()){
				if(m.groupCount()>0)
					domains.add(m.group(1));
			}
		}));

		Pattern p=Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
		//去除IP地址
		domains.stream().filter(d-> !p.matcher(d).find()).forEach(d->System.out.println(d));
	}

	@Test
	public void findDomainFromText(){
		String text="http://www.114best.com/ip/114.aspx?w=www.baidu.com";
		regs.forEach(r->{
			Pattern p=Pattern.compile(r);
			Matcher m=p.matcher(text);
			System.out.println(m.find());
			while(m.find()){
				if(m.groupCount()>0)
					System.out.println(m.group(1));
				else
					System.err.println(r+" not found");
			}
		});
	}

	@Test
	public void jsoup(){
		String html="在<a href='sasasasasasasasasa'>xxxx医院</a>工作";
		System.out.println(Jsoup.parse(html).text());
	}
}
