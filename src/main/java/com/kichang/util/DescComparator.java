package com.kichang.util;

import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;

public class DescComparator<T> implements Comparator<T> {
	private String key = "cnt";
	
	public DescComparator(String key) {
		super();
		this.key = key;
	}

	public int compare(T arg0, T arg1) {
		HashMap t1 = (HashMap)arg0;
		HashMap t2 = (HashMap)arg1;
		
		Long cnt1 = (Long)t1.get(key);
		Long cnt2 = (Long)t2.get(key);
		
		if (cnt1 > cnt2) {
			return -1;
		} else if(cnt1 < cnt2) {
			return 1;
		} else {
			return 0;
		}
	}

}
