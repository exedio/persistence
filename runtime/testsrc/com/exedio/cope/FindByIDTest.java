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
	private void assertFail(final String id, final String message, final boolean notAnID)
	{
		assertIDFails(id, message, notAnID);
	}

	public void testFindbyID()
	{
		assertFail("noDotInThisString", "no such id <noDotInThisString>, no dot in id", true);
		assertFail("noSuchType.x", "no such id <noSuchType.x>, no such type noSuchType", true);
		assertFail("EmptyItem.x", "no such id <EmptyItem.x>, wrong number format <x>", true);
		assertFail("EmptyItem.92386591832651832659213865193456293456", "no such id <EmptyItem.92386591832651832659213865193456293456>, wrong number format <92386591832651832659213865193456293456>", true);
		// special test for items without any attributes
		assertFail("EmptyItem.51", "no such id <EmptyItem.51>, item <51> does not exist", false);
		assertFail("AttributeItem.-1", "no such id number <-1>, must be positive", true);
		assertFail("AttributeItem.50", "no such id <AttributeItem.50>, item <50> does not exist", false);
	}
	
}
