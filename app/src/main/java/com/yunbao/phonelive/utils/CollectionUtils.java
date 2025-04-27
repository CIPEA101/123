package com.yunbao.phonelive.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 集合的工具类
 * 
 * @author Administrator
 * 
 */
public class CollectionUtils {

	public static boolean isEmpty(Collection c) {
		return !isNotEmpty(c);
	}

	/**
	 * 判断集合是否有元素
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isNotEmpty(Collection c) {
		return c != null && !c.isEmpty();
	}
	//截取list
	public static <T> List<T> getSubList(List<T> list, int fromIndex, int toIndex) {
		if (toIndex > list.size()) {
			return list;
		}
		List listClone = list;
		List sub = listClone.subList(fromIndex, toIndex);
		List two = new ArrayList(sub);
		sub.clear(); // since sub is backed by one, this removes all sub-list items from one
		return two;
	}
}
