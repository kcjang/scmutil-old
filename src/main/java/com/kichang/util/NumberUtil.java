package com.kichang.util;

public class NumberUtil {

	public static int bitNot(int src, int offset) {
		int mask = 1 << offset;
		return ~(src ^ (~mask));
	}
	
	public static long bitNot(long src, int offset) {
		long mask = 1 << offset;
		return ~(src ^ (~mask));
	}
	
	public static int bitSet(int src, int offset, boolean bSet) {
		boolean isBitSet = isBitSet(src,offset);
		if (isBitSet == bSet)
			return src;
		else
			return bitNot(src,offset);
	}
	public static long bitSet(long src, int offset, boolean bSet) {
		boolean isBitSet = isBitSet(src,offset);
		if (isBitSet == bSet)
			return src;
		else
			return bitNot(src,offset);
	}	
	public static boolean isBitSet(int src, int offset) {
		int mask = 1 << offset;
		return (src & mask) > 0 ? true : false;
	}
	public static boolean isBitSet(long src, int offset) {
		long mask = 1 << offset;
		return (src & mask) > 0 ? true : false;
	}
}
