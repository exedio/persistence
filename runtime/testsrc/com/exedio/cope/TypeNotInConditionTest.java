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


public class TypeNotInConditionTest extends AbstractLibTest
{
	public TypeNotInConditionTest()
	{
		super(Main.typeNotInConditionModel);
	}
	
	TypeNotInConditionAItem itema;
	TypeNotInConditionB1Item itemb1;
	TypeNotInConditionB2Item itemb2;
	TypeNotInConditionC1Item itemc1;

	TypeNotInConditionRefItem reffa;
	TypeNotInConditionRefItem reffb1;
	TypeNotInConditionRefItem reffb2;
	TypeNotInConditionRefItem reffc1;
	
	public void setUp() throws Exception
	{
		super.setUp();
		
		deleteOnTearDown(itema = new TypeNotInConditionAItem("itema"));
		deleteOnTearDown(itemb1 = new TypeNotInConditionB1Item("itemb1"));
		deleteOnTearDown(itemb2 = new TypeNotInConditionB2Item("itemb2"));
		deleteOnTearDown(itemc1 = new TypeNotInConditionC1Item("itemc1"));
		
		deleteOnTearDown(reffa = new TypeNotInConditionRefItem(itema));
		deleteOnTearDown(reffb1 = new TypeNotInConditionRefItem(itemb1));
		deleteOnTearDown(reffb2 = new TypeNotInConditionRefItem(itemb2));
		deleteOnTearDown(reffc1 = new TypeNotInConditionRefItem(itemc1));
	}
	
	public void testTypeNotInCondition()
	{
		assertContains(itema, itemb1, itemb2, itemc1, itema.TYPE.search(null));
		assertContains(reffa, reffb1, reffb2, reffc1, reffa.TYPE.search(null));

		assertContains(itema, itemb1, itemb2, itema.TYPE.search(Cope.typeNotIn(itema.TYPE.getThis(), itemc1.TYPE)));
		assertContains(itema, itemb2, itema.TYPE.search(Cope.typeNotIn(itema.TYPE.getThis(), itemb1.TYPE)));
		assertContains(itema, itemb2, itema.TYPE.search(Cope.typeNotIn(itema.TYPE.getThis(), itemb1.TYPE, itemc1.TYPE)));
		assertContains(itema, itemb1, itemc1, itema.TYPE.search(Cope.typeNotIn(itema.TYPE.getThis(), itemb2.TYPE)));
		assertContains(itema, itemb1, itema.TYPE.search(Cope.typeNotIn(itema.TYPE.getThis(), itemb2.TYPE, itemc1.TYPE)));
		assertContains(itema.TYPE.search(Cope.typeNotIn(itema.TYPE.getThis(), itema.TYPE)));
		assertContains(itema.TYPE.search(Cope.typeNotIn(itema.TYPE.getThis(), new Type[]{itema.TYPE, itemb1.TYPE, itemb2.TYPE, itemc1.TYPE})));

		assertContains(reffa, reffb1, reffb2, reffa.TYPE.search(Cope.typeNotIn(reffa.ref, itemc1.TYPE)));
		assertContains(reffa, reffb2, reffa.TYPE.search(Cope.typeNotIn(reffa.ref, itemb1.TYPE)));
		assertContains(reffa, reffb2, reffa.TYPE.search(Cope.typeNotIn(reffa.ref, itemb1.TYPE, itemc1.TYPE)));
		assertContains(reffa, reffb1, reffc1, reffa.TYPE.search(Cope.typeNotIn(reffa.ref, itemb2.TYPE)));
		assertContains(reffa, reffb1, reffa.TYPE.search(Cope.typeNotIn(reffa.ref, itemb2.TYPE, itemc1.TYPE)));
		assertContains(reffa.TYPE.search(Cope.typeNotIn(reffa.ref, itema.TYPE)));
		assertContains(reffa.TYPE.search(Cope.typeNotIn(reffa.ref, new Type[]{itema.TYPE, itemb1.TYPE, itemb2.TYPE, itemc1.TYPE})));
	}

	@SuppressWarnings("unchecked") // OK: test bad API usage
	public void testUnchecked()
	{
		try
		{
			itemb2.TYPE.search(Cope.typeNotIn(itemb2.TYPE.getThis(), (Type)itemb1.TYPE));
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("type TypeNotInConditionB2Item has no subtypes, therefore a TypeNotInCondition makes no sense", e.getMessage());
		}
		try
		{
			itemb1.TYPE.search(Cope.typeNotIn(itemb1.TYPE.getThis(), (Type)itema.TYPE));
			fail();
		}
		catch(RuntimeException e)
		{
			assertEquals("type TypeNotInConditionB1Item is not assignable from excluded type TypeNotInConditionAItem", e.getMessage());
		}
	}
	
}
