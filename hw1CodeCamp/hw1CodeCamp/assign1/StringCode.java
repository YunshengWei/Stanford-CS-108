package assign1;

import java.util.HashSet;
import java.util.Set;

// CS108 HW1 -- String static methods

public class StringCode {

	/**
	 * Given a string, returns the length of the largest run.
	 * A a run is a series of adajcent chars that are the same.
	 * @param str
	 * @return max run length
	 */
	public static int maxRun(String str) {
		if (str == "") {
			return 0;
		}
		int maxCount = 0;
		int currentCount = 1;
		char lastChar = str.charAt(0);
		for (int i = 1; i < str.length(); i++) {
			if (lastChar != str.charAt(i) || i == str.length() - 1) {
				maxCount = Math.max(maxCount, currentCount);
				lastChar = str.charAt(i);
				currentCount = 1;
			} else {
				currentCount += 1;
			}
		}
		return maxCount;
	}

	
	/**
	 * Given a string, for each digit in the original string,
	 * replaces the digit with that many occurrences of the character
	 * following. So the string "a3tx2z" yields "attttxzzz".
	 * @param str
	 * @return blown up string
	 */
	public static String blowup(String str) {
		StringBuilder ret = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (ch < '0' || ch > '9') {
				ret.append(ch);
			} else {
				if (i != str.length() - 1) {
					char ch2 = str.charAt(i + 1);
					for (int j = 0; j < ch - '0'; j++) {
						ret.append(ch2);
					}
				}
			}
		}
		return ret.toString();
	}
	
	/**
	 * Given 2 strings, consider all the substrings within them
	 * of length len. Returns true if there are any such substrings
	 * which appear in both strings.
	 * Compute this in linear time using a HashSet. Len will be 1 or more.
	 */
	public static boolean stringIntersect(String a, String b, int len) {
		HashSet<String> hs = new HashSet<String>();
		for (int i = 0; i <= a.length() - len; i++) {
			hs.add(a.substring(i, i + len));
		}
		for (int i = 0; i <= b.length() - len; i++) {
			if (hs.contains(b.substring(i, i + len))) {
				return true;
			}
		}
		return false;
	}
}
