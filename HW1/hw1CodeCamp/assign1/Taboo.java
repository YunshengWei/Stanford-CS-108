/*
 HW1 Taboo problem class.
 Taboo encapsulates some rules about what objects
 may not follow other objects.
 (See handout).
*/
package assign1;

import java.util.*;

public class Taboo<T> {
	private HashMap<T, Set<T>> rules;
	/**
	 * Constructs a new Taboo using the given rules (see handout.)
	 * @param rules rules for new Taboo
	 */
	public Taboo(List<T> rules) {
		this.rules = new HashMap<T, Set<T>>();
		for (int i = 1; i < rules.size(); i++) {
			T t1 = rules.get(i);
			T t2 = rules.get(i - 1);
			if (t1 != null && t2 != null) {
				if (this.rules.get(t2) == null) {
					this.rules.put(t2, new HashSet<T>());
				}
				this.rules.get(t2).add(t1);
			}
		}
	}
	
	/**
	 * Returns the set of elements which should not follow
	 * the given element.
	 * @param elem
	 * @return elements which should not follow the given element
	 */
	public Set<T> noFollow(T elem) {
		 if (rules.get(elem) != null) {
			 return rules.get(elem);
		 } else {
			 return Collections.emptySet();
		 }
	}
	
	/**
	 * Removes elements from the given list that
	 * violate the rules (see handout).
	 * @param list collection to reduce
	 */
	public void reduce(List<T> list) {
		int i = 1;
		int last = 0;
		while (i < list.size()) {
			if (rules.get(list.get(last)) != null && rules.get(list.get(last)).contains(list.get(i))) {
				list.remove(i);
			} else {
				i++;
				last++;
			}
		}
	}
}
