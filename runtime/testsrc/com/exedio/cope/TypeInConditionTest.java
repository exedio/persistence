/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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


public class TypeInConditionTest extends AbstractLibTest
{
	static final Model MODEL = new Model(
			TypeInConditionAItem.TYPE,
			TypeInConditionB1Item.TYPE,
			TypeInConditionB2Item.TYPE,
			TypeInConditionC1Item.TYPE,
			TypeInConditionRefItem.TYPE);

	public TypeInConditionTest()
	{
		super(MODEL);
	}
	
	TypeInConditionAItem itema;
	TypeInConditionB1Item itemb1;
	TypeInConditionB2Item itemb2;
	TypeInConditionC1Item itemc1;

	TypeInConditionRefItem reffa;
	TypeInConditionRefItem reffb1;
	TypeInConditionRefItem reffb2;
	TypeInConditionRefItem reffc1;
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		
		deleteOnTearDown(itema = new TypeInConditionAItem("itema"));
		deleteOnTearDown(itemb1 = new TypeInConditionB1Item("itemb1"));
		deleteOnTearDown(itemb2 = new TypeInConditionB2Item("itemb2"));
		deleteOnTearDown(itemc1 = new TypeInConditionC1Item("itemc1"));
		
		deleteOnTearDown(reffa = new TypeInConditionRefItem(itema));
		deleteOnTearDown(reffb1 = new TypeInConditionRefItem(itemb1));
		deleteOnTearDown(reffb2 = new TypeInConditionRefItem(itemb2));
		deleteOnTearDown(reffc1 = new TypeInConditionRefItem(itemc1));
	}
	
	public void testIt()
	{
		assertContains(itema, itemb1, itemb2, itemc1, itema.TYPE.search(null));
		assertContains(reffa, reffb1, reffb2, reffc1, reffa.TYPE.search(null));

		assertContains(itema, itemb1, itemb2, itema.TYPE.search(itema.TYPE.getThis().notInstanceOf(itemc1.TYPE)));
		assertContains(itema, itemb2, itema.TYPE.search(itema.TYPE.getThis().notInstanceOf(itemb1.TYPE)));
		assertContains(itema, itemb2, itema.TYPE.search(itema.TYPE.getThis().notInstanceOf(itemb1.TYPE, itemc1.TYPE)));
		assertContains(itema, itemb1, itemc1, itema.TYPE.search(itema.TYPE.getThis().notInstanceOf(itemb2.TYPE)));
		assertContains(itema, itemb1, itema.TYPE.search(itema.TYPE.getThis().notInstanceOf(itemb2.TYPE, itemc1.TYPE)));
		assertContains(itema.TYPE.search(itema.TYPE.getThis().notInstanceOf(itema.TYPE)));
		assertContains(itema.TYPE.search(itema.TYPE.getThis().notInstanceOf(new Type[]{itema.TYPE, itemb1.TYPE, itemb2.TYPE, itemc1.TYPE})));
		assertContains(itemc1, itema.TYPE.search(itema.TYPE.getThis().instanceOf(itemc1.TYPE)));

		assertContains(reffa, reffb1, reffb2, reffa.TYPE.search(reffa.ref.notInstanceOf(itemc1.TYPE)));
		assertContains(reffa, reffb2, reffa.TYPE.search(reffa.ref.notInstanceOf(itemb1.TYPE)));
		assertContains(reffa, reffb2, reffa.TYPE.search(reffa.ref.notInstanceOf(itemb1.TYPE, itemc1.TYPE)));
		assertContains(reffa, reffb1, reffc1, reffa.TYPE.search(reffa.ref.notInstanceOf(itemb2.TYPE)));
		assertContains(reffa, reffb1, reffa.TYPE.search(reffa.ref.notInstanceOf(itemb2.TYPE, itemc1.TYPE)));
		assertContains(reffa.TYPE.search(reffa.ref.notInstanceOf(itema.TYPE)));
		assertContains(reffa.TYPE.search(reffa.ref.notInstanceOf(new Type[]{itema.TYPE, itemb1.TYPE, itemb2.TYPE, itemc1.TYPE})));
		assertContains(reffc1, reffa.TYPE.search(reffa.ref.instanceOf(itemc1.TYPE)));
		
		model.checkTypeColumns();
		
		// test self joins and inheritance
		{
			itemc1.setTextc1("textC1");
			final Query<TypeInConditionC1Item> q = itemc1.TYPE.newQuery(itemc1.code.equal("itemc1"));
			final Join j = q.join(itemc1.TYPE);
			j.setCondition(itemc1.textc1.bind(j).equal(itemc1.textc1));
			if(!noJoinParentheses) assertContains(itemc1, q.search());
		}
		{
			final Query<TypeInConditionC1Item> q = itemc1.TYPE.newQuery(itemc1.code.equal("itemc1"));
			final Join j = q.join(itemb2.TYPE);
			j.setCondition(itemc1.code.bind(j).equal(itemb2.code));
			if(!noJoinParentheses) assertContains(q.search());
		}
		{
			final Query<TypeInConditionC1Item> q = itemc1.TYPE.newQuery(itemc1.code.equal("itemc1").and(itemb1.TYPE.getThis().notInstanceOf(itemc1.TYPE)));
			q.join(itemb2.TYPE);
			if(!noJoinParentheses) assertContains(q.search());
		}
		{
			final Query<TypeInConditionC1Item> q = itemc1.TYPE.newQuery(itemc1.code.equal("itemc1"));
			final Join j = q.join(itemb2.TYPE);
			j.setCondition(itemb1.TYPE.getThis().notInstanceOf(itemc1.TYPE));
			if(!noJoinParentheses) assertContains(q.search());
		}
		{
			final Query<TypeInConditionC1Item> q = itemc1.TYPE.newQuery(itemc1.code.equal("itemc1").and(itema.TYPE.getThis().notInstanceOf(itemc1.TYPE)));
			q.join(itemb2.TYPE);
			if(!noJoinParentheses) assertContains(q.search());
		}
		{
			final Query<TypeInConditionC1Item> q = itemc1.TYPE.newQuery(itemc1.code.equal("itemc1"));
			final Join j = q.join(itemb2.TYPE);
			j.setCondition(itema.TYPE.getThis().notInstanceOf(itemc1.TYPE));
			if(!noJoinParentheses) assertContains(q.search());
		}
		{
			final Query<TypeInConditionC1Item> q = itemc1.TYPE.newQuery(itemc1.code.equal("itemc1").and(itema.TYPE.getThis().notInstanceOf(itemc1.TYPE)));
			q.join(itemb1.TYPE);
			if(!noJoinParentheses) assertContains(q.search());
		}
		{
			final Query<TypeInConditionC1Item> q = itemc1.TYPE.newQuery(itemc1.code.equal("itemc1"));
			final Join j = q.join(itemb1.TYPE);
			j.setCondition(itema.TYPE.getThis().notInstanceOf(itemc1.TYPE));
			if(!noJoinParentheses) assertContains(q.search());
		}
		
		try
		{
			itema.TYPE.getThis().instanceOf((Type[])null);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals("types must not be null", e.getMessage());
		}
		try
		{
			itema.TYPE.getThis().instanceOf(new Type[]{});
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("types must not be empty", e.getMessage());
		}
	}

	public void testSchemaNames()
	{
		// with sub types
		assertEquals("TypeInConditionAItem", TypeInConditionAItem.TYPE.getTableName());
		assertEquals("this", TypeInConditionAItem.TYPE.getPrimaryKeyColumnName());
		assertEquals("class", TypeInConditionAItem.TYPE.getTypeColumnName());
		assertEquals("code", TypeInConditionAItem.code.getColumnName());
		assertEquals("ref", TypeInConditionRefItem.ref.getColumnName());
		assertEquals("refType", TypeInConditionRefItem.ref.getTypeColumnName());

		// without sub types
		assertEquals("TypeInConditionB2Item", TypeInConditionB2Item.TYPE.getTableName());
		assertEquals("this", TypeInConditionB2Item.TYPE.getPrimaryKeyColumnName());
		try
		{
			TypeInConditionB2Item.TYPE.getTypeColumnName();
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("no type column for TypeInConditionB2Item", e.getMessage());
		}
		assertEquals("textb2", TypeInConditionB2Item.textb2.getColumnName());
		assertEquals("refb2", TypeInConditionRefItem.refb2.getColumnName());
		try
		{
			TypeInConditionRefItem.refb2.getTypeColumnName();
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("no type column for TypeInConditionRefItem.refb2", e.getMessage());
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
		catch(IllegalArgumentException e)
		{
			assertEquals("type TypeInConditionB2Item has no subtypes, therefore a TypeInCondition makes no sense", e.getMessage());
		}
		try
		{
			reffa.TYPE.search(reffa.refb2.notInstanceOf((Type)itemb1.TYPE));
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("type TypeInConditionB2Item has no subtypes, therefore a TypeInCondition makes no sense", e.getMessage());
		}
		try
		{
			itemb1.TYPE.search(itemb1.TYPE.getThis().notInstanceOf((Type)itema.TYPE));
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("type TypeInConditionB1Item is not assignable from type TypeInConditionAItem", e.getMessage());
		}
	}
}
