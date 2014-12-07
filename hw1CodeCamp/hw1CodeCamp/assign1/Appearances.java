package assign1;

import java.util.*;

public class Appearances {
	
	/**
	 * Returns the number of elements that appear the same number
	 * of times in both collections. Static method. (see handout).
	 * @return number of same-appearance elements
	 */
	public static <T> int sameCount(Collection<T> a, Collection<T> b) {
		int same = 0;
		
		HashMap<T, Integer> hm1 = new HashMap<T, Integer>();
		for (T t : a) {
			if (!hm1.containsKey(t)) {
				hm1.put(t, 0);
			}
			hm1.put(t, hm1.get(t) + 1);
		}
		HashMap<T, Integer> hm2 = new HashMap<T, Integer>();
		for (T t : b) {
			if (!hm2.containsKey(t)) {
				hm2.put(t, 0);
			}
			hm2.put(t, hm2.get(t) + 1);
		}
		for (Map.Entry<T, Integer> me : hm2.entrySet()) {
			if (hm1.get(me.getKey()) == me.getValue()) {
				same += 1;
			}
		}
		
		
		return same;
	}
	
}
