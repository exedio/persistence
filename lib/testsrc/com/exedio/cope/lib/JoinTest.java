package com.exedio.cope.lib;

import com.exedio.cope.testmodel.PointerItem;
import com.exedio.cope.testmodel.PointerItem2;

public class JoinTest extends DatabaseLibTest
{
	PointerItem item1a;
	PointerItem item1b;
	PointerItem2 item2a;
	PointerItem2 item2b;

	protected void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(item2a = new PointerItem2("hallo"));
		deleteOnTearDown(item2b = new PointerItem2("bello"));
		deleteOnTearDown(item1a = new PointerItem("bello", item2a));
		deleteOnTearDown(item1b = new PointerItem("collo", item2b));
	}

	public void testJoin()
	{
		assertContains(
				item2b, item2a, item2b, item2a,
				Cope.search(new Query(PointerItem2.TYPE, PointerItem.TYPE, null)));

		assertContains(
				item2b, item2a,
				Cope.search(new Query(PointerItem2.TYPE, PointerItem.TYPE, Cope.join(PointerItem.pointer))));

		assertContains(
			item2b,
			Cope.search(
				new Query(
					PointerItem2.TYPE,
					PointerItem.TYPE,
					Cope.equal(PointerItem.code, PointerItem2.code))));

		assertContains(
			item1a,
			Cope.search(
				new Query(
					PointerItem.TYPE,
					PointerItem2.TYPE,
					Cope.and(
						Cope.join(PointerItem.pointer),
						Cope.equal(PointerItem2.code, "hallo")))));
	}

}
