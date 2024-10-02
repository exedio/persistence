/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.tojunit.SI;
import com.exedio.dsmf.SQLRuntimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InstanceOfTest extends TestWithEnvironment
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

	@BeforeEach final void setUp()
	{
		itema  = new InstanceOfAItem("itema");
		itemb1 = new InstanceOfB1Item("itemb1");
		itemb2 = new InstanceOfB2Item("itemb2");
		itemc1 = new InstanceOfC1Item("itemc1");

		reffa  = new InstanceOfRefItem(itema);
		reffb1 = new InstanceOfRefItem(itemb1);
		reffb2 = new InstanceOfRefItem(itemb2);
		reffc1 = new InstanceOfRefItem(itemc1);
		reffN  = new InstanceOfRefItem(null);
	}

	@Test void testAll()
	{
		assertContains(itema, itemb1, itemb2, itemc1,        TYPE_A.search(null));
		assertContains(itema, itemb1, itemb2, itemc1, null,  new Query<>(ref, TYPE_REF, null).search());
		assertContains(reffa, reffb1, reffb2, reffc1, reffN, TYPE_REF.search(null));
	}

	@Test void testThis()
	{
		assertCondition(itema, itemb1, itemb2, TYPE_A, TYPE_A.getThis().notInstanceOf(TYPE_C1));
		assertCondition(itema, itemb2, TYPE_A, TYPE_A.getThis().notInstanceOf(TYPE_B1));
		assertCondition(itema, itemb2, TYPE_A, TYPE_A.getThis().notInstanceOf(TYPE_B1, TYPE_C1));
		assertCondition(itema, itemb1, itemc1, TYPE_A, TYPE_A.getThis().notInstanceOf(TYPE_B2));
		assertCondition(itema, itemb1, TYPE_A, TYPE_A.getThis().notInstanceOf(TYPE_B2, TYPE_C1));
		assertCondition(TYPE_A, TYPE_A.getThis().notInstanceOf(TYPE_A));
		assertCondition(TYPE_A, TYPE_A.getThis().notInstanceOf(new Type<?>[]{TYPE_A, TYPE_B1, TYPE_B2, TYPE_C1}));
		assertCondition(itemc1, TYPE_A, TYPE_A.getThis().instanceOf(TYPE_C1));
	}

	@Test void testRef()
	{
		assertCondition(reffa, reffb1, reffb2, TYPE_REF, ref.notInstanceOf(TYPE_C1));
		assertCondition(reffa, reffb2, TYPE_REF, ref.notInstanceOf(TYPE_B1));
		assertCondition(reffa, reffb2, TYPE_REF, ref.notInstanceOf(TYPE_B1, TYPE_C1));
		assertCondition(reffa, reffb1, reffc1, TYPE_REF, ref.notInstanceOf(TYPE_B2));
		assertCondition(reffa, reffb1, TYPE_REF, ref.notInstanceOf(TYPE_B2, TYPE_C1));
		assertCondition(TYPE_REF, ref.notInstanceOf(TYPE_A));
		assertCondition(TYPE_REF, ref.notInstanceOf(new Type<?>[]{TYPE_A, TYPE_B1, TYPE_B2, TYPE_C1}));
		assertCondition(reffc1, TYPE_REF, ref.instanceOf(TYPE_C1));
	}

	@Test void testNot()
	{
		assertCondition(reffc1, TYPE_REF, ref.notInstanceOf(TYPE_C1).not());
		assertCondition(reffa, reffb1, reffb2, TYPE_REF, ref.instanceOf(TYPE_C1).not());
	}

	@Test void testCheckTypeColumns()
	{
		model.checkTypeColumns();
	}

	@Test void testBrokenSelectViewThis()
	{
		// TODO would be better if this did work
		final Query<InstanceOfAItem> q =
				new Query<>(CoalesceView.coalesce(TYPE_A.getThis(), itemc1), TYPE_A, null);
		final String param = model.getConnectProperties().isSupportDisabledForPreparedStatements() ? "3" : "?3?";
		final SQLRuntimeException e = assertFails(
				q::search,
				SQLRuntimeException.class,
				"SELECT coalesce(" + SI.pk(TYPE_A) + "," + SI.type(TYPE_A) + "," + param + ") FROM " + SI.tab(TYPE_A));
		switch(dialect)
		{
			case hsqldb ->
				assertEquals(
						"incompatible data types in combination" + ifPrep(" in statement [" +
						"SELECT coalesce(" + SI.pk(TYPE_A) + "," + SI.type(TYPE_A) + ",?) FROM " + SI.tab(TYPE_A) + "]"),
						e.getCause().getMessage());
			case mysql ->
				assertEquals(
						mariaDriver
						? "Wrong index position. Is 2 but must be in 1-1 range"
						: "Column Index out of range, 2 > 1. ",
						e.getCause().getMessage());
			case postgresql ->
				assertEquals(
						"ERROR: COALESCE types integer and character varying cannot be matched",
						e.getCause().getMessage());
			default ->
				throw new RuntimeException(String.valueOf(dialect));
		}
	}

	@Test void testBrokenSelectViewItemField()
	{
		// TODO would be better if this did work
		final Query<InstanceOfAItem> q =
				new Query<>(CoalesceView.coalesce(ref, itemc1), TYPE_REF, null);
		final String param = model.getConnectProperties().isSupportDisabledForPreparedStatements() ? "3" : "?3?";
		final SQLRuntimeException e = assertFails(
				q::search,
				SQLRuntimeException.class,
				"SELECT coalesce(" + SI.col(ref) + "," + SI.type(ref) + "," + param + ") FROM " + SI.tab(TYPE_REF));
		switch(dialect)
		{
			case hsqldb ->
				assertEquals(
						"incompatible data types in combination" + ifPrep(" in statement [" +
						"SELECT coalesce(" + SI.col(ref) + "," + SI.type(ref) + ",?) FROM " + SI.tab(TYPE_REF) + "]"),
						e.getCause().getMessage());
			case mysql ->
				assertEquals(
						mariaDriver
						? "Wrong index position. Is 2 but must be in 1-1 range"
						: "Column Index out of range, 2 > 1. ",
						e.getCause().getMessage());
			case postgresql ->
				assertEquals(
						"ERROR: COALESCE types integer and character varying cannot be matched",
						e.getCause().getMessage());
			default ->
				throw new RuntimeException(String.valueOf(dialect));
		}
	}

	@Test void testBrokenSelectAggregateThis()
	{
		// TODO would be better if this did work
		final Query<InstanceOfAItem> q =
				new Query<>(TYPE_A.getThis().max(), TYPE_A, null);
		final SQLRuntimeException e = assertFails(
				q::search,
				SQLRuntimeException.class,
				"SELECT MAX(" + SI.pk(TYPE_A) + "," + SI.type(TYPE_A) + ") FROM " + SI.tab(TYPE_A));
		switch(dialect)
		{
			case hsqldb ->
				assertEquals(
						"unexpected token : , required: )" + ifPrep(" in statement [" +
						"SELECT MAX(" + SI.pk(TYPE_A) + "," + SI.type(TYPE_A) + ") FROM " + SI.tab(TYPE_A) + "]"),
						e.getCause().getMessage());
			case mysql ->
				assertEquals(
						"You have an error in your SQL syntax; check the manual that corresponds " +
						"to your MySQL server version for the right syntax to use near " +
						"'" + (atLeastMysql8()?",":"") +
						SI.type(TYPE_A) + ") FROM " + SI.tab(TYPE_A) + "' at line 1",
						dropMariaConnectionId(e.getCause().getMessage()));
			case postgresql ->
				assertEquals(
						"ERROR: function max(integer, character varying) does not exist",
						e.getCause().getMessage());
			default->
				throw new RuntimeException(String.valueOf(dialect));
		}
	}

	@Test void testBrokenSelectAggregateItemField()
	{
		// TODO would be better if this did work
		final Query<InstanceOfAItem> q =
				new Query<>(ref.max(), TYPE_REF, null);
		final SQLRuntimeException e = assertFails(
				q::search,
				SQLRuntimeException.class,
				"SELECT MAX(" + SI.col(ref) + "," + SI.type(ref) + ") FROM " + SI.tab(TYPE_REF));
		switch(dialect)
		{
			case hsqldb ->
				assertEquals(
						"unexpected token : , required: )" + ifPrep(" in statement [" +
						"SELECT MAX(" + SI.col(ref) + "," + SI.type(ref) + ") FROM " + SI.tab(TYPE_REF) + "]"),
						e.getCause().getMessage());
			case mysql ->
				assertEquals(
						"You have an error in your SQL syntax; check the manual that corresponds " +
						"to your MySQL server version for the right syntax to use near " +
						"'" + (atLeastMysql8()?",":"") +
						SI.type(ref) + ") FROM " + SI.tab(TYPE_REF) + "' at line 1",
						dropMariaConnectionId(e.getCause().getMessage()));
			case postgresql ->
				assertEquals(
						"ERROR: function max(integer, character varying) does not exist",
						e.getCause().getMessage());
			default ->
				throw new RuntimeException(String.valueOf(dialect));
		}
	}

	@Test void testSelfJoinsAndInheritance()
	{
		{
			itemc1.setTextc1("textC1");
			final Query<InstanceOfC1Item> q = TYPE_C1.newQuery(code.equal("itemc1"));
			q.join(TYPE_C1, j -> textc1.bind(j).equal(textc1));
			assertContains(itemc1, q.search());
		}
		{
			final Query<InstanceOfC1Item> q = TYPE_C1.newQuery(code.equal("itemc1"));
			q.join(TYPE_B2, j -> code.bind(j).equal(code));
			assertContains(q.search());
		}
		{
			final Query<InstanceOfC1Item> q = TYPE_C1.newQuery(code.equal("itemc1").and(TYPE_B1.getThis().notInstanceOf(TYPE_C1)));
			q.join(TYPE_B2);
			assertContains(q.search());
		}
		{
			final Query<InstanceOfC1Item> q = TYPE_C1.newQuery(code.equal("itemc1"));
			q.join(TYPE_B2, j -> TYPE_B1.getThis().notInstanceOf(TYPE_C1));
			assertContains(q.search());
		}
		{
			final Query<InstanceOfC1Item> q = TYPE_C1.newQuery(code.equal("itemc1").and(TYPE_A.getThis().notInstanceOf(TYPE_C1)));
			q.join(TYPE_B2);
			assertContains(q.search());
		}
		{
			final Query<InstanceOfC1Item> q = TYPE_C1.newQuery(code.equal("itemc1"));
			q.join(TYPE_B2, j -> TYPE_A.getThis().notInstanceOf(TYPE_C1));
			assertContains(q.search());
		}
		{
			final Query<InstanceOfC1Item> q = TYPE_C1.newQuery(code.equal("itemc1").and(TYPE_A.getThis().notInstanceOf(TYPE_C1)));
			q.join(TYPE_B1);
			assertContains(q.search());
		}
		{
			final Query<InstanceOfC1Item> q = TYPE_C1.newQuery(code.equal("itemc1"));
			q.join(TYPE_B1, j -> TYPE_A.getThis().notInstanceOf(TYPE_C1));
			assertContains(q.search());
		}
	}

	@Test void testPolymorphicJoinCondition()
	{
		{
			final Query<InstanceOfRefItem> q = InstanceOfRefItem.TYPE.newQuery();
			q.join(InstanceOfAItem.TYPE, ref.equalTarget());
			assertContains(reffa, reffb1, reffb2, reffc1, q.search());
		}
		{
			final Query<InstanceOfRefItem> q = InstanceOfRefItem.TYPE.newQuery();
			q.join(InstanceOfB2Item.TYPE, refb2.equalTarget());
			assertContains(q.search());
		}

		{
			final Query<InstanceOfRefItem> q = InstanceOfRefItem.TYPE.newQuery();
			q.join(InstanceOfAItem.TYPE, refb2.equalTarget());
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
			q.join(InstanceOfB2Item.TYPE, ref.equalTarget());
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
	@Test void testPolymorphicJoinConditionUnchecked()
	{
		{
			final Query<InstanceOfRefItem> q = InstanceOfRefItem.TYPE.newQuery();
			q.join(InstanceOfAItem.TYPE, refb2.equal((Function<InstanceOfB2Item>)(This<?>)InstanceOfAItem.TYPE.getThis())); // TODO
			assertContains(q.search());
		}
	}

	@Test void testPolymorphicJoinCondition2()
	{
		{
			final Query<InstanceOfRefItem> q = InstanceOfRefItem.TYPE.newQuery();
			q.join(InstanceOfB2Item.TYPE, ref.equal(InstanceOfB2Item.TYPE.getThis()));
			assertContains(reffb2, q.search());
		}
	}

	@Test void testNoSubtypes()
	{
		final Condition c = TYPE_B2.getThis().notInstanceOf(TYPE_B2);
		try
		{
			TYPE_B2.search(c);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("type InstanceOfB2Item has no subtypes, therefore a TypeInCondition makes no sense", e.getMessage());
		}
	}

	@Test void testNotAssignableFromBrotherThis()
	{
		@SuppressWarnings({"unchecked","rawtypes"}) // OK: test bad API usage
		final Condition c = TYPE_B2.getThis().notInstanceOf((Type)TYPE_B1);
		try
		{
			TYPE_B2.search(c);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("type InstanceOfB2Item is not assignable from type InstanceOfB1Item", e.getMessage());
		}
	}

	@Test void testNotAssignableFromBrotherRef()
	{
		@SuppressWarnings({"unchecked","rawtypes"}) // OK: test bad API usage
		final Condition c = refb2.notInstanceOf((Type)TYPE_B1);
		try
		{
			TYPE_REF.search(c);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("type InstanceOfB2Item is not assignable from type InstanceOfB1Item", e.getMessage());
		}
	}

	@Test void testNotAssignableFromSuperThis()
	{
		@SuppressWarnings({"unchecked","rawtypes"}) // OK: test bad API usage
		final Condition c = TYPE_B1.getThis().notInstanceOf((Type)TYPE_A);
		try
		{
			TYPE_B1.search(c);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("type InstanceOfB1Item is not assignable from type InstanceOfAItem", e.getMessage());
		}
	}

	@Test void testSchema()
	{
		assertSchema();
	}
}
