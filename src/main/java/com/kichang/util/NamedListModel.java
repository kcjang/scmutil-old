package com.kichang.util;

import java.util.List;

public class NamedListModel<E, V> {
	private String name;
	private List<E> list;
	private List<V> list2;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List getList() {
		return list;
	}

	public List<V> getList2() {
		return list2;
	}
	public void setList2(List<V> list2) {
		this.list2 = list2;
	}
	public void setList(List<E> list) {
		this.list = list;
	}
	
	
	
}
