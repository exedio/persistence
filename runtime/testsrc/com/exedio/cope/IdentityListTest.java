package com.exedio.cope;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class IdentityListTest
{
	@Test
	void grow()
	{
		final IdentityList<String> list = new IdentityList<>();
		list.add("a");
		list.add("b");
		list.add("c");
		list.add("d");
		list.add("e");
		list.add("f"); // 6th entry exceeds initial size

		assertEquals(false, list.contains("x"));
		assertEquals(true, list.contains("a"));
		//noinspection StringOperationCanBeSimplified
		assertEquals(false, list.contains(new String("a")));
		assertEquals(true, list.contains("f"));
	}

	@Test
	void addNull()
	{
		final IdentityList<String> list = new IdentityList<>();
		assertEquals(false, list.contains(null));

		list.add(null);
		assertEquals(true, list.contains(null));
	}
}
