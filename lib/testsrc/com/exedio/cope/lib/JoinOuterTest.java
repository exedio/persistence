package com.exedio.cope.lib;

import com.exedio.cope.testmodel.PointerItem;
import com.exedio.cope.testmodel.PointerItem2;

public class JoinOuterTest extends DatabaseLibTest
{
	PointerItem leftJoined;
	PointerItem leftLonely;
	PointerItem2 rightJoined;
	PointerItem2 rightLonely;

	protected void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(rightLonely = new PointerItem2("right"));
		deleteOnTearDown(rightJoined = new PointerItem2("joined"));
		deleteOnTearDown(leftJoined = new PointerItem("joined", rightJoined));
		deleteOnTearDown(leftLonely = new PointerItem("left", rightJoined));
	}

	public void testJoin()
	{
		{
			final Query query = new Query(PointerItem.TYPE, null);
			query.join(PointerItem2.TYPE, Cope.equal(PointerItem.code, PointerItem2.code));
			assertContains(leftJoined, query.search());
		}
		{
			final Query query = new Query(PointerItem.TYPE, null);
			query.joinOuterLeft(PointerItem2.TYPE, Cope.equal(PointerItem.code, PointerItem2.code));
			assertContains(leftJoined, leftLonely, query.search());
		}

		if(model.getDatabase().getClass().getName().indexOf("Hsqldb")>=0)
		{
			final Query query = new Query(PointerItem.TYPE, null);
			query.joinOuterRight(PointerItem2.TYPE, Cope.equal(PointerItem.code, PointerItem2.code));
			try
			{
				query.search();
				fail("should have throws RuntimeException");
			}
			catch(RuntimeException e)
			{
				assertEquals("hsqldb not support right outer joins", e.getMessage());
			}
		}
		else
		{
			final Query query = new Query(PointerItem.TYPE, null);
			query.joinOuterRight(PointerItem2.TYPE, Cope.equal(PointerItem.code, PointerItem2.code));
			assertContains(leftJoined, null, query.search());
		}
	}

}
