package com.exedio.cope.lib;

import com.exedio.cope.testmodel.PointerItem;
import com.exedio.cope.testmodel.PointerItem2;

public class JoinTest extends DatabaseLibTest
{
	PointerItem item1a;
	PointerItem item1b;
	PointerItem2 item2a;
	PointerItem2 item2b;

	protected void setUp() throws NotNullViolationException
	{
		item2a = new PointerItem2("hallo");
		item2b = new PointerItem2("bello");
		item1a = new PointerItem("bello", item2a);
		item1b = new PointerItem("collo", item2b);
	}

	protected void tearDown() throws IntegrityViolationException
	{
		if (item1a != null)
			item1a.delete();
		if (item1b != null)
			item1b.delete();
		if (item2a != null)
			item2a.delete();
		if (item2b != null)
			item2b.delete();
	}

	public void testJoin()
	{
		assertContains(
			item2b,
			Search.search(
				new Query(
					PointerItem2.TYPE,
					PointerItem.TYPE,
					Search.equal(PointerItem.code, PointerItem2.code))));

		assertContains(
			item1a,
			Search.search(
				new Query(
					PointerItem.TYPE,
					PointerItem2.TYPE,
					Search.and(
						Search.join(PointerItem.pointer),
						Search.equal(PointerItem2.code, "hallo")))));
	}

}
