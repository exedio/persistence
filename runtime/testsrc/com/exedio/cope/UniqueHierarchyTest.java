/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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

/**
 * Test for bug 30
 * 
 * @author Ralf Wiebicke
 */
public class UniqueHierarchyTest extends AbstractLibTest
{
	private static final Model MODEL = new Model(UniqueHierarchySuperItem.TYPE, UniqueHierarchySubItem.TYPE);
	
	public UniqueHierarchyTest()
	{
		super(MODEL);
	}

	public void testIt()
	{
		assertEquals(list(), UniqueHierarchySuperItem.TYPE.search());
		assertEquals(list(), UniqueHierarchySubItem.TYPE.search());
		
		final UniqueHierarchySubItem item = new UniqueHierarchySubItem("super1", "sub1");
		//deleteOnTearDown(item);
		assertEquals(list(item), UniqueHierarchySuperItem.TYPE.search());
		assertEquals(list(item), UniqueHierarchySubItem.TYPE.search());

		try
		{
			new UniqueHierarchySubItem("super2", "sub1");
			fail();
		}
		catch(UniqueViolationException e)
		{
			assertEquals(item.subField.getImplicitUniqueConstraint(), e.getFeature());
		}
		assertEquals(2/* TODO sould be 1 */, UniqueHierarchySuperItem.TYPE.search().size());
		assertEquals(list(item), UniqueHierarchySubItem.TYPE.search());

		// no other way to clean up database after this bug
		model.commit();
		model.dropDatabase();
		model.createDatabase();
		model.startTransaction(getClass().getName());
	}
}
