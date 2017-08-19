package com.kichang.util;

import java.util.HashMap;
import java.util.Map;

public class VillageUtil {
	private Map map = new HashMap();
	public VillageUtil() {
		super();
    	map.put("2002:cbfb:37c:1:fdff:ff:fe00:2","źõ�� ��1��");
    	map.put("2002:cbfb:37c:1:fdff:ff:fe00:3","źõ�� ��1��");
    	map.put("2407:c000:9024:6:fdff:ff:fe00:2","���� �ΰ");
    	map.put("2407:c000:9024:6:fdff:ff:fe00:3","���� �ΰ");
    	map.put("2002:cbfb:3a0:1:fdff:ff:fe00:2","���ȸ� ����1��");
    	map.put("2002:cbfb:3a0:1:fdff:ff:fe00:3","���ȸ� ����1��");
    	map.put("2002:cbfb:3a0:1:fdff:ff:fe00:4","���ȸ� ����2��");
    	map.put("2002:cbfb:3a0:1:fdff:ff:fe00:5","���ȸ� ����2��");
    	map.put("2002:cbfb:3a0:1:fdff:ff:fe00:6","���ȸ� ����3��");
    	map.put("2002:cbfb:3a0:1:fdff:ff:fe00:7","���ȸ� ����3��");
    	map.put("2407:c000:9024:7:fdff:ff:fe00:2","���ȸ� ����");
    	map.put("2407:c000:9024:7:fdff:ff:fe00:3","���ȸ� ����");
    	map.put("2407:c000:9024:8:fdff:ff:fe00:2","�Ǵ�� �θ���");
    	map.put("2407:c000:9024:8:fdff:ff:fe00:3","�Ǵ�� �θ���");
    	map.put("2407:c000:9024:b::1","���¼��(���и�)");
    	map.put("2407:c000:9024:a::2","���ι��(���и�)");
    	
	}

	public String getVillage(final String ip) {
		return (String)map.get(ip);
	}
	
}
