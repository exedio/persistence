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



public class EnumTest extends AbstractLibTest
{
	EnumItem item;
	EnumItem2 item2;
	
	private static final EnumItem.Status status1 = EnumItem.Status.status1;
	private static final EnumItem2.Status state1 = EnumItem2.Status.state1;
	
	public EnumTest()
	{
		super(Main.enumModel);
	}
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		deleteOnTearDown(item = new EnumItem(EnumItem.Status.status1));
		deleteOnTearDown(item2 = new EnumItem2(EnumItem2.Status.state1));
	}

	public void testIt()
	{
		assertSame(null, item.status.cast(null));
		assertSame(EnumItem.Status.status1, item.status.cast(EnumItem.Status.status1));
		try
		{
			item.status.cast(1);
			fail();
		}
		catch(ClassCastException e)
		{
			assertEquals("expected a " + EnumItem.Status.class.getName() + ", but was a " + Integer.class.getName(), e.getMessage());
		}
		try
		{
			item.status.cast(EnumItem2.Status.state1);
			fail();
		}
		catch(ClassCastException e)
		{
			assertEquals("expected a " + EnumItem.Status.class.getName() + ", but was a " + EnumItem2.Status.class.getName(), e.getMessage());
		}
		
		assertEquals(status1, item.getStatus());
		assertEquals(state1, item2.getStatus());
	}
	
}
