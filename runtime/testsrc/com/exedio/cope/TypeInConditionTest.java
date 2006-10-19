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
	public TypeInConditionTest()
	{
		super(Main.typeInConditionModel);
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

		assertContains(itema, itemb1, itemb2, itema.TYPE.search(itema.TYPE.getThis().typeNotIn(itemc1.TYPE)));
		assertContains(itema, itemb2, itema.TYPE.search(itema.TYPE.getThis().typeNotIn(itemb1.TYPE)));
		assertContains(itema, itemb2, itema.TYPE.search(itema.TYPE.getThis().typeNotIn(itemb1.TYPE, itemc1.TYPE)));
		assertContains(itema, itemb1, itemc1, itema.TYPE.search(itema.TYPE.getThis().typeNotIn(itemb2.TYPE)));
		assertContains(itema, itemb1, itema.TYPE.search(itema.TYPE.getThis().typeNotIn(itemb2.TYPE, itemc1.TYPE)));
		assertContains(itema.TYPE.search(itema.TYPE.getThis().typeNotIn(itema.TYPE)));
		assertContains(itema.TYPE.search(itema.TYPE.getThis().typeNotIn(new Type[]{itema.TYPE, itemb1.TYPE, itemb2.TYPE, itemc1.TYPE})));
		assertContains(itemc1, itema.TYPE.search(itema.TYPE.getThis().typeIn(itemc1.TYPE)));

		assertContains(reffa, reffb1, reffb2, reffa.TYPE.search(reffa.ref.typeNotIn(itemc1.TYPE)));
		assertContains(reffa, reffb2, reffa.TYPE.search(reffa.ref.typeNotIn(itemb1.TYPE)));
		assertContains(reffa, reffb2, reffa.TYPE.search(reffa.ref.typeNotIn(itemb1.TYPE, itemc1.TYPE)));
		assertContains(reffa, reffb1, reffc1, reffa.TYPE.search(reffa.ref.typeNotIn(itemb2.TYPE)));
		assertContains(reffa, reffb1, reffa.TYPE.search(reffa.ref.typeNotIn(itemb2.TYPE, itemc1.TYPE)));
		assertContains(reffa.TYPE.search(reffa.ref.typeNotIn(itema.TYPE)));
		assertContains(reffa.TYPE.search(reffa.ref.typeNotIn(new Type[]{itema.TYPE, itemb1.TYPE, itemb2.TYPE, itemc1.TYPE})));
		assertContains(reffc1, reffa.TYPE.search(reffa.ref.typeIn(itemc1.TYPE)));
		
		model.checkTypeColumns();
		
		//System.out.println("------------------------"+model.getDatabaseInfo().get("database.version"));
		// test self joins and inheritance
		{
			itemc1.setTextc1("textC1");
			final Query<TypeInConditionC1Item> q = itemc1.TYPE.newQuery(itemc1.code.equal("itemc1"));
			final Join j = q.join(itemc1.TYPE);
			j.setCondition(itemc1.textc1.bind(j).equal(itemc1.textc1));
			assertContains(itemc1, q.search());
		}
		if(!hsqldb&&!oracle&&!postgresql&&!((String)model.getDatabaseInfo().get("database.version")).endsWith("(5.0)")) // TODO dont know why
		{
			final Query<TypeInConditionC1Item> q = itemc1.TYPE.newQuery(itemc1.code.equal("itemc1"));
			final Join j = q.join(itemb2.TYPE);
			j.setCondition(itemc1.code.bind(j).equal(itemb2.code));
			assertContains(q.search());
		}
		if(!hsqldb&&!oracle&&!postgresql) // TODO dont know why
		{
			final Query<TypeInConditionC1Item> q = itemc1.TYPE.newQuery(itemc1.code.equal("itemc1").and(itemb1.TYPE.getThis().typeNotIn(itemc1.TYPE)));
			q.join(itemb2.TYPE);
			assertContains(q.search());
		}
		{
			final Query<TypeInConditionC1Item> q = itemc1.TYPE.newQuery(itemc1.code.equal("itemc1"));
			final Join j = q.join(itemb2.TYPE);
			j.setCondition(itemb1.TYPE.getThis().typeNotIn(itemc1.TYPE));
			assertContains(q.search());
		}
		if(!hsqldb&&!oracle&&!postgresql) // TODO dont know why
		{
			final Query<TypeInConditionC1Item> q = itemc1.TYPE.newQuery(itemc1.code.equal("itemc1").and(itema.TYPE.getThis().typeNotIn(itemc1.TYPE)));
			q.join(itemb2.TYPE);
			assertContains(q.search());
		}
		{
			final Query<TypeInConditionC1Item> q = itemc1.TYPE.newQuery(itemc1.code.equal("itemc1"));
			final Join j = q.join(itemb2.TYPE);
			j.setCondition(itema.TYPE.getThis().typeNotIn(itemc1.TYPE));
			assertContains(q.search());
		}
		if(!hsqldb&&!oracle&&!postgresql) // TODO dont know why
		{
			final Query<TypeInConditionC1Item> q = itemc1.TYPE.newQuery(itemc1.code.equal("itemc1").and(itema.TYPE.getThis().typeNotIn(itemc1.TYPE)));
			q.join(itemb1.TYPE);
			assertContains(q.search());
		}
		{
			final Query<TypeInConditionC1Item> q = itemc1.TYPE.newQuery(itemc1.code.equal("itemc1"));
			final Join j = q.join(itemb1.TYPE);
			j.setCondition(itema.TYPE.getThis().typeNotIn(itemc1.TYPE));
			assertContains(q.search());
		}
	}

	@SuppressWarnings("unchecked") // OK: test bad API usage
	public void testUnchecked()
	{
		try
		{
			itemb2.TYPE.search(itemb2.TYPE.getThis().typeNotIn((Type)itemb1.TYPE));
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("type TypeInConditionB2Item has no subtypes, therefore a TypeInCondition makes no sense", e.getMessage());
		}
		try
		{
			reffa.TYPE.search(reffa.refb2.typeNotIn((Type)itemb1.TYPE));
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("type TypeInConditionB2Item has no subtypes, therefore a TypeInCondition makes no sense", e.getMessage());
		}
		try
		{
			itemb1.TYPE.search(itemb1.TYPE.getThis().typeNotIn((Type)itema.TYPE));
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("type TypeInConditionB1Item is not assignable from type TypeInConditionAItem", e.getMessage());
		}
	}
	
}
