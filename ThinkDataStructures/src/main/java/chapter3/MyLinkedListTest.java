/**
 * 
 */
package chapter3;

import java.util.ArrayList;

import chapter2.MyArrayListTest;
import org.junit.Before;


/**!
 * @author downey
 *
 */
public class MyLinkedListTest extends MyArrayListTest {

	/**
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		list = new ArrayList<Integer>();
		list.add(1);
		list.add(2);
		list.add(3);

		mylist = new MyLinkedList<>();
		mylist.addAll(list);
	}
}
