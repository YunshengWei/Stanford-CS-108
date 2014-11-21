// TabooTest.java
// Taboo class tests -- nothing provided.
package assign1;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.HashSet;

import org.junit.Test;

public class TabooTest {

	
	@Test
	public void testNoFollow1() {
		List<String> l = Arrays.asList("a", "c", "a", "b");
		Taboo<String> t = new Taboo<String>(l);
		assertEquals(new HashSet<String>(Arrays.asList("c", "b")), t.noFollow("a"));
		assertEquals(Collections.emptySet(), t.noFollow("x"));
	}
	
	@Test
	public void testReduce1() {
		List<String> l = Arrays.asList("a", "c", "a", "b");
		Taboo<String> t = new Taboo<String>(l);
		List<String> l2 = new ArrayList<String>(Arrays.asList("a", "c", "b", "x", "c", "a"));
		t.reduce(l2);
		assertEquals(Arrays.asList("a", "x", "c"), l2);
	}
	
}
