/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

public class InstanceOfTest extends AbstractRuntimeTest
{
	public/*for web.xml*/ static final Model MODEL = new Model(
			InstanceOfAItem.TYPE,
			InstanceOfB1Item.TYPE,
			InstanceOfB2Item.TYPE,
			InstanceOfC1Item.TYPE,
			InstanceOfRefItem.TYPE);

	public InstanceOfTest()
	{
		super(MODEL);
	}

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
		// test ItemField#as
		assertSame(InstanceOfRefItem.ref, InstanceOfRefItem.ref.as(InstanceOfAItem.class));
		try
		{
			InstanceOfRefItem.ref.as(InstanceOfB1Item.class);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals(
					"expected a " + ItemField.class.getName() + '<' + InstanceOfB1Item.class.getName() + ">, " +
					"but was a " + ItemField.class.getName() + '<' + InstanceOfAItem.class.getName() + '>',
				e.getMessage());
		}
		assertSame(InstanceOfRefItem.refb2, InstanceOfRefItem.refb2.as(InstanceOfB2Item.class));
		try
		{
			InstanceOfRefItem.refb2.as(InstanceOfB1Item.class);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals(
					"expected a " + ItemField.class.getName() + '<' + InstanceOfB1Item.class.getName() + ">, " +
					"but was a " + ItemField.class.getName() + '<' + InstanceOfB2Item.class.getName() + '>',
				e.getMessage());
		}
		try
		{
			InstanceOfRefItem.refb2.as(InstanceOfAItem.class);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals(
					"expected a " + ItemField.class.getName() + '<' + InstanceOfAItem.class.getName() + ">, " +
					"but was a " + ItemField.class.getName() + '<' + InstanceOfB2Item.class.getName() + '>',
				e.getMessage());
		}

		// test Type.getSubtypes()
		assertEqualsUnmodifiable(list(InstanceOfB1Item.TYPE, InstanceOfB2Item.TYPE), InstanceOfAItem.TYPE.getSubtypes());
		assertEqualsUnmodifiable(list(InstanceOfC1Item.TYPE), InstanceOfB1Item.TYPE.getSubtypes());
		assertEqualsUnmodifiable(list(), InstanceOfB2Item.TYPE.getSubtypes());
		assertEqualsUnmodifiable(list(), InstanceOfC1Item.TYPE.getSubtypes());
		// test Type.getSubtypesTransitively()
		assertEqualsUnmodifiable(list(InstanceOfAItem.TYPE, InstanceOfB1Item.TYPE, InstanceOfC1Item.TYPE, InstanceOfB2Item.TYPE), InstanceOfAItem.TYPE.getSubtypesTransitively());
		assertEqualsUnmodifiable(list(InstanceOfB1Item.TYPE, InstanceOfC1Item.TYPE), InstanceOfB1Item.TYPE.getSubtypesTransitively());
		assertEqualsUnmodifiable(list(InstanceOfB2Item.TYPE), InstanceOfB2Item.TYPE.getSubtypesTransitively());
		assertEqualsUnmodifiable(list(InstanceOfC1Item.TYPE), InstanceOfC1Item.TYPE.getSubtypesTransitively());
		// test Type.getTypesOfInstances()
		assertEqualsUnmodifiable(list(InstanceOfAItem.TYPE, InstanceOfB1Item.TYPE, InstanceOfC1Item.TYPE, InstanceOfB2Item.TYPE), InstanceOfAItem.TYPE.getTypesOfInstances());
		assertEqualsUnmodifiable(list(InstanceOfB1Item.TYPE, InstanceOfC1Item.TYPE), InstanceOfB1Item.TYPE.getSubtypesTransitively());
		assertEqualsUnmodifiable(list(InstanceOfB2Item.TYPE), InstanceOfB2Item.TYPE.getSubtypesTransitively());
		assertEqualsUnmodifiable(list(InstanceOfC1Item.TYPE), InstanceOfC1Item.TYPE.getSubtypesTransitively());


		assertContains(itema, itemb1, itemb2, itemc1,        itema.TYPE.search(null));
		assertContains(reffa, reffb1, reffb2, reffc1, reffN, reffa.TYPE.search(null));

		assertCondition(itema, itemb1, itemb2, itema.TYPE, itema.TYPE.getThis().notInstanceOf(itemc1.TYPE));
		assertCondition(itema, itemb2, itema.TYPE, itema.TYPE.getThis().notInstanceOf(itemb1.TYPE));
		assertCondition(itema, itemb2, itema.TYPE, itema.TYPE.getThis().notInstanceOf(itemb1.TYPE, itemc1.TYPE));
		assertCondition(itema, itemb1, itemc1, itema.TYPE, itema.TYPE.getThis().notInstanceOf(itemb2.TYPE));
		assertCondition(itema, itemb1, itema.TYPE, itema.TYPE.getThis().notInstanceOf(itemb2.TYPE, itemc1.TYPE));
		assertCondition(itema.TYPE, itema.TYPE.getThis().notInstanceOf(itema.TYPE));
		assertCondition(itema.TYPE, itema.TYPE.getThis().notInstanceOf(new Type[]{itema.TYPE, itemb1.TYPE, itemb2.TYPE, itemc1.TYPE}));
		assertCondition(itemc1, itema.TYPE, itema.TYPE.getThis().instanceOf(itemc1.TYPE));

		assertCondition(reffa, reffb1, reffb2, reffa.TYPE, reffa.ref.notInstanceOf(itemc1.TYPE));
		assertCondition(reffa, reffb2, reffa.TYPE, reffa.ref.notInstanceOf(itemb1.TYPE));
		assertCondition(reffa, reffb2, reffa.TYPE, reffa.ref.notInstanceOf(itemb1.TYPE, itemc1.TYPE));
		assertCondition(reffa, reffb1, reffc1, reffa.TYPE, reffa.ref.notInstanceOf(itemb2.TYPE));
		assertCondition(reffa, reffb1, reffa.TYPE, reffa.ref.notInstanceOf(itemb2.TYPE, itemc1.TYPE));
		assertCondition(reffa.TYPE, reffa.ref.notInstanceOf(itema.TYPE));
		assertCondition(reffa.TYPE, reffa.ref.notInstanceOf(new Type[]{itema.TYPE, itemb1.TYPE, itemb2.TYPE, itemc1.TYPE}));
		assertCondition(reffc1, reffa.TYPE, reffa.ref.instanceOf(itemc1.TYPE));

		model.checkTypeColumns();

		// test self joins and inheritance
		{
			itemc1.setTextc1("textC1");
			final Query<InstanceOfC1Item> q = itemc1.TYPE.newQuery(itemc1.code.equal("itemc1"));
			final Join j = q.join(itemc1.TYPE);
			j.setCondition(itemc1.textc1.bind(j).equal(itemc1.textc1));
			assertContains(itemc1, q.search());
		}
		{
			final Query<InstanceOfC1Item> q = itemc1.TYPE.newQuery(itemc1.code.equal("itemc1"));
			final Join j = q.join(itemb2.TYPE);
			j.setCondition(itemc1.code.bind(j).equal(itemb2.code));
			assertContains(q.search());
		}
		{
			final Query<InstanceOfC1Item> q = itemc1.TYPE.newQuery(itemc1.code.equal("itemc1").and(itemb1.TYPE.getThis().notInstanceOf(itemc1.TYPE)));
			q.join(itemb2.TYPE);
			assertContains(q.search());
		}
		{
			final Query<InstanceOfC1Item> q = itemc1.TYPE.newQuery(itemc1.code.equal("itemc1"));
			final Join j = q.join(itemb2.TYPE);
			j.setCondition(itemb1.TYPE.getThis().notInstanceOf(itemc1.TYPE));
			assertContains(q.search());
		}
		{
			final Query<InstanceOfC1Item> q = itemc1.TYPE.newQuery(itemc1.code.equal("itemc1").and(itema.TYPE.getThis().notInstanceOf(itemc1.TYPE)));
			q.join(itemb2.TYPE);
			assertContains(q.search());
		}
		{
			final Query<InstanceOfC1Item> q = itemc1.TYPE.newQuery(itemc1.code.equal("itemc1"));
			final Join j = q.join(itemb2.TYPE);
			j.setCondition(itema.TYPE.getThis().notInstanceOf(itemc1.TYPE));
			assertContains(q.search());
		}
		{
			final Query<InstanceOfC1Item> q = itemc1.TYPE.newQuery(itemc1.code.equal("itemc1").and(itema.TYPE.getThis().notInstanceOf(itemc1.TYPE)));
			q.join(itemb1.TYPE);
			assertContains(q.search());
		}
		{
			final Query<InstanceOfC1Item> q = itemc1.TYPE.newQuery(itemc1.code.equal("itemc1"));
			final Join j = q.join(itemb1.TYPE);
			j.setCondition(itema.TYPE.getThis().notInstanceOf(itemc1.TYPE));
			assertContains(q.search());
		}

		assertEquals("InstanceOfAItem.this instanceOf InstanceOfC1Item", itema.TYPE.getThis().instanceOf(itemc1.TYPE).toString());
		assertEquals("InstanceOfAItem.this instanceOf [InstanceOfC1Item, InstanceOfB1Item]", itema.TYPE.getThis().instanceOf(itemc1.TYPE, itemb1.TYPE).toString());
		assertEquals("InstanceOfAItem.this not instanceOf InstanceOfC1Item", itema.TYPE.getThis().notInstanceOf(itemc1.TYPE).toString());
		assertEquals("InstanceOfAItem.this not instanceOf [InstanceOfC1Item, InstanceOfB1Item]", itema.TYPE.getThis().notInstanceOf(itemc1.TYPE, itemb1.TYPE).toString());
		try
		{
			itema.TYPE.getThis().instanceOf((Type[])null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("types", e.getMessage());
		}
		try
		{
			itema.TYPE.getThis().instanceOf(new Type[]{});
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("types must not be empty", e.getMessage());
		}
	}

	public void testPolymorphicJoinCondition()
	{
		{
			final Query q = InstanceOfRefItem.TYPE.newQuery();
			q.join(InstanceOfAItem.TYPE, InstanceOfRefItem.ref.equalTarget());
			assertContains(reffa, reffb1, reffb2, reffc1, q.search());
		}
		{
			final Query q = InstanceOfRefItem.TYPE.newQuery();
			q.join(InstanceOfB2Item.TYPE, InstanceOfRefItem.refb2.equalTarget());
			assertContains(q.search());
		}

		{
			final Query q = InstanceOfRefItem.TYPE.newQuery();
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
			final Query q = InstanceOfRefItem.TYPE.newQuery();
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

	@SuppressWarnings({"unchecked", "cast"})
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
			final Query q = InstanceOfRefItem.TYPE.newQuery();
			q.join(InstanceOfB2Item.TYPE, InstanceOfRefItem.ref.equal(InstanceOfB2Item.TYPE.getThis()));
			assertContains(reffb2, q.search());
		}
	}

	@SuppressWarnings("unchecked") // OK: test bad API usage
	public void testUnchecked()
	{
		try
		{
			itemb2.TYPE.search(itemb2.TYPE.getThis().notInstanceOf((Type)itemb1.TYPE));
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("type InstanceOfB2Item has no subtypes, therefore a TypeInCondition makes no sense", e.getMessage());
		}
		try
		{
			reffa.TYPE.search(reffa.refb2.notInstanceOf((Type)itemb1.TYPE));
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("type InstanceOfB2Item has no subtypes, therefore a TypeInCondition makes no sense", e.getMessage());
		}
		try
		{
			itemb1.TYPE.search(itemb1.TYPE.getThis().notInstanceOf((Type)itema.TYPE));
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("type InstanceOfB1Item is not assignable from type InstanceOfAItem", e.getMessage());
		}
	}
}
