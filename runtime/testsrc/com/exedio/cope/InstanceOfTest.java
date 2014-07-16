/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cope;

import static com.exedio.cope.InstanceOfAItem.code;
import static com.exedio.cope.InstanceOfC1Item.textc1;
import static com.exedio.cope.InstanceOfRefItem.ref;
import static com.exedio.cope.InstanceOfRefItem.refb2;
import static com.exedio.cope.RuntimeAssert.assertCondition;

public class InstanceOfTest extends AbstractRuntimeTest
{
	public InstanceOfTest()
	{
		super(InstanceOfModelTest.MODEL);
	}

	private final Type<InstanceOfAItem> TYPE_A = InstanceOfAItem.TYPE;
	private final Type<InstanceOfB1Item> TYPE_B1 = InstanceOfB1Item.TYPE;
	private final Type<InstanceOfB2Item> TYPE_B2 = InstanceOfB2Item.TYPE;
	private final Type<InstanceOfC1Item> TYPE_C1 = InstanceOfC1Item.TYPE;
	private final Type<InstanceOfRefItem> TYPE_REF = InstanceOfRefItem.TYPE;

	InstanceOfAItem itema;
	InstanceOfB1Item itemb1;
	InstanceOfB2Item itemb2;
	InstanceOfC1Item itemc1;

	InstanceOfRefItem reffa;
	InstanceOfRefItem reffb1;
	InstanceOfRefItem reffb2;
	InstanceOfRefItem reffc1;
	InstanceOfRefItem reffN;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();

		itema = deleteOnTearDown(new InstanceOfAItem("itema"));
		itemb1 = deleteOnTearDown(new InstanceOfB1Item("itemb1"));
		itemb2 = deleteOnTearDown(new InstanceOfB2Item("itemb2"));
		itemc1 = deleteOnTearDown(new InstanceOfC1Item("itemc1"));

		reffa = deleteOnTearDown(new InstanceOfRefItem(itema));
		reffb1 = deleteOnTearDown(new InstanceOfRefItem(itemb1));
		reffb2 = deleteOnTearDown(new InstanceOfRefItem(itemb2));
		reffc1 = deleteOnTearDown(new InstanceOfRefItem(itemc1));
		reffN = deleteOnTearDown(new InstanceOfRefItem(null));
	}

	public void testIt()
	{
		assertContains(itema, itemb1, itemb2, itemc1,        TYPE_A.search(null));
		assertContains(reffa, reffb1, reffb2, reffc1, reffN, TYPE_REF.search(null));

		assertCondition(itema, itemb1, itemb2, TYPE_A, TYPE_A.getThis().notInstanceOf(TYPE_C1));
		assertCondition(itema, itemb2, TYPE_A, TYPE_A.getThis().notInstanceOf(TYPE_B1));
		assertCondition(itema, itemb2, TYPE_A, TYPE_A.getThis().notInstanceOf(TYPE_B1, TYPE_C1));
		assertCondition(itema, itemb1, itemc1, TYPE_A, TYPE_A.getThis().notInstanceOf(TYPE_B2));
		assertCondition(itema, itemb1, TYPE_A, TYPE_A.getThis().notInstanceOf(TYPE_B2, TYPE_C1));
		assertCondition(TYPE_A, TYPE_A.getThis().notInstanceOf(TYPE_A));
		assertCondition(TYPE_A, TYPE_A.getThis().notInstanceOf(new Type<?>[]{TYPE_A, TYPE_B1, TYPE_B2, TYPE_C1}));
		assertCondition(itemc1, TYPE_A, TYPE_A.getThis().instanceOf(TYPE_C1));

		assertCondition(reffa, reffb1, reffb2, TYPE_REF, ref.notInstanceOf(TYPE_C1));
		assertCondition(reffa, reffb2, TYPE_REF, ref.notInstanceOf(TYPE_B1));
		assertCondition(reffa, reffb2, TYPE_REF, ref.notInstanceOf(TYPE_B1, TYPE_C1));
		assertCondition(reffa, reffb1, reffc1, TYPE_REF, ref.notInstanceOf(TYPE_B2));
		assertCondition(reffa, reffb1, TYPE_REF, ref.notInstanceOf(TYPE_B2, TYPE_C1));
		assertCondition(TYPE_REF, ref.notInstanceOf(TYPE_A));
		assertCondition(TYPE_REF, ref.notInstanceOf(new Type<?>[]{TYPE_A, TYPE_B1, TYPE_B2, TYPE_C1}));
		assertCondition(reffc1, TYPE_REF, ref.instanceOf(TYPE_C1));

		model.checkTypeColumns();

		// test self joins and inheritance
		{
			itemc1.setTextc1("textC1");
			final Query<InstanceOfC1Item> q = TYPE_C1.newQuery(code.equal("itemc1"));
			final Join j = q.join(TYPE_C1);
			j.setCondition(textc1.bind(j).equal(textc1));
			assertContains(itemc1, q.search());
		}
		{
			final Query<InstanceOfC1Item> q = TYPE_C1.newQuery(code.equal("itemc1"));
			final Join j = q.join(TYPE_B2);
			j.setCondition(code.bind(j).equal(code));
			assertContains(q.search());
		}
		{
			final Query<InstanceOfC1Item> q = TYPE_C1.newQuery(code.equal("itemc1").and(TYPE_B1.getThis().notInstanceOf(TYPE_C1)));
			q.join(TYPE_B2);
			assertContains(q.search());
		}
		{
			final Query<InstanceOfC1Item> q = TYPE_C1.newQuery(code.equal("itemc1"));
			final Join j = q.join(TYPE_B2);
			j.setCondition(TYPE_B1.getThis().notInstanceOf(TYPE_C1));
			assertContains(q.search());
		}
		{
			final Query<InstanceOfC1Item> q = TYPE_C1.newQuery(code.equal("itemc1").and(TYPE_A.getThis().notInstanceOf(TYPE_C1)));
			q.join(TYPE_B2);
			assertContains(q.search());
		}
		{
			final Query<InstanceOfC1Item> q = TYPE_C1.newQuery(code.equal("itemc1"));
			final Join j = q.join(TYPE_B2);
			j.setCondition(TYPE_A.getThis().notInstanceOf(TYPE_C1));
			assertContains(q.search());
		}
		{
			final Query<InstanceOfC1Item> q = TYPE_C1.newQuery(code.equal("itemc1").and(TYPE_A.getThis().notInstanceOf(TYPE_C1)));
			q.join(TYPE_B1);
			assertContains(q.search());
		}
		{
			final Query<InstanceOfC1Item> q = TYPE_C1.newQuery(code.equal("itemc1"));
			final Join j = q.join(TYPE_B1);
			j.setCondition(TYPE_A.getThis().notInstanceOf(TYPE_C1));
			assertContains(q.search());
		}

		assertEquals("InstanceOfAItem.this instanceOf InstanceOfC1Item", TYPE_A.getThis().instanceOf(TYPE_C1).toString());
		assertEquals("InstanceOfAItem.this instanceOf [InstanceOfC1Item, InstanceOfB1Item]", TYPE_A.getThis().instanceOf(TYPE_C1, TYPE_B1).toString());
		assertEquals("InstanceOfAItem.this not instanceOf InstanceOfC1Item", TYPE_A.getThis().notInstanceOf(TYPE_C1).toString());
		assertEquals("InstanceOfAItem.this not instanceOf [InstanceOfC1Item, InstanceOfB1Item]", TYPE_A.getThis().notInstanceOf(TYPE_C1, TYPE_B1).toString());
		try
		{
			TYPE_A.getThis().instanceOf((Type[])null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("types", e.getMessage());
		}
		try
		{
			TYPE_A.getThis().instanceOf(new Type<?>[]{});
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("types must not be empty", e.getMessage());
		}
		try
		{
			TYPE_A.getThis().instanceOf(new Type<?>[]{null});
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("types[0]", e.getMessage());
		}
	}

	public void testPolymorphicJoinCondition()
	{
		{
			final Query<InstanceOfRefItem> q = InstanceOfRefItem.TYPE.newQuery();
			q.join(InstanceOfAItem.TYPE, InstanceOfRefItem.ref.equalTarget());
			assertContains(reffa, reffb1, reffb2, reffc1, q.search());
		}
		{
			final Query<InstanceOfRefItem> q = InstanceOfRefItem.TYPE.newQuery();
			q.join(InstanceOfB2Item.TYPE, InstanceOfRefItem.refb2.equalTarget());
			assertContains(q.search());
		}

		{
			final Query<InstanceOfRefItem> q = InstanceOfRefItem.TYPE.newQuery();
			q.join(InstanceOfAItem.TYPE, InstanceOfRefItem.refb2.equalTarget());
			try
			{
				q.search();
				fail();
			}
			catch(final IllegalArgumentException e)
			{
				assertEquals(
						"InstanceOfB2Item.this does not belong to a type of the query: " +
						"select this from InstanceOfRefItem join InstanceOfAItem i1 on refb2=InstanceOfB2Item.this",
						e.getMessage());
			}
		}
		{
			final Query<InstanceOfRefItem> q = InstanceOfRefItem.TYPE.newQuery();
			q.join(InstanceOfB2Item.TYPE, InstanceOfRefItem.ref.equalTarget());
			try
			{
				q.search();
				fail();
			}
			catch(final IllegalArgumentException e)
			{
				assertEquals(
						"InstanceOfAItem.this does not belong to a type of the query: " +
						"select this from InstanceOfRefItem join InstanceOfB2Item i1 on ref=InstanceOfAItem.this",
						e.getMessage());
			}
		}
	}

	@SuppressWarnings({"unchecked", "cast", "rawtypes"})
	public void testPolymorphicJoinConditionUnchecked()
	{
		{
			final Query q = InstanceOfRefItem.TYPE.newQuery();
			q.join(InstanceOfAItem.TYPE, InstanceOfRefItem.refb2.equal((This<InstanceOfB2Item>)(This)InstanceOfAItem.TYPE.getThis())); // TODO
			assertContains(q.search());
		}
	}

	public void testPolymorphicJoinCondition2()
	{
		{
			final Query<InstanceOfRefItem> q = InstanceOfRefItem.TYPE.newQuery();
			q.join(InstanceOfB2Item.TYPE, InstanceOfRefItem.ref.equal(InstanceOfB2Item.TYPE.getThis()));
			assertContains(reffb2, q.search());
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"}) // OK: test bad API usage
	public void testUnchecked()
	{
		try
		{
			TYPE_B2.search(TYPE_B2.getThis().notInstanceOf((Type)TYPE_B1));
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("type InstanceOfB2Item has no subtypes, therefore a TypeInCondition makes no sense", e.getMessage());
		}
		try
		{
			TYPE_REF.search(refb2.notInstanceOf((Type)TYPE_B1));
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("type InstanceOfB2Item has no subtypes, therefore a TypeInCondition makes no sense", e.getMessage());
		}
		try
		{
			TYPE_B1.search(TYPE_B1.getThis().notInstanceOf((Type)TYPE_A));
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("type InstanceOfB1Item is not assignable from type InstanceOfAItem", e.getMessage());
		}
	}

	public void testSchema()
	{
		assertSchema();
	}
}
