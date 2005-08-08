/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

public class FindByIDTest extends TestmodelTest
{
	private void assertFail(final String id, final String message)
	{
		try
		{
			model.findByID(id);
			fail("should have thrown NoSuchIDException");
		}
		catch(NoSuchIDException e)
		{
			assertEquals(message, e.getMessage());
		}
	}

	public void testFindbyID()
	{
		assertFail("noDotInThisString", "no such id <noDotInThisString>, no dot in id");
		assertFail("noSuchType.x", "no such id <noSuchType.x>, no such type noSuchType");
		assertFail("EmptyItem.x", "no such id <EmptyItem.x>, wrong number format <x>");
		assertFail("EmptyItem.92386591832651832659213865193456293456", "no such id <EmptyItem.92386591832651832659213865193456293456>, wrong number format <92386591832651832659213865193456293456>");
		// special test for items without any attributes
		assertFail("EmptyItem.51", "no such id <EmptyItem.51>, item <51> does not exist");
		assertFail("AttributeItem.-1", "no such id number <-1>, must be positive");
		assertFail("AttributeItem.50", "no such id <AttributeItem.50>, item <50> does not exist");
	}
	
}
