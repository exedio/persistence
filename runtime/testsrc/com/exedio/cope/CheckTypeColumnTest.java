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

public class CheckTypeColumnTest extends AbstractRuntimeTest
{
	public CheckTypeColumnTest()
	{
		super(InstanceOfTest.MODEL);
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

	public void testOk()
	{
		try
		{
			InstanceOfAItem.TYPE.getThis().checkTypeColumn();
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("no check for type column needed for InstanceOfAItem.this", e.getMessage());
		}

		assertEquals(0, InstanceOfB1Item.TYPE.getThis().checkTypeColumn());
		assertEquals(0, InstanceOfB2Item.TYPE.getThis().checkTypeColumn());
		assertEquals(0, InstanceOfC1Item.TYPE.getThis().checkTypeColumn());

		assertEquals(0, InstanceOfRefItem.ref.checkTypeColumn());
		try
		{
			assertEquals(0, InstanceOfRefItem.refb2.checkTypeColumn());
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("no check for type column needed for InstanceOfRefItem.refb2", e.getMessage());
		}
	}
}
