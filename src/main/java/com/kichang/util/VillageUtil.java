package com.kichang.util;

import java.util.HashMap;
import java.util.Map;

public class VillageUtil {
	private Map map = new HashMap();
	public VillageUtil() {
		super();
    	map.put("2002:cbfb:37c:1:fdff:ff:fe00:2","탄천면 상각1구");
    	map.put("2002:cbfb:37c:1:fdff:ff:fe00:3","탄천면 상각1구");
    	map.put("2407:c000:9024:6:fdff:ff:fe00:2","사곡면 부곡리");
    	map.put("2407:c000:9024:6:fdff:ff:fe00:3","사곡면 부곡리");
    	map.put("2002:cbfb:3a0:1:fdff:ff:fe00:2","정안면 광정1구");
    	map.put("2002:cbfb:3a0:1:fdff:ff:fe00:3","정안면 광정1구");
    	map.put("2002:cbfb:3a0:1:fdff:ff:fe00:4","정안면 광정2구");
    	map.put("2002:cbfb:3a0:1:fdff:ff:fe00:5","정안면 광정2구");
    	map.put("2002:cbfb:3a0:1:fdff:ff:fe00:6","정안면 광정3구");
    	map.put("2002:cbfb:3a0:1:fdff:ff:fe00:7","정안면 광정3구");
    	map.put("2407:c000:9024:7:fdff:ff:fe00:2","정안면 고성리");
    	map.put("2407:c000:9024:7:fdff:ff:fe00:3","정안면 고성리");
    	map.put("2407:c000:9024:8:fdff:ff:fe00:2","의당면 두만리");
    	map.put("2407:c000:9024:8:fdff:ff:fe00:3","의당면 두만리");
    	map.put("2407:c000:9024:b::1","한태수氏(산학리)");
    	map.put("2407:c000:9024:a::2","김인배氏(산학리)");
    	
	}

	public String getVillage(final String ip) {
		return (String)map.get(ip);
	}
	
}
