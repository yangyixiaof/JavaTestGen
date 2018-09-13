package randoop.generation.date.test;

import org.junit.Assert;
import org.junit.Test;

import randoop.generation.date.runtime.DateRuntime;

public class DateRuntimeStringBoringTest {

	@Test
	public void testInsert1() {
		String before = "aloha";
		try {
			String after = DateRuntime.insert(before, -1);
			System.err.println(after);
		} catch (ArrayIndexOutOfBoundsException e) {
			return;
		}
		Assert.fail();
	}

	@Test
	public void testInsert2() {
		String before = "aloha";
		String after = DateRuntime.insert(before, 0);
		Assert.assertEquals(" aloha", after);
	}

	@Test
	public void testInsert3() {
		String before = "aloha";
		String after = DateRuntime.insert(before, 2);
		Assert.assertEquals("al oha", after);
	}

	@Test
	public void testInsert4() {
		String before = "aloha";
		String after = DateRuntime.insert(before, before.length());
		Assert.assertEquals("aloha ", after);
	}

	@Test
	public void testInsert5() {
		String before = "aloha";
		try {
			String after = DateRuntime.insert(before, before.length() + 1);
			System.err.println(after);
		} catch (ArrayIndexOutOfBoundsException e) {
			return;
		}
		Assert.fail();
	}

	@Test
	public void testRemove1() {
		String before = "aloha";
		try {
			String after = DateRuntime.remove(before, -1);
			System.err.println(after);
		} catch (ArrayIndexOutOfBoundsException e) {
			return;
		}
		Assert.fail();
	}

	@Test
	public void testRemove2() {
		String before = "aloha";
		String after = DateRuntime.remove(before, 0);
		Assert.assertEquals("loha", after);
	}

	@Test
	public void testRemove3() {
		String before = "aloha";
		String after = DateRuntime.remove(before, 2);
		Assert.assertEquals("alha", after);
	}

	@Test
	public void testRemove4() {
		String before = "aloha";
		String after = DateRuntime.remove(before, before.length() - 1);
		Assert.assertEquals("aloh", after);
	}

	@Test
	public void testRemove5() {
		String before = "aloha";
		try {
			String after = DateRuntime.remove(before, before.length());
			System.err.println(after);
		} catch (ArrayIndexOutOfBoundsException e) {
			return;
		}
		Assert.fail();
	}

	@Test
	public void testModify1() {
		String before = "aloha";
		try {
			String after = DateRuntime.modify(before, -1, 2);
			System.err.println(after);
		} catch (ArrayIndexOutOfBoundsException e) {
			return;
		}
		Assert.fail();
	}

	@Test
	public void testModify2() {
		String before = "aloha";
		String after = DateRuntime.modify(before, 0, 2);
		Assert.assertEquals("cloha", after);
	}

	@Test
	public void testModify3() {
		String before = "aloha";
		String after = DateRuntime.modify(before, 2, 2);
		Assert.assertEquals("alqha", after);
	}

	@Test
	public void testModify4() {
		String before = "aloha";
		String after = DateRuntime.modify(before, before.length() - 1, 2);
		Assert.assertEquals("alohc", after);
	}

	@Test
	public void testModify5() {
		String before = "aloha";
		try {
			String after = DateRuntime.modify(before, before.length(), 2);
			System.err.println(after);
		} catch (ArrayIndexOutOfBoundsException e) {
			return;
		}
		Assert.fail();
	}

	@Test
	public void testModifyTooMuch() {
		String before = "aloha";
		String after = DateRuntime.modify(before, before.length() - 1, 200);
		System.out.println(after);
		Assert.fail();
		// Assert.assertEquals("alohc", after);
	}
}
